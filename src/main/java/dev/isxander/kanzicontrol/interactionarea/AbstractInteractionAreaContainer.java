package dev.isxander.kanzicontrol.interactionarea;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInteractionAreaContainer implements InteractionAreaContainer {
    private final List<InteractionArea> areas = new ArrayList<>();
    private InteractionArea currentInteractionArea = InteractionArea.EMPTY;

    @Override
    public void setCurrentInteractionArea(InteractionArea currentInteractionArea) {
        this.currentInteractionArea = currentInteractionArea;
    }

    @NotNull
    @Override
    public InteractionArea getCurrentInteractionArea() {
        return currentInteractionArea;
    }

    @Override
    public List<InteractionArea> getInteractionAreas() {
        return ImmutableList.copyOf(areas);
    }

    public <T extends InteractionArea> T insertTop(T area) {
        areas.add(area);
        return area;
    }

    public <T extends InteractionArea> T insertBottom(T area) {
        areas.add(0, area);
        return area;
    }

    public <T extends InteractionArea> T insertAbove(T area, InteractionArea above) {
        int index = areas.indexOf(above);
        if (index == -1)
            throw new IllegalArgumentException("InteractionArea " + above + " is not registered!");

        areas.add(index + 1, area);
        return area;
    }

    public <T extends InteractionArea> T insertBelow(T area, InteractionArea below) {
        int index = areas.indexOf(below);
        if (index == -1)
            throw new IllegalArgumentException("InteractionArea " + below + " is not registered!");

        areas.add(index, area);
        return area;
    }
}
