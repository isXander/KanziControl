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
    private final Consumer<Boolean> fingerDownListener;

    public ButtonInteractionArea(ButtonRenderer renderer, float width, float height, Consumer<Boolean> fingerDownListener) {
        this.renderer = renderer;
        this.position = new Vector2f();
        this.size = new Vector2f(width, height);
        this.fingerDownListener = fingerDownListener;
    }

    public ButtonInteractionArea(ButtonRenderer renderer, float width, float height, Runnable onTap) {
        this(renderer, width, height, fingerDown -> {
            if (!fingerDown) onTap.run();
        });
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
        this.fingerDownListener.accept(true);
    }

    @Override
    public void fingerUp(Vector2fc position) {
        this.fingerDownListener.accept(false);
    }

    @Override
    public void render(PoseStack stack, float deltaTime, Vector2fc position, boolean interacting) {
        this.renderer.render(stack, deltaTime, this, interacting);
    }

}
