package fr.panncake.harvestengine;

import org.bukkit.plugin.java.JavaPlugin;

public class PannHarvestEngine extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Enabling plugin...");

        getLogger().info("Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin...");

        getLogger().info("Plugin has been disabled!");
    }
}
