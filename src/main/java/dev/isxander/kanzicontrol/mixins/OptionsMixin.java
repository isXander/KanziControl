package dev.isxander.kanzicontrol.mixins;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Options.class)
public class OptionsMixin {
    // boolean constants are int constants in bytecode
    // intValue = 0 is implied if everything is set to 0 - 0 is a special case
    @ModifyConstant(method = "<init>", constant = @Constant(ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=options.autoJump")))
    private int modifyAutoJumpDefaultValue(int original) {
        return 1;
    }
}
