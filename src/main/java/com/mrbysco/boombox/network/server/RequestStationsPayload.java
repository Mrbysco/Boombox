package com.mrbysco.boombox.network.server;

import com.mrbysco.boombox.BoomboxMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestStationsPayload() implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestStationsPayload> STREAM_CODEC = CustomPacketPayload.codec(
			RequestStationsPayload::write,
			RequestStationsPayload::new);

	public static final Type<RequestStationsPayload> ID = new Type<>(BoomboxMod.modLoc("request_stations"));

	public RequestStationsPayload(final FriendlyByteBuf packetBuffer) {
		this();
	}

	public void write(FriendlyByteBuf buf) {

	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
