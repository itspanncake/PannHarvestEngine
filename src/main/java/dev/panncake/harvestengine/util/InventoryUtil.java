package dev.panncake.harvestengine.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryUtil {
    public static boolean canFitAll(Player player, List<ItemStack> items) {
        if (items.isEmpty()) return true;

        Inventory fakeInv = Bukkit.createInventory(null, 36);
        fakeInv.setContents(player.getInventory().getStorageContents());

        for (ItemStack item : items) {
            if (item == null || item.getType().isAir()) continue;
            if (!fakeInv.addItem(item.clone()).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
