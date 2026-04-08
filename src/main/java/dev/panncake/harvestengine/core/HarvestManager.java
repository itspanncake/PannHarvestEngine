package dev.panncake.harvestengine.core;

import dev.lone.itemsadder.api.CustomStack;
import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.models.LootEntry;
import dev.panncake.harvestengine.models.ResourceBlock;
import dev.panncake.harvestengine.util.InventoryUtil;
import dev.panncake.harvestengine.util.LootFactory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HarvestManager {
    private static HarvestManager instance;
    private final Map<UUID, HarvestSession> activeSessions = new HashMap<>();

    public void handleInteract(Player player, Block block, ResourceBlock data) {
        HarvestSession session = activeSessions.get(player.getUniqueId());

        if (!data.whitelist().isEmpty()) {
            ItemStack item = player.getInventory().getItemInMainHand();
            String toolId = getToolId(item);

            if (!data.whitelist().contains(toolId)) {
                String msg = PannHarvestEngine.get().getConfigManager().getLang().node("messages", "wrong-tool").getString("<red>Outil inadapté !");
                player.sendRichMessage(msg);
                return;
            }
        }

        if (session != null && !session.getBlock().getLocation().equals(block.getLocation())) {
            session.abort();
            session = null;
        }

        if (session == null) {
            session = new HarvestSession(player, block, data);
            activeSessions.put(player.getUniqueId(), session);
        }

        session.hit(PannHarvestEngine.get().getConfigManager().getToolPower(player.getInventory().getItemInMainHand()));
    }

    public void completeSession(Player player, HarvestSession session) {
        ResourceBlock data = session.getData();
        List<ItemStack> loots = new ArrayList<>();

        for (LootEntry entry : data.loots()) {
            ItemStack item = LootFactory.create(entry);
            if (item != null) loots.add(item);
        }

        if (!InventoryUtil.canFitAll(player, loots)) {
            player.sendRichMessage(Objects.requireNonNull(PannHarvestEngine.get().getConfigManager().getLang().node("messages", "inventory-full").getString()));
            session.abort();
            activeSessions.remove(player.getUniqueId());
            return;
        }

        loots.forEach(player.getInventory()::addItem);
        if (data.xp() > 0) player.giveExp(data.xp());

        session.getBlock().setType(org.bukkit.Material.AIR);

        player.playSound(player.getLocation(), "minecraft:block.stone.break", 1.0f, 1.0f);

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
            if (now - entry.getValue().getLastHitTime() > 2000) {
                entry.getValue().abort();
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
