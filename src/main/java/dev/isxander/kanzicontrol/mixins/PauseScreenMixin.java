package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.config.ConfigGuiGen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void addConfigButton(CallbackInfo ci) {
        addRenderableWidget(
                Button.builder(
                                Component.literal("Bonobos..."),
                                btn ->
                                        minecraft.setScreen(ConfigGuiGen.generateConfigScreen((PauseScreen) (Object) this))
                        )
                        .pos(0, 0)
                        .width(60)
                        .build()
        );
    }
}
