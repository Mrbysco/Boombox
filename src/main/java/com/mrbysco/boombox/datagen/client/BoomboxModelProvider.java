package com.mrbysco.boombox.datagen.client;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class BoomboxModelProvider extends ModelProvider {
	public BoomboxModelProvider(PackOutput output) {
		super(output, BoomboxMod.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		Identifier model = ModelLocationUtils.getModelLocation(ModRegistry.BOOMBOX.get());
		blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModRegistry.BOOMBOX.get(), BlockModelGenerators.plainVariant(model)).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
	}
}
