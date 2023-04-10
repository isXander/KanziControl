package dev.isxander.kanzicontrol.interactionarea.button;

@FunctionalInterface
public interface ButtonAction {
    void onFingerStateChange(boolean fingerDown);

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
