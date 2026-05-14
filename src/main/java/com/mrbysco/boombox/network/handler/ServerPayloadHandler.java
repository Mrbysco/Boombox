package com.mrbysco.boombox.network.handler;

import com.mrbysco.boombox.network.server.SetStationPayload;
import com.mrbysco.boombox.util.RadioHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSetStation(final SetStationPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					//Execute craft if button is pressed
					if (context.player() != null) {
						Player player = context.player();
						if (data.pos().isPresent()) {
							boolean inRange = player.distanceToSqr(data.pos().get().getCenter()) <= 64;
							if (inRange && player.level().getBlockEntity(data.pos().get()) instanceof RadioHolder holder) {
								playStation(holder, data.url());
							}
						} else if (player.getVehicle() instanceof RadioHolder holder) {
							playStation(holder, data.url());
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("boombox.networking.set_station.failed", e.getMessage()));
					return null;
				});
	}

	private void playStation(RadioHolder holder, String url) {
		if (url.isBlank() || isValidStreamUrl(url)) {
			holder.setRadioUrl(url);
		}
	}

	private boolean isValidStreamUrl(String url) {
		if (url == null || url.isEmpty()) {
			return false;
		}
		String lower = url.toLowerCase();
		return lower.startsWith("http://") || lower.startsWith("https://");
	}
}
