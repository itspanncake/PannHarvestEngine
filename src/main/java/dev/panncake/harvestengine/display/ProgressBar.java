package dev.panncake.harvestengine.display;

import dev.panncake.harvestengine.PannHarvestEngine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurationNode;

public class ProgressBar {
    public static Component build(float progress, String resourceId) {
        ConfigurationNode node = PannHarvestEngine.get().getConfigManager().getLang().node("progress-bar");

        int totalBars = node.node("length").getInt(10);
        int filledBars = Math.round(progress * totalBars);

        String fullChar = node.node("chars", "filled").getString("█");
        String emptyChar = node.node("chars", "empty").getString("░");

        String color;
        if (progress > 0.66) color = node.node("colors", "high").getString("<green>");
        else if (progress > 0.33) color = node.node("colors", "medium").getString("<yellow>");
        else color = node.node("colors", "low").getString("<red>");

        String barBuilder = color +
                fullChar.repeat(Math.max(0, filledBars)) +
                "<gray>" +
                emptyChar.repeat(Math.max(0, (totalBars - filledBars)));

        String format = node.node("format").getString("[%bar%] %percent%%")
                .replace("%bar%", barBuilder)
                .replace("%percent%", String.valueOf((int) (progress * 100)));

        return MiniMessage.miniMessage().deserialize(format);
    }
}
