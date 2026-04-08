package dev.panncake.harvestengine;

import dev.panncake.harvestengine.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PannHarvestEngine extends JavaPlugin {
    private static PannHarvestEngine instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling plugin...");

        instance = this;

        this.configManager = new ConfigManager(this);
        this.configManager.reloadAll();

        getLogger().info("Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");

        if (harvestManager != null) {
            harvestManager.stopAllSessions();
        }

        getLogger().info("Plugin has been disabled!");
    }

    /* GETTERS */
    public static PannHarvestEngine get() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
}
