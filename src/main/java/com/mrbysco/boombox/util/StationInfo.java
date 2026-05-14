package com.mrbysco.boombox.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public record StationInfo(
		String url,
		String title,
		String name,
		String countryCode,
		Optional<UUID> uuid,
		StationLocation location
) {

	public static final Codec<StationInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("url").forGetter(StationInfo::url),
			Codec.STRING.fieldOf("title").orElse("0.00 N/A").forGetter(StationInfo::title),
			Codec.STRING.fieldOf("name").orElse("Unknown Station").forGetter(StationInfo::name),
			Codec.STRING.fieldOf("countryCode").orElse("Unknown Country Code").forGetter(StationInfo::name),
			UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(StationInfo::uuid),
			StationLocation.CODEC.fieldOf("location").orElse(StationLocation.UNKNOWN).forGetter(StationInfo::location)
	).apply(instance, StationInfo::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StationInfo> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			o -> o.url,
			ByteBufCodecs.STRING_UTF8,
			o -> o.title,
			ByteBufCodecs.STRING_UTF8,
			o -> o.name,
			ByteBufCodecs.STRING_UTF8,
			o -> o.countryCode,
			ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
			o -> o.uuid,
			StationLocation.STREAM_CODEC,
			o -> o.location,
			StationInfo::new
	);
}