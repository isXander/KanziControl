package dev.isxander.kanzicontrol.server;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundSetClientTagPacket(Action action, String tag, int entityId) implements FabricPacket {
    public static final PacketType<ClientboundSetClientTagPacket> TYPE = PacketType.create(
            new ResourceLocation("kanzicontrol", "clienttag_s2c"),
            ClientboundSetClientTagPacket::new
    );

    public ClientboundSetClientTagPacket(FriendlyByteBuf buf) {
        this(buf.readEnum(Action.class), buf.readUtf(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeUtf(tag);
        buf.writeInt(entityId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public enum Action {
        ADD,
        REMOVE
    }
}
