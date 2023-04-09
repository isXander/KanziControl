package dev.isxander.kanzicontrol.mixins;

import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Tutorial.class)
public class TutorialMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void start() {
        // go away
    }
}
