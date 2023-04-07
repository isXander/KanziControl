package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class ButtonInteractionArea implements InteractionArea {
    private float x, y, width, height;

    public ButtonInteractionArea(AnchorPoint windowAnchor, float x, float y, float width, float height, AnchorPoint origin) {
        Vector2fc anchorPosition = windowAnchor.getAnchorPosition(windowSize());
        this.x = anchorPosition.x() - (x + width * origin.anchorX);
        this.y = anchorPosition.y() - (y + height * origin.anchorY);
        this.width = width;
        this.height = height;
    }

    public void reposition(AnchorPoint windowAnchor, float x, float y, float width, float height, AnchorPoint origin) {
        Vector2fc anchorPosition = windowAnchor.getAnchorPosition(windowSize());
        this.x = anchorPosition.x() - (x + width * origin.anchorX);
        this.y = anchorPosition.y() - (y + height * origin.anchorY);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return position.x() >= x && position.x() <= x + width
                && position.y() >= y && position.y() <= y + height;
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position) {
        GuiComponent.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height), -1);
    }

    public enum AnchorPoint {
        TOP_LEFT(0, 0),
        TOP_CENTER(0.5f, 0),
        TOP_RIGHT(1, 0),
        CENTER_LEFT(0, 0.5f),
        CENTER(0.5f, 0.5f),
        CENTER_RIGHT(0.5f, 1),
        BOTTOM_LEFT(0f, 1f),
        BOTTOM_CENTER(0.5f, 1f),
        BOTTOM_RIGHT(1f, 1f);

        public final float anchorX, anchorY;

        AnchorPoint(float anchorX, float anchorY) {
            this.anchorX = anchorX;
            this.anchorY = anchorY;
        }

        public Vector2f getAnchorPosition(Vector2fc windowSize) {
            return new Vector2f(windowSize).mul(anchorX, anchorY);
        }
    }
}
