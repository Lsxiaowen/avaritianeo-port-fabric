package net.byAqua3.avaritia.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.byAqua3.avaritia.Avaritia;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric has no config API; this mirrors the NeoForge ModConfigSpec defaults
 * with a small properties file so existing values keep the same keys and limits.
 */
public class AvaritiaConfigs {

	private static final String FILE_NAME = "avaritia-server.properties";

	private static final Properties VALUES = new Properties();
	private static boolean loaded;

	public static int dropChange = 100;
	public static boolean nightVision = true;
	public static double flySpeedValue = 1.0D;
	public static boolean clearBadEffect = true;
	public static boolean clearFire = true;
	public static boolean clearFrozen = true;
	public static boolean highJump = true;
	public static boolean stepHeight = true;
	public static boolean speed = true;
	public static double speedValue = 1.0D;
	public static int productionTicks = 7111;
	public static int maxStackSize = Integer.MAX_VALUE;

	public static void registerConfigs() {
		load();
	}

	private static Path configPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}

	private static void load() {
		if (loaded) {
			return;
		}
		loaded = true;

		Path path = configPath();
		if (Files.isRegularFile(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				VALUES.load(in);
			} catch (IOException e) {
				Avaritia.LOGGER.warn("Could not read {}", FILE_NAME, e);
			}
		}

		dropChange = clampInt("skullfireSword.dropChange", dropChange, 0, 100);
		nightVision = getBool("infinityArmor.nightVision", nightVision);
		flySpeedValue = clampDouble("infinityArmor.flySpeedValue", flySpeedValue, 0.1D, 2.0D);
		clearBadEffect = getBool("infinityArmor.clearBadEffect", clearBadEffect);
		clearFire = getBool("infinityArmor.clearFire", clearFire);
		clearFrozen = getBool("infinityArmor.clearFrozen", clearFrozen);
		highJump = getBool("infinityArmor.highJump", highJump);
		stepHeight = getBool("infinityArmor.stepHeight", stepHeight);
		speed = getBool("infinityArmor.speed", speed);
		speedValue = clampDouble("infinityArmor.speedValue", speedValue, 0.1D, 2.0D);
		productionTicks = clampInt("neutronCollector.productionTicks", productionTicks, 1, Integer.MAX_VALUE);
		maxStackSize = clampInt("infinityChest.maxStackSize", maxStackSize, 64, Integer.MAX_VALUE);

		writeDefaults();
	}

	private static void writeDefaults() {
		Path path = configPath();
		try {
			Files.createDirectories(path.getParent());
			try (OutputStream out = Files.newOutputStream(path)) {
				VALUES.store(out, "Avaritia server configuration");
			}
		} catch (IOException e) {
			Avaritia.LOGGER.warn("Could not write {}", FILE_NAME, e);
		}
	}

	private static int getInt(String key, int fallback) {
		String value = VALUES.getProperty(key);
		if (value == null) {
			VALUES.setProperty(key, Integer.toString(fallback));
			return fallback;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	private static boolean getBool(String key, boolean fallback) {
		String value = VALUES.getProperty(key);
		if (value == null) {
			VALUES.setProperty(key, Boolean.toString(fallback));
			return fallback;
		}
		return Boolean.parseBoolean(value.trim());
	}

	private static double getDouble(String key, double fallback) {
		String value = VALUES.getProperty(key);
		if (value == null) {
			VALUES.setProperty(key, Double.toString(fallback));
			return fallback;
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	private static int clampInt(String key, int fallback, int min, int max) {
		int value = getInt(key, fallback);
		return Math.max(min, Math.min(max, value));
	}

	private static double clampDouble(String key, double fallback, double min, double max) {
		double value = getDouble(key, fallback);
		return Math.max(min, Math.min(max, value));
	}

	public static void registerConfigScreen() {
		// No Fabric config UI; the properties file is edited by hand.
	}
}
