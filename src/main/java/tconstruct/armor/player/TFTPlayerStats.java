package tconstruct.armor.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class TFTPlayerStats implements IExtendedEntityProperties {

    public static final String PROP_NAME = "TF/TConstruct";

    public WeakReference<EntityPlayer> player;

    public boolean twilightManual = false;

    public TFTPlayerStats(EntityPlayer entityplayer) {
        this.player = new WeakReference<>(entityplayer);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tTag = new NBTTagCompound();
        tTag.setBoolean("twilightManual", this.twilightManual);
        compound.setTag(PROP_NAME, tTag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound) compound.getTag(PROP_NAME);
        if (properties != null) {
            this.twilightManual = properties.getBoolean("twilightManual");
        }
    }

    @Override
    public void init(Entity entity, World world) {
        this.player = new WeakReference<>((EntityPlayer) entity);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(TFTPlayerStats.PROP_NAME, new TFTPlayerStats(player));
    }

    public static TFTPlayerStats get(EntityPlayer player) {
        return (TFTPlayerStats) player.getExtendedProperties(PROP_NAME);
    }
}
