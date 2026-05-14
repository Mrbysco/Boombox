package com.mrbysco.boombox.util;

import com.mrbysco.boombox.BoomboxMod;
import de.sfuhrm.radiobrowser4j.AdvancedSearch;
import de.sfuhrm.radiobrowser4j.ConnectionParams;
import de.sfuhrm.radiobrowser4j.EndpointDiscovery;
import de.sfuhrm.radiobrowser4j.FieldName;
import de.sfuhrm.radiobrowser4j.Paging;
import de.sfuhrm.radiobrowser4j.RadioBrowser;
import de.sfuhrm.radiobrowser4j.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.mrbysco.boombox.BoomboxMod.USER_AGENT;

public class RadioBrowserService {
	private static final int DEFAULT_TIMEOUT = 5000;
	private RadioBrowser radioBrowser;

	public RadioBrowserService() {
		try {
			Optional<String> endpoint = new EndpointDiscovery(USER_AGENT).discover();
			endpoint.ifPresent(s -> this.radioBrowser = new RadioBrowser(
					ConnectionParams.builder()
							.apiUrl(s)
							.userAgent(USER_AGENT)
							.timeout(DEFAULT_TIMEOUT)
							.build()
			));
		} catch (IOException e) {
			BoomboxMod.LOGGER.error("Failed to initialize RadioBrowser API", e);
		}
	}

	/**
	 * Search for stations with optional filters
	 *
	 * @param limit       - max number of results to return
	 * @param countryCode - filter by countryCode code (e.g. "US", "CA")
	 * @param language    - filter by language (e.g. "english", "spanish")
	 * @param tag         - filter by tag/genre (e.g. "rock", "jazz")
	 * @param searchTerm  - filter by station name (partial match)
	 * @return list of matching stations
	 */
	public List<StationInfo> searchStations(int limit, String countryCode,
	                                        String language, String tag, String searchTerm) {
		if (radioBrowser == null) {
			BoomboxMod.LOGGER.warn("RadioBrowser not initialized");
			return Collections.emptyList();
		}

		try {
			AdvancedSearch.AdvancedSearchBuilder searchBuilder = AdvancedSearch.builder()
					.hideBroken(true)
					.order(FieldName.CLICKTREND)
					.reverse(true)
					.codec("MP3");  // Only MP3 streams

			if (countryCode != null && !countryCode.isEmpty()) {
				searchBuilder.countryCode(countryCode.toUpperCase());
			}
			if (language != null && !language.isEmpty()) {
				searchBuilder.language(language);
			}
			if (tag != null && !tag.isEmpty()) {
				searchBuilder.tag(tag);
			}
			if (searchTerm != null && !searchTerm.isEmpty()) {
				searchBuilder.name(searchTerm);
			}

			List<Station> stations = radioBrowser.listStationsWithAdvancedSearch(
					Paging.at(0, limit * 2),
					searchBuilder.build()
			);

			return removeDuplicates(stations).stream()
					.limit(limit)
					.map(this::convertToStationInfo)
					.collect(Collectors.toList());
		} catch (Exception e) {
			BoomboxMod.LOGGER.error("Failed to search stations", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Get top stations by click count
	 *
	 * @param limit - max number of results to return
	 * @return list of top stations
	 */
	public List<StationInfo> getTopStations(int limit) {
		if (radioBrowser == null) {
			return Collections.emptyList();
		}

		try {
			List<Station> stations = radioBrowser.listStationsWithAdvancedSearch(
					Paging.at(0, limit * 2),
					AdvancedSearch.builder()
							.hideBroken(true)
							.codec("MP3")  // Only MP3 streams
							.order(FieldName.CLICKCOUNT)
							.reverse(true)
							.build()
			);

			return removeDuplicates(stations).stream()
					.limit(limit)
					.map(this::convertToStationInfo)
					.collect(Collectors.toList());
		} catch (Exception e) {
			BoomboxMod.LOGGER.error("Failed to get top stations", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Remove duplicate stations based on normalized name (case-insensitive, trimmed)
	 *
	 * @param stations - list of stations to filter
	 * @return list of unique stations with duplicates removed
	 */
	private List<Station> removeDuplicates(List<Station> stations) {
		Set<String> seenNames = new HashSet<>();
		List<Station> unique = new ArrayList<>();

		for (Station station : stations) {
			String normalizedName = station.getName().toLowerCase().trim();
			if (seenNames.add(normalizedName)) {
				unique.add(station);
			}
		}

		return unique;
	}

	/**
	 * Convert RadioBrowser Station to our internal StationInfo format
	 *
	 * @param station - station from RadioBrowser API
	 * @return converted StationInfo object
	 */
	private StationInfo convertToStationInfo(Station station) {
		String url = station.getUrlResolved() != null ? station.getUrlResolved() : station.getUrl();
		String strippedName = station.getName().strip();

		// Map countryCode code to StationLocation
		StationLocation location = mapCountryToLocation(station.getCountryCode());

		return new StationInfo(url, strippedName, strippedName, station.getCountryCode(),
				Optional.ofNullable(station.getStationUUID()), location);
	}

	/**
	 * Map a countryCode code (e.g. "US", "CA") to our internal StationLocation enum
	 *
	 * @param countryCode - ISO countryCode code from RadioBrowser API
	 * @return corresponding StationLocation enum value, or UNKNOWN if not recognized
	 */
	private StationLocation mapCountryToLocation(String countryCode) {
		if (countryCode == null || countryCode.isEmpty()) {
			return StationLocation.UNKNOWN;
		}

		return switch (countryCode.toUpperCase()) {
			case "CA" -> StationLocation.CANADA;
			case "US" -> StationLocation.USA;
			case "EU", "DE", "FR", "GB", "IT", "ES", "NL", "BE", "SE", "NO", "DK" -> StationLocation.EUROPE;
			default -> StationLocation.UNKNOWN;
		};
	}

	/**
	 * Get station details by UUID
	 *
	 * @param uuid - station UUID as a string
	 * @return StationInfo if found, or null if not found or invalid UUID
	 */
	public StationInfo getStationByUUID(String uuid) {
		if (radioBrowser == null || uuid == null || uuid.isEmpty()) {
			return null;
		}

		try {
			UUID stationUUID = UUID.fromString(uuid);
			Optional<Station> station = radioBrowser.getStationByUUID(stationUUID);
			if (station.isPresent()) {
				return convertToStationInfo(station.get());
			}
		} catch (IllegalArgumentException e) {
			BoomboxMod.LOGGER.debug("Invalid UUID format: {}", uuid, e);
		} catch (Exception e) {
			BoomboxMod.LOGGER.debug("Failed to find station by UUID: {}", uuid, e);
		}

		return null;
	}
}
