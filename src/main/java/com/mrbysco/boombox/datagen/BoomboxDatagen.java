package com.mrbysco.boombox.datagen;

import com.mrbysco.boombox.datagen.client.BoomboxLanguageProvider;
import com.mrbysco.boombox.datagen.client.BoomboxModelProvider;
import com.mrbysco.boombox.datagen.server.BoomboxBlockLoot;
import com.mrbysco.boombox.datagen.server.BoomboxRecipeProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber
public class BoomboxDatagen {

	@SubscribeEvent
	static void onGatherRegistries(GatherDataRegistryEntriesEvent event) {
		event.lootTable(new LootTableProvider.SubProviderEntry(BoomboxBlockLoot::new, LootContextParamSets.BLOCK));
		event.recipe(BoomboxRecipeProvider::new);
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(BoomboxLanguageProvider::new);
		event.createProvider(BoomboxModelProvider::new);
	}
}
