package dev.isxander.kanzicontrol.blockhighlight;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.isxander.kanzicontrol.config.KanziConfig;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class BlockOutlineRenderType extends RenderType {
    public static final RenderType OUTLINE = RenderType.create(
            "kanzicontrol_outline",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setLineState(new CustomWidthLineState())
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(true)
    );

    private BlockOutlineRenderType(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
        throw new UnsupportedOperationException();
    }

    private static class CustomWidthLineState extends LineStateShard {
        public CustomWidthLineState() {
            super(OptionalDouble.empty());
        }

        @Override
        public void setupRenderState() {
            RenderSystem.lineWidth(KanziConfig.INSTANCE.getConfig().blockOutlineWidth);
        }

        @Override
        public void clearRenderState() {
            RenderSystem.lineWidth(1f);
        }
    }
}
