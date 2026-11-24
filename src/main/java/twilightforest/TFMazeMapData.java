package twilightforest;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.MapData;

public class TFMazeMapData extends MapData {

    private static final String Y_CENTER_TAG = "yCenter";

    public int yCenter;

    public TFMazeMapData(String mapName) {
        super(mapName);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.yCenter = data.getInteger(Y_CENTER_TAG);
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger(Y_CENTER_TAG, this.yCenter);
    }
}
