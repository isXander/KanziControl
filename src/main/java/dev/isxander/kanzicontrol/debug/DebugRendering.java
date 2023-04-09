package dev.isxander.kanzicontrol.debug;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Vector2f;

import java.util.List;

public class DebugRendering {
    private static List<Integer> DEBUG_COLOURS = List.of(
            0x40FF0000,
            0x4000FF00,
            0x400000FF,
            0x40FFFF00,
            0x4000FFFF,
            0x40FF00FF
    );
    private static int debugColourIndex = -1;

    public static void fillCircle(float x, float y, float radius, int numSegments, int color) {
        if ((color >> 24 & 0xFF) == 0) return;

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < numSegments; i++) {
            double angle = Math.toRadians(i * 360.0 / numSegments);
            double x1 = Math.sin(angle) * radius;
            double y1 = Math.cos(angle) * radius;
            bufferBuilder
                    .vertex(x + x1, y + y1, 0)
                    .color(
                            color >> 16 & 0xFF,
                            color >> 8 & 0xFF,
                            color & 0xFF,
                            color >> 24 & 0xFF
                    )
                    .endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static int getDebugColor() {
        return DEBUG_COLOURS.get(++debugColourIndex);
    }

    private static Vector2f descaledPosition(float x, float y) {
        Window window = Minecraft.getInstance().getWindow();
        return new Vector2f(
                x / ((float) window.getGuiScaledWidth() / window.getWidth()),
                y / ((float) window.getGuiScaledHeight() / window.getHeight())
        );
    }

    public static void finishRender() {
        debugColourIndex = -1;
    }
}
