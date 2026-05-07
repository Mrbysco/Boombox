package com.mrbysco.boombox.client.screen;

import com.mrbysco.boombox.config.BoomboxConfig;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class VolumeSlider extends ExtendedSlider {
	public VolumeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
	}

	public VolumeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
		super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		super.onRelease(event);
		int newVolume = getValueInt();
		int volume = BoomboxConfig.CLIENT.volume.getAsInt();
		if (volume != newVolume) {
			BoomboxConfig.CLIENT.volume.set(newVolume);
			BoomboxConfig.CLIENT.volume.save();
		}
	}
}
