package dev.isxander.kanzicontrol.interactionarea.button;

import java.util.HashMap;
import java.util.Map;

public final class Lexigrams {
    public static final Map<String, LexigramRenderer> ALL = new HashMap<>();

    public static final LexigramRenderer
            FIGHT = renderer("fight"),
            JUMP = renderer("jump"),
            BREAK = renderer("break"),
            TOGGLE_SWIM_DOWN = renderer("swim_down"),
            USE = renderer("use");

    private static LexigramRenderer renderer(String lexigramId) {
        LexigramRenderer renderer = new LexigramRenderer(lexigramId);
        ALL.put(lexigramId, renderer);
        return renderer;
    }
}
