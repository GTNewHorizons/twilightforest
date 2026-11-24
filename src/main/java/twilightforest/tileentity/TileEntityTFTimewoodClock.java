package twilightforest.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTFTimewoodClock extends TileEntity {

    private static final String MUTED_TAG = "Muted";

    private boolean muted;

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        if (this.muted != muted) {
            this.muted = muted;
            markDirty();
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        muted = nbt.getBoolean(MUTED_TAG);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean(MUTED_TAG, muted);
    }
}
