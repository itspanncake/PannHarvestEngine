package dev.panncake.harvestengine.listeners;

import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.core.HarvestManager;
import dev.panncake.harvestengine.models.ResourceBlock;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (e.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        ResourceBlock data = PannHarvestEngine.get().getConfigManager().getResource(block);
        if (data == null) return;

        e.setCancelled(true);
        HarvestManager.getInstance().handleInteract(e.getPlayer(), block, data);
    }
}
