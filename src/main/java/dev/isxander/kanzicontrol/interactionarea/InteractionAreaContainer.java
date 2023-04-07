package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

import java.util.List;

public interface InteractionAreaContainer extends InteractionArea {
    List<InteractionArea> getInteractionAreas();
    void setCurrentInteractionArea(InteractionArea area);
    @NotNull InteractionArea getCurrentInteractionArea();

    default void fingerDown(Vector2fc position) {
        InteractionArea area = InteractionArea.EMPTY;

        var iterator = getInteractionAreas().listIterator(getInteractionAreas().size());
        while (iterator.hasPrevious()) {
            var thisArea = iterator.previous();
            if (thisArea.isInBounds(position)) {
                area = thisArea;
            }
        }

        setCurrentInteractionArea(area);
        area.fingerDown(position);
    }

    @Override
    default void fingerMove(Vector2fc position) {
        getCurrentInteractionArea().fingerMove(position);
    }

    default void fingerUp(Vector2fc position) {
        getCurrentInteractionArea().fingerUp(position);
        setCurrentInteractionArea(null);
    }

    @Override
    default void render(PoseStack stack, float deltaTime, Vector2fc position) {
        for (InteractionArea area : getInteractionAreas()) {
            area.render(stack, deltaTime, position);
        }
    }

    @Override
    default void tick(Vector2fc position, boolean interacting) {
        for (InteractionArea area : getInteractionAreas()) {
            area.tick(position, area == getCurrentInteractionArea());
        }
    }
}
