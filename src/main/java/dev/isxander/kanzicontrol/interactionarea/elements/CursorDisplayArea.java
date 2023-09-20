package dev.isxander.kanzicontrol.interactionarea.elements;

import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2fc;

public class CursorDisplayArea implements InteractionArea {
    private static final ResourceLocation CURSOR_TEXTURE = new ResourceLocation("kanzicontrol", "textures/misc/cursor.png");

    private int targetX, targetY;
    private float currentX, currentY;
    private int displayTicks;

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (displayTicks > 0) {
            currentX = Mth.lerp(deltaTime, currentX, targetX);
            currentY = Mth.lerp(deltaTime, currentY, targetY);

            graphics.pose().pushPose();
            graphics.pose().translate(currentX, currentY, 0);
            graphics.pose().scale(0.75f, 0.75f, 1f);

            graphics.blit(CURSOR_TEXTURE, 0, 0, 0, 0, 0, 16, 16, 16, 16);

            graphics.pose().popPose();
        }
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        displayTicks--;
        displayTicks = Math.max(0, displayTicks);
    }

    public void moveCursor(int x, int y) {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (displayTicks <= 0) {
            this.currentX = this.targetX = width / 2;
            this.currentY = this.targetY = height / 2;
        }

        this.targetX += x;
        this.targetY += y;
        this.targetX = Mth.clamp(targetX, 0, width - 12);
        this.targetY = Mth.clamp(targetY, 0, height - 12);

        displayTicks = 20;
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return false;
    }
}
