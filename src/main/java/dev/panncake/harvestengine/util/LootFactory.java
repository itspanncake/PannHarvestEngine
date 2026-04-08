package dev.panncake.harvestengine.util;

import dev.lone.itemsadder.api.CustomStack;
import dev.panncake.harvestengine.models.LootEntry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootFactory {
    public static ItemStack create(LootEntry entry) {
        if (ThreadLocalRandom.current().nextDouble(100.0) > entry.chance()) return null;

        int amount = ThreadLocalRandom.current().nextInt(entry.min(), entry.max() + 1);
        ItemStack item;

        if (entry.itemId().contains(":")) {
            CustomStack cs = CustomStack.getInstance(entry.itemId());
            item = (cs != null) ? cs.getItemStack() : null;
        } else {
            Material mat = Material.matchMaterial(entry.itemId().toUpperCase());
            item = (mat != null) ? new ItemStack(mat) : null;
        }

        if (item != null) item.setAmount(amount);
        return item;
    }
}
