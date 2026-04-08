package dev.panncake.harvestengine.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.panncake.harvestengine.PannHarvestEngine;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class CommandManager {
    private static final PannHarvestEngine plugin =  PannHarvestEngine.get();

    public static void register() {
        registerCommand(new PannHarvestCommand());
    }

    private static void registerCommand(AbstractCommand command) {
        LiteralCommandNode<CommandSourceStack> node = command.build().build();

        plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> event.registrar().register(node, command.getAliases())
        );
    }
}
