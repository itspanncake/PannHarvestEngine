package dev.panncake.harvestengine.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public abstract class AbstractCommand {
    public abstract String getName();

    public List<String> getAliases() {
        return List.of();
    }

    public String getPermission() {
        return null;
    }

    public abstract LiteralArgumentBuilder<CommandSourceStack> build();
}
