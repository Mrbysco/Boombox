package com.mrbysco.boombox.block.blockentity;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.network.client.PlayStationPayload;
import com.mrbysco.boombox.registry.ModRegistry;
import com.mrbysco.boombox.util.RadioHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class BoomboxBlockEntity extends BlockEntity implements RadioHolder {

	private String station = "";

	public BoomboxBlockEntity(BlockPos pos, BlockState state) {
		super(ModRegistry.BOOMBOX_BE.get(), pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);

		Optional<String> station = input.getString("Station");
		if (station.isPresent()) {
			this.station = station.get();

			if (this.level == null) return;
			if (!this.level.isClientSide()) return;
			if (this.station.isBlank()) return;
			RadioHandler.play(this.station, this.level.getRandom(), this.worldPosition);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putString("Station", this.station);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(BoomboxMod.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
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
