package dev.panncake.harvestengine.util;

import dev.panncake.harvestengine.PannHarvestEngine;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.time.Duration;

public class TitlesUtil {
    private static final PannHarvestEngine plugin = PannHarvestEngine.get();

    private static final CommentedConfigurationNode lang = plugin.getConfigManager().getLang();

    public static void showInventoryFull(Player player) {
        Title title = Title.title(
                MiniMessage.miniMessage().deserialize(lang.node("titles", "inventory-full", "title").getString("<red>Your inventory is full!")),
                MiniMessage.miniMessage().deserialize(lang.node("titles", "inventory-full", "subtitle").getString("<gray>Clear some space to break blocks")),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(2000),
                        Duration.ofMillis(500)
                )
        );

        player.showTitle(title);
    }
}
