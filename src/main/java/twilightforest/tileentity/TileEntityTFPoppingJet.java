package twilightforest.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import twilightforest.block.BlockTFFireJet;
import twilightforest.block.TFBlocks;

public class TileEntityTFPoppingJet extends TileEntity {

    private static final int POP_DURATION_TICKS = 80;
    private static final int POP_INTERVAL_TICKS = 20;
    private static final String NEXT_META_TAG = "NextMeta";

    int counter;
    int nextMeta;

    public TileEntityTFPoppingJet() {
        this(BlockTFFireJet.META_JET_FLAME);
    }

    public TileEntityTFPoppingJet(int nextMetadata) {
        this.nextMeta = nextMetadata;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        if (++counter >= POP_DURATION_TICKS) {
            counter = 0;
            // turn to flame
            if (!worldObj.isRemote && worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == TFBlocks.fireJet) {
                worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, TFBlocks.fireJet, this.nextMeta, 3);
            }
            this.invalidate();
        } else {
            if (counter % POP_INTERVAL_TICKS == 0) {
                worldObj.spawnParticle(
                        "lava",
                        this.xCoord + 0.5,
                        this.yCoord + 1.5,
                        this.zCoord + 0.5,
                        0.0D,
                        0.0D,
                        0.0D);
                worldObj.playSoundEffect(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        "liquid.lavapop",
                        0.2F + worldObj.rand.nextFloat() * 0.2F,
                        0.9F + worldObj.rand.nextFloat() * 0.15F);
            }

        }

    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.nextMeta = par1NBTTagCompound.getInteger(NEXT_META_TAG);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger(NEXT_META_TAG, this.nextMeta);
    }
}
