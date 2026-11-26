package twilightforest.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class TFWeatherData extends WorldSavedData {

    public static final String ID = "weather";

    private static final String TAG_TIME = "Time";
    private static final String TAG_RAINING = "Raining";
    private static final String TAG_RAIN_TIME = "RainTime";
    private static final String TAG_THUNDERING = "Thundering";
    private static final String TAG_THUNDER_TIME = "ThunderTime";

    public long worldTime;
    public boolean raining;
    public int rainTime;
    public boolean thundering;
    public int thunderTime;

    @SuppressWarnings("unused")
    public TFWeatherData() {
        super(ID);
    }

    public TFWeatherData(String id) {
        super(id);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        worldTime = nbt.getLong(TAG_TIME);
        raining = nbt.getBoolean(TAG_RAINING);
        rainTime = nbt.getInteger(TAG_RAIN_TIME);
        thundering = nbt.getBoolean(TAG_THUNDERING);
        thunderTime = nbt.getInteger(TAG_THUNDER_TIME);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setLong(TAG_TIME, worldTime);
        nbt.setBoolean(TAG_RAINING, raining);
        nbt.setInteger(TAG_RAIN_TIME, rainTime);
        nbt.setBoolean(TAG_THUNDERING, thundering);
        nbt.setInteger(TAG_THUNDER_TIME, thunderTime);
    }

    public static TFWeatherData get(World world) {
        MapStorage storage = world.perWorldStorage;

        TFWeatherData data = (TFWeatherData) storage.loadData(TFWeatherData.class, ID);

        if (data == null) {
            data = new TFWeatherData(ID);
            storage.setData(ID, data);
        }

        return data;
    }
}
