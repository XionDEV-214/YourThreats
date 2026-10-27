package com.eisiadev.yourthreats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public final class ThreatCommand {
    private ThreatCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("threat")
                .executes(ThreatCommand::showCurrent)
                .then(Commands.literal("list").executes(ThreatCommand::list))
                .then(Commands.literal("set")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("level", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(ThreatLevel.values()).map(ThreatLevel::getDisplayName), builder))
                                .executes(ThreatCommand::set))));
    }

    private static int showCurrent(CommandContext<CommandSourceStack> ctx) {
        ThreatLevel level = ThreatManager.getLevel(ctx.getSource().getServer());
        ctx.getSource().sendSuccess(() -> Component.translatable("yourthreats.command.current", level.getName()), false);
        ctx.getSource().sendSuccess(() -> stats(level), false);
        return level.ordinal() + 1;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        ThreatLevel current = ThreatManager.getLevel(ctx.getSource().getServer());
        for (ThreatLevel level : ThreatLevel.values()) {
            MutableComponent line = Component.literal(level == current ? "▶ " : "  ").withStyle(ChatFormatting.WHITE)
                    .append(level.getName())
                    .append(Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(stats(level));
            ctx.getSource().sendSuccess(() -> line, false);
        }
        return ThreatLevel.values().length;
    }

    private static int set(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "level");
        ThreatLevel level = ThreatLevel.byName(name);
        if (level == null) {
            ctx.getSource().sendFailure(Component.translatable("yourthreats.command.unknown", name));
            return 0;
        }
        ThreatManager.setLevel(ctx.getSource().getServer(), level);
        ctx.getSource().getServer().getPlayerList().broadcastSystemMessage(
                Component.translatable("yourthreats.command.changed", level.getName()), false);
        return 1;
    }

    private static MutableComponent stats(ThreatLevel l) {
        return l.getStats().withStyle(ChatFormatting.GRAY);
    }
}
