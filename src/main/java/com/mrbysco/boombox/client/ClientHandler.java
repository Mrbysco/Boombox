package com.mrbysco.boombox.client;

import com.mrbysco.boombox.client.audio.RadioHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {
	@SubscribeEvent
	public static void onPlayerLeave(ClientPlayerNetworkEvent.LoggingOut event) {
		RadioHandler.stop();
	}
}
