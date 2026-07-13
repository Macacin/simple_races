package net.simpleraces.init;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.simpleraces.configuration.SimpleRPGRacesConfiguration;

public final class SimpleracesModConfigs {
	private static final String COMMON_CONFIG_FILE = "simpleraces-common.toml";

	private SimpleracesModConfigs() {
	}

	public static void register(ModContainer modContainer) {
		modContainer.registerConfig(
				ModConfig.Type.COMMON,
				SimpleRPGRacesConfiguration.SPEC,
				COMMON_CONFIG_FILE
		);
	}
}
