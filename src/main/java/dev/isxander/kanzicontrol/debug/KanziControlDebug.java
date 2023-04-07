package dev.isxander.kanzicontrol.debug;

import net.fabricmc.loader.api.FabricLoader;

public class KanziControlDebug {
    public static final boolean DEBUG_MOUSE_POSITION = prop("debug.mousePosition", false, true);

    private static boolean prop(String name, boolean def, boolean defDevenv) {
        boolean defThisEnv = FabricLoader.getInstance().isDevelopmentEnvironment() ? defDevenv : def;
        return Boolean.parseBoolean(System.getProperty("kanzi." + name, Boolean.toString(def)));
    }
}
