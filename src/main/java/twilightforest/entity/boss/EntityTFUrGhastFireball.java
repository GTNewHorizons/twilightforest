package twilightforest.entity.boss;

import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public class EntityTFUrGhastFireball extends EntityLargeFireball implements IEntityAdditionalSpawnData {

    private static final int DIRECT_HIT_DAMAGE = 16;

    public EntityTFUrGhastFireball(World world) {
        super(world);
    }

    public EntityTFUrGhastFireball(World world, EntityTFUrGhast urGhast, double x, double y, double z) {
        super(world, urGhast, x, y, z);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeDouble(accelerationX);
        buffer.writeDouble(accelerationY);
        buffer.writeDouble(accelerationZ);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        accelerationX = buffer.readDouble();
        accelerationY = buffer.readDouble();
        accelerationZ = buffer.readDouble();
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition impact) {
        if (worldObj.isRemote || isDead || impact == null || impact.entityHit instanceof EntityFireball) {
            return;
        }

        // consume the projectile first so reentrant or repeated collision callbacks
        // cant cause another explosion or apply the direct hit twice
        setDead();

        if (impact.entityHit != null) {
            impact.entityHit
                    .attackEntityFrom(DamageSource.causeFireballDamage(this, shootingEntity), DIRECT_HIT_DAMAGE);
        }

        worldObj.newExplosion(
                null,
                posX,
                posY,
                posZ,
                (float) field_92057_e,
                true,
                worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
    }
}
