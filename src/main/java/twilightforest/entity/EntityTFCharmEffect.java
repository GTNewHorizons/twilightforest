package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import twilightforest.item.TFItems;

public class EntityTFCharmEffect extends EntityItem {

    private static final int DATA_OWNER = 17;
    private static final int DATA_ITEMID = 16;
    private static final double DISTANCE = 1;
    private EntityLivingBase orbiting;
    private double newPosX;
    private double newPosY;
    private double newPosZ;
    private double newRotationYaw;
    private double newRotationPitch;
    private int newPosRotationIncrements;
    private float offset = 0;

    // client constructor
    public EntityTFCharmEffect(World par1World) {
        super(par1World);
        this.setSize(0.25F, 0.25F);

        this.setItemID(TFItems.charmOfLife1);
        // this.setEntityItemStack(new ItemStack(TFItems.charmOfLife1));
    }

    // server constructor
    public EntityTFCharmEffect(World par1World, EntityLivingBase par2EntityLiving, Item item, float offset) {
        super(par1World);
        this.setSize(0.25F, 0.25F);

        this.orbiting = par2EntityLiving;
        if (this.orbiting instanceof EntityPlayer) this.setOwner(((EntityPlayer) orbiting).getDisplayName());
        this.setItemID(item);
        this.setEntityItemStack(new ItemStack(item));

        Vec3 look = Vec3.createVectorHelper(DISTANCE, 0, 0);
        this.offset = offset;
        look.rotateAroundY(offset);

        this.setLocationAndAngles(
                par2EntityLiving.posX,
                par2EntityLiving.posY + par2EntityLiving.getEyeHeight() - 0.5d,
                par2EntityLiving.posZ,
                par2EntityLiving.rotationYaw,
                par2EntityLiving.rotationPitch);
        this.posX += look.xCoord;
        this.posY += look.yCoord;
        this.posZ += look.zCoord;
        this.yOffset = 0.0F;

    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        this.onEntityUpdate();

        if (this.newPosRotationIncrements > 0) {
            double var1 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
            double var3 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
            double var5 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
            double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + var7 / this.newPosRotationIncrements);
            this.rotationPitch = (float) ((double) this.rotationPitch
                    + (this.newRotationPitch - (double) this.rotationPitch) / this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        float rotation = this.ticksExisted / 5.0F + offset;

        if (this.orbiting == null) {
            this.orbiting = getOwner();
        }

        if (this.orbiting != null && !worldObj.isRemote) {
            this.setLocationAndAngles(
                    orbiting.posX,
                    orbiting.posY + orbiting.getEyeHeight() - 0.5d,
                    orbiting.posZ,
                    orbiting.rotationYaw,
                    orbiting.rotationPitch);

            Vec3 look = Vec3.createVectorHelper(DISTANCE, 0, 0);
            look.rotateAroundY(rotation);
            this.posX += look.xCoord;
            this.posY += Math.sin(this.ticksExisted / 3.0F + offset) / 8;
            this.posZ += look.zCoord;
        }

        if (this.getItemID() > 0) {
            for (int i = 0; i < 3; i++) {
                double dx = posX + 0.25 * (rand.nextDouble() - rand.nextDouble());
                double dy = posY + 0.25 * (rand.nextDouble() - rand.nextDouble());
                double dz = posZ + 0.25 * (rand.nextDouble() - rand.nextDouble());

                worldObj.spawnParticle("iconcrack_" + this.getItemID(), dx, dy, dz, 0, 0.2, 0);
            }
        }

        if (this.ticksExisted > 200 || this.orbiting == null || this.orbiting.isDead) {
            this.setDead();
        }
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
        this.yOffset = 0.0F;
        this.newPosX = par1;
        this.newPosY = par3;
        this.newPosZ = par5;
        this.newRotationYaw = par7;
        this.newRotationPitch = par8;
        this.newPosRotationIncrements = par9;
    }

    protected void entityInit() {
        this.dataWatcher.addObject(DATA_ITEMID, 0);
        this.dataWatcher.addObject(DATA_OWNER, "");
        super.entityInit();
    }

    public String getOwnerName() {
        return this.dataWatcher.getWatchableObjectString(DATA_OWNER);
    }

    public void setOwner(String par1Str) {
        this.dataWatcher.updateObject(DATA_OWNER, par1Str);
    }

    public EntityLivingBase getOwner() {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }

    public int getItemID() {
        return this.dataWatcher.getWatchableObjectInt(DATA_ITEMID);
    }

    public void setItemID(Item charmOfLife1) {
        this.dataWatcher.updateObject(DATA_ITEMID, Item.getIdFromItem(charmOfLife1));
    }

    public void setItemID(int id) {
        this.dataWatcher.updateObject(DATA_ITEMID, id);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        par1NBTTagCompound.setString("Owner", this.getOwnerName());
        par1NBTTagCompound.setShort("ItemID", (short) this.getItemID());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        this.setOwner(par1NBTTagCompound.getString("Owner"));
        this.setItemID(par1NBTTagCompound.getShort("ItemID"));
        super.writeEntityToNBT(par1NBTTagCompound);
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        // Can't pick it up
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will be removed from the world.
     */
    public boolean combineItems(EntityItem p_70289_1_) {
        // No, we don't
        return false;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement() {
        // Magic is not affected by water
        return false;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int amount) {
        // No, we don't
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        // Can't be attacked, magic
        return false;
    }

}
