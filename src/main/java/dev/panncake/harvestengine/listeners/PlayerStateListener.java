package dev.panncake.harvestengine.listeners;

import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.core.HarvestManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerStateListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVanillaDamage(BlockDamageEvent e) {
        if (PannHarvestEngine.get().getConfigManager().isResource(e.getBlock())) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVanillaBreak(BlockBreakEvent e) {
        if (PannHarvestEngine.get().getConfigManager().isResource(e.getBlock())) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() ||
                e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
                e.getFrom().getBlockY() != e.getTo().getBlockY()) {

            HarvestManager.getInstance().stopSession(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PannHarvestEngine.get().getHarvestManager().stopSession(e.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        PannHarvestEngine.get().getHarvestManager().stopSession(e.getEntity());
    }
}
