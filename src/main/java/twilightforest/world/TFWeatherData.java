package twilightforest.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class TFWeatherData extends WorldSavedData {

    public static final String ID = "weather";

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
        worldTime = nbt.getLong("Time");
        raining = nbt.getBoolean("Raining");
        rainTime = nbt.getInteger("RainTime");
        thundering = nbt.getBoolean("Thundering");
        thunderTime = nbt.getInteger("ThunderTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setLong("Time", worldTime);
        nbt.setBoolean("Raining", raining);
        nbt.setInteger("RainTime", rainTime);
        nbt.setBoolean("Thundering", thundering);
        nbt.setInteger("ThunderTime", thunderTime);
    }

    public static TFWeatherData get(World world) {
        MapStorage storage = world.perWorldStorage;

        TFWeatherData data = (TFWeatherData) storage.loadData(TFWeatherData.class, "weather");

        if (data == null) {
            data = new TFWeatherData("weather");
            storage.setData("weather", data);
        }

        return data;
    }
}
