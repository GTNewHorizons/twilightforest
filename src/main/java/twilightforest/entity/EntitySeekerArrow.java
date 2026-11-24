package twilightforest.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySeekerArrow extends EntityArrow {

    private EntityLivingBase homingTarget;

    public EntitySeekerArrow(World world) {
        super(world);
    }

    public EntitySeekerArrow(World world, EntityPlayer player, float velocity) {
        super(world, player, velocity);
    }

    private static final double SEEK_DISTANCE = 5.0D;
    private static final double VERTICAL_SEARCH = 3.0D;
    private static final double GRAVITY_COMPENSATION = 0.045D;
    private static final double TURN_FACTOR = 0.25D;

    @Override
    public void onUpdate() {
        if (!this.worldObj.isRemote && isThisArrowFlying()) {
            if (!isValidTarget(this.homingTarget)) {
                this.homingTarget = findHomingTarget();
            }
            if (this.homingTarget != null) {
                steerTowards(this.homingTarget);
            }
        }

        // the seeker bow launches its arrows at half speed, reduce the usual gravity loss
        if (isThisArrowFlying()) {
            this.motionY += GRAVITY_COMPENSATION;
        }
        super.onUpdate();
    }

    private boolean isValidTarget(EntityLivingBase target) {
        return target != null && !target.isDead && target.getHealth() > 0.0F && target.worldObj == this.worldObj;
    }

    private EntityLivingBase findHomingTarget() {
        Vec3 heading = Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ).normalize();
        if (heading.lengthVector() == 0.0D) {
            return null;
        }

        double reach = SEEK_DISTANCE
                * Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        AxisAlignedBB searchBox = this.boundingBox
                .addCoord(heading.xCoord * reach, heading.yCoord * reach, heading.zCoord * reach)
                .expand(reach * 0.6D, VERTICAL_SEARCH, reach * 0.6D);

        EntityLivingBase best = null;
        double bestAlignment = 0.0D;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (Entity entity : this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this, searchBox, IEntitySelector.selectAnything)) {
            if (!(entity instanceof EntityLivingBase candidate) || candidate instanceof EntityPlayer
                    || candidate == this.shootingEntity
                    || !isValidTarget(candidate)) {
                continue;
            }
            Vec3 toTarget = Vec3.createVectorHelper(
                    candidate.posX - this.posX,
                    candidate.posY + candidate.getEyeHeight() - this.posY,
                    candidate.posZ - this.posZ);
            double distance = toTarget.lengthVector();
            if (distance < 1.0E-6D) {
                continue;
            }
            double alignment = heading.dotProduct(toTarget.normalize());
            // dont select mobs behind the arrow
            if (alignment > 0.0D
                    && (alignment > bestAlignment || (alignment == bestAlignment && distance < bestDistance))) {
                best = candidate;
                bestAlignment = alignment;
                bestDistance = distance;
            }
        }
        return best;
    }

    private void steerTowards(EntityLivingBase target) {
        double speed = Math
                .sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        if (speed < 1.0E-6D) {
            return;
        }
        Vec3 desired = Vec3.createVectorHelper(
                target.posX - this.posX,
                target.posY + target.getEyeHeight() - this.posY,
                target.posZ - this.posZ).normalize();
        Vec3 adjusted = Vec3.createVectorHelper(
                this.motionX * (1.0D - TURN_FACTOR) + desired.xCoord * speed * TURN_FACTOR,
                this.motionY * (1.0D - TURN_FACTOR) + desired.yCoord * speed * TURN_FACTOR,
                this.motionZ * (1.0D - TURN_FACTOR) + desired.zCoord * speed * TURN_FACTOR);
        double adjustedLength = adjusted.lengthVector();
        if (adjustedLength > 1.0E-6D) {
            this.motionX = adjusted.xCoord * speed / adjustedLength;
            this.motionY = adjusted.yCoord * speed / adjustedLength;
            this.motionZ = adjusted.zCoord * speed / adjustedLength;
            this.velocityChanged = true;
        }
    }

    private boolean isThisArrowFlying() {
        return this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ > 1.0D;
    }
}
