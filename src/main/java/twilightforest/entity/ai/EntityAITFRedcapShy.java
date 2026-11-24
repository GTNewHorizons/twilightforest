package twilightforest.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

import twilightforest.entity.EntityTFRedcap;

public class EntityAITFRedcapShy extends EntityAITFRedcapBase {

    private static final double MIN_DISTANCE_SQ = 9.0D;
    private static final double MAX_DISTANCE_SQ = 36.0D;
    private static final double FLANK_RADIUS = 5.0D;

    private EntityLivingBase target;
    private final float speed;
    private boolean lefty;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double targetX;
    private double targetZ;
    private int rePathCooldown;
    private int retryCooldown;

    public EntityAITFRedcapShy(EntityTFRedcap redcap, float moveSpeed) {
        this.entityObj = redcap;
        this.speed = moveSpeed;
        this.lefty = redcap.getRNG().nextBoolean();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.retryCooldown > 0) {
            --this.retryCooldown;
            return false;
        }

        EntityLivingBase attackTarget = this.entityObj.getAttackTarget();
        if (!this.shouldFlank(attackTarget)) {
            return false;
        }

        this.target = attackTarget;
        this.chooseFlankDestination();
        return true;
    }

    @Override
    public void startExecuting() {
        this.rePathCooldown = 12;
        if (!this.tryCurrentFlank()) {
            this.lefty = !this.lefty;
            this.chooseFlankDestination();
            if (!this.tryCurrentFlank()) {
                this.retryCooldown = 15;
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        return this.target != null && this.target == this.entityObj.getAttackTarget()
                && this.shouldFlank(this.target)
                && !this.entityObj.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        if (this.target == null) {
            return;
        }
        this.entityObj.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

        // follow a moving opponents flank instead of running to its old position forever
        if (--this.rePathCooldown <= 0) {
            this.rePathCooldown = 12;
            double dx = this.target.posX - this.targetX;
            double dz = this.target.posZ - this.targetZ;
            if (dx * dx + dz * dz > 2.25D) {
                this.chooseFlankDestination();
                if (!this.tryCurrentFlank()) {
                    this.lefty = !this.lefty;
                    this.chooseFlankDestination();
                    this.tryCurrentFlank();
                }
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.entityObj.getNavigator().clearPathEntity();
        this.lefty = !this.lefty;
    }

    private boolean shouldFlank(EntityLivingBase attackTarget) {
        if (attackTarget == null || !attackTarget.isEntityAlive() || !this.entityObj.isShy()) {
            return false;
        }
        double distance = this.entityObj.getDistanceSqToEntity(attackTarget);
        return distance > MIN_DISTANCE_SQ && distance < MAX_DISTANCE_SQ && this.isTargetLookingAtMe(attackTarget);
    }

    private void chooseFlankDestination() {
        Vec3 flank = this.findCirclePoint(this.entityObj, this.target, FLANK_RADIUS, this.lefty ? 1 : -1);
        this.xPosition = flank.xCoord;
        this.yPosition = flank.yCoord;
        this.zPosition = flank.zCoord;
        this.targetX = this.target.posX;
        this.targetZ = this.target.posZ;
    }

    private boolean tryCurrentFlank() {
        return this.entityObj.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    // finds a circling position relative to the enemy, not to the world origin
    protected Vec3 findCirclePoint(Entity circler, Entity toCircle, double radius, double rotation) {
        double dx = circler.posX - toCircle.posX;
        double dz = circler.posZ - toCircle.posZ;
        double angle = Math.atan2(dz, dx) + rotation;
        return Vec3.createVectorHelper(
                toCircle.posX + Math.cos(angle) * radius,
                circler.boundingBox.minY,
                toCircle.posZ + Math.sin(angle) * radius);
    }
}
