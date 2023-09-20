package dev.isxander.kanzicontrol.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(from = @At(
                    value = "CONSTANT",
                    args = "stringValue=end_crystal"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    ordinal = 0
            ),
            index = 1
    )
    private static float modifyEndCrystalHeight(float original) {
        return 3.5f;
    }
}
