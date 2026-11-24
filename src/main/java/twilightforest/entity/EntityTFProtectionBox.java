package twilightforest.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityTFProtectionBox extends Entity {

    public int lifeTime;
    public int sizeX;
    public int sizeY;
    public int sizeZ;

    public EntityTFProtectionBox(World world) {
        super(world);
        this.lifeTime = 60;
        this.sizeX = this.sizeY = this.sizeZ = 1;
        this.setSize(1.0F, 1.0F);
    }

    public EntityTFProtectionBox(World worldObj, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this(worldObj);

        this.setLocationAndAngles(minX, minY, minZ, 0.0F, 0.0F);

        sizeX = Math.abs(maxX - minX) + 1;
        sizeY = Math.abs(maxY - minY) + 1;
        sizeZ = Math.abs(maxZ - minZ) + 1;

        this.setSize(Math.max(sizeX, sizeZ), sizeY);

        // System.out.println("Made new box");

    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!worldObj.isRemote && --lifeTime <= 0) {
            setDead();
        }

    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1) {
        return 1.0F;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        lifeTime = nbt.getInteger("LifeTime");
        sizeX = Math.max(1, nbt.getInteger("SizeX"));
        sizeY = Math.max(1, nbt.getInteger("SizeY"));
        sizeZ = Math.max(1, nbt.getInteger("SizeZ"));
        setSize(Math.max(sizeX, sizeZ), sizeY);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("LifeTime", lifeTime);
        nbt.setInteger("SizeX", sizeX);
        nbt.setInteger("SizeY", sizeY);
        nbt.setInteger("SizeZ", sizeZ);
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

}
