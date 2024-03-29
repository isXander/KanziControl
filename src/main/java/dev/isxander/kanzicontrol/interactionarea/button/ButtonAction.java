package dev.isxander.kanzicontrol.interactionarea.button;

import com.mojang.text2speech.Narrator;
import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.mixins.GameNarratorAccessor;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

@FunctionalInterface
public interface ButtonAction {
    void onFingerStateChange(boolean fingerDown);

    default ButtonAction cooldown(int cooldown) {
        return new ButtonAction() {
            private long lastTime = 0;

            @Override
            public void onFingerStateChange(boolean fingerDown) {
                if (System.currentTimeMillis() - lastTime > cooldown) {
                    lastTime = System.currentTimeMillis();
                    ButtonAction.this.onFingerStateChange(fingerDown);
                }
            }
        };
    }

    default ButtonAction withNarration(Supplier<String> message) {
        return (fingerDown) -> {
            this.onFingerStateChange(fingerDown);
            if (fingerDown && KanziConfig.INSTANCE.instance().speechEnabled) {
                Narrator narrator = ((GameNarratorAccessor) Minecraft.getInstance().getNarrator()).getNarrator();
                if (narrator.active()) {
                    narrator.say(message.get(), true);
                }
            }
        };
    }

    default ButtonAction withNarration(String message) {
        return withNarration(() -> message);
    }

    static ButtonAction none() {
        return (fingerDown) -> {};
    }

    static ButtonAction down(Runnable action) {
        return (fingerDown) -> {
            if (fingerDown) action.run();
        };
    }

    static ButtonAction up(Runnable action) {
        return (fingerDown) -> {
            if (!fingerDown) action.run();
        };
    }
}
