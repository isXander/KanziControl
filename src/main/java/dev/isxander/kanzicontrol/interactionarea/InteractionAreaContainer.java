package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2fc;

import java.util.List;

public interface InteractionAreaContainer<T extends InteractionArea> extends InteractionArea {
    List<T> getInteractionAreas();
    void setCurrentInteractionArea(T area);
    @Nullable T getCurrentInteractionArea();

    default void fingerDown(Vector2fc position) {
        T touchedArea = null;

        // find topmost area that is over mouse
        var iterator = getInteractionAreas().listIterator(getInteractionAreas().size());
        while (iterator.hasPrevious()) {
            var area = iterator.previous();
            if (area.isInBounds(position)) {
                touchedArea = area;
                break;
            }
        }

        setCurrentInteractionArea(touchedArea);
        if (touchedArea != null)
            touchedArea.fingerDown(position);
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
    default void render(PoseStack stack, float deltaTime, Vector2fc position, boolean interacting) {
        for (InteractionArea area : getInteractionAreas()) {
            area.render(stack, deltaTime, position, area == getCurrentInteractionArea());
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
