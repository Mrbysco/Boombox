package com.mrbysco.boombox.client.screen;

import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.config.BoomboxConfig;
import com.mrbysco.boombox.network.server.RequestStationsPayload;
import com.mrbysco.boombox.util.StationInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RadioScreen extends Screen {
	private static final Component title = Component.translatable("boombox.screen.title");
	private final List<StationInfo> stations = new ArrayList<>();
	private final Map<String, String> stationNames = new HashMap<>();

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

		this.addRenderableWidget(new VolumeSlider(6, 6, 100, 20,
				Component.translatable("boombox.screen.volume"), Component.empty(),
				0, 100, BoomboxConfig.CLIENT.volume.getAsInt(), 1, 2, true));
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
