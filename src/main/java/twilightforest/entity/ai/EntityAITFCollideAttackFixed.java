package twilightforest.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

/**
 * This is a copy of EntityAIAttackOnCollide modified to change a few hardcoded behaviors
 *
 * @author Mojang
 *
 */
public class EntityAITFCollideAttackFixed extends EntityAIBase {

    EntityCreature attacker;
    /**
     * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
     */
    int attackTick;
    /** The speed with which the mob will approach the target */
    double speedTowardsTarget;
    /**
     * When true, the mob will continue chasing its target, even if it can't find a path to them right now.
     */
    boolean longMemory;
    /** The PathEntity of our entity. */
    PathEntity entityPathEntity;
    Class<? extends EntityLivingBase> classTarget;
    private int delayTicks;
    private double pathX;
    private double pathY;
    private double pathZ;
    private int failedPathFindingPenalty;

    public EntityAITFCollideAttackFixed(EntityCreature par1EntityCreature, Class<? extends EntityLivingBase> par2Class,
            double par3, boolean par5) {
        this(par1EntityCreature, par3, par5);
        this.classTarget = par2Class;
    }

    public EntityAITFCollideAttackFixed(EntityCreature par1EntityCreature, double par2, boolean par4) {
        this.attacker = par1EntityCreature;
        this.speedTowardsTarget = par2;
        this.longMemory = par4;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive() || classTarget != null && !classTarget.isInstance(target)) {
            return false;
        }
        if (--delayTicks > 0) {
            return entityPathEntity != null || attacker.getDistanceSqToEntity(target) < 16.0D;
        }
        delayTicks = 4 + attacker.getRNG().nextInt(7);
        entityPathEntity = attacker.getNavigator().getPathToEntityLiving(target);
        return entityPathEntity != null || attacker.getDistanceSqToEntity(target) < 16.0D;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        return entitylivingbase == null ? false
                : (!entitylivingbase.isEntityAlive() ? false
                        : (!this.longMemory ? !this.attacker.getNavigator().noPath()
                                : this.attacker.isWithinHomeDistance(
                                        MathHelper.floor_double(entitylivingbase.posX),
                                        MathHelper.floor_double(entitylivingbase.posY),
                                        MathHelper.floor_double(entitylivingbase.posZ))));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        if (entityPathEntity != null) {
            attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
        }
        delayTicks = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        attacker.getNavigator().clearPathEntity();
        entityPathEntity = null;
        failedPathFindingPenalty = 0;
    }

    /**
     * Updates the task
     */
    public void updateTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
            return;
        }
        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);

        double distanceToAttacker = this.attacker
                .getDistanceSq(entitylivingbase.posX, entitylivingbase.boundingBox.minY, entitylivingbase.posZ);
        double attackRange = this.attacker.width * 2.0F * this.attacker.width * 2.0F + entitylivingbase.width;

        --this.delayTicks;

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayTicks <= 0
                && (this.pathX == 0.0D && this.pathY == 0.0D && this.pathZ == 0.0D
                        || entitylivingbase.getDistanceSq(this.pathX, this.pathY, this.pathZ) >= 1.0D
                        || this.attacker.getRNG().nextFloat() < 0.05F)) {
            this.pathX = entitylivingbase.posX;
            this.pathY = entitylivingbase.boundingBox.minY;
            this.pathZ = entitylivingbase.posZ;
            this.delayTicks = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

            if (distanceToAttacker > 1024.0D) {
                delayTicks += 10;
            } else if (distanceToAttacker > 256.0D) {
                delayTicks += 5;
            }

            boolean foundPath = attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, speedTowardsTarget);
            PathEntity path = attacker.getNavigator().getPath();
            PathPoint endpoint = path == null ? null : path.getFinalPathPoint();
            if (foundPath && endpoint != null
                    && entitylivingbase.getDistanceSq(endpoint.xCoord, endpoint.yCoord, endpoint.zCoord) < 1.0D) {
                failedPathFindingPenalty = 0;
            } else {
                // a bounded penalty prevents permanently stalled mobs from waiting minutes between retries
                failedPathFindingPenalty = Math.min(80, failedPathFindingPenalty + 10);
                if (!foundPath) {
                    delayTicks += 15;
                }
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);

        // fix bug removing any delay from attacks
        if (distanceToAttacker <= attackRange && this.attackTick <= 0) {
            // double attack timeout
            this.attackTick = 20;

            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }

            this.attacker.attackEntityAsMob(entitylivingbase);
        }
    }
}
