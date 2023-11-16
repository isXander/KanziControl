package dev.isxander.kanzicontrol.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.isxander.kanzicontrol.server.ClientboundKanziIndicatorPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class IndicateCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("indicate")
                .requires(source -> source.hasPermission(2))
                .then(getIndicateArgument("touch_up"))
                .then(getIndicateArgument("touch_down"))
                .then(getIndicateArgument("touch_left"))
                .then(getIndicateArgument("touch_right"))
                .then(getIndicateArgument("damage"))
                .then(getIndicateArgument("walk"))
                .then(getIndicateArgument("success"))
                .then(getIndicateArgument("fail"))
                .then(getIndicateArgument("grape"))
                .then(getIndicateArgument("peanut"))
                .then(getIndicateArgument("apple"))
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getIndicateArgument(String name) {
        return Commands.literal(name)
                .then(Commands.argument("duration_ticks", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                            int duration = IntegerArgumentType.getInteger(ctx, "duration_ticks");
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "target");

                            sendIndicator(new ResourceLocation("kanzicontrol", name), duration, players);

                            ctx.getSource().sendSuccess(() -> Component.literal("Sent " + name + " to " + players.size() + " players"), true);
                            return 1;
                        })
                );
    }

    private static void sendIndicator(ResourceLocation type, int duration, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, new ClientboundKanziIndicatorPacket(type, duration));
        }
    }
}
