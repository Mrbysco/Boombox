package com.mrbysco.boombox.network.handler;

import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.network.client.PlayStationPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handlePlayStation(final PlayStationPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player();
					if (data.url().isBlank()) {
						RadioHandler.stop();
					} else {
						data.pos().ifPresentOrElse(
								pos -> RadioHandler.play(data.url(), player.getRandom(), pos),
								() -> RadioHandler.play(data.url(), player.getRandom())
						);
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("boombox.networking.play_station.failed", e.getMessage()));
					return null;
				});
	}
}
