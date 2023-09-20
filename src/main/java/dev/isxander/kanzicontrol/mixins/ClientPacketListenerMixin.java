package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;input:Lnet/minecraft/client/player/Input;", opcode = Opcodes.ASTORE, shift = At.Shift.AFTER))
    private void overrideNewPlayerInput(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (KanziConfig.INSTANCE.instance().enabled) {
            TouchInput.INSTANCE.setEnabled(true, minecraft.player);
        }
    }

    @Inject(method = "handleRespawn", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;input:Lnet/minecraft/client/player/Input;", opcode = Opcodes.ASTORE, shift = At.Shift.AFTER))
    private void overrideRespawnInput(ClientboundRespawnPacket packet, CallbackInfo ci, @Local(ordinal = 1) LocalPlayer newPlayer) {
        if (KanziConfig.INSTANCE.instance().enabled) {
            TouchInput.INSTANCE.setEnabled(true, newPlayer);
        }
    }
}
