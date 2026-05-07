package com.mrbysco.boombox.network.client;

import com.mrbysco.boombox.BoomboxMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

public record PlayStationPayload(String url, Optional<BlockPos> pos) implements CustomPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayStationPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			o -> o.url,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
			o -> o.pos,
			PlayStationPayload::new
	);

	public static final Type<PlayStationPayload> ID = new Type<>(BoomboxMod.modLoc("play_station"));

	public PlayStationPayload(String url, BlockPos pos) {
		this(url, Optional.ofNullable(pos));
	}

	public PlayStationPayload(String url) {
		this(url, Optional.empty());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
