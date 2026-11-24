package twilightforest.entity.passive;

import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTFTinyFirefly extends EntityWeatherEffect {

    int lifeTime;
    int halfLife;

    public float glowSize;

    public EntityTFTinyFirefly(World world, double d, double d1, double d2) {
        super(world);
        setLocationAndAngles(d, d1, d2, 0.0F, 0.0F);

        lifeTime = 10 + rand.nextInt(21);
        halfLife = lifeTime / 2;

        glowSize = 0.2f + (rand.nextFloat() * 0.6f);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (--lifeTime <= 0) {
            setDead();
        }

    }

    public float getGlowBrightness() {
        if (halfLife <= 0) {
            return 0.0F;
        }
        float brightness = lifeTime <= halfLife ? (float) lifeTime / halfLife
                : 1.0F - (float) (lifeTime - halfLife) / halfLife;
        return Math.max(0.0F, Math.min(1.0F, brightness));
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

}
