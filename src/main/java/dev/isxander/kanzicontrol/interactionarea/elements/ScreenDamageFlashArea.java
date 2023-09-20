package dev.isxander.kanzicontrol.interactionarea.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.utils.Animator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2fc;

public class ScreenDamageFlashArea implements InteractionArea {
    private static final ResourceLocation VIGNETTE_LOCATION
            = new ResourceLocation("kanzicontrol", "textures/misc/strong-vignette.png");

    private static final float MAX_ALPHA = 0.9f;
    private float damageFlashAlpha = 0f;

    private Animator.AnimationInstance currentAnimation;

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (damageFlashAlpha > 0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
            );

            graphics.setColor(0f, damageFlashAlpha, damageFlashAlpha, 1f);

            graphics.blit(VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.defaultBlendFunc();
        }
    }

    public void indicateDamage() {
        if (currentAnimation == null || currentAnimation.isThisDone()) {
            currentAnimation = createAnimation(3);
            Animator.INSTANCE.play(currentAnimation);
        }
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return false;
    }

    private Animator.AnimationInstance createAnimation(int flashCount) {
        Animator.AnimationInstance animation = new Animator.AnimationInstance(0, t -> t);
        Animator.AnimationInstance innerAnimation = animation;

        for (int i = 0; i < flashCount; i++) {
            innerAnimation.andThen(createFadeInAnimation()
                    .andThen(createPauseAnimation()
                            .andThen(innerAnimation = createFadeOutAnimation())));
        }

        return animation;
    }

    private Animator.AnimationInstance createFadeInAnimation() {
        return new Animator.AnimationInstance(5, t -> t)
                .addConsumer(t -> damageFlashAlpha = t, 0f, MAX_ALPHA);
    }

    private Animator.AnimationInstance createFadeOutAnimation() {
        return new Animator.AnimationInstance(5, t -> t)
                .addConsumer(t -> damageFlashAlpha = t, MAX_ALPHA, 0f);
    }

    private Animator.AnimationInstance createPauseAnimation() {
        return new Animator.AnimationInstance(2, t -> t);
    }
}
