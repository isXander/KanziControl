package dev.isxander.kanzicontrol.server;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record S2CIndicatorPacket(ResourceLocation indicatorType, int durationTicks) implements FabricPacket {
    public static final PacketType<S2CIndicatorPacket> TYPE = PacketType.create(
            new ResourceLocation("kanzicontrol", "indicator_s2c"),
            S2CIndicatorPacket::new
    );

    public S2CIndicatorPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(indicatorType);
        buf.writeInt(durationTicks);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
