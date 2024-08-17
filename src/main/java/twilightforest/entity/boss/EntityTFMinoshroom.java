package twilightforest.entity.boss;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMinotaur;
import twilightforest.item.TFItems;

public class EntityTFMinoshroom extends EntityTFMinotaur {

    public EntityTFMinoshroom(World par1World) {
        super(par1World);
        // this.texture = TwilightForestMod.MODEL_DIR + "minoshroomtaur.png";
        this.setSize(1.49F, 2.9F);

        this.experienceValue = 100;

        this.setCurrentItemOrArmor(0, new ItemStack(TFItems.minotaurAxe));
        this.equipmentDropChances[0] = 0.0F;
    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(120.0D); // max health
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected Item getDropItem() {
        return TFItems.meefStroganoff;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    @Override
    protected void dropFewItems(boolean par1, int par2) {
        int numDrops = this.rand.nextInt(4) + 2 + this.rand.nextInt(1 + par2);

        for (int i = 0; i < numDrops; ++i) {
            this.dropItem(TFItems.meefStroganoff, 1);
        }

        // trophy
        this.entityDropItem(new ItemStack(TFItems.trophy, 1, 5), 0);
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        despawnIfInvalid();
        super.onLivingUpdate();
    }

    protected void despawnIfInvalid() {
        // check to see if we're valid
        if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            despawnMe();
        }
    }

    /**
     * Despawn the minoshroom, and restore the boss spawner at our home location, if set
     */
    protected void despawnMe() {
        if (this.hasHome()) {
            ChunkCoordinates home = this.getHomePosition();
            worldObj.setBlock(home.posX, home.posY, home.posZ, TFBlocks.bossSpawner, 6, 2);
        }
        setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        ChunkCoordinates home = this.getHomePosition();
        nbttagcompound.setTag("Home", newDoubleNBTList(home.posX, home.posY, home.posZ));
        nbttagcompound.setBoolean("HasHome", this.hasHome());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("Home", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Home", 6);
            int hx = (int) nbttaglist.func_150309_d(0);
            int hy = (int) nbttaglist.func_150309_d(1);
            int hz = (int) nbttaglist.func_150309_d(2);
            this.setHomeArea(hx, hy, hz, 20);
        }
        if (!nbttagcompound.getBoolean("HasHome")) {
            this.detachHome();
        }
    }

    /**
     * Drop the equipment for this entity.
     */
    protected void dropEquipment(boolean par1, int par2) {
        super.dropEquipment(par1, par2);
        this.entityDropItem(new ItemStack(TFItems.minotaurAxe), 0.0F);

    }

    /**
     * Basically a public getter for living sounds
     */
    public String getTrophySound() {
        return this.getLivingSound();
    }

}
