package dev.isxander.kanzicontrol.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.isxander.kanzicontrol.server.ClientboundSortInventoryPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class SortInventoryCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sortinventory")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(ctx -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "targets");
                            for (ServerPlayer player : players) {
                                ServerPlayNetworking.send(player, new ClientboundSortInventoryPacket());
                            }
                            return 1;
                        })
                )
        );
    }
}
