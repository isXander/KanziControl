package dev.isxander.kanzicontrol.server;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSortInventoryPacket implements FabricPacket {
    public static final PacketType<ClientboundSortInventoryPacket> TYPE = PacketType.create(
            new ResourceLocation("kanzicontrol", "sort_inventory_s2c"),
            ClientboundSortInventoryPacket::new
    );

    public ClientboundSortInventoryPacket() {
    }

    public ClientboundSortInventoryPacket(FriendlyByteBuf buf) {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
