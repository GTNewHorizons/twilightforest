package twilightforest.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;

public class EntityTFBoar extends EntityPig {

    public EntityTFBoar(World world) {
        super(world);
        // texture = TwilightForestMod.MODEL_DIR + "wildboar.png";
        setSize(0.9F, 0.9F);
    }

    public EntityTFBoar(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    /**
     * What is our baby?!
     */
    @Override
    public EntityPig createChild(EntityAgeable entityanimal) {
        return new EntityTFBoar(worldObj);
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) par1DamageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
        }
    }

}
