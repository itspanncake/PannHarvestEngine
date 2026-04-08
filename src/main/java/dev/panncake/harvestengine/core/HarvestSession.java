package dev.panncake.harvestengine.core;

import dev.panncake.harvestengine.display.PacketDisplay;
import dev.panncake.harvestengine.display.ProgressBar;
import dev.panncake.harvestengine.models.ResourceBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class HarvestSession {
    private final Player player;
    private final Block block;
    private final ResourceBlock data;
    private final PacketDisplay display;

    private double currentDamage = 0;
    private long lastHitTime;

    public HarvestSession(Player player, Block block, ResourceBlock data) {
        this.player = player;
        this.block = block;
        this.data = data;
        this.display = new PacketDisplay(block.getLocation());
        this.lastHitTime = System.currentTimeMillis();
    }

    public void hit(double power) {
        this.currentDamage += power;
        this.lastHitTime = System.currentTimeMillis();

        float progress = (float) Math.min(currentDamage / data.hp(), 1.0);

        display.update(progress, ProgressBar.build(progress, data.id()));

        if (currentDamage >= data.hp()) {
            finish();
        }
    }

    private void finish() { HarvestManager.getInstance().completeSession(player, this); }

    public void abort() { display.destroy(); }

    /* GETTERS */
    public Block getBlock() { return block; }
    public ResourceBlock getData() { return data; }
    public long getLastHitTime() { return lastHitTime; }
}
