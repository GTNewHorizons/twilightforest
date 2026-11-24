package twilightforest.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFFireJet;
import twilightforest.block.TFBlocks;

public class TileEntityTFFlameJet extends TileEntity {

    private static final int ACTIVE_DURATION = 60;
    private static final int DAMAGE_INTERVAL = 5;

    int counter = 0;
    private int nextMeta;

    public TileEntityTFFlameJet() {
        this(BlockTFFireJet.META_JET_IDLE);
    }

    public TileEntityTFFlameJet(int parNextMeta) {
        this.nextMeta = parNextMeta;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        if (++counter > ACTIVE_DURATION) {
            counter = 0;
            // idle again
            if (!worldObj.isRemote && worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == TFBlocks.fireJet) {
                worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, TFBlocks.fireJet, this.nextMeta, 3);
            }
            this.invalidate();
        } else if (counter % 2 == 0) {
            worldObj.spawnParticle(
                    "largesmoke",
                    this.xCoord + 0.5,
                    this.yCoord + 1.0,
                    this.zCoord + 0.5,
                    0.0D,
                    0.0D,
                    0.0D);
            spawnFlameParticle(0.5D, 0.5D, 0.0D, 0.0D);
            // TwilightForestMod.proxy.spawnParticle("largeflame", this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord +
            // 0.5,
            // Math.cos(counter / 4.0) * 0.2, 0.35D, Math.sin(counter / 4.0) * 0.2);
            // TwilightForestMod.proxy.spawnParticle("largeflame", this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord +
            // 0.5,
            // Math.cos(counter / 4.0 + Math.PI) * 0.2, 0.35D, Math.sin(counter / 4.0 + Math.PI) * 0.2);
            // TwilightForestMod.proxy.spawnParticle("largeflame", this.xCoord + 0.5 + Math.cos(counter / 4.0),
            // this.yCoord + 1.0, this.zCoord + 0.5 + Math.sin(counter / 4.0),
            // Math.sin(counter / 4.0) * 0.05, 0.35D, Math.cos(counter / 4.0) * 0.05);
            // TwilightForestMod.proxy.spawnParticle("largeflame", this.xCoord + 0.5 + Math.cos(counter / 4.0 +
            // Math.PI), this.yCoord + 1.0, this.zCoord + 0.5 + Math.sin(counter / 4.0 + Math.PI),
            // Math.sin(counter / 4.0 + Math.PI) * 0.05, 0.35D, Math.cos(counter / 4.0 + Math.PI) * 0.05);

            spawnFlameParticle(-0.5D, 0.5D, 0.05D, 0.0D);
            spawnFlameParticle(0.5D, -0.5D, 0.0D, 0.05D);
            spawnFlameParticle(1.5D, 0.5D, -0.05D, 0.0D);
            spawnFlameParticle(0.5D, 1.5D, 0.0D, -0.05D);

        }

        // sounds
        if (counter % 4 == 0) {
            worldObj.playSoundEffect(
                    this.xCoord + 0.5,
                    this.yCoord + 0.5,
                    this.zCoord + 0.5,
                    "mob.ghast.fireball",
                    1.0F + worldObj.rand.nextFloat(),
                    worldObj.rand.nextFloat() * 0.7F + 0.3F);

        } else if (counter == 1) {
            worldObj.playSoundEffect(
                    this.xCoord + 0.5,
                    this.yCoord + 0.5,
                    this.zCoord + 0.5,
                    "fire.ignite",
                    1.0F + worldObj.rand.nextFloat(),
                    worldObj.rand.nextFloat() * 0.7F + 0.3F);
        }

        // actual fire effects
        if (!worldObj.isRemote) {
            if (counter % DAMAGE_INTERVAL == 0) {
                // find entities in the area of effect
                List<Entity> entitiesInRange = worldObj.getEntitiesWithinAABB(
                        Entity.class,
                        AxisAlignedBB.getBoundingBox(
                                this.xCoord - 2,
                                this.yCoord,
                                this.zCoord - 2,
                                this.xCoord + 2,
                                this.yCoord + 4,
                                this.zCoord + 2));
                // fire!
                for (Entity entity : entitiesInRange) {
                    if (!entity.isImmuneToFire()) {
                        entity.attackEntityFrom(DamageSource.inFire, 2);
                        entity.setFire(15);
                    }
                }
            }
        }
    }

    private void spawnFlameParticle(double offsetX, double offsetZ, double motionX, double motionZ) {
        TwilightForestMod.proxy.spawnParticle(
                worldObj,
                "largeflame",
                xCoord + offsetX,
                yCoord + 1.0D,
                zCoord + offsetZ,
                motionX,
                0.5D,
                motionZ);
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.nextMeta = par1NBTTagCompound.getInteger("NextMeta");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("NextMeta", this.nextMeta);
    }
}
