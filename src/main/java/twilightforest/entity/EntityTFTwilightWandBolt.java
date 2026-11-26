package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFTwilightWandBolt extends EntityThrowable {

    private static final int TRAIL_PARTICLES = 5;
    private static final int IMPACT_PARTICLES = 8;
    private static final float MAGIC_DAMAGE = 6.0F;

    private boolean impacted;

    public EntityTFTwilightWandBolt(World world) {
        super(world);
    }

    public EntityTFTwilightWandBolt(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityTFTwilightWandBolt(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote && !this.isDead) {
            this.makeTrail();
        }
    }

    public void makeTrail() {
        if (!this.worldObj.isRemote) {
            return;
        }
        for (int i = 0; i < TRAIL_PARTICLES; ++i) {
            double x = this.posX + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
            double y = this.posY + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
            double z = this.posZ + 0.5D * (this.rand.nextDouble() - this.rand.nextDouble());
            double red = (this.rand.nextFloat() * 0.5F + 0.5F) * 0.17D;
            double green = (this.rand.nextFloat() * 0.5F + 0.5F) * 0.80D;
            double blue = (this.rand.nextFloat() * 0.5F + 0.5F) * 0.69D;
            this.worldObj.spawnParticle("mobSpell", x, y, z, red, green, blue);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.003F;
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (this.impacted || hit == null) {
            return;
        }
        this.impacted = true;

        if (this.worldObj.isRemote) {
            for (int i = 0; i < IMPACT_PARTICLES; ++i) {
                this.worldObj.spawnParticle(
                        "iconcrack_" + Item.getIdFromItem(Items.ender_pearl),
                        this.posX,
                        this.posY,
                        this.posZ,
                        this.rand.nextGaussian() * 0.05D,
                        this.rand.nextDouble() * 0.2D,
                        this.rand.nextGaussian() * 0.05D);
            }
        } else {
            // damage applied once and only by the server
            if (hit.entityHit instanceof EntityLivingBase) {
                hit.entityHit
                        .attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getThrower()), MAGIC_DAMAGE);
            }
            this.setDead();
        }
    }
}
