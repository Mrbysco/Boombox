package com.mrbysco.boombox.network.server;

import com.mrbysco.boombox.BoomboxMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

public record SetStationPayload(String url, Optional<BlockPos> pos) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, SetStationPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			o -> o.url,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
			o -> o.pos,
			SetStationPayload::new
	);

	public static final Type<SetStationPayload> ID = new Type<>(BoomboxMod.modLoc("set_station"));

	public SetStationPayload(String url, BlockPos pos) {
		this(url, Optional.ofNullable(pos));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
