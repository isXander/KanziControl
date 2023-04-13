package dev.isxander.kanzicontrol.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static Vector2f scaledFingerPosition(double fingerScreenX, double fingerScreenY) {
        Window window = Minecraft.getInstance().getWindow();
        return new Vector2f(
                (float) fingerScreenX * window.getGuiScaledWidth() / window.getWidth(),
                (float) fingerScreenY * window.getGuiScaledHeight() / window.getHeight()
        );
    }

    public static Vec3 screenSpaceToWorldSpace(double x, double y, double d) {
        Camera camera = minecraft.getEntityRenderDispatcher().camera;

        int displayWidth = minecraft.getWindow().getGuiScaledWidth();
        int displayHeight = minecraft.getWindow().getGuiScaledHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        Matrix4f matrixProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        Matrix4f matrixModel = new Matrix4f(RenderSystem.getModelViewMatrix());

        matrixProj.mul(matrixModel)
                .mul(lastWorldSpaceMatrix)
                .unproject((float) x / displayWidth * viewport[2], (float) y / displayHeight * viewport[3], (float) d, viewport, target);

        return new Vec3(target.x, target.y, target.z).add(camera.getPosition());
    }
}
