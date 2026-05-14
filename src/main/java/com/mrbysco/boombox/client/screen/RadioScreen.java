package com.mrbysco.boombox.client.screen;

import com.mrbysco.boombox.BoomboxMod;
import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.config.BoomboxConfig;
import com.mrbysco.boombox.network.server.RequestStationsPayload;
import com.mrbysco.boombox.util.FavoriteHelper;
import com.mrbysco.boombox.util.RadioBrowserService;
import com.mrbysco.boombox.util.StationInfo;
import com.mrbysco.boombox.util.StationLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RadioScreen extends Screen {
	private static final Component title = Component.translatable("boombox.screen.title");
	private final List<StationInfo> stations = new ArrayList<>();
	private final Map<String, String> stationNames = new HashMap<>();

	private VolumeSlider volumeSlider;
	private EditBox searchBox;
	private CycleButton<String> countryButton;
	private CycleButton<String> languageButton;
	private CycleButton<String> genreButton;

	@Nullable
	private final BlockPos pos;

	private RadioList list;

	public RadioScreen(@Nullable BlockPos pos) {
		super(title);
		this.pos = pos;
		ClientPacketDistributor.sendToServer(new RequestStationsPayload());
	}

	@Override
	protected void init() {
		int y = this.height - 20 - 6;

		int fullButtonHeight = 34;
		this.list = new RadioList(this, width, fullButtonHeight, y - font.lineHeight - 6, pos);
		this.list.setX(0);
		if (!this.stations.isEmpty()) {
			this.list.update(this.stations, RadioHandler.getPlaying());
		}
		this.addWidget(this.list);

		// Volume slider
		this.volumeSlider = new VolumeSlider(6, 6, 100, 20,
				Component.translatable("boombox.screen.volume"), Component.empty(),
				0, 100, BoomboxConfig.CLIENT.volume.getAsInt(), 1, 2, true);
		this.addRenderableWidget(this.volumeSlider);

		int bottomControlY = this.height - 26;
		int x = 6;

		// Search box
		this.searchBox = new EditBox(this.font, x, bottomControlY, 120, 20,
				Component.translatable("boombox.screen.search"));
		this.searchBox.setHint(Component.translatable("boombox.screen.search.hint"));
		this.addRenderableWidget(this.searchBox);
		x += 125;

		// Country
		List<String> countries = getCommonCountries();
		this.countryButton = CycleButton.builder((value -> Component.literal(capitalizeFirst(value))), "All")
				.withValues(countries)
				.withTooltip((value) -> Tooltip.create(Component.literal(capitalizeFirst(value))))
				.create(x, bottomControlY, 70, 20, Component.translatable("boombox.screen.country"));
		this.addRenderableWidget(this.countryButton);
		x += 75;

		// Language
		List<String> languages = getCommonLanguages();
		this.languageButton = CycleButton.builder((value -> Component.literal(capitalizeFirst(value))), "All")
				.withValues(languages)
				.withTooltip((value) -> Tooltip.create(Component.literal(capitalizeFirst(value))))
				.create(x, bottomControlY, 70, 20, Component.translatable("boombox.screen.language"));
		this.addRenderableWidget(this.languageButton);
		x += 75;

		// Genre
		List<String> genres = getCommonGenres();
		this.genreButton = CycleButton.builder((value -> Component.literal(capitalizeFirst(value))), "All")
				.withValues(genres)
				.withTooltip((value) -> Tooltip.create(Component.literal(capitalizeFirst(value))))
				.create(x, bottomControlY, 70, 20, Component.translatable("boombox.screen.genre"));
		this.addRenderableWidget(this.genreButton);
		x += 75;

		// Search button
		this.addRenderableWidget(Button.builder(
						Component.translatable("boombox.screen.search.button"),
						button -> this.applyFilters())
				.bounds(x, bottomControlY, 40, 20)
				.tooltip(Tooltip.create(Component.translatable("boombox.screen.search.button.tooltip")))
				.build());
		x += 45;

		// Refresh button
		this.addRenderableWidget(Button.builder(
						Component.literal("🔄"),
						button -> this.refreshStations())
				.bounds(x, bottomControlY, 20, 20)
				.tooltip(Tooltip.create(Component.translatable("boombox.screen.refresh.button.tooltip", Component.literal("🔄"))))
				.build());
	}

	private String capitalizeFirst(String text) {
		if (text == null || text.isEmpty() || text.equals("All")) {
			return text;
		}
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	@Override
	public void resize(int width, int height) {
		// Get current button values before resizing
		String oldCountry = countryButton.getValue();
		String oldLanguage = languageButton.getValue();
		String oldGenre = genreButton.getValue();
		String oldSearch = searchBox.getValue();
		super.resize(width, height);
		// Revert to old button values after resizing
		countryButton.setValue(oldCountry);
		languageButton.setValue(oldLanguage);
		genreButton.setValue(oldGenre);
		searchBox.setValue(oldSearch);
	}

	private void applyFilters() {
		RadioBrowserService service = StationLoader.getRadioBrowserService();
		if (service == null) {
			BoomboxMod.LOGGER.warn("RadioBrowser service not available");
			return;
		}

		String country = getCountryCode(countryButton.getValue());
		String language = "All".equals(languageButton.getValue()) ? null : languageButton.getValue();
		String genre = "All".equals(genreButton.getValue()) ? null : genreButton.getValue();
		String search = searchBox.getValue().isEmpty() ? null : searchBox.getValue();

		BoomboxMod.LOGGER.debug("Searching stations with filters - Country: {}, Language: {}, Genre: {}, Search: {}",
				country, language, genre, search);

		List<StationInfo> filtered = service.searchStations(
				BoomboxConfig.CLIENT.maxStations.get(),
				country,
				language,
				genre,
				search
		);

		List<StationInfo> combined = mergeFavorites(filtered);

		if (filtered.isEmpty()) {
			BoomboxMod.LOGGER.warn("No stations found with current filters");
		} else {
			BoomboxMod.LOGGER.debug("Found {} stations", filtered.size());
		}

		this.list.update(combined, RadioHandler.getPlaying());
	}

	private void refreshStations() {
		RadioBrowserService service = StationLoader.getRadioBrowserService();
		if (service == null) {
			ClientPacketDistributor.sendToServer(new RequestStationsPayload());
			return;
		}

		BoomboxMod.LOGGER.debug("Refreshing with top stations");
		List<StationInfo> topStations = service.getTopStations(
				BoomboxConfig.CLIENT.maxStations.get()
		);

		List<StationInfo> combined = mergeFavorites(topStations);

		this.list.update(combined, RadioHandler.getPlaying());

		//Reset search
		this.searchBox.setValue("");
	}

	private List<StationInfo> mergeFavorites(List<StationInfo> apiResults) {
		RadioBrowserService service = StationLoader.getRadioBrowserService();
		return FavoriteHelper.mergeFavoritesWithResults(apiResults, service);
	}

	private List<String> getCommonCountries() {
		return Arrays.asList(
				"All",
				"US - United States",
				"GB - United Kingdom",
				"CA - Canada",
				"DE - Germany",
				"FR - France",
				"ES - Spain",
				"IT - Italy",
				"AU - Australia",
				"NL - Netherlands",
				"SE - Sweden",
				"NO - Norway",
				"JP - Japan",
				"BR - Brazil",
				"MX - Mexico"
		);
	}

	private List<String> getCommonLanguages() {
		return Arrays.asList(
				"All",
				"english",
				"spanish",
				"french",
				"german",
				"italian",
				"portuguese",
				"dutch",
				"japanese",
				"korean",
				"chinese"
		);
	}

	private List<String> getCommonGenres() {
		return Arrays.asList(
				"All",
				"rock",
				"pop",
				"jazz",
				"classical",
				"electronic",
				"dance",
				"hip-hop",
				"countryCode",
				"metal",
				"indie",
				"folk",
				"blues",
				"reggae",
				"latin",
				"ambient",
				"techno",
				"house",
				"80s",
				"90s",
				"news",
				"talk",
				"sports"
		);
	}

	private String getCountryCode(String displayValue) {
		if (displayValue == null || displayValue.equals("All")) {
			return null;
		}
		if (displayValue.contains(" - ")) {
			return displayValue.substring(0, displayValue.indexOf(" - "));
		}
		return displayValue;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

		this.list.extractRenderState(graphics, mouseX, mouseY, partialTicks);

		graphics.centeredText(font, title, this.width / 2 + 6, 6, -1);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public static void handleStationUpdates(List<StationInfo> stations) {
		if (Minecraft.getInstance().screen instanceof RadioScreen screen) {
			screen.list.update(stations, RadioHandler.getPlaying());
			screen.stations.clear();
			screen.stationNames.clear();
			for (StationInfo station : stations) {
				screen.stationNames.put(station.url().toLowerCase(Locale.ROOT), station.name());
				screen.stations.add(station);
			}
		}
	}
}
