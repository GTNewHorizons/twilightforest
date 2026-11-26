package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityTFTomeBolt extends EntityThrowable {

    public EntityTFTomeBolt(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    public EntityTFTomeBolt(World par1World, EntityLivingBase par2EntityLiving) {
        super(par1World, par2EntityLiving);
    }

    public EntityTFTomeBolt(World par1World) {
        super(par1World);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            makeTrail();
        }
    }

    /**
     * How much this entity falls each tick
     */
    @Override
    protected float getGravityVelocity() {
        return 0.003F;
    }

    /**
     * Make sparkly trail
     */
    public void makeTrail() {
        for (int i = 0; i < 5; i++) {
            double dx = posX + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dy = posY + 0.5 * (rand.nextDouble() - rand.nextDouble());
            double dz = posZ + 0.5 * (rand.nextDouble() - rand.nextDouble());
            worldObj.spawnParticle("crit", dx, dy, dz, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * What happens when we hit something?
     */
    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (worldObj.isRemote) {
            for (int i = 0; i < 8; i++) {
                worldObj.spawnParticle(
                        "iconcrack_" + Item.getIdFromItem(Items.fire_charge),
                        posX,
                        posY,
                        posZ,
                        rand.nextGaussian() * 0.05D,
                        rand.nextDouble() * 0.2D,
                        rand.nextGaussian() * 0.05D);
            }
            return;
        }
        if (isDead || hit == null) {
            return;
        }
        setDead();
        if (hit.entityHit instanceof EntityLivingBase living
                && living.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 6.0F)) {
            int duration = switch (worldObj.difficultySetting) {
                case PEACEFUL -> 0;
                case EASY -> 3;
                case NORMAL -> 7;
                case HARD -> 9;
            };
            if (duration > 0) {
                living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, duration * 20, 1));
            }
        }
    }
}
