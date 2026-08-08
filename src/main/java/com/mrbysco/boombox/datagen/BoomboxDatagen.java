package com.mrbysco.boombox.datagen;

import com.mrbysco.boombox.datagen.client.BoomboxItemModelProvider;
import com.mrbysco.boombox.datagen.client.BoomboxLanguageProvider;
import com.mrbysco.boombox.datagen.client.BoomboxBlockStateProvider;
import com.mrbysco.boombox.datagen.server.BoomboxLootProvider;
import com.mrbysco.boombox.datagen.server.BoomboxRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class BoomboxDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new BoomboxLanguageProvider(packOutput));
		generator.addProvider(true, new BoomboxBlockStateProvider(packOutput, event.getExistingFileHelper()));
		generator.addProvider(true, new BoomboxItemModelProvider(packOutput, event.getExistingFileHelper()));

		generator.addProvider(true, new BoomboxLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new BoomboxRecipeProvider(packOutput, lookupProvider));
	}
}
