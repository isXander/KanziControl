package dev.isxander.kanzicontrol.interactionarea.button;

import dev.isxander.kanzicontrol.interactionarea.PositionableElement;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class ButtonInteractionArea implements PositionableElement {
    protected final ButtonRenderer renderer;
    protected final Vector2f position;
    protected final Vector2fc size;
    protected final ButtonAction action;
    protected final ButtonRenderPredicate predicate;
    protected boolean canRender = true;

    public ButtonInteractionArea(ButtonRenderer renderer, float width, float height, ButtonAction action, ButtonRenderPredicate predicate) {
        this.renderer = renderer;
        this.position = new Vector2f();
        this.size = new Vector2f(width, height);
        this.action = action;
        this.predicate = predicate;
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    @Override
    public Vector2fc getSize() {
        return size;
    }

    @Override
    public boolean fingerDown(Vector2fc position) {
        this.action.onFingerStateChange(true);
        return true;
    }

    @Override
    public void fingerUp(Vector2fc position) {
        this.action.onFingerStateChange(false);
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (canRender) {
            this.renderer.render(graphics, deltaTime, this, interacting);
        }
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        canRender = this.predicate.test(new ButtonRenderPredicate.RenderCtx(minecraft().hitResult, false));
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return PositionableElement.super.isInBounds(position) && this.canRender;
    }
}
