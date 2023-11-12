package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.server.ClientboundSortInventoryPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    @Inject(method = "giveItem", at = @At(value = "CONSTANT", args = "intValue=1"))
    private static void tellPlayersToSort(CommandSourceStack source, ItemInput item, Collection<ServerPlayer> targets, int count, CallbackInfoReturnable<Integer> cir) {
        for (ServerPlayer player : targets) {
            ServerPlayNetworking.send(player, new ClientboundSortInventoryPacket());
        }
    }
}
