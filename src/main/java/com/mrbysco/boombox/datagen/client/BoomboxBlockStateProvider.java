package com.mrbysco.boombox.datagen.client;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.block.BoomboxBlock;
import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BoomboxBlockStateProvider extends BlockStateProvider {
	public BoomboxBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, BoomboxMod.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		makeBoombox(ModRegistry.BOOMBOX);
	}

	private void makeBoombox(DeferredBlock<BoomboxBlock> deferredBlock) {
		ModelFile model = models().getExistingFile(modLoc("block/boombox"));
		getVariantBuilder(deferredBlock.get())
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.modelForState().modelFile(model).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
				.modelForState().modelFile(model).rotationY(90).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
				.modelForState().modelFile(model).rotationY(180).addModel()
				.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
				.modelForState().modelFile(model).rotationY(270).addModel();
	}
}
