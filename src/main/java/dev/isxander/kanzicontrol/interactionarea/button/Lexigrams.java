package dev.isxander.kanzicontrol.interactionarea.button;

public final class Lexigrams {
    public static final LexigramRenderer
            FIGHT = renderer("fight"),
            JUMP = renderer("jump"),
            BREAK = renderer("break"),
            TOGGLE_SWIM_DOWN = renderer("swim_down"),
            USE = renderer("use");

    private static LexigramRenderer renderer(String lexigramId) {
        return new LexigramRenderer(lexigramId);
    }
}
