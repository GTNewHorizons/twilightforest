package twilightforest.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import twilightforest.entity.EntityTFRedcap;

public class EntityAITFAvoidFrontalAttack extends EntityAIBase {

    private EntityLivingBase entityTarget;
    private final EntityLiving me;
    private final float speed;
    boolean lefty;
    double xPosition;
    double yPosition;
    double zPosition;

    private static final double MIN_DISTANCE_SQ = 9.0D;
    private static final double MAX_DISTANCE_SQ = 36.0D;

    public EntityAITFAvoidFrontalAttack(EntityTFRedcap entityTFRedcap, float moveSpeed) {
        this.me = entityTFRedcap;
        this.speed = moveSpeed;
        this.lefty = me.worldObj.rand.nextBoolean();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = me.getAttackTarget();
        if (target == null || !target.isEntityAlive() || !isInRange(target) || !isTargetLookingAtMe(target)) {
            return false;
        }
        entityTarget = target;
        Vec3 point = findCirclePoint(me, target, 5.0D, lefty ? 1.0D : -1.0D);
        xPosition = point.xCoord;
        yPosition = point.yCoord;
        zPosition = point.zCoord;
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        if (!me.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed)) {
            // alternate the direction when an obstruction blocks the original flank
            lefty = !lefty;
            Vec3 alternative = findCirclePoint(me, entityTarget, 5.0D, lefty ? 1.0D : -1.0D);
            me.getNavigator().tryMoveToXYZ(alternative.xCoord, alternative.yCoord, alternative.zCoord, speed);
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting() {
        return entityTarget != null && entityTarget.isEntityAlive()
                && entityTarget == me.getAttackTarget()
                && isInRange(entityTarget)
                && isTargetLookingAtMe(entityTarget)
                && !me.getNavigator().noPath();
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.me.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        entityTarget = null;
        me.getNavigator().clearPathEntity();
        lefty = !lefty;
    }

    /**
     * Finds a point that allows us to circle the player clockwise.
     */
    protected Vec3 findCirclePoint(Entity circler, Entity toCircle, double radius, double rotation) {

        // compute angle
        double vecx = circler.posX - toCircle.posX;
        double vecz = circler.posZ - toCircle.posZ;
        float rangle = (float) (Math.atan2(vecz, vecx));

        // add a little, so he circles
        rangle += rotation;

        // figure out where we're headed from the target angle
        double dx = MathHelper.cos(rangle) * radius;
        double dz = MathHelper.sin(rangle) * radius;

        // add that to the target entity's position, and we have our destination
        return Vec3.createVectorHelper(toCircle.posX + dx, circler.boundingBox.minY, toCircle.posZ + dz);
    }

    /**
     * Fairly straightforward. Returns true in a 120 degree arc in front of the target's view.
     *
     * @return
     */
    public boolean isTargetLookingAtMe(EntityLivingBase target) {
        double dx = me.posX - target.posX;
        double dz = me.posZ - target.posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;
        if (horizontalDistanceSq < 0.0001D) {
            return true;
        }
        Vec3 look = target.getLookVec();
        // dot product >= cos(60 degrees), a 120-degree cone in front of the target
        return look.xCoord * dx + look.zCoord * dz >= Math.sqrt(horizontalDistanceSq) * 0.5D;
    }

    private boolean isInRange(EntityLivingBase target) {
        double distance = me.getDistanceSqToEntity(target);
        return distance >= MIN_DISTANCE_SQ && distance <= MAX_DISTANCE_SQ;
    }

}
