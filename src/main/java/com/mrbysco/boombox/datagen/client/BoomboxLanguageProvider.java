package com.mrbysco.boombox.datagen.client;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.registry.ModRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class BoomboxLanguageProvider extends LanguageProvider {
	public BoomboxLanguageProvider(PackOutput output) {
		super(output, BoomboxMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.addBlock(ModRegistry.BOOMBOX, "Boombox");

		this.add("text.boombox.radio.none", "No Station");
		this.add("boombox.screen.volume", "Volume: ");
		this.add("boombox.screen.title", "Radio Channel Selector");

		this.add("itemGroup.boombox", "Boombox");
	}
}
