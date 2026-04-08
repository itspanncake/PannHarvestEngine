package dev.panncake.harvestengine;

import dev.panncake.harvestengine.commands.CommandManager;
import dev.panncake.harvestengine.config.ConfigManager;
import dev.panncake.harvestengine.core.HarvestManager;
import dev.panncake.harvestengine.listeners.PlayerInteractListener;
import dev.panncake.harvestengine.listeners.PlayerStateListener;
import org.bukkit.plugin.java.JavaPlugin;

public class PannHarvestEngine extends JavaPlugin {
    private static PannHarvestEngine instance;
    private ConfigManager configManager;
    private HarvestManager harvestManager;

    @Override
    public void onEnable() {
        getLogger().info("Enabling plugin...");

        instance = this;

        this.configManager = new ConfigManager(this);
        this.configManager.reloadAll();

        this.harvestManager = new HarvestManager();

        CommandManager.register();

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerStateListener(), this);

        getServer().getScheduler().runTaskTimer(this, () ->
                harvestManager.tickCleanup(), 20L, 20L);

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
    public HarvestManager getHarvestManager() { return harvestManager; }
}
