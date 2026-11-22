package twilightforest.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTFCake extends TileEntity {

    private static final String NBT_SPRINKLED = "Sprinkled";

    private boolean isSprinkled;

    public boolean getSprinkled() {
        return isSprinkled;
    }

    public void setSprinkled(boolean sprinkled) {
        if (isSprinkled != sprinkled) {
            isSprinkled = sprinkled;
            markDirty();
        }
    }

    @Override
    public Packet getDescriptionPacket() {

        NBTTagCompound tagCompound = new NBTTagCompound();

        this.writeToNBT(tagCompound);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {

        NBTTagCompound tagCompound = packet.func_148857_g();

        this.readFromNBT(tagCompound);
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey(NBT_SPRINKLED)) isSprinkled = par1NBTTagCompound.getBoolean(NBT_SPRINKLED);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean(NBT_SPRINKLED, isSprinkled);
    }

    /**
     * Determines if this TileEntity requires update calls.
     *
     * @return False, since the cake has no per-tick behavior
     */
    @Override
    public boolean canUpdate() {
        return false;
    }

}
