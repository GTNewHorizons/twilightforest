package twilightforest.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.entity.ai.EntityAITFThrowRider;
import twilightforest.item.TFItems;

public class EntityTFYeti extends EntityMob {

    private static final int ANGER_FLAG = 16;
    private static final double CALM_FOLLOW_RANGE = 4.0D;
    private static final double ANGRY_FOLLOW_RANGE = 40.0D;

    public EntityTFYeti(World par1World) {
        super(par1World);
        this.setSize(1.4F, 2.4F);

        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAITFThrowRider(this, 1.0F));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(3, new EntityAIWander(this, 1.0F));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false));
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D); // max health
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.38D); // movement speed
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(CALM_FOLLOW_RANGE);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(ANGER_FLAG, (byte) 0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.riddenByEntity != null) {
            // captured riders cant sneak before the throw task finishes
            if (this.riddenByEntity.isSneaking()) {
                this.riddenByEntity.setSneaking(false);
            }

            this.getLookHelper().setLookPositionWithEntity(this.riddenByEntity, 100.0F, 100.0F);
            Vec3 riderPos = this.getRiderPosition();
            this.func_145771_j(riderPos.xCoord, riderPos.yCoord, riderPos.zCoord);
        }
    }

    /**
     * Pick up things we attack!
     */
    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (this.worldObj.isRemote) {
            return false;
        }

        boolean attacked = super.attackEntityAsMob(target);
        if (this.riddenByEntity == null && target != this
                && target.ridingEntity == null
                && target instanceof net.minecraft.entity.EntityLivingBase
                && target.isEntityAlive()
                && !target.isEntityInvulnerable()
                && !(target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage)) {
            // the Yetis base melee damage is zero, grabbing is its actual attack
            this.playSound(TwilightForestMod.ID + ":mob.yeti.grab", 4.0F, 0.75F + this.rand.nextFloat() * 0.25F);
            target.mountEntity(this);
            return true;
        }
        return attacked;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        boolean damaged = super.attackEntityFrom(source, damage);
        if (damaged && !this.worldObj.isRemote && source.getEntity() != null) {
            this.setAngry(true);
        }
        return damaged;
    }

    /**
     * Determines whether this yeti is angry or not.
     */
    public boolean isAngry() {
        return (this.dataWatcher.getWatchableObjectByte(ANGER_FLAG) & 2) != 0;
    }

    /**
     * Sets whether this yeti is angry or not.
     */
    public void setAngry(boolean angry) {
        byte previous = this.dataWatcher.getWatchableObjectByte(ANGER_FLAG);
        byte updated = (byte) (angry ? previous | 2 : previous & ~2);
        if (updated != previous) {
            this.dataWatcher.updateObject(ANGER_FLAG, updated);
        }
        // clearing anger must also restore the peaceful detection radius
        this.getEntityAttribute(SharedMonsterAttributes.followRange)
                .setBaseValue(angry ? ANGRY_FOLLOW_RANGE : CALM_FOLLOW_RANGE);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Angry", this.isAngry());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setAngry(par1NBTTagCompound.getBoolean("Angry"));
    }

    /**
     * Put the player out in front of where we are
     */
    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            Vec3 riderPos = this.getRiderPosition();

            this.riddenByEntity.setPosition(riderPos.xCoord, riderPos.yCoord, riderPos.zCoord);
        }
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getMountedYOffset() {
        return 2.25D;
    }

    /**
     * Used to both get a rider position and to push out of blocks
     */
    public Vec3 getRiderPosition() {
        if (this.riddenByEntity != null) {
            float distance = 0.4F;

            double var1 = Math.cos((this.rotationYaw + 90) * Math.PI / 180.0D) * distance;
            double var3 = Math.sin((this.rotationYaw + 90) * Math.PI / 180.0D) * distance;

            return Vec3.createVectorHelper(
                    this.posX + var1,
                    this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(),
                    this.posZ + var3);
        } else {
            return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        }
    }

    /**
     * If a rider of this entity can interact with this entity. Should return true on the ridden entity if so.
     *
     * @return if the entity can be interacted with from a rider
     */
    public boolean canRiderInteract() {
        return true;
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (!this.worldObj.isRemote && par1DamageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) par1DamageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            return false;
        }
        // snow biomes bypass the light check, not collision and liquid checks
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getBiomeGenForCoords(x, z) == TFBiomeBase.tfSnow) {
            return this.worldObj.checkNoEntityCollision(this.boundingBox)
                    && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                    && !this.worldObj.isAnyLiquid(this.boundingBox);
        }
        return super.getCanSpawnHere();
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    @Override
    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);

        if (worldObj.getBiomeGenForCoords(x, z) == TFBiomeBase.tfSnow) {
            return true;
        } else {
            return super.isValidLightLevel();
        }
    }

    @Override
    protected Item getDropItem() {
        return TFItems.arcticFur;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
    protected String getLivingSound() {
        return TwilightForestMod.ID + ":mob.yeti.growl";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound() {
        return TwilightForestMod.ID + ":mob.yeti.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound() {
        return TwilightForestMod.ID + ":mob.yeti.death";
    }

}
