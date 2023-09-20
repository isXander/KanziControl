package dev.isxander.kanzicontrol.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.ValueFormatters;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class KanziConfig {
    public static final ConfigClassHandler<KanziConfig> INSTANCE =
            ConfigClassHandler.createBuilder(KanziConfig.class)
                    .id(new ResourceLocation("kanzicontrol", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("kanzicontrol.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    private static final String TOUCH_CONTROLS = "touch_controls";
    private static final String LOOKING = "looking";
    private static final String WALKING = "walking";
    private static final String RENDERING = "rendering";
    private static final String BLOCK_OVERLAY = "block_overlay";
    private static final String MISC = "misc";

    private static final String FMT_DEGREES = "kanzicontrol.fmt.degrees";
    private static final String FMT_DEGREES_PER_SECOND = "kanzicontrol.fmt.degrees_per_second";
    private static final String FMT_SECONDS = "kanzicontrol.fmt.seconds";

    @AutoGen(category = TOUCH_CONTROLS, group = LOOKING)
    @IntSlider(min = 5, max = 180, step = 5)
    @FormatTranslation(FMT_DEGREES)
    @SerialEntry
    public int touchLookDegreesPerTap = 30;

    @AutoGen(category = TOUCH_CONTROLS, group = LOOKING)
    @IntSlider(min = 5, max = 360, step = 5)
    @FormatTranslation(FMT_DEGREES_PER_SECOND)
    @SerialEntry
    public int touchLookDegreesPerSecond = 90;

    @AutoGen(category = TOUCH_CONTROLS, group = LOOKING)
    @FloatSlider(min = 0.5f, max = 10f, step = 0.5f)
    @FormatTranslation(FMT_SECONDS)
    @SerialEntry
    public float verticalResetDelay = 3f;

    @AutoGen(category = TOUCH_CONTROLS, group = LOOKING)
    @IntSlider(min = 0, max = 90, step = 5)
    @FormatTranslation(FMT_DEGREES)
    @SerialEntry
    public int maxMinVerticalDegrees = 60;


    @AutoGen(category = TOUCH_CONTROLS, group = WALKING)
    @FloatSlider(min = 0.1f, max = 1f, step = 0.1f)
    @CustomFormat(ValueFormatters.PercentFormatter.class)
    @SerialEntry
    public float touchForwardRadius = 0.33f;

    @AutoGen(category = TOUCH_CONTROLS, group = WALKING)
    @FloatSlider(min = 0.1f, max = 5f, step = 0.1f)
    @FormatTranslation(FMT_SECONDS)
    @SerialEntry
    public float walkForwardDuration = 1f;

    @AutoGen(category = RENDERING, group = BLOCK_OVERLAY)
    @TickBox
    @SerialEntry
    public boolean useEnhancedBlockHighlight = true;

    @AutoGen(category = RENDERING, group = BLOCK_OVERLAY)
    @ColorField(allowAlpha = true)
    @SerialEntry
    public Color blockHighlightColor = new Color(0x66FFFFFF, true);

    @AutoGen(category = RENDERING, group = BLOCK_OVERLAY)
    @TickBox
    @SerialEntry
    public boolean ignoreBlockHighlightDepth = false;

    @SerialEntry
    public boolean showCursor = true;

    @AutoGen(category = MISC)
    @MasterModSwitch
    @SerialEntry(comment = "Enable/disable the whole mod.")
    public boolean enabled = true;

    @AutoGen(category = MISC)
    @TickBox
    @SerialEntry
    public boolean speechEnabled = true;
}
