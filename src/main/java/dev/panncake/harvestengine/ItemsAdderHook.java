package dev.panncake.harvestengine;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook {
    public static boolean notFound() {
        return Bukkit.getPluginManager().getPlugin("ItemsAdder") == null ||
                !Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    public static String getCustomBlockId(Block block) {
        if (notFound()) return null;

        try {
            CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
            return (cb != null) ? cb.getNamespacedID() : null;
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    public static String getCustomItemId(ItemStack item) {
        if (notFound()) return null;

        try {
            CustomStack cs = CustomStack.byItemStack(item);
            return (cs != null) ? cs.getNamespacedID() : null;
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    public static ItemStack getCustomItemStack(String id) {
        if (notFound()) return null;

        try {
            CustomStack cs = CustomStack.getInstance(id);
            return (cs != null) ? cs.getItemStack() : null;
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }
}
