package twilightforest.entity.boss;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import twilightforest.TwilightForestMod;
import twilightforest.entity.EntityTFYeti;
import twilightforest.entity.TFDamageSources;

public class EntityTFIceBomb extends EntityThrowable {

    private int zoneTimer = 80;
    private boolean hasHit;

    public EntityTFIceBomb(World world) {
        super(world);
    }

    public EntityTFIceBomb(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition mop) {
        if (this.getThrower() != null && this.getThrower() instanceof EntityTFYetiAlpha) {
            double dist = this.getDistanceSqToEntity(this.getThrower());

            if (dist <= 100) {
                this.setDead();
            }
        }

        this.motionY = 0;
        this.hasHit = true;

        if (!worldObj.isRemote) {
            this.doTerrainEffects();
        }
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    private void doTerrainEffects() {
        int range = 3;
        int ix = MathHelper.floor_double(this.lastTickPosX);
        int iy = MathHelper.floor_double(this.lastTickPosY);
        int iz = MathHelper.floor_double(this.lastTickPosZ);

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (Vec3.createVectorHelper(x, y, z).lengthVector() <= range + 0.5d)
                        this.doTerrainEffect(ix + x, iy + y, iz + z);
                }
            }
        }
    }

    /**
     * Freeze water, put snow on snowable surfaces
     */
    private void doTerrainEffect(int x, int y, int z) {
        if (this.worldObj.getBlock(x, y, z).getMaterial() == Material.water) {
            this.worldObj.setBlock(x, y, z, Blocks.ice);
        }
        if (this.worldObj.getBlock(x, y, z).getMaterial() == Material.lava) {
            this.worldObj.setBlock(x, y, z, Blocks.obsidian);
        }
        if (this.worldObj.isAirBlock(x, y, z) && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, x, y, z)) {
            this.worldObj.setBlock(x, y, z, Blocks.snow_layer);
        } else
            if (this.worldObj.getBlock(x, y, z) == Blocks.snow_layer && this.worldObj.getBlockMetadata(x, y, z) < 7) {
                this.worldObj.setBlockMetadataWithNotify(x, y, z, this.worldObj.getBlockMetadata(x, y, z) + 1, 2);
            }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        // this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        // // push out of block

        if (this.hasHit) {
            if (!worldObj.isRemote) {
                // slow down
                this.motionX *= 0.1D;
                this.motionY *= 0.1D;
                this.motionZ *= 0.1D;
            }

            this.zoneTimer--;

            makeIceZone();

            // eventually explode
            if (this.zoneTimer <= 0) {
                detonate();
            }

        } else {
            makeTrail();
        }
    }

    public void makeTrail() {
        for (int i = 0; i < 10; i++) {
            double dx = posX + 0.75F * (rand.nextFloat() - 0.5F);
            double dy = posY + 0.75F * (rand.nextFloat() - 0.5F);
            double dz = posZ + 0.75F * (rand.nextFloat() - 0.5F);

            TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", dx, dy, dz, 0, 0, 0);
        }
    }

    private void makeIceZone() {
        // nearby sparkles
        if (this.worldObj.isRemote) {
            // sparkles
            for (int i = 0; i < 20; i++) {
                double dx = this.posX + (rand.nextFloat() - rand.nextFloat()) * 3.0F;
                double dy = this.posY + (rand.nextFloat() - rand.nextFloat()) * 3.0F;
                double dz = this.posZ + (rand.nextFloat() - rand.nextFloat()) * 3.0F;

                TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", dx, dy, dz, 0.0D, 0.0D, 0.0D);
            }
        } else {
            // damage
            if (this.zoneTimer % 10 == 0) {
                hitNearbyEntities();
            }
        }
    }

    private void hitNearbyEntities() {
        ArrayList<Entity> nearby = new ArrayList<>(
                this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(3, 2, 3)));

        for (Entity entity : nearby) {
            if (entity instanceof EntityLivingBase && entity != this.getThrower()) {

                if (entity instanceof EntityTFYeti) {
                    // TODO: make "frozen yeti" entity?
                    entity.setDead();
                    int ix = MathHelper.floor_double(entity.lastTickPosX);
                    int iy = MathHelper.floor_double(entity.lastTickPosY);
                    int iz = MathHelper.floor_double(entity.lastTickPosZ);

                    worldObj.setBlock(ix, iy, iz, Blocks.ice);
                    worldObj.setBlock(ix, iy + 1, iz, Blocks.ice);

                } else {
                    entity.attackEntityFrom(TFDamageSources.iceBomb, 1);
                    int chillLevel = 2;
                    ((EntityLivingBase) entity)
                            .addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20 * 5, chillLevel, true));
                }
            }
        }
    }

    private void detonate() {
        this.setDead();
    }

    public Block getBlock() {
        return Blocks.packed_ice;
    }

    @Override
    protected float func_70182_d() {
        // velocity
        return 0.75F;
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return this.hasHit ? 0F : 0.025F;
    }

    @Override
    protected float func_70183_g() {
        return -20.0F;
    }
}
