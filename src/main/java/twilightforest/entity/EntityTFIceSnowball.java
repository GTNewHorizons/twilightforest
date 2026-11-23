package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFIceSnowball extends EntityThrowable {

    private static final int DAMAGE = 8;
    private boolean popped;

    public EntityTFIceSnowball(World world) {
        super(world);
    }

    public EntityTFIceSnowball(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.isDead && !this.popped && this.worldObj.isRemote) {
            makeTrail();
        }
    }

    // How much this entity falls each tick
    @Override
    protected float getGravityVelocity() {
        return 0.006F;
    }

    // Keep the existing snowball-puff trail client-side.
    public void makeTrail() {
        for (int i = 0; i < 2; i++) {
            double dx = posX + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dy = posY + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dz = posZ + 0.5 * (rand.nextDouble() - rand.nextDouble());
            worldObj.spawnParticle("snowballpoof", dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }
    }

    // Reflect!
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isDead || this.popped) {
            return false;
        }
        this.setBeenAttacked();
        this.pop();
        return true;
    }

    /**
     * What happens when we hit something?
     */
    @Override
    protected void onImpact(MovingObjectPosition impact) {
        if (this.isDead || this.popped) {
            return;
        }
        if (!this.worldObj.isRemote && impact.entityHit instanceof EntityLivingBase target
                && impact.entityHit != this.getThrower()) {
            target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), DAMAGE);
        }
        this.pop();
    }

    /**
     * Yeah, do that
     */
    protected void pop() {
        if (this.isDead || this.popped) {
            return;
        }
        this.popped = true;

        if (this.worldObj.isRemote) {
            for (int i = 0; i < 8; ++i) {
                this.worldObj.spawnParticle(
                        "snowballpoof",
                        this.posX,
                        this.posY,
                        this.posZ,
                        rand.nextGaussian() * 0.05D,
                        rand.nextDouble() * 0.2D,
                        rand.nextGaussian() * 0.05D);
            }
        }

        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }

}
