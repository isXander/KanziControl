package dev.isxander.kanzicontrol.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.isxander.kanzicontrol.server.packets.ClientboundElytraPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ElytraCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("elytra")
                .then(Commands.literal("force")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("start")
                                        .executes(ctx -> {
                                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                            forceElytraFlight(player, true);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Forced elytra flight"), true);
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("stop")
                                        .executes(ctx -> {
                                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                            forceElytraFlight(player, false);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Stopped elytra flight"), true);
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("toggle")
                                        .executes(ctx -> {
                                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                            boolean isFlying = player.isFallFlying();
                                            forceElytraFlight(player, !isFlying);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Toggled elytra flight: " + (!isFlying ? "enabled" : "disabled")), true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private static void forceElytraFlight(ServerPlayer player, boolean enable) {
        ServerPlayNetworking.send(player, new ClientboundElytraPacket(enable));
        if (!enable) {
            player.stopFallFlying();
        }
    }
}
