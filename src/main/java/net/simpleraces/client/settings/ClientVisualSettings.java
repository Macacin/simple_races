package net.simpleraces.client.settings;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ClientVisualSettings {
	private static final String KEY_AMBIENT_RACE_EFFECTS = "ambientRaceEffects";
	private static final Path SETTINGS_PATH = FMLPaths.CONFIGDIR.get().resolve("simpleraces-client.properties");
	private static boolean loaded;
	private static boolean ambientRaceEffectsEnabled = true;

	private ClientVisualSettings() {
	}

	public static boolean isAmbientRaceEffectsEnabled() {
		ensureLoaded();
		return ambientRaceEffectsEnabled;
	}

	public static void setAmbientRaceEffectsEnabled(boolean enabled) {
		ensureLoaded();
		if (ambientRaceEffectsEnabled == enabled) {
			return;
		}
		ambientRaceEffectsEnabled = enabled;
		save();
	}

	public static void ensureLoaded() {
		if (loaded) {
			return;
		}
		loaded = true;
		if (!Files.exists(SETTINGS_PATH)) {
			save();
			return;
		}
		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(SETTINGS_PATH)) {
			properties.load(input);
			ambientRaceEffectsEnabled = Boolean.parseBoolean(properties.getProperty(KEY_AMBIENT_RACE_EFFECTS, "true"));
		} catch (IOException ignored) {
			ambientRaceEffectsEnabled = true;
		}
	}

	private static void save() {
		Properties properties = new Properties();
		properties.setProperty(KEY_AMBIENT_RACE_EFFECTS, Boolean.toString(ambientRaceEffectsEnabled));
		try {
			Files.createDirectories(SETTINGS_PATH.getParent());
			try (OutputStream output = Files.newOutputStream(SETTINGS_PATH)) {
				properties.store(output, "Simple Races client settings");
			}
		} catch (IOException ignored) {
		}
	}
}





