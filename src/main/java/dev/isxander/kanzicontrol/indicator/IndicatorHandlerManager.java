package dev.isxander.kanzicontrol.indicator;

import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import dev.isxander.kanzicontrol.interactionarea.elements.IndicatorTextureArea;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class IndicatorHandlerManager {
    private static final Map<ResourceLocation, IndicatorHandler> ALL = new HashMap<>();

    static {
        register("touch_up", duration -> {
            RootInteractionArea.getInstance().TOUCH_LOOK.indicateUp(duration);
            return true;
        });
        register("touch_down", duration -> {
            RootInteractionArea.getInstance().TOUCH_LOOK.indicateDown(duration);
            return true;
        });
        register("touch_left", duration -> {
            RootInteractionArea.getInstance().TOUCH_LOOK.indicateLeft(duration);
            return true;
        });
        register("touch_right", duration -> {
            RootInteractionArea.getInstance().TOUCH_LOOK.indicateRight(duration);
            return true;
        });
        register("damage", duration -> {
            RootInteractionArea.getInstance().SCREEN_DAMAGE_FLASH.indicateDamage();
            return true;
        });
        register("success", duration -> {
            RootInteractionArea.getInstance().INDICATOR_TEXTURE.indicate(IndicatorTextureArea.Type.CHECK, duration);
            return true;
        });
        register("fail", duration -> {
            RootInteractionArea.getInstance().INDICATOR_TEXTURE.indicate(IndicatorTextureArea.Type.FAIL, duration);
            return true;
        });
        register("grape", duration -> {
            RootInteractionArea.getInstance().INDICATOR_TEXTURE.indicate(IndicatorTextureArea.Type.GRAPE, duration);
            return true;
        });
        register("peanut", duration -> {
            RootInteractionArea.getInstance().INDICATOR_TEXTURE.indicate(IndicatorTextureArea.Type.PEANUT, duration);
            return true;
        });
        register("apple", duration -> {
            RootInteractionArea.getInstance().INDICATOR_TEXTURE.indicate(IndicatorTextureArea.Type.APPLE, duration);
            return true;
        });
    }

    private static void register(String type, IndicatorHandler handler) {
        ALL.put(new ResourceLocation("kanzicontrol", type), handler);
    }

    public static boolean handleIndicator(ResourceLocation indicatorType, int duration) {
        var handler = ALL.get(indicatorType);
        if (handler != null) {
            return handler.handleIndicator(duration);
        }
        return false;
    }
}
