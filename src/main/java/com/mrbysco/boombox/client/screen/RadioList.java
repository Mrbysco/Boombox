package com.mrbysco.boombox.client.screen;

import com.mrbysco.boombox.client.audio.RadioHandler;
import com.mrbysco.boombox.network.server.SetStationPayload;
import com.mrbysco.boombox.util.FavoriteHelper;
import com.mrbysco.boombox.util.StationInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class RadioList extends ObjectSelectionList<RadioList.RadioEntry> {
	private List<StationInfo> stations = new ArrayList<>();
	private String playing = null;
	private final BlockPos pos;
	private final int listWidth;

	public RadioList(RadioScreen parent, int listWidth, int top, int bottom, @Nullable BlockPos pos) {
		super(parent.getMinecraft(), listWidth, bottom - top, top, parent.getFont().lineHeight * 2 + 8);
		this.pos = pos;
		this.listWidth = listWidth;
	}

	@Override
	protected int scrollBarX() {
		return this.listWidth - 6;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void update(List<StationInfo> stations, String url) {
		this.stations = stations;
		this.playing = url;
		update();
	}

	private void update() {
		RadioEntry selected = null;
		List<RadioEntry> entries = new ArrayList<>();
		Set<String> favoriteKeys = FavoriteHelper.getFavoriteKeys();
		List<StationInfo> favorites = new ArrayList<>();
		List<StationInfo> others = new ArrayList<>();

		for (StationInfo station : this.stations) {
			String key = FavoriteHelper.getFavoriteKey(station);
			if (favoriteKeys.contains(key)) {
				favorites.add(station);
			} else {
				others.add(station);
			}
		}
		favorites.sort(Comparator.comparing(StationInfo::title));
		others.sort(Comparator.comparing(StationInfo::title));

		entries.add(new RadioEntry(null));

		for (StationInfo favorite : favorites) {
			RadioEntry entry = new RadioEntry(favorite);
			if (favorite.url().equals(this.playing)) selected = entry;
			entry.favorite = true;
			entries.add(entry);
		}

		for (StationInfo other : others) {
			RadioEntry entry = new RadioEntry(other);
			if (other.url().equals(this.playing)) selected = entry;
			entries.add(entry);
		}

		updateEntries(entries);
		if (selected != null) {
			setSelected(selected);
		}
	}

	private void updateEntries(List<RadioEntry> entries) {
		clearEntries();
		entries.forEach(this::addEntry);
	}

	@Override
	public void setSelected(@Nullable RadioList.RadioEntry selected) {
		super.setSelected(selected);
		if (selected != null) {
			StationInfo info = selected.info;
			if (info == null) {
				ClientPacketDistributor.sendToServer(new SetStationPayload("", pos));
			} else if (!Objects.equals(RadioHandler.getPlaying(), info.url())) {
				ClientPacketDistributor.sendToServer(new SetStationPayload(info.url(), pos));
			}
		}
	}

	public class RadioEntry extends Entry<RadioEntry> {
		@Nullable
		private final StationInfo info;
		private boolean favorite = false;

		public RadioEntry(@Nullable StationInfo info) {
			this.info = info;
		}

		@Override
		public Component getNarration() {
			return getName();
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			int left = getContentX();
			int top = getContentY();

			int textStart = left + 3;
			MutableComponent text = getName().copy();
			if (this.info != null) {
				text.append(" " + (favorite ? "★" : "☆"));
			}

			graphics.text(Minecraft.getInstance().font, text, textStart, top + 3, 0xFFFFFFff);

			if (hovered) {
				if (info != null) {
					graphics.setTooltipForNextFrame(getTooltip(), mouseX, mouseY);
				}
			}
		}

		private List<FormattedCharSequence> getTooltip() {
			List<FormattedCharSequence> tooltip = new ArrayList<>();
			tooltip.add(Component.literal(info.name()).withStyle(ChatFormatting.GRAY).getVisualOrderText());

			if (!info.countryCode().isEmpty()) {
				tooltip.add(Component.translatable("text.boombox.radio.country_code").append(Component.literal(info.countryCode())).getVisualOrderText());
			}
			return tooltip;
		}


		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
			if (Minecraft.getInstance().hasShiftDown() && info != null) {
				FavoriteHelper.toggleFavorite(info);
				update();
				return true;
			}

			return super.mouseClicked(event, doubleClick);
		}

		private Component getName() {
			return info == null ? Component.translatable("text.boombox.radio.none") : Component.literal(info.title());
		}

		@Override
		public void setFocused(boolean focused) {

		}

		@Override
		public boolean isFocused() {
			return false;
		}

		@Override
		public String toString() {
			return this.info + " Favorite " + favorite;
		}
	}
}