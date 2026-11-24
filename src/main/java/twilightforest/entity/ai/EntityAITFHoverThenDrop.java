package twilightforest.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import twilightforest.entity.boss.EntityTFSnowQueen;
import twilightforest.entity.boss.EntityTFSnowQueen.Phase;

public class EntityAITFHoverThenDrop extends EntityAIBase {

    private static final float HOVER_HEIGHT = 6F;
    private static final float HOVER_RADIUS = 0;
    private Class<? extends EntityLivingBase> classTarget;
    private EntityTFSnowQueen attacker;

    private double hoverPosX;
    private double hoverPosY;
    private double hoverPosZ;
    private int hoverTimer;
    private int dropTimer;
    private int maxHoverTime;
    private int maxDropTime;
    private int seekTimer;
    private int maxSeekTime;

    private double dropY;
    private boolean hasHoverSpot;
    private boolean arrived;
    private boolean countedDrop;
    private int retryCooldown;

    public EntityAITFHoverThenDrop(EntityTFSnowQueen entityTFSnowQueen, Class<EntityPlayer> class1, int hoverTime,
            int dropTime) {
        this.attacker = entityTFSnowQueen;
        this.classTarget = class1;
        this.setMutexBits(3);
        this.maxHoverTime = hoverTime;
        this.maxSeekTime = hoverTime;
        this.maxDropTime = dropTime;

        this.hoverTimer = 0;

    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (retryCooldown > 0) {
            --retryCooldown;
            return false;
        }
        EntityLivingBase target = attacker.getAttackTarget();
        return target != null && target.isEntityAlive()
                && (classTarget == null || classTarget.isInstance(target))
                && attacker.getCurrentPhase() == Phase.DROP;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        return hasHoverSpot && target != null
                && target.isEntityAlive()
                && attacker.getCurrentPhase() == Phase.DROP
                && !countedDrop
                && (arrived || seekTimer < maxSeekTime);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        hoverTimer = 0;
        dropTimer = 0;
        seekTimer = 0;
        arrived = false;
        countedDrop = false;
        hasHoverSpot = false;
        EntityLivingBase target = attacker.getAttackTarget();
        if (target != null) {
            makeNewHoverSpot(target);
        }
        if (!hasHoverSpot) {
            retryCooldown = 20;
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        hoverTimer = 0;
        dropTimer = 0;
        seekTimer = 0;
        arrived = false;
        hasHoverSpot = false;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (!hasHoverSpot || countedDrop) {
            return;
        }
        if (!arrived) {
            if (attacker.getDistanceSq(hoverPosX, hoverPosY, hoverPosZ) <= 1.0D) {
                arrived = true;
            } else {
                ++seekTimer;
            }
        }
        if (!arrived || hoverTimer < maxHoverTime) {
            if (arrived) {
                ++hoverTimer;
            }
            SnowQueenHoverHelper.steerToward(attacker, hoverPosX, hoverPosY, hoverPosZ);
        } else {
            ++dropTimer;
            if (!attacker.worldObj.isRemote && attacker.posY > dropY
                    && attacker.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                attacker.destroyBlocksInAABB(attacker.boundingBox.expand(1.0D, 0.5D, 1.0D));
            }
            if (dropTimer >= maxDropTime) {
                countedDrop = true;
                if (!attacker.worldObj.isRemote) {
                    attacker.incrementSuccessfulDrops();
                }
            }
        }
        EntityLivingBase target = attacker.getAttackTarget();
        if (target != null) {
            attacker.faceEntity(target, 30.0F, 30.0F);
            attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }
    }

    private void makeNewHoverSpot(EntityLivingBase target) {
        // The dive begins directly above its target; try one exact position first.
        Vec3 spot = SnowQueenHoverHelper.findHoverPosition(attacker, target, HOVER_HEIGHT, HOVER_RADIUS, 1);
        if (spot == null) {
            // a low ceiling shouldnt leave the queen steering into solid blocks forever
            spot = SnowQueenHoverHelper.findHoverPosition(attacker, target, HOVER_HEIGHT, 2.0D, 16);
        }
        hasHoverSpot = spot != null;
        if (spot != null) {
            hoverPosX = spot.xCoord;
            hoverPosY = spot.yCoord;
            hoverPosZ = spot.zCoord;
            dropY = target.posY - 1.0D;
            seekTimer = 0;
        }
    }
}
