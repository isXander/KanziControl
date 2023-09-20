package dev.isxander.kanzicontrol.interactionarea.elements;

import com.mojang.math.Axis;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2fc;

public class IndicatorTextureArea implements InteractionArea {
    private Type currentType;

    private Animator.AnimationInstance currentAnimation;
    private float rotation, scale;

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (currentType == null) return;

        graphics.pose().pushPose();
        graphics.pose().translate(graphics.guiWidth() / 2f, graphics.guiHeight() / 2f, 0);
        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(rotation), 0f, 0f, 0f);
        graphics.pose().translate(-8f * scale * 10f, -8f * scale * 10f, 0f);
        graphics.pose().scale(scale * 10f, scale * 10f, 0f);

        graphics.blit(currentType.getTexture(), 0, 0, 0, 0, 0, 16, 16, 16, 16);

        graphics.pose().popPose();

    }

    public void indicate(Type type, int duration) {
        this.currentType = type;
        if (currentAnimation != null) {
            currentAnimation.finishFamily();
        }

        currentAnimation = Animator.INSTANCE.play(new Animator.AnimationInstance(10, Animator::easeOutSin)
                .addConsumer(f -> rotation = f, 0f, 360 * 2)
                .addConsumer(f -> scale = f, 0f, 1f))
                .andThen(new Animator.AnimationInstance(duration, t -> t)
                        .andThen(new Animator.AnimationInstance(5, Animator::easeOutExpo)
                                .addConsumer(f -> scale = f, 1f, 0f)
                                .onComplete(() -> currentType = null)));
    }

    @Override
    public boolean fingerDown(Vector2fc position) {
        return isInBounds(position);
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return currentType != null;
    }

    public enum Type {
        CHECK(new ResourceLocation("kanzicontrol", "textures/indications/check.png")),
        FAIL(new ResourceLocation("kanzicontrol", "textures/indications/fail.png")),
        GRAPE(new ResourceLocation("kanzicontrol", "textures/indications/grape.png")),
        PEANUT(new ResourceLocation("kanzicontrol", "textures/indications/peanut.png")),
        APPLE(new ResourceLocation("minecraft", "textures/item/apple.png"));

        private final ResourceLocation texture;

        Type(ResourceLocation texture) {
            this.texture = texture;
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }
}
