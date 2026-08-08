package com.mrbysco.boombox.datagen.client;

import com.mrbysco.boombox.BoomboxMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BoomboxItemModelProvider extends ItemModelProvider {
	public BoomboxItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, BoomboxMod.MOD_ID, helper);
	}

	@Override
	protected void registerModels() {
		withExistingParent("boombox", modLoc("block/boombox"));
	}
}
