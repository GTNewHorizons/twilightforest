package twilightforest.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import twilightforest.entity.ITFCharger;

public class EntityAITFChargeAttack extends EntityAIBase {

    protected static final double MIN_RANGE_SQ = 16.0D;
    protected static final double MAX_RANGE_SQ = 64.0D;
    protected static final int FREQ = 1;
    private static final int CHARGE_COOLDOWN = 30;

    protected EntityCreature charger;
    protected EntityLivingBase chargeTarget;
    protected double chargeX;
    protected double chargeY;
    protected double chargeZ;

    protected float speed;

    protected int windup;

    protected boolean hasAttacked;
    private int cooldown;
    private int chargeTicks;

    public EntityAITFChargeAttack(EntityCreature entityLiving, float f) {
        this.charger = entityLiving;
        this.speed = f;
        this.windup = 0;
        this.hasAttacked = false;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (cooldown > 0) {
            --cooldown;
            return false;
        }
        EntityLivingBase target = charger.getAttackTarget();
        if (target == null || !target.isEntityAlive() || !charger.onGround) {
            return false;
        }
        double distance = charger.getDistanceSqToEntity(target);
        if (distance < MIN_RANGE_SQ || distance > MAX_RANGE_SQ) {
            return false;
        }
        Vec3 point = findChargePoint(charger, target, 2.1D);
        if (point == null || charger.worldObj.rayTraceBlocks(
                Vec3.createVectorHelper(charger.posX, charger.posY + charger.getEyeHeight(), charger.posZ),
                Vec3.createVectorHelper(target.posX, target.posY + target.getEyeHeight(), target.posZ)) != null) {
            return false;
        }
        chargeTarget = target;
        chargeX = point.xCoord;
        chargeY = point.yCoord;
        chargeZ = point.zCoord;
        return charger.getRNG().nextInt(FREQ) == 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        windup = 15 + charger.getRNG().nextInt(30);
        chargeTicks = 0;
        hasAttacked = false;
        charger.getNavigator().clearPathEntity();
        if (charger instanceof ITFCharger) {
            ((ITFCharger) charger).setCharging(true);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return !charger.isDead && chargeTarget != null
                && chargeTarget.isEntityAlive()
                && charger.getAttackTarget() == chargeTarget
                && (windup > 0 || (!hasAttacked && chargeTicks < 40 && !charger.getNavigator().noPath()));
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        charger.getLookHelper()
                .setLookPosition(chargeX, chargeY + 0.5D, chargeZ, 10.0F, charger.getVerticalFaceSpeed());
        if (windup > 0) {
            --windup;
            charger.limbSwingAmount += 0.8F;
            if (windup == 0) {
                charger.getNavigator().tryMoveToXYZ(chargeX, chargeY, chargeZ, speed);
            }
            return;
        }
        ++chargeTicks;
        double reach = charger.width * 2.1D;
        if (!hasAttacked && charger.getDistanceSq(chargeTarget.posX, chargeTarget.boundingBox.minY, chargeTarget.posZ)
                <= reach * reach) {
            hasAttacked = true;
            charger.attackEntityAsMob(chargeTarget);
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        windup = 0;
        chargeTicks = 0;
        chargeTarget = null;
        hasAttacked = false;
        cooldown = CHARGE_COOLDOWN;
        if (charger instanceof ITFCharger) {
            ((ITFCharger) charger).setCharging(false);
        }
    }

    /**
     * Finds a point that is overshoot blocks "beyond" the target from our position.
     */
    protected Vec3 findChargePoint(Entity attacker, Entity target, double overshoot) {
        double dx = target.posX - attacker.posX;
        double dz = target.posZ - attacker.posZ;
        double distance = MathHelper.sqrt_double(dx * dx + dz * dz);
        if (distance < 0.001D) {
            return null;
        }
        return Vec3.createVectorHelper(
                target.posX + dx / distance * overshoot,
                target.posY,
                target.posZ + dz / distance * overshoot);
    }

}
