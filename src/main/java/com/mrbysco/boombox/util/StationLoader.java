package com.mrbysco.boombox.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.boombox.BoomboxMod;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class StationLoader {
	private static final Codec<List<StationInfo>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			StationInfo.CODEC.listOf().fieldOf("stations").forGetter(Function.identity())
	).apply(instance, Function.identity()));

	private static final List<StationInfo> STATIONS = new ArrayList<>();

	public static void init() {
		JsonObject object = readLocalStations();
		if (object == null) {
			object = WebUtils.getJson("https://raw.githubusercontent.com/Mrbysco/Boombox/refs/heads/main/stations.json");
		}

		if (object == null) return;
		STATIONS.clear();
		CODEC.parse(JsonOps.INSTANCE, object)
				.result()
				.ifPresent(STATIONS::addAll);
	}

	public static List<StationInfo> stations() {
		return STATIONS;
	}

	public static boolean hasStation(String url) {
		return STATIONS.stream().anyMatch(station -> station.url().equals(url));
	}

	@Nullable
	private static JsonObject readLocalStations() {
		String stationsFile = System.getProperty("boombox.stations");
		if (stationsFile != null) {
			try {
				String fileName = stationsFile.indexOf('/') == -1 ? stationsFile : stationsFile.substring(stationsFile.lastIndexOf('/') + 1);
				BoomboxMod.LOGGER.info("Loading stations from {}", fileName);
				try {
					return BoomboxMod.GSON.fromJson(Files.readString(Path.of(stationsFile)), JsonObject.class);
				} catch (Exception e) {
					return null;
				}
			} catch (Exception ignored) {
				return null;
			}
		}
		return null;
	}
}
