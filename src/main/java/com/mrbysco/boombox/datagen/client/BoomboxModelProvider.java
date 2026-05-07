package com.mrbysco.boombox.datagen.client;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class BoomboxModelProvider extends ModelProvider {
	public BoomboxModelProvider(PackOutput output) {
		super(output, BoomboxMod.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		blockModels.createNonTemplateModelBlock(ModRegistry.BOOMBOX.get());
	}
}
