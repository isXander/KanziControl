package dev.isxander.kanzicontrol.interactionarea;

import org.joml.Vector2fc;

public interface PositionableElement extends InteractionArea {
    void setPosition(float x, float y);

    Vector2fc getPosition();

    Vector2fc getSize();

    @Override
    default boolean isInBounds(Vector2fc position) {
        return position.x() >= getPosition().x() && position.x() <= getPosition().x() + getSize().x()
                && position.y() >= getPosition().y() && position.y() <= getPosition().y() + getSize().y();
    }

    default PositionableElement reposition(AnchorPoint windowAnchor, float x, float y, AnchorPoint origin) {
        Vector2fc anchorPosition = windowAnchor.getAnchorPosition(windowSize());
        Vector2fc size = getSize();

        setPosition(
                anchorPosition.x() - (x + size.x() * origin.anchorX),
                anchorPosition.y() - (y + size.y() * origin.anchorY)
        );

        return this;
    }

    record PositionData(AnchorPoint windowAnchor, float x, float y, AnchorPoint origin) {
    }
}
