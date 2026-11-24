package twilightforest.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFTowerDevice;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFTowerGhast;
import twilightforest.entity.boss.EntityTFUrGhast;

public class TileEntityTFGhastTrapActive extends TileEntity {

    public int counter = 0;

    public Random rand = new Random();

    private void spawnTrapParticle(double motionX, double motionY, double motionZ) {
        TwilightForestMod.proxy.spawnParticle(
                worldObj,
                "ghasttrap",
                xCoord + 0.5D,
                yCoord + 1.0D,
                zCoord + 0.5D,
                motionX,
                motionY,
                motionZ);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {

        ++counter;
        if (!worldObj.isRemote) {
            markDirty();
        }

        if (worldObj.isRemote) {
            // smoke when done
            if (counter > 100 && counter % 4 == 0) {
                TwilightForestMod.proxy.spawnParticle(
                        this.worldObj,
                        "hugesmoke",
                        this.xCoord + 0.5,
                        this.yCoord + 0.95,
                        this.zCoord + 0.5,
                        Math.cos(counter / 10.0) * 0.05,
                        0.25D,
                        Math.sin(counter / 10.0) * 0.05);
            } else if (counter < 100) {
                double dx = Math.cos(counter / 10.0) * 2.5;
                double dy = 20D;
                double dz = Math.sin(counter / 10.0) * 2.5;

                spawnTrapParticle(dx, dy, dz);
                spawnTrapParticle(-dx, dy, -dz);
                spawnTrapParticle(-dx, dy / 2, dz);
                spawnTrapParticle(dx, dy / 2, -dz);
                spawnTrapParticle(dx / 2, dy / 4, dz / 2);
                spawnTrapParticle(-dx / 2, dy / 4, -dz / 2);
            }

            // appropriate sound
            if (counter < 30) {
                worldObj.playSound(
                        this.xCoord + 0.5D,
                        this.yCoord + 1.5D,
                        this.zCoord + 0.5D,
                        TwilightForestMod.ID + ":mob.urghast.trapwarmup",
                        1.0F,
                        4.0F,
                        false);
            } else if (counter < 80) {
                worldObj.playSound(
                        this.xCoord + 0.5D,
                        this.yCoord + 1.5D,
                        this.zCoord + 0.5D,
                        TwilightForestMod.ID + ":mob.urghast.trapon",
                        1.0F,
                        4.0F,
                        false);
            } else {
                worldObj.playSound(
                        this.xCoord + 0.5D,
                        this.yCoord + 1.5D,
                        this.zCoord + 0.5D,
                        TwilightForestMod.ID + ":mob.urghast.trapspindown",
                        1.0F,
                        4.0F,
                        false);
            }
        }

        if (!worldObj.isRemote) {
            // trap nearby ghasts
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                    this.xCoord,
                    (double) this.yCoord + 16,
                    this.zCoord,
                    this.xCoord + 1,
                    this.yCoord + 16 + 1,
                    this.zCoord + 1).expand(6D, 16D, 6D);

            List<EntityGhast> nearbyGhasts = worldObj.getEntitiesWithinAABB(EntityGhast.class, aabb);

            for (EntityGhast ghast : nearbyGhasts) {
                if (!ghast.isEntityAlive()) {
                    continue;
                }
                if (ghast instanceof EntityTFTowerGhast towerGhast) {
                    towerGhast.setInTrap();
                }
                boolean isBoss = ghast instanceof EntityTFUrGhast;
                if (isBoss) {
                    // stop boss tantrum
                    ((EntityTFUrGhast) ghast).stopTantrum();
                }

                // pull regular ghasts and the ur ghast toward their original target heights
                ghast.motionX = (ghast.posX - this.xCoord - 0.5) * -0.1;
                ghast.motionY = (ghast.posY - this.yCoord - (isBoss ? 2.5 : 1.5)) * -0.1;
                ghast.motionZ = (ghast.posZ - this.zCoord - 0.5) * -0.1;

                if (rand.nextInt(10) == 0) {
                    ghast.attackEntityFrom(DamageSource.generic, isBoss ? 3 : 10);
                }

            }

            if (counter >= 120) {
                // deactivate
                worldObj.setBlock(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        TFBlocks.towerDevice,
                        BlockTFTowerDevice.META_GHASTTRAP_INACTIVE,
                        3);
                // worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, TFBlocks.towerDevice, 4);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("TrapTicks", counter);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        counter = Math.max(0, Math.min(120, tag.getInteger("TrapTicks")));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("TrapTicks", counter);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 3, tag);
    }

    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        counter = Math.max(0, Math.min(120, packet.func_148857_g().getInteger("TrapTicks")));
    }
}
