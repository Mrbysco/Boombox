package com.mrbysco.boombox;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.mrbysco.boombox.config.BoomboxConfig;
import com.mrbysco.boombox.registry.ModRegistry;
import com.mrbysco.boombox.util.StationLoader;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(BoomboxMod.MOD_ID)
public class BoomboxMod {
	public static final String MOD_ID = "boombox";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Gson GSON = new Gson();

	public BoomboxMod(IEventBus eventBus, Dist dist, ModContainer container) {
		StationLoader.init();

		ModRegistry.BLOCKS.register(eventBus);
		ModRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		ModRegistry.ITEMS.register(eventBus);
		ModRegistry.CREATIVE_MODE_TABS.register(eventBus);

		if (dist.isClient()) {
			container.registerConfig(ModConfig.Type.CLIENT, BoomboxConfig.clientSpec);
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
