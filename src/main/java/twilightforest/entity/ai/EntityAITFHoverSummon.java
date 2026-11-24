package twilightforest.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import twilightforest.entity.boss.EntityTFSnowQueen;
import twilightforest.entity.boss.EntityTFSnowQueen.Phase;

public class EntityAITFHoverSummon extends EntityAIBase {

    private static final float HOVER_HEIGHT = 6F;
    private static final float HOVER_RADIUS = 6F;
    private static final int MAX_MINIONS_AT_ONCE = 4;

    private Class<? extends EntityLivingBase> classTarget;
    private EntityTFSnowQueen attacker;

    private double hoverPosX;
    private double hoverPosY;
    private double hoverPosZ;
    private int seekTimer;
    private int maxSeekTime;
    private boolean hasHoverSpot;
    private int retryCooldown;

    public EntityAITFHoverSummon(EntityTFSnowQueen entityTFSnowQueen, Class<EntityPlayer> class1, double speed) {
        this.attacker = entityTFSnowQueen;
        this.classTarget = class1;
        this.setMutexBits(3);
        this.maxSeekTime = 80;

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
                && attacker.getCurrentPhase() == Phase.SUMMON;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        return hasHoverSpot && target != null
                && target.isEntityAlive()
                && attacker.getCurrentPhase() == Phase.SUMMON
                && seekTimer < maxSeekTime;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        seekTimer = 0;
        EntityLivingBase target = attacker.getAttackTarget();
        hasHoverSpot = false;
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
        hasHoverSpot = false;
        seekTimer = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (!hasHoverSpot) {
            return;
        }
        ++seekTimer;
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) {
            return;
        }
        if (attacker.getDistanceSq(hoverPosX, hoverPosY, hoverPosZ) <= 1.0D) {
            checkAndSummon();
            makeNewHoverSpot(target);
            if (!hasHoverSpot) {
                retryCooldown = 20;
                return;
            }
        }
        // Recheck an obstructed destination occasionally, not every tick.
        if (seekTimer % 10 == 0 && !SnowQueenHoverHelper.canSee(attacker, hoverPosX, hoverPosY, hoverPosZ)) {
            makeNewHoverSpot(target);
            if (!hasHoverSpot) {
                retryCooldown = 20;
                return;
            }
        }
        SnowQueenHoverHelper.steerToward(attacker, hoverPosX, hoverPosY, hoverPosZ);
        attacker.faceEntity(target, 30.0F, 30.0F);
        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
    }

    private void makeNewHoverSpot(EntityLivingBase target) {
        Vec3 spot = SnowQueenHoverHelper.findHoverPosition(attacker, target, HOVER_HEIGHT, HOVER_RADIUS, 32);
        hasHoverSpot = spot != null;
        if (spot != null) {
            hoverPosX = spot.xCoord;
            hoverPosY = spot.yCoord;
            hoverPosZ = spot.zCoord;
            seekTimer = 0;
        }
    }

    private void checkAndSummon() {
        if (!attacker.worldObj.isRemote && attacker.getSummonsRemaining() > 0
                && attacker.countMyMinions() < MAX_MINIONS_AT_ONCE) {
            this.attacker.summonMinionAt(this.attacker.getAttackTarget());

            // System.out.println("Summoning minion");
        } else {
            // System.out.println("Summons full");
        }
    }
}
