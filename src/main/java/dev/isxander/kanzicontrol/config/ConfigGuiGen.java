package dev.isxander.kanzicontrol.config;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Function;

public class ConfigGuiGen {
    public static Screen generateConfigScreen(Screen parent) {
        return YetAnotherConfigLib.create(KanziConfig.INSTANCE, (def, cfg, builder) -> {
            Function<Integer, Component> degreesFormat = f -> Component.literal(f + "°");
            Function<Integer, Component> degreesPerSecondFormat = f -> Component.literal(f + "°/s");

            return builder
                    .title(Component.literal("Kanzi Control"))
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Touch Controls"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Looking"))
                                    .option(Option.createBuilder(int.class)
                                            .name(Component.literal("Degrees per tap"))
                                            .tooltip(Component.literal("How many degrees to turn when a bonobo taps the screen in a direction."))
                                            .binding(def.touchLookDegreesPerTap, () -> cfg.touchLookDegreesPerTap, v -> cfg.touchLookDegreesPerTap = v)
                                            .controller(opt -> new IntegerSliderController(opt, 5, 180, 5, degreesFormat))
                                            .build())
                                    .option(Option.createBuilder(int.class)
                                            .name(Component.literal("Looking speed"))
                                            .tooltip(Component.literal("How many degrees to turn per second to complete a tap."))
                                            .tooltip(Component.literal("For total time, divide degrees per tap by looking speed."))
                                            .binding(def.touchLookDegreesPerSecond, () -> cfg.touchLookDegreesPerSecond, v -> cfg.touchLookDegreesPerSecond = v)
                                            .controller(opt -> new IntegerSliderController(opt, 5, 360, 5, degreesPerSecondFormat))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Walking"))
                                    .option(Option.createBuilder(float.class)
                                            .name(Component.literal("Tap to walk radius"))
                                            .tooltip(Component.literal("The radius of the circle in the center of the screen in which a bonobo can tap to walk forward."))
                                            .tooltip(Component.literal("% is relative to the window's minimum dimension, width or height."))
                                            .binding(def.touchForwardRadius, () -> cfg.touchForwardRadius, v -> cfg.touchForwardRadius = v)
                                            .controller(opt -> new FloatSliderController(opt, 0.1f, 1f, 0.01f, f -> Component.literal((int)(f * 100) + "%")))
                                            .build())
                                    .option(Option.createBuilder(float.class)
                                            .name(Component.literal("Walk forward duration"))
                                            .tooltip(Component.literal("The amount of time to walk forward per tap."))
                                            .binding(def.walkForwardDuration, () -> cfg.walkForwardDuration, v -> cfg.walkForwardDuration = v)
                                            .controller(opt -> new FloatSliderController(opt, 0.1f, 5f, 0.1f, f -> Component.literal(String.format("%.1f", f) + " sec(s)")))
                                            .build())
                                    .option(Option.createBuilder(Color.class)
                                            .name(Component.literal("Forward overlay color"))
                                            .tooltip(Component.literal("The color of the overlay that appears when walking forward."))
                                            .binding(new Color(def.forwardOverlayColor, true), () -> new Color(cfg.forwardOverlayColor, true), v -> cfg.forwardOverlayColor = v.getRGB())
                                            .controller(opt -> new ColorController(opt, true))
                                            .build())
                                    .option(Option.createBuilder(boolean.class)
                                            .name(Component.literal("Disable forward button"))
                                            .tooltip(Component.literal("Completely disabled the forward button."))
                                            .binding(def.disableForwardOverlay, () -> cfg.disableForwardOverlay, v -> cfg.disableForwardOverlay = v)
                                            .controller(TickBoxController::new)
                                            .build())
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Rendering"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Block Overlay"))
                                    .option(Option.createBuilder(boolean.class)
                                            .name(Component.literal("Use custom block highlight"))
                                            .tooltip(Component.literal("Renders a full box over the top of the crosshair hovered block, to make it more clear of the block that a bonobo can interact with."))
                                            .binding(def.useEnhancedBlockHighlight, () -> cfg.useEnhancedBlockHighlight, v -> cfg.useEnhancedBlockHighlight = v)
                                            .controller(TickBoxController::new)
                                            .build())
                                    .option(Option.createBuilder(Color.class)
                                            .name(Component.literal("Block highlight color"))
                                            .tooltip(Component.literal("Color of the box if 'Use custom block highlight' option is enabled."))
                                            .binding(new Color(def.blockHighlightColor, true), () -> new Color(cfg.blockHighlightColor, true), v -> cfg.blockHighlightColor = v.getRGB())
                                            .controller(opt -> new ColorController(opt, true))
                                            .build())
                                    .option(Option.createBuilder(boolean.class)
                                            .name(Component.literal("Ignore depth test"))
                                            .tooltip(Component.literal("Renders the block highlight with no occlusion from other blocks or itself."))
                                            .binding(def.ignoreBlockHighlightDepth, () -> cfg.ignoreBlockHighlightDepth, v -> cfg.ignoreBlockHighlightDepth = v)
                                            .controller(opt -> new BooleanController(opt, BooleanController.YES_NO_FORMATTER, false))
                                            .build())
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Miscellaneous"))
                            .option(Option.createBuilder(boolean.class)
                                    .name(Component.literal("Mod enabled"))
                                    .binding(def.enabled, () -> cfg.enabled, v -> cfg.enabled = v)
                                    .controller(TickBoxController::new)
                                    .listener((opt, value) -> TouchInput.INSTANCE.setEnabled(value, Minecraft.getInstance().player))
                                    .build())
                            .option(Option.createBuilder(boolean.class)
                                    .name(Component.literal("Speech enabled"))
                                    .binding(def.speechEnabled, () -> cfg.speechEnabled, v -> cfg.speechEnabled = v)
                                    .controller(TickBoxController::new)
                                    .build())
                            .build());
        }).generateScreen(parent);
    }
}
