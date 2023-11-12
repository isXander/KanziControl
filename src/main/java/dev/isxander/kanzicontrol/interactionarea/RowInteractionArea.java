package dev.isxander.kanzicontrol.interactionarea;

import org.apache.commons.lang3.Validate;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RowInteractionArea extends AbstractInteractionAreaContainer<PositionableElement> implements PositionableElement {
    private final float elementPaddingHorizontal;
    private final float rowPaddingLeft, rowPaddingRight, rowPaddingTop, rowPaddingBottom;
    private final ElementPosition elementPosition;

    private final Vector2f position;
    private final Vector2f size;

    private RowInteractionArea(Collection<? extends PositionableElement> elements,
                               float elementPaddingHorizontal,
                               float rowPaddingLeft, float rowPaddingRight,
                               float rowPaddingTop, float rowPaddingBottom,
                               ElementPosition elementPosition,
                               float x, float y, AnchorPoint windowAnchor,
                               AnchorPoint origin
    ) {
        for (var element : elements) {
            insertTop(element);
        }

        this.elementPaddingHorizontal = elementPaddingHorizontal;
        this.rowPaddingLeft = rowPaddingLeft;
        this.rowPaddingRight = rowPaddingRight;
        this.rowPaddingTop = rowPaddingTop;
        this.rowPaddingBottom = rowPaddingBottom;
        this.elementPosition = elementPosition;

        float width = 0;
        float height = 0;
        for (var element : elements) {
            width += element.getSize().x() + elementPaddingHorizontal;
            height = Math.max(height, element.getSize().y());
        }
        width -= elementPaddingHorizontal;
        this.size = new Vector2f(width + rowPaddingLeft + rowPaddingRight, height + rowPaddingTop + rowPaddingBottom);
        this.position = new Vector2f();

        reposition(windowAnchor, x, y, origin);
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        // super = AbstractInteractionAreaContainer so equivalent to
        // InteractionAreaStorage.super.isInBounds(position)
        return super.isInBounds(position);
    }

    @Override
    public void setPosition(float x, float y) {
        float width = 0;
        for (var element : getInteractionAreas()) {
            element.setPosition(
                    x + rowPaddingLeft - rowPaddingRight + width,
                    y + rowPaddingTop - rowPaddingBottom + elementPosition.positionFunction.apply(getSize().y(), element.getSize().y())
            );

            width += element.getSize().x() + elementPaddingHorizontal;
        }

        this.position.set(x, y);
    }

    @Override
    public Vector2fc getPosition() {
        return this.position;
    }

    @Override
    public Vector2fc getSize() {
        return this.size;
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum ElementPosition {
        TOP((rowHeight, elementHeight) -> 0f),
        BOTTOM((rowHeight, elementHeight) -> rowHeight - elementHeight),
        MIDDLE((rowHeight, elementHeight) -> rowHeight / 2f - elementHeight / 2f);

        public final BiFunction<Float, Float, Float> positionFunction;

        ElementPosition(BiFunction<Float, Float, Float> positionFunction) {
            this.positionFunction = positionFunction;
        }
    }

    public static class Builder {
        private final List<PositionableElement> elements = new ArrayList<>();
        private float elementPaddingHorizontal;
        private float rowPaddingLeft, rowPaddingRight, rowPaddingTop, rowPaddingBottom;
        private ElementPosition elementPosition = null;

        private float x, y;
        private AnchorPoint windowAnchor = AnchorPoint.TOP_LEFT;
        private AnchorPoint origin = AnchorPoint.TOP_LEFT;

        public Builder element(PositionableElement... elements) {
            this.elements.addAll(Arrays.asList(elements));
            return this;
        }

        public Builder elementIf(boolean condition, Supplier<PositionableElement> elementSupplier) {
            if (condition) {
                this.elements.add(elementSupplier.get());
            }
            return this;
        }

        public Builder elements(Collection<? extends PositionableElement> elements) {
            this.elements.addAll(elements);
            return this;
        }

        public Builder elementPadding(float padding) {
            this.elementPaddingHorizontal = padding;
            return this;
        }

        public Builder rowPadding(float left, float right, float top, float bottom) {
            this.rowPaddingLeft = left;
            this.rowPaddingRight = right;
            this.rowPaddingTop = top;
            this.rowPaddingBottom = bottom;
            return this;
        }

        public Builder rowPadding(float horizontal, float vertical) {
            return rowPadding(horizontal, horizontal, vertical, vertical);
        }

        public Builder rowPadding(float padding) {
            return rowPadding(padding, padding, padding, padding);
        }

        public Builder elementPosition(ElementPosition elementPosition) {
            this.elementPosition = elementPosition;
            return this;
        }

        public Builder position(AnchorPoint windowAnchor, float x, float y, AnchorPoint origin) {
            this.x = x;
            this.y = y;
            this.windowAnchor = windowAnchor;
            this.origin = origin;
            return this;
        }

        public RowInteractionArea build() {
            Validate.notEmpty(elements, "No elements were added to the row!");
            Validate.notNull(elementPosition, "Element position cannot be null!");

            return new RowInteractionArea(
                    elements,
                    elementPaddingHorizontal,
                    rowPaddingLeft, rowPaddingRight,
                    rowPaddingTop, rowPaddingBottom,
                    elementPosition,
                    x, y,
                    windowAnchor, origin
            );
        }
    }
}
