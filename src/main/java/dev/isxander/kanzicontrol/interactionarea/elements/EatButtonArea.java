package dev.isxander.kanzicontrol.interactionarea.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.kanzicontrol.interactionarea.button.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.food.FoodData;
import org.joml.Vector2fc;

public class EatButtonArea extends ButtonInteractionArea {
    private float eatUrgency;

    private float time = 0f;

    public EatButtonArea(float width, float height) {
        super(Lexigrams.EAT, width, height, ButtonActions.EAT, ButtonRenderPredicates.ALWAYS);
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        super.render(graphics, deltaTime, position, interacting);

        if (eatUrgency > 0f && canRender) {
            float alpha = this.time % 20f;
            if (alpha > 10f) alpha = 20f - alpha;
            alpha /= 10f;

            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
            Lexigrams.EAT_HIGHLIGHTED.render(graphics, deltaTime, this, interacting);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            this.time += eatUrgency * deltaTime;
        } else {
            this.time = 0f;
        }

//        if (eatUrgency > 0f && canRender) {
//            int flashTime = (int) ((1f - eatUrgency + 0.2f) * 40);
//            int interval = ticks % flashTime * 5;
//            if (interval < flashTime || (interval > flashTime * 3 && interval < flashTime * 4)) {
//                Lexigrams.EAT_HIGHLIGHTED.render(graphics, deltaTime, this, interacting);
//            } else {
//                super.render(graphics, deltaTime, position, interacting);
//            }
//        } else {
//            super.render(graphics, deltaTime, position, interacting);
//        }
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        updateEatUrgency();

//       ticks++;
    }

    private void updateEatUrgency() {
        LocalPlayer player = minecraft().player;
        if (player == null) return;

        FoodData food = player.getFoodData();

        if (!food.needsFood()) {
            eatUrgency = 0;
            return;
        }

        float healthPercent = player.getHealth() / player.getMaxHealth();
        float hungerPercent = (food.getFoodLevel() - 6f) / 14f;

        float hungerUrgency = 1f - hungerPercent;
        float healthUrgency = 1f - healthPercent;

        eatUrgency = Math.max(hungerUrgency, healthUrgency);
    }
}
