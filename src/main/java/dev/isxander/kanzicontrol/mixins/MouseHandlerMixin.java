package dev.isxander.kanzicontrol.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.kanzicontrol.debug.KanziControlDebug;
import dev.isxander.kanzicontrol.interactionarea.InteractionAreaStorage;
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

    /**
     * Don't lock the mouse we need it for touch input!
     */
    @ModifyArg(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(JIDD)V"))
    private int changeMouseState(int disabledState) {
        return KanziControlDebug.DEBUG_MOUSE_POSITION ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_HIDDEN;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onPress(long window, int button, int action, int mods) {
        if (window != minecraft.getWindow().getWindow())
            return;

        boolean isPress = action == GLFW.GLFW_PRESS;

        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            isLeftPressed = isPress;
            if (isPress) {
                InteractionAreaStorage.onMouseDown((float) xpos, (float) ypos);
            } else {
                InteractionAreaStorage.onMouseUp((float) xpos, (float) ypos);
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
        InteractionAreaStorage.onMouseMove((float) x, (float) y);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void turnPlayer() {

    }
}
