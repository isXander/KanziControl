package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.kanzicontrol.server.ClientboundSortInventoryPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void notifyPlayerInvChanged(BlockSource pointer, ItemStack armor, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            ServerPlayNetworking.send(player, new ClientboundSortInventoryPacket());
        }
    }
}
