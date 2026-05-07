package com.mrbysco.boombox.util;

import org.jetbrains.annotations.NotNull;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public interface RadioHolder {
	int RANGE = 3072;
	int RANGE_DROPOFF = 1024;

	@NotNull String getRadioUrl();

	void setRadioUrl(@NotNull String url);
}
