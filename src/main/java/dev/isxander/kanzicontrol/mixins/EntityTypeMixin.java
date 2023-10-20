package dev.isxander.kanzicontrol.mixins;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    /**
     * Modify the hitbox size of the end crystal to be 6x6 instead of 2x2.
     */
    @ModifyArgs(
            method = "<clinit>",
            slice = @Slice(from = @At(
                    value = "CONSTANT",
                    args = "stringValue=end_crystal"
            )),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    ordinal = 0
            )
    )
    private static void modifyEndCrystalHeight(Args args) {
        args.set(0, 6f); // width
        args.set(1, 6f); // height
    }
}
