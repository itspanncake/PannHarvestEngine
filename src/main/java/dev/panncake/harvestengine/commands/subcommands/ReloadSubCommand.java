package dev.panncake.harvestengine.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.panncake.harvestengine.PannHarvestEngine;
import dev.panncake.harvestengine.commands.AbstractCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ReloadSubCommand extends AbstractCommand {
    private final PannHarvestEngine plugin = PannHarvestEngine.get();

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(getName())
                .requires(src -> src.getSender().hasPermission("panncake.harvestengine.reload"))

                .executes(ctx -> {
                    ctx.getSource().getExecutor().sendRichMessage("<gray>Reloading plugin...");
                    reloadConfigs();
                    ctx.getSource().getExecutor().sendRichMessage("<green>Plugin reloaded!");
                    return Command.SINGLE_SUCCESS;
                });
    }

    private void reloadConfigs() {
        try {
            plugin.getConfigManager().reloadAll();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
