package dev.isxander.kanzicontrol.utils;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;

public class RenderUtils {
    public static Vector2f scaledFingerPosition(double fingerScreenX, double fingerScreenY) {
        Window window = Minecraft.getInstance().getWindow();
        return new Vector2f(
                (float) fingerScreenX * window.getGuiScaledWidth() / window.getWidth(),
                (float) fingerScreenY * window.getGuiScaledHeight() / window.getHeight()
        );
    }
}
