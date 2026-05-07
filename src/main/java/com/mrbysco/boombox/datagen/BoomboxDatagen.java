package com.mrbysco.boombox.datagen;

import com.mrbysco.boombox.datagen.client.BoomboxLanguageProvider;
import com.mrbysco.boombox.datagen.client.BoomboxModelProvider;
import com.mrbysco.boombox.datagen.server.BoomboxLootProvider;
import com.mrbysco.boombox.datagen.server.BoomboxRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class BoomboxDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new BoomboxLanguageProvider(packOutput));
		generator.addProvider(true, new BoomboxModelProvider(packOutput));

		generator.addProvider(true, new BoomboxLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new BoomboxRecipeProvider.Runner(packOutput, lookupProvider));


	}
}
