package dev.isxander.kanzicontrol.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BowAimUtils {
    public static final float CROSSBOW_POWER = 1f;

    public static Rotation aimCorrectlyAtTarget(Vec3 targetPos, Entity target) {
        Vec3 eyePos = Minecraft.getInstance().player.getEyePosition();
        Vec3 deltaPos = targetPos.subtract(eyePos);

        // naive predication to get the predicted travel time
        BowPredictionResult basePredication = predictBow(deltaPos);

        // predict how long it will take for the arrow to reach the target
        float realTravelTime = getTravelTime(
                basePredication.travelledOnX,
                Math.cos(Math.toRadians(basePredication.pitch)) * CROSSBOW_POWER * 3f * 0.7f
        );

        if (!Float.isNaN(realTravelTime)) {
            // use the predicted travel time to foresee where the target will be when the arrow reaches it
            deltaPos = deltaPos.add(
                    (target.getX() - target.xOld) * realTravelTime,
                    (target.getY() - target.yOld) * realTravelTime,
                    (target.getZ() - target.zOld) * realTravelTime
            );
        }

        // now we have a more accurate prediction of where the target will be when the arrow reaches it
        BowPredictionResult prediction = predictBow(deltaPos);

        // make sure the arrow will hit the target
        Vec3 vertex = getHighestPointOfTrajectory(deltaPos, prediction.yaw, prediction.pitch);
        List<Vec3> positions = new ArrayList<>();
        positions.add(eyePos);
        if (vertex != null)
            positions.add(vertex.add(eyePos));
        positions.add(targetPos);

        // cast from eye -> vertex -> target, if any of the blocks are solid then we can't shoot
        for (int i = 0; i < positions.size() - 2; i++) {
            BlockHitResult raycast = Minecraft.getInstance().level.clip(
                    new ClipContext(
                            positions.get(i),
                            positions.get(i + 1),
                            ClipContext.Block.OUTLINE,
                            ClipContext.Fluid.ANY,
                            Minecraft.getInstance().player
                    )
            );

            if (raycast.getType() == HitResult.Type.BLOCK)
                return null;
        }

        return new Rotation(prediction.yaw, prediction.pitch);
    }

    public static BowPredictionResult predictBow(Vec3 target) {
        double travelledOnX = Math.sqrt(target.x * target.x + target.z * target.z);
        float velocity = CROSSBOW_POWER;

        return new BowPredictionResult(
                Mth.wrapDegrees((float) (Mth.atan2(target.z, target.x) * Mth.RAD_TO_DEG - 90f)),
                Mth.wrapDegrees((float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006f * (0.006f * (travelledOnX * travelledOnX) + 2 * target.y * (velocity * velocity)))) / (0.006f * travelledOnX)))),
                travelledOnX
        );
    }

    public static float getTravelTime(double dist, double v0) {
        return (float) (Math.log((v0 / Math.log(0.99) + dist) / (v0 / Math.log(0.99))) / Math.log(0.99));
    }

    public static Vec3 getHighestPointOfTrajectory(Vec3 deltaPos, float yaw, float pitch) {
        float v0 = CROSSBOW_POWER * 3f * 0.7f;

        double v_x = v0 * Math.cos(Math.toRadians(pitch));
        double v_y = v0 * Math.sin(Math.toRadians(pitch));

        double maxX = -(v_x * v_y) / (2.0 * -0.006);
        double maxY = -(v_y * v_y) / (4.0 * -0.006);

        double xPitch = Math.cos(Math.toRadians(yaw - 90f));
        double zPitch = Math.sin(Math.toRadians(yaw - 90f));

        if (maxX < 0 && maxX * maxX < deltaPos.x * deltaPos.x + deltaPos.z * deltaPos.z) {
            return new Vec3(xPitch * maxX, maxY, zPitch * maxX);
        } else {
            return null;
        }
    }

    public record BowPredictionResult(float yaw, float pitch, double travelledOnX) {
    }

    public record Rotation(float yaw, float pitch) {
    }
}
