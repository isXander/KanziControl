package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.interactionarea.PositionableElement;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.function.Consumer;

public class ButtonInteractionArea implements PositionableElement {
    private final ButtonRenderer renderer;
    private final Vector2f position;
    private final Vector2fc size;
    private final ButtonAction action;
    private final ButtonRenderPredicate predicate;

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
    public void fingerDown(Vector2fc position) {
        this.action.onFingerStateChange(true);
    }

    @Override
    public void fingerUp(Vector2fc position) {
        this.action.onFingerStateChange(false);
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position, boolean interacting) {
        if (this.canRender()) {
            this.renderer.render(stack, deltaTime, this, interacting);
        }
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return PositionableElement.super.isInBounds(position) && this.canRender();
    }

    public boolean canRender() {
        return this.predicate.test(new ButtonRenderPredicate.RenderCtx(minecraft().hitResult, false));
    }
}
