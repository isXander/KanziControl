package dev.isxander.kanzicontrol.interactionarea.button;

public final class Lexigrams {
    public static final LexigramRenderer
            PUNCH = renderer("punch"),
            JUMP = renderer("jump"),
            SNEAK = renderer("sneak");

    private static LexigramRenderer renderer(String lexigramId) {
        return new LexigramRenderer(lexigramId);
    }
}
