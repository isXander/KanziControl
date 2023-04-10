package dev.isxander.kanzicontrol.interactionarea.reader;

import dev.isxander.kanzicontrol.KanziControl;
import dev.isxander.kanzicontrol.interactionarea.*;
import dev.isxander.kanzicontrol.interactionarea.button.*;
import org.quiltmc.json5.JsonReader;
import org.quiltmc.json5.JsonToken;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InteractionAreaReader {
    public static List<InteractionArea> readChildren(JsonReader reader) throws IOException {
        List<InteractionArea> children = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "row" -> children.add(readRow(reader));
                case "column" -> children.add(readColumn(reader));
                case "button" -> children.add(readButton(reader));
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return children;
    }

    public static List<PositionableElement> readPositionableChildren(JsonReader reader) throws IOException {
        List<InteractionArea> children = readChildren(reader);
        return children.stream()
                .filter(area -> {
                    var isPositionable = area instanceof PositionableElement;
                    if (!isPositionable) {
                        KanziControl.LOGGER.error("Whilst reading positionable children came across non-positionable child.");
                    }
                    return isPositionable;
                })
                .map(PositionableElement.class::cast)
                .toList();
    }

    public static RowInteractionArea readRow(JsonReader reader) throws IOException {
        List<PositionableElement> children = null;
        AnchorPoint windowAnchor = null;
        AnchorPoint origin = null;
        float x = 0, y = 0;
        float elementPadding = 0;
        RowInteractionArea.ElementPosition elementPosition = RowInteractionArea.ElementPosition.MIDDLE;
        float rowPaddingLeft = 0, rowPaddingRight = 0, rowPaddingTop = 0, rowPaddingBottom = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "window_anchor" -> windowAnchor = AnchorPoint.valueOf(reader.nextString());
                case "offset" -> {
                    reader.beginArray();
                    x = (float) reader.nextDouble();
                    y = (float) reader.nextDouble();
                    reader.endArray();
                }
                case "origin" -> origin = AnchorPoint.valueOf(reader.nextString());
                case "element_padding" -> elementPadding = (float) reader.nextDouble();
                case "row_padding" -> {
                    if (reader.peek() == JsonToken.NUMBER) {
                        float padding = (float) reader.nextDouble();
                        rowPaddingLeft = padding;
                        rowPaddingRight = padding;
                        rowPaddingTop = padding;
                        rowPaddingBottom = padding;
                    } else {
                        reader.beginArray();
                        rowPaddingLeft = (float) reader.nextDouble();
                        rowPaddingTop = (float) reader.nextDouble();
                        if (!reader.hasNext()) {
                            rowPaddingRight = rowPaddingLeft;
                            rowPaddingBottom = rowPaddingTop;
                        } else {
                            rowPaddingRight = (float) reader.nextDouble();
                            rowPaddingBottom = (float) reader.nextDouble();
                        }
                        reader.endArray();
                    }
                }
                case "element_position" -> elementPosition = RowInteractionArea.ElementPosition.valueOf(reader.nextString());
                case "children" -> children = readPositionableChildren(reader);
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return RowInteractionArea.builder()
                .elements(children)
                .elementPadding(elementPadding)
                .rowPadding(rowPaddingLeft, rowPaddingRight, rowPaddingTop, rowPaddingBottom)
                .elementPosition(elementPosition)
                .position(windowAnchor, x, y, origin)
                .build();
    }

    public static ColumnInteractionArea readColumn(JsonReader reader) throws IOException {
        List<PositionableElement> children = null;
        AnchorPoint windowAnchor = null;
        AnchorPoint origin = null;
        float x = 0, y = 0;
        float elementPadding = 0;
        ColumnInteractionArea.ElementPosition elementPosition = null;
        float columnPaddingLeft = 0, columnPaddingRight = 0, columnPaddingTop = 0, columnPaddingBottom = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "window_anchor" -> windowAnchor = AnchorPoint.valueOf(reader.nextString());
                case "offset" -> {
                    reader.beginArray();
                    x = (float) reader.nextDouble();
                    y = (float) reader.nextDouble();
                    reader.endArray();
                }
                case "origin" -> origin = AnchorPoint.valueOf(reader.nextString());
                case "element_padding" -> elementPadding = (float) reader.nextDouble();
                case "column_padding" -> {
                    if (reader.peek() == JsonToken.NUMBER) {
                        float padding = (float) reader.nextDouble();
                        columnPaddingLeft = padding;
                        columnPaddingRight = padding;
                        columnPaddingTop = padding;
                        columnPaddingBottom = padding;
                    } else {
                        reader.beginArray();
                        columnPaddingLeft = (float) reader.nextDouble();
                        columnPaddingTop = (float) reader.nextDouble();
                        if (!reader.hasNext()) {
                            columnPaddingRight = columnPaddingLeft;
                            columnPaddingBottom = columnPaddingTop;
                        } else {
                            columnPaddingRight = (float) reader.nextDouble();
                            columnPaddingBottom = (float) reader.nextDouble();
                        }
                        reader.endArray();
                    }
                }
                case "element_position" -> elementPosition = ColumnInteractionArea.ElementPosition.valueOf(reader.nextString());
                case "children" -> children = readPositionableChildren(reader);
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return ColumnInteractionArea.builder()
                .elements(children)
                .elementPadding(elementPadding)
                .colPadding(columnPaddingLeft, columnPaddingRight, columnPaddingTop, columnPaddingBottom)
                .elementPosition(elementPosition)
                .position(windowAnchor, x, y, origin)
                .build();
    }

    public static ButtonInteractionArea readButton(JsonReader reader) throws IOException {
        AnchorPoint windowAnchor = null;
        AnchorPoint origin = null;
        float x = 0, y = 0;
        float width = 0, height = 0;
        ButtonRenderer renderer = null;
        ButtonAction action = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "size" -> {
                    reader.beginArray();
                    width = (float) reader.nextDouble();
                    height = (float) reader.nextDouble();
                    reader.endArray();
                }
                case "renderer" -> renderer = readButtonRenderer(reader);
                case "action" -> action = ButtonActions.ACTIONS.get(reader.nextString());
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return new ButtonInteractionArea(renderer, width, height, action);
    }

    public static ButtonRenderer readButtonRenderer(JsonReader reader) throws IOException {
        ButtonRenderer renderer = null;

        reader.beginObject();

        if (reader.nextName().equals("type")) {
            switch (reader.nextString()) {
                case "lexigram" -> renderer = readLexigramInfo(reader);
                case "color" -> renderer = readColorInfo(reader);
                case "empty" -> renderer = EmptyRenderer.INSTANCE;
                default -> reader.skipValue();
            }
        } else {
            throw new IllegalStateException("First element in button renderer must be type");
        }


        reader.endObject();

        return renderer;
    }

    public static LexigramRenderer readLexigramInfo(JsonReader reader) throws IOException {
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "lexigram" -> {
                    return Lexigrams.ALL.get(reader.nextString());
                }
                default -> reader.skipValue();
            }
        }

        return null;
    }

    public static SolidColorRenderer readColorInfo(JsonReader reader) throws IOException {
        int color = 0;

        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "color" -> {
                    switch (reader.peek()) {
                        case BEGIN_ARRAY -> {
                            reader.beginArray();
                            int r = reader.nextInt();
                            int g = reader.nextInt();
                            int b = reader.nextInt();
                            int a = reader.hasNext() ? reader.nextInt() : 255;
                            color = (a << 24) | (r << 16) | (g << 8) | b;
                            reader.endArray();
                        }
                        case STRING -> {
                            Color awtColor = Color.decode(reader.nextString());
                            color = awtColor.getRGB();
                        }
                        case NUMBER -> {
                            color = reader.nextNumber().intValue();
                        }
                    }
                }
                default -> reader.skipValue();
            }
        }

        return new SolidColorRenderer(color);
    }
}
