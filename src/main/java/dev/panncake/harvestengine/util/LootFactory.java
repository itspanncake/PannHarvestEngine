package dev.panncake.harvestengine.util;

import dev.panncake.harvestengine.ItemsAdderHook;
import dev.panncake.harvestengine.models.LootEntry;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootFactory {
    public static ItemStack create(LootEntry entry) {
        if (ThreadLocalRandom.current().nextDouble(100.0) > entry.chance()) return null;

        int amount = ThreadLocalRandom.current().nextInt(entry.min(), entry.max() + 1);
        ItemStack item = null;

        if (entry.itemId().contains(":")) {
            item = ItemsAdderHook.getCustomItemStack(entry.itemId());
        }

        if (item == null) {
            org.bukkit.Material mat = org.bukkit.Material.matchMaterial(entry.itemId().replace("minecraft:", "").toUpperCase());
            if (mat != null) item = new ItemStack(mat);
        }

        if (item != null) item.setAmount(amount);
        return item;
    }
}
