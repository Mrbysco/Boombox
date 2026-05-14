package com.mrbysco.boombox.util;

import com.mrbysco.boombox.BoomboxMod;

/**
 * This class was based on the class from Ad Astra which is licensed under MIT.
 */
public class StationLoader {
	private static RadioBrowserService radioBrowserService;

	public static void init() {
		try {
			radioBrowserService = new RadioBrowserService();
			BoomboxMod.LOGGER.info("RadioBrowser service initialized");
		} catch (Exception e) {
			BoomboxMod.LOGGER.error("Failed to initialize RadioBrowser service", e);
		}
	}

	public static RadioBrowserService getRadioBrowserService() {
		return radioBrowserService;
	}
}
