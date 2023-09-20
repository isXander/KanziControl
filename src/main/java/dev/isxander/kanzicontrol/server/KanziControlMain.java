package dev.isxander.kanzicontrol.server;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class KanziControlMain implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) -> {
            var inner = Commands.argument("target", EntityArgument.players());
            var indicators = List.of(
                    "touch_up",
                    "touch_down",
                    "touch_left",
                    "touch_right",
                    "damage",
                    "walk",
                    "success",
                    "fail",
                    "grape",
                    "peanut",
                    "apple"
            );

            for (String indicator : indicators) {
                inner.then(Commands.literal(indicator)
                        .then(Commands.argument("duration_ticks", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    int duration = IntegerArgumentType.getInteger(ctx, "duration_ticks");
                                    sendIndicator(new ResourceLocation("kanzicontrol", indicator), duration, EntityArgument.getPlayers(ctx, "target"));
                                    return 1;
                                })));
            }

            dispatcher.register(Commands.literal("indicate")
                    .requires(source -> source.hasPermission(2))
                    .then(inner));
        });

        KanziHandshake.setupOnServer();
    }

    private void sendIndicator(ResourceLocation type, int duration, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, new S2CIndicatorPacket(type, duration));
        }
    }
}
