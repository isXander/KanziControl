package dev.isxander.kanzicontrol.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.isxander.kanzicontrol.server.ClientboundSetClientTagPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ClientTagCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("clienttag")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.literal("add")
                                .then(Commands.argument("tag", StringArgumentType.string())
                                        .executes(ctx -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "target");
                                            String tag = StringArgumentType.getString(ctx, "tag");

                                            sendPackets(
                                                    ctx.getSource(),
                                                    players,
                                                    tag,
                                                    ClientboundSetClientTagPacket.Action.ADD
                                            );

                                            ctx.getSource().sendSuccess(() -> Component.literal("Added tag " + tag + " to " + players.size() + " players"), true);

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("tag", StringArgumentType.string())
                                        .executes(ctx -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "target");
                                            String tag = StringArgumentType.getString(ctx, "tag");

                                            sendPackets(
                                                    ctx.getSource(),
                                                    players,
                                                    tag,
                                                    ClientboundSetClientTagPacket.Action.REMOVE
                                            );

                                            ctx.getSource().sendSuccess(() -> Component.literal("Removed tag " + tag + " to " + players.size() + " players"), true);

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    private static void sendPackets(CommandSourceStack src, Collection<ServerPlayer> targets, String tag, ClientboundSetClientTagPacket.Action action) {
        for (ServerPlayer player : src.getLevel().players()) {
            for (ServerPlayer target : targets) {
                ServerPlayNetworking.send(player, new ClientboundSetClientTagPacket(action, tag, target.getId()));
            }
        }
    }
}
