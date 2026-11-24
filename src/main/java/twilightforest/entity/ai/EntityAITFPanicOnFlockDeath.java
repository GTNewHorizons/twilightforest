package twilightforest.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

import twilightforest.entity.EntityTFKobold;

public class EntityAITFPanicOnFlockDeath extends EntityAIBase {

    private final EntityCreature flockCreature;
    private final float speed;
    private double fleeX;
    private double fleeY;
    private double fleeZ;

    private int fleeTimer;
    private int scanCooldown;
    private int recoveryCooldown;

    public EntityAITFPanicOnFlockDeath(EntityCreature par1EntityCreature, float par2) {
        this.flockCreature = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
        this.fleeTimer = 0;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (recoveryCooldown > 0) {
            --recoveryCooldown;
            return false;
        }
        if (scanCooldown > 0) {
            --scanCooldown;
            return false;
        }
        scanCooldown = 10;
        List<? extends EntityLiving> nearby = flockCreature.worldObj
                .getEntitiesWithinAABB(flockCreature.getClass(), flockCreature.boundingBox.expand(4.0D, 2.0D, 4.0D));
        EntityLiving casualty = null;
        for (EntityLiving member : nearby) {
            if (member != flockCreature && member.deathTime > 0) {
                casualty = member;
                break;
            }
        }
        if (casualty == null) {
            return false;
        }
        Vec3 danger = Vec3.createVectorHelper(casualty.posX, casualty.posY, casualty.posZ);
        Vec3 flee = RandomPositionGenerator.findRandomTargetBlockAwayFrom(flockCreature, 8, 4, danger);
        if (flee == null) {
            flee = RandomPositionGenerator.findRandomTarget(flockCreature, 5, 4);
        }
        if (flee == null) {
            return false;
        }
        fleeX = flee.xCoord;
        fleeY = flee.yCoord;
        fleeZ = flee.zCoord;
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        fleeTimer = 40;
        flockCreature.getNavigator().tryMoveToXYZ(fleeX, fleeY, fleeZ, speed);
        if (flockCreature instanceof EntityTFKobold) {
            ((EntityTFKobold) flockCreature).setPanicked(true);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return fleeTimer > 0 && !flockCreature.getNavigator().noPath();
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        fleeTimer--;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        fleeTimer = 0;
        recoveryCooldown = 40;
        if (flockCreature instanceof EntityTFKobold) {
            ((EntityTFKobold) flockCreature).setPanicked(false);
        }
    }

}
