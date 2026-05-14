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
		this.add("text.boombox.radio.country_code", "Country Code: ");
		this.add("boombox.screen.country", "Country");
		this.add("boombox.screen.countryCode", "Country");
		this.add("boombox.screen.genre", "Genre");
		this.add("boombox.screen.language", "Language");
		this.add("boombox.screen.refresh.button.tooltip", "Refresh stations");
		this.add("boombox.screen.search", "Search");
		this.add("boombox.screen.search.button", "Search");
		this.add("boombox.screen.search.button.tooltip", "Search for matching stations");
		this.add("boombox.screen.search.hint", "Station name...");
		this.add("boombox.screen.title", "Radio Channel Selector");
		this.add("boombox.screen.volume", "Volume: ");

		this.add("itemGroup.boombox", "Boombox");

		this.add("boombox.networking.play_station.failed", "Failed to play station: %s");
		this.add("boombox.networking.send_stations.failed", "Failed to update stations: %s");
		this.add("boombox.networking.request_stations.failed", "Failed to request stations: %s");
		this.add("boombox.networking.set_station.failed", "Failed to set station: %s");
	}
}
