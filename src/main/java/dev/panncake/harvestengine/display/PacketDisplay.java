package dev.panncake.harvestengine.display;

import dev.panncake.harvestengine.PannHarvestEngine;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class PacketDisplay {
    private final Location loc;
    private final int blockEntityId;
    private ArmorStand stand;

    public PacketDisplay(Location loc) {
        this.loc = loc;
        this.blockEntityId = loc.hashCode();
    }

    public void update(float progress, Component title) {
        if (stand == null || stand.isDead()) spawnArmorStand();

        stand.customName(title);

        for (Player p : loc.getNearbyPlayers(PannHarvestEngine.get().getConfigManager().getSettings().node("settings", "packet-radius").getInt(20))) {
            p.sendBlockDamage(loc, progress, blockEntityId);
        }
    }

    private void spawnArmorStand() {
        this.stand = loc.getWorld().spawn(loc.clone().add(
                0.5,
                PannHarvestEngine.get().getConfigManager().getSettings().node("progress-bar", "offset-y").getDouble(1.3),
                0.5
        ), ArmorStand.class, s -> {
            s.setInvisible(true);
            s.setMarker(true);
            s.setCustomNameVisible(true);
            s.setGravity(false);
            s.setBasePlate(false);
            s.setSmall(true);
            s.customName(net.kyori.adventure.text.Component.text(" "));

            s.setPersistent(false);
        });
    }

    public void destroy() {
        if (stand != null) stand.remove();
        for (Player p : loc.getNearbyPlayers(PannHarvestEngine.get().getConfigManager().getSettings().node("settings", "packet-radius").getInt(20))) {
            p.sendBlockDamage(loc, 0f, blockEntityId);
        }
    }
}
