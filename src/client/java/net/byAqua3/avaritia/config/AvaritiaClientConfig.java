package net.byAqua3.avaritia.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.Properties;
import net.byAqua3.avaritia.Avaritia;
import net.fabricmc.loader.api.FabricLoader;

public final class AvaritiaClientConfig {

	public enum Option {
		ITEM_COSMIC("itemCosmic"),
		ARMOR_COSMIC("armorCosmic"),
		ARMOR_RAINBOW("armorRainbow"),
		ITEM_HALO("itemHalo"),
		ITEM_PULSE("itemPulse");

		private final String key;

		Option(String key) {
			this.key = key;
		}

		public String key() {
			return key;
		}

		public String translationKey() {
			return "config.avaritia." + key;
		}
	}

	public static final AvaritiaClientConfig INSTANCE = new AvaritiaClientConfig(
			FabricLoader.getInstance().getConfigDir().resolve("avaritia-client.properties"));

	private final Path path;
	private final EnumMap<Option, Boolean> values = new EnumMap<>(Option.class);

	public AvaritiaClientConfig(Path path) {
		this.path = path;
		reset();
	}

	public boolean enabled(Option option) {
		return values.get(option);
	}

	public void set(Option option, boolean enabled) {
		values.put(option, enabled);
	}

	public void reset() {
		for (Option option : Option.values()) values.put(option, true);
	}

	public void lowEffects() {
		for (Option option : Option.values()) values.put(option, false);
	}

	public void load() {
		reset();
		if (!Files.exists(path)) {
			save();
			return;
		}
		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(path)) {
			properties.load(input);
		} catch (IOException | IllegalArgumentException exception) {
			Avaritia.LOGGER.warn("Could not read client configuration {}", path, exception);
			return;
		}
		for (Option option : Option.values()) {
			String value = properties.getProperty(option.key());
			if (value == null) continue;
			if (value.trim().equalsIgnoreCase("true") || value.trim().equalsIgnoreCase("false")) {
				set(option, Boolean.parseBoolean(value.trim()));
			} else {
				Avaritia.LOGGER.warn("Invalid client setting {}={}, using true", option.key(), value);
			}
		}
	}

	public boolean save() {
		Path temporary = null;
		try {
			Path target = path.toAbsolutePath();
			Files.createDirectories(target.getParent());
			temporary = Files.createTempFile(target.getParent(), "avaritia-client-", ".tmp");
			Properties properties = new Properties();
			for (Option option : Option.values()) properties.setProperty(option.key(), Boolean.toString(enabled(option)));
			try (OutputStream output = Files.newOutputStream(temporary)) {
				properties.store(output, "Avaritia client visuals - no gameplay or server settings");
			}
			try {
				Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			} catch (AtomicMoveNotSupportedException exception) {
				Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
			}
			return true;
		} catch (IOException exception) {
			Avaritia.LOGGER.warn("Could not save client configuration {}", path, exception);
			return false;
		} finally {
			if (temporary != null) {
				try {
					Files.deleteIfExists(temporary);
				} catch (IOException exception) {
					Avaritia.LOGGER.warn("Could not remove temporary client configuration {}", temporary, exception);
				}
			}
		}
	}
}
