package dev.isxander.kanzicontrol.server;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class KanziHandshake {
    public static final Logger LOGGER = LoggerFactory.getLogger("Kanzi Handshake");

    public static final int PROTOCOL_VERSION = 7;
    public static final ResourceLocation HANDSHAKE_CHANNEL = new ResourceLocation("kanzicontrol", "handshake");

    public static void setupOnServer() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(PROTOCOL_VERSION);
            sender.sendPacket(HANDSHAKE_CHANNEL, buf);
        });
        ServerLoginNetworking.registerGlobalReceiver(HANDSHAKE_CHANNEL, (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (!understood) {
                handler.disconnect(Component.literal("You cannot play on this server without KanziControl mod.").withStyle(ChatFormatting.RED));
                return;
            }

            int kanziControlProtocol = buf.readInt();
            if (kanziControlProtocol > PROTOCOL_VERSION) {
                handler.disconnect(Component.literal("Server has an old version of KanziControl installed and is incompatible with this client.").withStyle(ChatFormatting.RED));
            } else if (kanziControlProtocol < PROTOCOL_VERSION) {
                handler.disconnect(Component.literal("Client has an old version of KanziControl installed and is incompatible with this server.").withStyle(ChatFormatting.RED));
            }
        });
    }

    public static void setupOnClient() {
        ClientLoginNetworking.registerGlobalReceiver(HANDSHAKE_CHANNEL, (client, handler, buf, listenerAdder) -> {
            LOGGER.info("Received handshake initiation, responding...");
            FriendlyByteBuf response = PacketByteBufs.create();
            response.writeInt(PROTOCOL_VERSION);

            return CompletableFuture.completedFuture(response);
        });
    }
}
