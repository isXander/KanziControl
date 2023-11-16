package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.kanzicontrol.server.KanziControlMain;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public class DragonFireballMixin {
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
    private void addDragonEffectToCloud(HitResult hitResult, CallbackInfo ci, @Local AreaEffectCloud areaEffectCloud) {
        areaEffectCloud.addEffect(
                new MobEffectInstance(
                        KanziControlMain.DRAGON_BREATH_EFFECT,
                        25, 0,
                        false, false
                )
        );
    }
}
