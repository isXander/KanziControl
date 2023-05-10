package dev.isxander.kanzicontrol.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class KanziConfig {
    public static final ConfigInstance<KanziConfig> INSTANCE =
            GsonConfigInstance.createBuilder(KanziConfig.class)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("kanzicontrol.json"))
                    .overrideGsonBuilder(new GsonBuilder().setPrettyPrinting())
                    .build();

    @ConfigEntry public boolean enabled = true;

    @ConfigEntry public int touchLookDegreesPerTap = 30;
    @ConfigEntry public int touchLookDegreesPerSecond = 90;
    @ConfigEntry public float verticalResetDelay = 3f;
    @ConfigEntry public int maxMinVerticalDegrees = 60;

    @ConfigEntry public float touchForwardRadius = 0.33f;
    @ConfigEntry public float walkForwardDuration = 1f;
    @ConfigEntry public int forwardOverlayColor = 0x50FFFFFF;
    @ConfigEntry public boolean disableForwardOverlay = false;

    @ConfigEntry public boolean useEnhancedBlockHighlight = true;
    @ConfigEntry public int blockHighlightColor = 0x66FFFFFF;
    @ConfigEntry public boolean ignoreBlockHighlightDepth = false;
    @ConfigEntry public boolean showCursor = true;

    @ConfigEntry public Map<String, Boolean> enabledButtons = new LinkedHashMap<>();

    @ConfigEntry public boolean speechEnabled = true;
}
