package dev.panncake.harvestengine.core;

import dev.lone.itemsadder.api.CustomStack;
import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.models.LootEntry;
import dev.panncake.harvestengine.models.ResourceBlock;
import dev.panncake.harvestengine.util.InventoryUtil;
import dev.panncake.harvestengine.util.LootFactory;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.*;

public class HarvestManager {
    private static HarvestManager instance;

    private final PannHarvestEngine plugin = PannHarvestEngine.get();

    private final Map<UUID, HarvestSession> activeSessions = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private final CommentedConfigurationNode settingsNode = plugin.getConfigManager().getSettings();
    private final CommentedConfigurationNode langNode = plugin.getConfigManager().getLang();

    public void handleInteract(Player player, Block block, ResourceBlock data) {
        HarvestSession session = activeSessions.get(player.getUniqueId());
        String currentTool = PannHarvestEngine.get().getConfigManager().getToolId(player.getInventory().getItemInMainHand());

        if (!data.whitelist().isEmpty() && !data.whitelist().contains(currentTool)) {
            String msg = langNode.node("messages", "wrong-tool").getString("<red>Wrong tool!");
            player.sendRichMessage(msg);
            return;
        }

        long now = System.currentTimeMillis();
        long lastHit = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        double cooldownMillis = PannHarvestEngine.get().getConfigManager().getHitCooldown() * 1000;

        if (now - lastHit < cooldownMillis) return;

        if (session != null && !session.getBlock().getLocation().equals(block.getLocation())) {
            session.abort();
            session = null;
        }

        if (session == null) {
            session = new HarvestSession(player, block, data);
            activeSessions.put(player.getUniqueId(), session);
        }

        session.hit(PannHarvestEngine.get().getConfigManager().getToolPower(player.getInventory().getItemInMainHand()));
        cooldowns.put(player.getUniqueId(), now);
    }

    public void completeSession(Player player, HarvestSession session) {
        ResourceBlock data = session.getData();
        List<ItemStack> loots = new ArrayList<>();

        for (LootEntry entry : data.loots()) {
            ItemStack item = LootFactory.create(entry);
            if (item != null) loots.add(item);
        }

        if (!InventoryUtil.canFitAll(player, loots)) {
            player.sendRichMessage(langNode.node("messages", "inventory-full").getString("<red>Inventory full!"));
            session.abort();
            activeSessions.remove(player.getUniqueId());
            return;
        }

        loots.forEach(player.getInventory()::addItem);
        if (data.xp() > 0) player.giveExp(data.xp());

        session.getBlock().setType(org.bukkit.Material.AIR);

        player.playSound(player.getLocation(),
                settingsNode.node("sounds", "block-break", "sound").getString("minecraft:block.stone.break"),
                settingsNode.node("sounds", "block-break", "volume").getFloat(1.0f),
                settingsNode.node("sounds", "block-break", "pitch").getFloat(1.0f)
        );

        session.abort();
        activeSessions.remove(player.getUniqueId());
    }

    public void stopSession(Player player) {
        HarvestSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) session.abort();
    }

    public void stopAllSessions() {
        activeSessions.values().forEach(HarvestSession::abort);
        activeSessions.clear();
    }

    public void tickCleanup() {
        long now = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry -> {
            HarvestSession session = entry.getValue();
            Player player = Bukkit.getPlayer(entry.getKey());

            boolean expired = (now - session.getLastHitTime() > (settingsNode.node("settings", "expiration").getInt(2) * 1000L));

            boolean tooFar = (player == null || !player.isOnline() ||
                    player.getLocation().distanceSquared(session.getBlock().getLocation()) > (settingsNode.node("settings", "max-distance").getInt(6) * 6));

            if (expired || tooFar) {
                session.abort();
                return true;
            }
            return false;
        });
    }

    private String getToolId(ItemStack item) {
        if (item == null || item.getType().isAir()) return "HAND";
        CustomStack cs = CustomStack.byItemStack(item);
        return (cs != null) ? cs.getNamespacedID() : "minecraft:" + item.getType().name().toLowerCase();
    }

    public static HarvestManager getInstance() {
        if (instance == null) instance = new HarvestManager();
        return instance;
    }
}
