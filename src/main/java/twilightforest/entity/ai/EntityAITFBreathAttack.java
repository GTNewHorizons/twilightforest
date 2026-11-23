package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import twilightforest.entity.IBreathAttacker;

public class EntityAITFBreathAttack extends EntityAIBase {

    private final EntityLiving entityHost;
    private EntityLivingBase attackTarget;

    protected double breathX;
    protected double breathY;
    protected double breathZ;

    private final int maxDuration;
    private final float attackChance;
    private final float breathRange;

    private int durationLeft;
    private int recoveryTicks;

    public EntityAITFBreathAttack(EntityLiving par1EntityLiving, float speed, float range, int time, float chance) {
        this.entityHost = par1EntityLiving;
        this.breathRange = Math.max(0.1F, range);
        this.maxDuration = Math.max(1, time);
        this.attackChance = Math.max(0.0F, Math.min(1.0F, chance));
        this.setMutexBits(7);
    }

    /**
     * Breathe when we are within range and line of sight of the target
     */
    @Override
    public boolean shouldExecute() {
        if (recoveryTicks > 0) {
            --recoveryTicks;
            return false;
        }
        if (!(entityHost instanceof IBreathAttacker)) {
            return false;
        }
        EntityLivingBase target = entityHost.getAttackTarget();
        if (target == null || !target.isEntityAlive()
                || entityHost.getDistanceSqToEntity(target) > breathRange * breathRange
                || !entityHost.canEntityBeSeen(target)
                || entityHost.getRNG().nextFloat() >= attackChance) {
            return false;
        }
        attackTarget = target;
        return true;
    }

    /**
     * Initialize counters
     */
    @Override
    public void startExecuting() {
        durationLeft = maxDuration;
        ((IBreathAttacker) entityHost).setBreathing(true);
    }

    /**
     * Keep breathing until the target dies, or moves out of range or line of sight
     */
    @Override
    public boolean continueExecuting() {
        return durationLeft > 0 && !entityHost.isDead
                && attackTarget != null
                && attackTarget.isEntityAlive()
                && entityHost.getAttackTarget() == attackTarget
                && entityHost.getDistanceSqToEntity(attackTarget) <= breathRange * breathRange
                && entityHost.canEntityBeSeen(attackTarget);
    }

    /**
     * Update timers, deal damage
     */
    @Override
    public void updateTask() {
        --durationLeft;
        // track a moving target instead of continuously aiming at its old location
        breathX = attackTarget.posX;
        breathY = attackTarget.posY + attackTarget.getEyeHeight() * 0.5D;
        breathZ = attackTarget.posZ;
        entityHost.getLookHelper().setLookPosition(breathX, breathY, breathZ, 100.0F, 100.0F);
        faceVec(breathX, breathY, breathZ, 100.0F, 100.0F);
        if (maxDuration - durationLeft > 5 && !entityHost.worldObj.isRemote) {
            Entity victim = getHeadLookTarget();
            if (victim != null) {
                ((IBreathAttacker) entityHost).doBreathAttack(victim);
            }
        }
    }

    @Override
    public void resetTask() {
        durationLeft = 0;
        recoveryTicks = 10;
        if (entityHost instanceof IBreathAttacker) {
            ((IBreathAttacker) entityHost).setBreathing(false);
        }
        attackTarget = null;
    }

    /**
     * What, if anything, is the head currently looking at?
     */
    private Entity getHeadLookTarget() {
        // an entity in the actual breath range must be in front of the host unobstructed
        Vec3 origin = Vec3
                .createVectorHelper(entityHost.posX, entityHost.posY + entityHost.getEyeHeight(), entityHost.posZ);
        Vec3 direction = entityHost.getLook(1.0F);
        Vec3 end = origin.addVector(
                direction.xCoord * breathRange,
                direction.yCoord * breathRange,
                direction.zCoord * breathRange);
        MovingObjectPosition wall = entityHost.worldObj.rayTraceBlocks(origin, end);
        double maximum = wall == null ? breathRange : origin.distanceTo(wall.hitVec);
        Entity nearest = null;
        double nearestDistance = maximum;
        List<Entity> candidates = entityHost.worldObj.getEntitiesWithinAABBExcludingEntity(
                entityHost,
                entityHost.boundingBox.addCoord(
                        direction.xCoord * breathRange,
                        direction.yCoord * breathRange,
                        direction.zCoord * breathRange).expand(1.0D, 1.0D, 1.0D));
        for (Entity candidate : candidates) {
            if (!(candidate instanceof EntityLivingBase) || !candidate.isEntityAlive()
                    || !candidate.canBeCollidedWith()) {
                continue;
            }
            float border = candidate.getCollisionBorderSize();
            AxisAlignedBB hitBox = candidate.boundingBox.expand(border, border, border);
            MovingObjectPosition impact = hitBox.calculateIntercept(origin, end);
            if (hitBox.isVecInside(origin)) {
                return candidate;
            }
            if (impact != null) {
                double distance = origin.distanceTo(impact.hitVec);
                if (distance <= nearestDistance) {
                    nearest = candidate;
                    nearestDistance = distance;
                }
            }
        }
        return nearest;
    }

    /**
     * Face the head towards a specific Vector
     */
    public void faceVec(double xCoord, double yCoord, double zCoord, float yawConstraint, float pitchConstraint) {
        double xOffset = xCoord - entityHost.posX;
        double zOffset = zCoord - entityHost.posZ;
        double yOffset = (entityHost.posY + 0.25) - yCoord;

        double distance = MathHelper.sqrt_double(xOffset * xOffset + zOffset * zOffset);
        float xyAngle = (float) ((Math.atan2(zOffset, xOffset) * 180D) / Math.PI) - 90F;
        float zdAngle = (float) (-((Math.atan2(yOffset, distance) * 180D) / Math.PI));
        entityHost.rotationPitch = updateRotation(entityHost.rotationPitch, -zdAngle, pitchConstraint);
        entityHost.rotationYaw = updateRotation(entityHost.rotationYaw, xyAngle, yawConstraint);

    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float par1, float par2, float par3) {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3) {
            var4 = par3;
        }

        if (var4 < -par3) {
            var4 = -par3;
        }

        return par1 + var4;
    }

}
