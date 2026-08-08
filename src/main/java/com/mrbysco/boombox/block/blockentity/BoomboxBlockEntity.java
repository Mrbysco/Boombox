package com.mrbysco.boombox.block.blockentity;

import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.network.client.PlayStationPayload;
import com.mrbysco.boombox.registry.ModRegistry;
import com.mrbysco.boombox.util.RadioHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class BoomboxBlockEntity extends BlockEntity implements RadioHolder {

	private String station = "";

	public BoomboxBlockEntity(BlockPos pos, BlockState state) {
		super(ModRegistry.BOOMBOX_BE.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		String station = tag.getString("Station");
		if (!station.isEmpty()) {
			this.station = station;

			if (this.level == null) return;
			if (!this.level.isClientSide()) return;
			if (this.station.isBlank()) return;
			RadioHandler.play(this.station, this.level.getRandom(), this.worldPosition);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("Station", this.station);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
		loadAdditional(pkt.getTag(), provider);

		BlockState state = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), state, state, 3);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt, provider);
		return nbt;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt, level != null ? level.registryAccess() : VanillaRegistries.createLookup());
		return nbt;
	}

	@Override
	public @NotNull String getRadioUrl() {
		return this.station;
	}

	@Override
	public void setRadioUrl(@NotNull String url) {
		this.station = url;
		if (this.level == null) return;
		if (this.level.isClientSide()) return;
		PacketDistributor.sendToPlayersNear((ServerLevel) this.level, null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), RadioHolder.RANGE, new PlayStationPayload(url, this.worldPosition));
	}
}
