package twilightforest.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import twilightforest.entity.boss.EntityTFSnowQueen;

final class SnowQueenHoverHelper {

    private SnowQueenHoverHelper() {}

    static Vec3 findHoverPosition(EntityTFSnowQueen queen, EntityLivingBase target, double height, double radius,
            int attempts) {
        for (int i = 0; i < attempts; ++i) {
            double x = target.posX + (queen.getRNG().nextDouble() - queen.getRNG().nextDouble()) * radius;
            double y = target.posY + height;
            double z = target.posZ + (queen.getRNG().nextDouble() - queen.getRNG().nextDouble()) * radius;
            if (isPositionClear(queen, x, y, z) && canSee(queen, x, y, z) && canSee(target, x, y, z)) {
                return Vec3.createVectorHelper(x, y, z);
            }
        }
        return null;
    }

    static boolean isPositionClear(EntityTFSnowQueen queen, double x, double y, double z) {
        double radius = queen.width * 0.5D;
        AxisAlignedBB box = AxisAlignedBB
                .getBoundingBox(x - radius, y, z - radius, x + radius, y + queen.height, z + radius);
        // an empty collision list means the hover position is free, not occupied
        return queen.worldObj.getCollidingBoundingBoxes(queen, box).isEmpty();
    }

    static boolean canSee(Entity entity, double x, double y, double z) {
        return entity.worldObj.rayTraceBlocks(
                Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ),
                Vec3.createVectorHelper(x, y, z)) == null;
    }

    static void steerToward(EntityTFSnowQueen queen, double x, double y, double z, double verticalBias) {
        double dx = x - queen.posX;
        double dy = y - queen.posY;
        double dz = z - queen.posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if (distanceSq < 0.0001D) {
            // brake on arrival, never divide by zero or accelerate indefinitely
            queen.addVelocity(-queen.motionX * 0.12D, verticalBias - queen.motionY * 0.12D, -queen.motionZ * 0.12D);
            return;
        }
        double distance = Math.sqrt(distanceSq);
        double desiredX = dx / distance * 0.2D;
        double desiredY = dy / distance * 0.2D;
        double desiredZ = dz / distance * 0.2D;
        queen.addVelocity(
                MathHelper.clamp_double((desiredX - queen.motionX) * 0.15D, -0.05D, 0.05D),
                MathHelper.clamp_double((desiredY - queen.motionY) * 0.15D + verticalBias, -0.07D, 0.07D),
                MathHelper.clamp_double((desiredZ - queen.motionZ) * 0.15D, -0.05D, 0.05D));
    }

    static Entity findFirstLookTarget(EntityTFSnowQueen queen, double range) {
        Vec3 origin = Vec3.createVectorHelper(queen.posX, queen.posY + queen.getEyeHeight(), queen.posZ);
        Vec3 look = queen.getLook(1.0F);
        Vec3 end = origin.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        MovingObjectPosition wall = queen.worldObj.rayTraceBlocks(origin, end);
        double nearestDistance = wall == null ? range : origin.distanceTo(wall.hitVec);
        Entity nearest = null;
        for (Entity candidate : queen.worldObj.getEntitiesWithinAABBExcludingEntity(
                queen,
                queen.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range)
                        .expand(1.0D, 1.0D, 1.0D))) {
            if (!(candidate instanceof EntityLivingBase) || !candidate.isEntityAlive()
                    || !candidate.canBeCollidedWith()) {
                continue;
            }
            double border = candidate.getCollisionBorderSize();
            AxisAlignedBB hitBox = candidate.boundingBox.expand(border, border, border);
            if (hitBox.isVecInside(origin)) {
                return candidate;
            }
            MovingObjectPosition hit = hitBox.calculateIntercept(origin, end);
            if (hit != null) {
                double distance = origin.distanceTo(hit.hitVec);
                if (distance <= nearestDistance) {
                    nearest = candidate;
                    nearestDistance = distance;
                }
            }
        }
        return nearest;
    }
}
