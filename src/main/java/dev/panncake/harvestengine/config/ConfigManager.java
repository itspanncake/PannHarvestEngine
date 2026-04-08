package dev.panncake.harvestengine.config;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.models.LootEntry;
import dev.panncake.harvestengine.models.ResourceBlock;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    private final PannHarvestEngine plugin;
    private final Map<String, ResourceBlock> resources = new HashMap<>();
    private final Map<String, Double> toolPowers = new HashMap<>();
    private double hitCooldown;

    private CommentedConfigurationNode settingsNode;
    private CommentedConfigurationNode langNode;

    public ConfigManager(PannHarvestEngine plugin) {
        this.plugin = plugin;
    }

    public void reloadAll() {
        resources.clear();
        toolPowers.clear();

        plugin.getLogger().info("Start PannHarvestEngine Configuration Reload");
        loadTools();
        loadSettings();
        loadLang();
        loadResources();
        plugin.getLogger().info("Configuration Reload Complete (" + resources.size() + " resources loaded)");
    }

    private void loadTools() {
        File file = new File(plugin.getDataFolder(), "tools.yml");
        if (!file.exists()) plugin.saveResource("tools.yml", false);

        CommentedConfigurationNode node = loadFile(file.toPath());
        node.node("tools").childrenMap().forEach((k, v) -> {
            double power = v.getDouble(1.0);
            toolPowers.put(k.toString(), power);
            plugin.getLogger().info("Tools loaded: " + k + " (Power: " + power + ")");
        });
    }

    private void loadSettings() {
        File file = new File(plugin.getDataFolder(), "settings.yml");
        if (!file.exists()) plugin.saveResource("settings.yml", false);

        settingsNode = loadFile(file.toPath());
        this.hitCooldown = settingsNode.node("settings", "hit-cooldown").getDouble(0.5);
    }

    private void loadLang() {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) plugin.saveResource("lang.yml", false);
        langNode = loadFile(file.toPath());
    }

    private void loadResources() {
        File folder = new File(plugin.getDataFolder(), "resources");
        if (!folder.exists()) {
            folder.mkdirs();
            plugin.saveResource("resources/ores.yml", false);
        }

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (!f.getName().endsWith(".yml")) continue;
            CommentedConfigurationNode node = loadFile(f.toPath());
            int fileCount = 0;

            for (Map.Entry<Object, ? extends CommentedConfigurationNode> entry : node.node("resources").childrenMap().entrySet()) {
                String id = entry.getKey().toString();
                CommentedConfigurationNode data = entry.getValue();

                List<LootEntry> loots = new ArrayList<>();
                data.node("loots").childrenList().forEach(l -> loots.add(new LootEntry(
                        l.node("item").getString(),
                        l.node("min").getInt(1),
                        l.node("max").getInt(1),
                        l.node("chance").getDouble(100.0)
                )));

                try {
                    List<String> whitelist = data.node("whitelist").getList(String.class);
                    if (whitelist == null) whitelist = new ArrayList<>();

                    resources.put(id, new ResourceBlock(
                            id,
                            data.node("hp").getDouble(10.0),
                            whitelist,
                            loots,
                            data.node("experience").getInt(0)
                    ));
                    fileCount++;
                } catch (SerializationException e) {
                    plugin.getLogger().warning("Config error for block " + id + " in " + f.getName() + ": " + e.getMessage());
                }
            }
            plugin.getLogger().info("Resources loaded " + fileCount + " entries from " + f.getName());
        }
    }

    public ResourceBlock getResource(Block block) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
        String id = (cb != null) ? cb.getNamespacedID() : block.getType().getKey().toString();
        return resources.get(id);
    }

    public double getToolPower(ItemStack item) {
        if (item == null || item.getType().isAir()) return toolPowers.getOrDefault("HAND", 1.0);
        CustomStack cs = CustomStack.byItemStack(item);
        String id = (cs != null) ? cs.getNamespacedID() : "minecraft:" + item.getType().name().toLowerCase();
        return toolPowers.getOrDefault(id, toolPowers.getOrDefault("HAND", 1.0));
    }

    public String getToolId(ItemStack item) {
        if (item == null || item.getType().isAir()) return "HAND";
        CustomStack cs = CustomStack.byItemStack(item);
        return (cs != null) ? cs.getNamespacedID() : "minecraft:" + item.getType().name().toLowerCase();
    }

    public CommentedConfigurationNode getSettings() { return settingsNode; }
    public CommentedConfigurationNode getLang() { return langNode; }

    public boolean isResource(Block block) { return getResource(block) != null; }

    public double getHitCooldown() { return hitCooldown; }

    private CommentedConfigurationNode loadFile(Path path) {
        try {
            return YamlConfigurationLoader.builder().path(path).build().load();
        } catch (Exception e) {
            throw new RuntimeException("Error loading config: " + path, e);
        }
    }
}
