package com.mrbysco.boombox.network.handler;

import com.mrbysco.boombox.network.client.SendStationsPayload;
import com.mrbysco.boombox.network.server.RequestStationsPayload;
import com.mrbysco.boombox.network.server.SetStationPayload;
import com.mrbysco.boombox.util.RadioHolder;
import com.mrbysco.boombox.util.StationLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleRequestStations(final RequestStationsPayload data, final IPayloadContext context) {
		context.enqueueWork(() -> {
					//Execute craft if button is pressed
					if (context.player() != null) {
						Player player = context.player();
						if (player instanceof ServerPlayer serverPlayer) {
							PacketDistributor.sendToPlayer(serverPlayer, new SendStationsPayload(StationLoader.stations()));
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("boombox.networking.request_stations.failed", e.getMessage()));
					return null;
				});
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
		if (StationLoader.hasStation(url) || url.isBlank()) {
			holder.setRadioUrl(url);
		}
	}
}
