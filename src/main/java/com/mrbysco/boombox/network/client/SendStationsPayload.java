package com.mrbysco.boombox.network.client;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.util.StationInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record SendStationsPayload(List<StationInfo> stations) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, SendStationsPayload> STREAM_CODEC = StreamCodec.composite(
			StationInfo.STREAM_CODEC.apply(ByteBufCodecs.list()),
			o -> o.stations,
			SendStationsPayload::new
	);

	public static final Type<SendStationsPayload> ID = new Type<>(BoomboxMod.modLoc("send_stations"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
