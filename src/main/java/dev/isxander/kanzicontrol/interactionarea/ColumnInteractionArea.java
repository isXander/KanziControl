package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import org.apache.commons.lang3.Validate;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class ColumnInteractionArea extends AbstractInteractionAreaContainer<PositionableElement> implements PositionableElement {
    private final float elementPaddingVertical;
    private final float colPaddingLeft, colPaddingRight, colPaddingTop, colPaddingBottom;
    private final ElementPosition elementPosition;

    private final Vector2f position;
    private final Vector2f size;

    private ColumnInteractionArea(Collection<? extends PositionableElement> elements,
                                 float elementPaddingVertical,
                                 float colPaddingLeft, float colPaddingRight,
                                 float colPaddingTop, float colPaddingBottom,
                                 ElementPosition elementPosition,
                                 float x, float y, AnchorPoint windowAnchor,
                                 AnchorPoint origin
    ) {
        for (var element : elements) {
            insertTop(element);
        }

        this.elementPaddingVertical = elementPaddingVertical;
        this.colPaddingLeft = colPaddingLeft;
        this.colPaddingRight = colPaddingRight;
        this.colPaddingTop = colPaddingTop;
        this.colPaddingBottom = colPaddingBottom;
        this.elementPosition = elementPosition;

        float width = 0;
        float height = 0;
        for (var element : elements) {
            width = Math.max(width, element.getSize().x());
            height += element.getSize().y() + elementPaddingVertical;
        }
        height -= elementPaddingVertical;

        this.size = new Vector2f(width + colPaddingLeft + colPaddingRight, height + colPaddingTop + colPaddingBottom);
        this.position = new Vector2f();

        reposition(windowAnchor, x, y, origin);
    }

    @Override
    public void setPosition(float x, float y) {
        float height = 0;
        for (var element : getInteractionAreas()) {
            element.setPosition(
                    x + colPaddingLeft - colPaddingRight + elementPosition.positionFunction.apply(getSize().x(), element.getSize().x()),
                    y + colPaddingTop - colPaddingBottom + height
            );

            height += element.getSize().y() + elementPaddingVertical;
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
        TOP((rowWidth, elementWidth) -> 0f),
        BOTTOM((rowWidth, elementWidth) -> rowWidth - elementWidth),
        MIDDLE((rowWidth, elementWidth) -> rowWidth / 2f - elementWidth / 2f);

        public final BiFunction<Float, Float, Float> positionFunction;

        ElementPosition(BiFunction<Float, Float, Float> positionFunction) {
            this.positionFunction = positionFunction;
        }
    }

    public static class Builder {
        private final List<PositionableElement> elements = new ArrayList<>();
        private float elementPaddingVertical;
        private float colPaddingLeft, colPaddingRight, colPaddingTop, colPaddingBottom;
        private ElementPosition elementPosition = null;

        private float x, y;
        private AnchorPoint windowAnchor = AnchorPoint.TOP_LEFT;
        private AnchorPoint origin = AnchorPoint.TOP_LEFT;

        public Builder element(PositionableElement element) {
            elements.add(element);
            return this;
        }

        public Builder elements(PositionableElement... elements) {
            this.elements.addAll(Arrays.asList(elements));
            return this;
        }

        public Builder elementPadding(float padding) {
            this.elementPaddingVertical = padding;
            return this;
        }

        public Builder colPadding(float left, float right, float top, float bottom) {
            this.colPaddingLeft = left;
            this.colPaddingRight = right;
            this.colPaddingTop = top;
            this.colPaddingBottom = bottom;
            return this;
        }

        public Builder colPadding(float horizontal, float vertical) {
            return colPadding(horizontal, horizontal, vertical, vertical);
        }

        public Builder colPadding(float padding) {
            return colPadding(padding, padding, padding, padding);
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

        public ColumnInteractionArea build() {
            Validate.notEmpty(elements, "No elements were added to the row!");
            Validate.notNull(elementPosition, "Element position cannot be null!");

            return new ColumnInteractionArea(
                    elements,
                    elementPaddingVertical,
                    colPaddingLeft, colPaddingRight,
                    colPaddingTop, colPaddingBottom,
                    elementPosition,
                    x, y,
                    windowAnchor, origin
            );
        }
    }
}
