package dev.isxander.kanzicontrol.interactionarea.elements;

import dev.isxander.kanzicontrol.TouchInput;
import dev.isxander.kanzicontrol.entityhandler.AutomatedPlayerTask;
import dev.isxander.kanzicontrol.entityhandler.EntityClickHandlerManager;
import dev.isxander.kanzicontrol.interactionarea.InteractionArea;
import dev.isxander.kanzicontrol.interactionarea.RootInteractionArea;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.joml.Vector2fc;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class TouchEntity implements InteractionArea {
    private AutomatedPlayerTask currentHandler = null;
    private final Queue<AutomatedPlayerTask> handlerQueue = new ArrayDeque<>();

    public void queueHandler(AutomatedPlayerTask handler) {
        handlerQueue.add(handler);
    }

    @Override
    public boolean fingerDown(Vector2fc position) {
        if (currentHandler != null)
            return true;

        HitResult hitResult = getHitResultFromScreen(position);

        if (hitResult.getType() != HitResult.Type.ENTITY)
            return false;

        EntityHitResult entityHit = (EntityHitResult) hitResult;
        Optional<AutomatedPlayerTask> handler = EntityClickHandlerManager.create(entityHit.getEntity());

        if (handler.isEmpty())
            return false;

        queueHandler(handler.get());

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f));

        return true;
    }

    @Override
    public void tick(Vector2fc position, boolean interacting) {
        if (currentHandler == null) {
            currentHandler = handlerQueue.poll();
            if (currentHandler != null) {
                TouchInput.INSTANCE.setForward(0);
                currentHandler.start();
            }
        }

        if (currentHandler != null) {
            currentHandler.tick();
        }
    }

    @Override
    public void render(GuiGraphics graphics, float deltaTime, Vector2fc position, boolean interacting) {
        if (currentHandler != null) {
            if (currentHandler.shouldFinish()) {
                currentHandler.finish();
                currentHandler = null;
            } else {
                currentHandler.onFrame(deltaTime);
            }
        }
    }

    public boolean isCurrentlyHandling() {
        return currentHandler != null;
    }

    private HitResult getHitResultFromScreen(Vector2fc position) {
        Vec3 near = RendererUtils.screenSpaceToWorldSpace(position.x(), position.y(), 0);
        Vec3 far = RendererUtils.screenSpaceToWorldSpace(position.x(), position.y(), 1);

        AABB aABB = new AABB(near, far).inflate(1.0);

        HitResult result = ProjectileUtil.getEntityHitResult(
                minecraft().level,
                minecraft().player,
                near, far,
                aABB,
                entity -> !entity.isSpectator(),
                0f
        );
        BlockHitResult blockHitResult = minecraft().level.clip(
                new ClipContext(
                        near, far,
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        minecraft().player
                )
        );
        if (result == null) {
            result = blockHitResult;
        } else {
            if (near.distanceToSqr(blockHitResult.getLocation()) < near.distanceToSqr(result.getLocation())) {
                result = blockHitResult;
            }
        }

        return result;
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }
}
