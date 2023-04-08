package dev.isxander.kanzicontrol.interactionarea;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInteractionAreaContainer<T extends InteractionArea> implements InteractionAreaContainer<T> {
    private final List<T> areas = new ArrayList<>();
    private T currentInteractionArea = null;

    @Override
    public void setCurrentInteractionArea(T currentInteractionArea) {
        this.currentInteractionArea = currentInteractionArea;
    }

    @Nullable
    @Override
    public T getCurrentInteractionArea() {
        return currentInteractionArea;
    }

    @Override
    public List<T> getInteractionAreas() {
        return ImmutableList.copyOf(areas);
    }

    public <U extends T> U insertTop(U area) {
        areas.add(area);
        return area;
    }

    public <U extends T> U insertBottom(U area) {
        areas.add(0, area);
        return area;
    }

    public <U extends T> U insertAbove(U area, T above) {
        int index = areas.indexOf(above);
        if (index == -1)
            throw new IllegalArgumentException("InteractionArea " + above + " is not registered!");

        areas.add(index + 1, area);
        return area;
    }

    public <U extends T> U insertBelow(U area, T below) {
        int index = areas.indexOf(below);
        if (index == -1)
            throw new IllegalArgumentException("InteractionArea " + below + " is not registered!");

        areas.add(index, area);
        return area;
    }
}
