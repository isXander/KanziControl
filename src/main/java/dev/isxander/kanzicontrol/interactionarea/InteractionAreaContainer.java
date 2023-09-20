package dev.isxander.kanzicontrol.interactionarea;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2fc;

import java.util.List;

public interface InteractionAreaContainer<T extends InteractionArea> extends InteractionArea {
    List<T> getInteractionAreas();
    void setCurrentInteractionArea(T area);
    @Nullable T getCurrentInteractionArea();

    default boolean fingerDown(Vector2fc position) {
        List<T> candidates = getInteractionAreas().stream()
                .filter(area -> area.isInBounds(position))
                .toList();

        // find topmost area that is over mouse
        var iterator = candidates.listIterator(candidates.size());
        while (iterator.hasPrevious()) {
            var area = iterator.previous();
            if (area.fingerDown(position)) {
                setCurrentInteractionArea(area);
                return true;
            }
        }

        return false;
    }

    @Override
    default void fingerMove(Vector2fc position) {
        var currentArea = getCurrentInteractionArea();
        if (currentArea != null) {
            currentArea.fingerMove(position);
        }
    }

    default void fingerUp(Vector2fc position) {
        if (getCurrentInteractionArea() != null) {
            getCurrentInteractionArea().fingerUp(position);
            setCurrentInteractionArea(null);
        }
    }

    @Override
    default void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        for (InteractionArea area : getInteractionAreas()) {
            area.render(graphics, deltaTime, position, area == getCurrentInteractionArea());
        }
    }

    @Override
    default void tick(Vector2fc position, boolean interacting) {
        for (InteractionArea area : getInteractionAreas()) {
            area.tick(position, area == getCurrentInteractionArea());
        }
    }

    @Override
    default boolean isInBounds(Vector2fc position) {
        for (InteractionArea area : getInteractionAreas()) {
            if (area.isInBounds(position)) return true;
        }
        return false;
    }
}
