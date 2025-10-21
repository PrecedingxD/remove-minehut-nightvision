package me.preceding.nightvision.manager;

import me.preceding.nightvision.RemoveNightvisionClient;
import me.preceding.nightvision.config.NightvisionConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

public class ConfigManager {

    private static final File CONFIG_FILE = new File(
            new File(FabricLoader.getInstance().getConfigDir().toFile(), "minehut-lobby-nightvision"),
            "config.json"
    );

    private NightvisionConfig config;

    public ConfigManager() {
        reloadConfig();
    }

    public void saveConfig() {
        if (config == null) {
            this.config = new NightvisionConfig();
        }

        try (final FileWriter writer = new FileWriter(CONFIG_FILE)) {
            RemoveNightvisionClient.GSON.toJson(config, writer);
        } catch (final Exception e) {
            RemoveNightvisionClient.LOGGER.log(Level.SEVERE, "Failed to save config", e);
        }
    }

    public void reloadConfig() {
        if (!CONFIG_FILE.getParentFile().exists()) {
            CONFIG_FILE.getParentFile().mkdirs();
        }

        if (!CONFIG_FILE.exists()) {
            saveConfig();
        }

        try (final FileReader reader = new FileReader(CONFIG_FILE)) {
            this.config = RemoveNightvisionClient.GSON.fromJson(reader, NightvisionConfig.class);
        } catch (final Exception e) {
            RemoveNightvisionClient.LOGGER.log(Level.SEVERE, "Failed to reload config", e);
        }
    }

    public NightvisionConfig getConfig() {
        return config;
    }

}
