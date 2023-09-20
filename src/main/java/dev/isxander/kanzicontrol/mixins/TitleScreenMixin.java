package dev.isxander.kanzicontrol.mixins;

import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    private void addConfigButton(CallbackInfo ci) {
        addRenderableWidget(
                Button.builder(
                        Component.literal("Bonobos..."),
                        btn ->
                                minecraft.setScreen(KanziConfig.INSTANCE.generateGui().generateScreen((TitleScreen) (Object) this))
                )
                .pos(0, 0)
                .width(60)
                .build()
        );
    }
}
