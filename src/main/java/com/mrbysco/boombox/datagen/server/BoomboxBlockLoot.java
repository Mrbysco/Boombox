package com.mrbysco.boombox.datagen.server;

import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BoomboxBlockLoot extends BlockLootSubProvider {

	public BoomboxBlockLoot(LootTableSubProvider.Context context) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
	}

	@Override
	protected void generate() {
		this.dropSelf(ModRegistry.BOOMBOX.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ModRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.value())::iterator;
	}
}
