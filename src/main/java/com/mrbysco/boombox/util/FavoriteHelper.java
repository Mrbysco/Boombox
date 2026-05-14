package com.mrbysco.boombox.util;

import com.mrbysco.boombox.config.BoomboxConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FavoriteHelper {
	public static String getFavoriteKey(StationInfo station) {
		return station.uuid()
				.map(UUID::toString)
				.orElse(station.url());
	}

	public static boolean isFavorite(StationInfo station) {
		String key = getFavoriteKey(station);
		return BoomboxConfig.CLIENT.favorites.get().contains(key);
	}

	public static void toggleFavorite(StationInfo station) {
		List<String> favorites = new ArrayList<>(BoomboxConfig.CLIENT.favorites.get());
		String key = getFavoriteKey(station);

		if (favorites.contains(key)) {
			favorites.remove(key);
		} else {
			favorites.add(key);
		}

		BoomboxConfig.CLIENT.favorites.set(favorites);
		BoomboxConfig.CLIENT.favorites.save();
	}

	public static Set<String> getFavoriteKeys() {
		return new HashSet<>(BoomboxConfig.CLIENT.favorites.get());
	}

	public static boolean isUUID(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static List<StationInfo> mergeFavoritesWithResults(List<StationInfo> apiResults, RadioBrowserService service) {
		List<StationInfo> combined = new ArrayList<>();
		Set<String> addedUrls = new HashSet<>();

		// Add favorites first
		List<? extends String> favoriteKeys = BoomboxConfig.CLIENT.favorites.get();
		for (String favKey : favoriteKeys) {
			StationInfo favStation = null;

			// Check if this is a UUID
			if (service != null && isUUID(favKey)) {
				favStation = service.getStationByUUID(favKey);
			}

			// Fallback to URL-based name
			if (favStation == null) {
				favStation = new StationInfo(
						favKey,
						extractStationName(favKey),
						extractStationName(favKey),
						"",
						Optional.empty(),
						StationLocation.UNKNOWN
				);
			}

			combined.add(favStation);
			addedUrls.add(favStation.url().toLowerCase());
		}

		for (StationInfo station : apiResults) {
			if (!addedUrls.contains(station.url().toLowerCase())) {
				combined.add(station);
				addedUrls.add(station.url().toLowerCase());
			}
		}

		return combined;
	}

	private static String extractStationName(String url) {
		try {
			String filename = url.substring(url.lastIndexOf('/') + 1);
			int dotIndex = filename.lastIndexOf('.');
			if (dotIndex > 0) {
				filename = filename.substring(0, dotIndex);
			}
			return filename;
		} catch (Exception e) {
			return "Favorite Station";
		}
	}
}
