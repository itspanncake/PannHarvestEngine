package dev.panncake.harvestengine.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

public class PannHarvestCommand extends AbstractCommand {
    private final List<AbstractCommand> subCommands = List.of(
    );

    @Override
    public String getName() {
        return "pannharvestengine";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
                "phe",
                "pannharvest",
                "harvest",
                "harvestengine",
                "pannhe"
        );
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> root =
                Commands.literal(getName())
                        .requires(src -> src.getSender().hasPermission("panncake.harvestengine.admin"))
                        .executes(ctx -> {
                            invalidUsage(ctx);
                            return Command.SINGLE_SUCCESS;
                        });

        for (AbstractCommand sub : subCommands) {
            root.then(sub.build());
        }

        return root;
    }

    private void invalidUsage(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getExecutor().sendRichMessage("<red>Invalid usage! Use <white>/pannharvestengine help <red>for more information.");
    }
}
