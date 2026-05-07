package com.mrbysco.boombox.network;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.network.client.PlayStationPayload;
import com.mrbysco.boombox.network.client.SendStationsPayload;
import com.mrbysco.boombox.network.handler.ClientPayloadHandler;
import com.mrbysco.boombox.network.handler.ServerPayloadHandler;
import com.mrbysco.boombox.network.server.RequestStationsPayload;
import com.mrbysco.boombox.network.server.SetStationPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class BoomboxNetworking {
	@SubscribeEvent
	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(BoomboxMod.MOD_ID);

		registrar.playToClient(PlayStationPayload.ID, PlayStationPayload.STREAM_CODEC, ClientPayloadHandler.getInstance()::handlePlayStation);
		registrar.playToClient(SendStationsPayload.ID, SendStationsPayload.STREAM_CODEC, ClientPayloadHandler.getInstance()::handleSendStations);

		registrar.playToServer(RequestStationsPayload.ID, RequestStationsPayload.STREAM_CODEC, ServerPayloadHandler.getInstance()::handleRequestStations);
		registrar.playToServer(SetStationPayload.ID, SetStationPayload.STREAM_CODEC, ServerPayloadHandler.getInstance()::handleSetStation);

	}
}
