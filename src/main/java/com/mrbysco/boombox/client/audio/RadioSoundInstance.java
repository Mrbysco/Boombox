package com.mrbysco.boombox.client.audio;

import com.google.common.hash.Hashing;
import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.config.BoomboxConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.valueproviders.ConstantFloat;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public sealed class RadioSoundInstance extends AbstractSoundInstance implements TickableSoundInstance permits StaticRadioSoundInstance {

	protected final String url;
	protected boolean stopped = false;

	@SuppressWarnings("deprecation")
	public RadioSoundInstance(String url, RandomSource randomSource) {
		super(
				Identifier.fromNamespaceAndPath(BoomboxMod.MOD_ID, "radio/" + Hashing.sha1().hashUnencodedChars(url)),
				SoundSource.MASTER,
				randomSource
		);
		this.url = url;
	}

	@Override
	public WeighedSoundEvents resolve(@NotNull SoundManager manager) {
		WeighedSoundEvents soundEvents = new WeighedSoundEvents(this.getIdentifier(), null);
		soundEvents.addSound(new Sound(
				getIdentifier(),
				ConstantFloat.of(1f),
				ConstantFloat.of(1f),
				1,
				Sound.Type.FILE,
				true,
				false,
				0
		));
		this.sound = soundEvents.getSound(this.random);
		return soundEvents;
	}

	@Override
	public float getVolume() {
		return BoomboxConfig.CLIENT.volume.get() / 100f;
	}

	@Override
	public boolean isStopped() {
		return this.stopped;
	}

	@Override
	public void tick() {
		if (this != RadioHandler.getLastStation()) {
			this.stopped = true;
			return;
		}
		if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isAlive()) {
			this.x = (float) Minecraft.getInstance().player.getX();
			this.y = (float) Minecraft.getInstance().player.getY();
			this.z = (float) Minecraft.getInstance().player.getZ();
		} else {
			this.stopped = true;
		}
	}

	public CompletableFuture<AudioStream> getStream() {
		return RadioHandler.getRadioStream(this.url)
				.thenApplyAsync(stream -> {
					try {
						return new Mp3AudioStream(stream);
					} catch (Exception e) {
						throw new CompletionException(e);
					}
				}, Util.backgroundExecutor())
				.handleAsync((stream, e) -> {
					if (e != null) {
						e.printStackTrace();
					}
					return stream;
				}, Util.backgroundExecutor());
	}

	public String url() {
		return this.url;
	}

	@Override
	public CompletableFuture<AudioStream> getStream(SoundBufferLibrary library, Sound sound, boolean loop) {
		return getStream();
	}
}
