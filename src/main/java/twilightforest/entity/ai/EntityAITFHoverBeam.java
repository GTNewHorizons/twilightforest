package twilightforest.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import twilightforest.entity.boss.EntityTFSnowQueen;
import twilightforest.entity.boss.EntityTFSnowQueen.Phase;

public class EntityAITFHoverBeam extends EntityAIBase {

    private static final float HOVER_HEIGHT = 3F;
    private static final float HOVER_RADIUS = 4F;
    private Class<? extends EntityLivingBase> classTarget;
    private EntityTFSnowQueen attacker;

    private double hoverPosX;
    private double hoverPosY;
    private double hoverPosZ;
    private int hoverTimer;
    private int beamTimer;
    private int maxHoverTime;
    private int maxBeamTime;
    private int seekTimer;
    private int maxSeekTime;
    private double beamY;
    private boolean isInPosition;
    private boolean hasHoverSpot;
    private int retryCooldown;

    public EntityAITFHoverBeam(EntityTFSnowQueen entityTFSnowQueen, Class<EntityPlayer> class1, int hoverTime,
            int dropTime) {
        this.attacker = entityTFSnowQueen;
        this.classTarget = class1;
        this.setMutexBits(3);
        this.maxHoverTime = hoverTime;
        this.maxSeekTime = hoverTime;
        this.maxBeamTime = dropTime;

        this.hoverTimer = 0;
        this.isInPosition = false;
    }

    // Returns whether the EntityAIBase should begin execution.
    @Override
    public boolean shouldExecute() {
        if (retryCooldown > 0) {
            --retryCooldown;
            return false;
        }
        EntityLivingBase target = attacker.getAttackTarget();
        return target != null && target.isEntityAlive()
                && (classTarget == null || classTarget.isInstance(target))
                && attacker.getCurrentPhase() == Phase.BEAM;
    }

    // Returns whether an in-progress EntityAIBase should continue executing
    @Override
    public boolean continueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        return hasHoverSpot && target != null
                && target.isEntityAlive()
                && attacker.getCurrentPhase() == Phase.BEAM
                && (isInPosition || seekTimer < maxSeekTime)
                && beamTimer < maxBeamTime;
    }

    // Execute a one shot task or start executing a continuous task
    @Override
    public void startExecuting() {
        hoverTimer = 0;
        seekTimer = 0;
        beamTimer = 0;
        isInPosition = false;
        hasHoverSpot = false;
        EntityLivingBase target = attacker.getAttackTarget();
        if (target != null) {
            makeNewHoverSpot(target);
        }
        if (!hasHoverSpot) {
            retryCooldown = 20;
        }
    }

    // Resets the task
    @Override
    public void resetTask() {
        seekTimer = 0;
        hoverTimer = 0;
        beamTimer = 0;
        isInPosition = false;
        hasHoverSpot = false;
        attacker.setBreathing(false);
    }

    // Updates the task
    @Override
    public void updateTask() {
        if (!hasHoverSpot) {
            return;
        }
        if (!isInPosition && attacker.getDistanceSq(hoverPosX, hoverPosY, hoverPosZ) <= 1.0D) {
            isInPosition = true;
        }
        if (isInPosition) {
            ++hoverTimer;
        } else {
            ++seekTimer;
        }
        if (hoverTimer >= maxHoverTime) {
            ++beamTimer;
            attacker.setBreathing(true);
            if (!attacker.worldObj.isRemote) {
                net.minecraft.entity.Entity hit = SnowQueenHoverHelper.findFirstLookTarget(attacker, 30.0D);
                if (hit != null) {
                    attacker.doBreathAttack(hit);
                }
            }
            hoverPosY = Math.max(beamY, hoverPosY - 0.05D);
        }
        SnowQueenHoverHelper.steerToward(attacker, hoverPosX, hoverPosY, hoverPosZ);
        EntityLivingBase target = attacker.getAttackTarget();
        if (target != null) {
            float tracking = isInPosition ? 1.0F : 20.0F;
            attacker.faceEntity(target, tracking, tracking);
            attacker.getLookHelper().setLookPositionWithEntity(target, tracking, tracking);
        }
    }

    // select a free, visible hover point or retry after a short cooldown
    private void makeNewHoverSpot(EntityLivingBase target) {
        Vec3 spot = SnowQueenHoverHelper.findHoverPosition(attacker, target, HOVER_HEIGHT, HOVER_RADIUS, 32);
        if (spot == null) {
            hasHoverSpot = false;
            return;
        }
        hasHoverSpot = true;
        hoverPosX = spot.xCoord;
        hoverPosY = spot.yCoord;
        hoverPosZ = spot.zCoord;
        beamY = target.posY;
        seekTimer = 0;
    }
}
