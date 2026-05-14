package com.mrbysco.boombox.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BoomboxConfig {

	public static class Client {
		public final ModConfigSpec.IntValue volume;
		public final ModConfigSpec.IntValue maxStations;
		public final ModConfigSpec.ConfigValue<List<? extends String>> favorites;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("client");

			volume = builder
					.comment("The volume of the boombox, from 0 to 100")
					.defineInRange("volume", 50, 0, 100);

			maxStations = builder
					.comment("The maximum number of radio stations to display in the list")
					.defineInRange("maxStations", 100, 1, 500);

			favorites = builder
					.comment("List of favorite radio stations")
					.defineListAllowEmpty(List.of("favorites"), List::of, () -> "", o -> (o instanceof String));

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

}
