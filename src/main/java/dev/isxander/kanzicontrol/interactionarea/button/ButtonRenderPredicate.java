package dev.isxander.kanzicontrol.interactionarea.button;

import net.minecraft.world.phys.HitResult;

import java.util.function.Predicate;

@FunctionalInterface
public interface ButtonRenderPredicate extends Predicate<ButtonRenderPredicate.RenderCtx> {
    record RenderCtx(HitResult hitResult, boolean isHovered) {}
}
