package com.mrbysco.boombox.client.audio;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import com.mrbysco.boombox.client.screen.RadioScreen;
import com.mrbysco.boombox.util.WebUtils;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class RadioHandler {

	private static final Logger LOGGER = LogUtils.getLogger();
	public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(
			Executors.newCachedThreadPool((new ThreadFactoryBuilder())
					.setDaemon(true)
					.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER))
					.setNameFormat("Downloader %d")
					.build())
	);
	private static final Map<String, CompletableFuture<InputStream>> IN_PROGRESS = new ConcurrentHashMap<>();
	private static RadioSoundInstance lastStation = null;

	public static void open(@Nullable BlockPos pos) {
		Minecraft.getInstance().setScreen(new RadioScreen(pos));
	}


	/**
	 * Returns an already existing stream or fetches a new one.
	 */
	public static CompletableFuture<InputStream> getRadioStream(String url) {
		CompletableFuture<InputStream> future = IN_PROGRESS.get(url);
		if (future != null && !future.isDone()) {
			return future;
		}

		future = streamRadio(url).handle((value, e) -> {
			if (e != null) {
				throw new CompletionException(e);
			}
			return value;
		}).thenApplyAsync(value -> {
			IN_PROGRESS.remove(url);
			return value;
		}, Minecraft.getInstance());

		IN_PROGRESS.put(url, future);
		return future;
	}

	/**
	 * Fetches a radio stream.
	 */
	private static CompletableFuture<InputStream> streamRadio(String url) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				HttpResponse<InputStream> response = WebUtils.get(url, HttpResponse.BodyHandlers.ofInputStream()).orElse(null);
				if (response == null) throw new IOException("Radio response was null for: " + url);
				if (response.headers().firstValueAsLong("Content-Length").orElse(-1L) >= 0)
					throw new IOException("Radio response had content length for: " + url + " (" + response.headers().firstValueAsLong("Content-Length").orElse(-1L) + ")");
				return new RadioStream(response::body);
			} catch (Throwable e) {
				throw new CompletionException(e);
			}
		}, DOWNLOAD_EXECUTOR);
	}

	public static void play(String url, RandomSource random, BlockPos pos) {
		stopRadioInstances();
		lastStation = new StaticRadioSoundInstance(url, random, pos);
		Minecraft.getInstance().getSoundManager().play(lastStation);
	}

	public static void play(String url, RandomSource random) {
		stopRadioInstances();
		lastStation = new RadioSoundInstance(url, random);
		Minecraft.getInstance().getSoundManager().play(lastStation);
	}

	public static void stop() {
		stopRadioInstances();
		lastStation = null;
	}

	private static void stopRadioInstances() {
		SoundManager manager = Minecraft.getInstance().getSoundManager();
		SoundEngine engine = manager.soundEngine;
		List<RadioSoundInstance> radioInstances = new ArrayList<>();
		for (SoundInstance soundInstance : engine.instanceToChannel.keySet()) {
			if (soundInstance instanceof RadioSoundInstance radioInstance) {
				radioInstances.add(radioInstance);
			}
		}
		radioInstances.forEach(manager::stop);
	}

	public static String getPlaying() {
		if (lastStation == null) return null;
		if (lastStation.isStopped()) return null;
		if (!Minecraft.getInstance().getSoundManager().isActive(lastStation)) return null;
		return lastStation.url();
	}

	public static RadioSoundInstance getLastStation() {
		return lastStation;
	}
}
