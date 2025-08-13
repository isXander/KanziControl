package dev.isxander.kanzicontrol.server.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundElytraPacket(boolean enable) implements FabricPacket {
    public static final PacketType<ClientboundElytraPacket> TYPE = PacketType.create(
            new ResourceLocation("kanzicontrol", "elytra_s2c"),
            ClientboundElytraPacket::new
    );

    public ClientboundElytraPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.enable);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
