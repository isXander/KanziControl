package dev.isxander.kanzicontrol.mixins;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean isLeftPressed;
    @Shadow private double xpos;
    @Shadow private double ypos;

    @Shadow private int activeButton;

    @Shadow private double mousePressedTime;

    /**
     * Don't lock the mouse we need it for touch input!
     */
    @ModifyArg(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V"))
    private int changeMouseState(int disabledState) {
        if (!KanziConfig.INSTANCE.instance().enabled) {
            return disabledState;
        }

        return KanziConfig.INSTANCE.instance().showCursor ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_HIDDEN;
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void overridePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (!KanziConfig.INSTANCE.instance().enabled) {
            return;
        }

        ci.cancel();

        if (window != minecraft.getWindow().getWindow())
            return;

        boolean isPress = action == GLFW.GLFW_PRESS;

        activeButton = -1;
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            isLeftPressed = isPress;
            if (isPress) {
                activeButton = 0;
                mousePressedTime = Blaze3D.getTime();

                RootInteractionArea.onMouseDown((float) xpos, (float) ypos);
            } else {
                RootInteractionArea.onMouseUp((float) xpos, (float) ypos);
            }
        }

        if (minecraft.getOverlay() == null && minecraft.screen != null) {
            double scaledX = this.xpos * (double) minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getScreenWidth();
            double scaledY = this.ypos * (double) minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getScreenHeight();

            Screen screen = minecraft.screen;
            if (isPress) {
                screen.afterMouseAction();
                Screen.wrapScreenError(() -> screen.mouseClicked(scaledX, scaledY, button), "mouseClicked event handler", screen.getClass().getCanonicalName());
            } else {
                Screen.wrapScreenError(() -> screen.mouseReleased(scaledX, scaledY, button), "mouseReleased event handler", screen.getClass().getCanonicalName());
            }
        }
    }

    @Inject(method = "onMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;turnPlayer()V"))
    private void onProcessMouseMove(long window, double x, double y, CallbackInfo ci) {
        if (KanziConfig.INSTANCE.instance().enabled)
            RootInteractionArea.onMouseMove((float) x, (float) y);
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    public void stopTurningPlayer(CallbackInfo ci) {
        if (KanziConfig.INSTANCE.instance().enabled)
            ci.cancel();
    }
}
