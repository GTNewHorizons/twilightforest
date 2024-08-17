
package twilightforest.entity;

import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class EntityTFSkeletonDruid extends EntityMob implements IRangedAttackMob {

    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 60, 10.0F);
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(
            this,
            EntityPlayer.class,
            1.2D,
            false);

    public EntityTFSkeletonDruid(World world) {
        super(world);
        // texture = TwilightForestMod.MODEL_DIR + "skeletondruid.png";

        // this.moveSpeed = 0.25F;
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        // this.tasks.addTask(4, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        if (world != null && !world.isRemote) {
            this.setCombatTask();
        }

        // this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_hoe));

    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    public boolean isAIEnabled() {
        return true;
    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D); // max health
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D); // movement speed
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound() {
        return "mob.skeleton.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "mob.skeleton.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "mob.skeleton.death";
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    protected void func_145780_a(int par1, int par2, int par3, Block par4) {
        this.playSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    @Override
    protected Item getDropItem() {
        return TFItems.torchberries;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean par1, int lootingModifier) {
        int numberOfItemsToDrop;
        int i;

        numberOfItemsToDrop = this.rand.nextInt(3 + lootingModifier);

        for (i = 0; i < numberOfItemsToDrop; ++i) {
            this.dropItem(TFItems.torchberries, 1);
        }

        numberOfItemsToDrop = this.rand.nextInt(3 + lootingModifier);

        for (i = 0; i < numberOfItemsToDrop; ++i) {
            this.dropItem(Items.bone, 1);
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
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

    /**
     * Makes entity wear random armor based on difficulty
     */
    protected void addRandomArmor() {
        super.addRandomArmor();
        this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_hoe));
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_) {
        p_110161_1_ = super.onSpawnWithEgg(p_110161_1_);

        // this.tasks.addTask(4, this.aiArrowAttack);
        this.addRandomArmor();
        // this.enchantEquipment();

        this.setCanPickUpLoot(
                this.rand.nextFloat() < 0.55F * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ));

        this.setCombatTask();

        if (this.getEquipmentInSlot(4) == null) {
            Calendar calendar = this.worldObj.getCurrentDate();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
                this.setCurrentItemOrArmor(
                        4,
                        new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        return p_110161_1_;
    }

    /**
     * sets this entity's combat AI.
     */
    public void setCombatTask() {
        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        ItemStack itemstack = this.getHeldItem();

        if (itemstack != null && itemstack.getItem() instanceof ItemHoe) {
            this.tasks.addTask(4, this.aiArrowAttack);
        } else {
            this.tasks.addTask(4, this.aiAttackOnCollide);
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase attackTarget, float extraDamage) {
        EntityTFNatureBolt natureBolt = new EntityTFNatureBolt(this.worldObj, this);
        this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));

        natureBolt.setTarget(attackTarget);

        double tx = attackTarget.posX - this.posX;
        double ty = attackTarget.posY + attackTarget.getEyeHeight() - 2.699999988079071D - this.posY;
        double tz = attackTarget.posZ - this.posZ;
        float heightOffset = MathHelper.sqrt_double(tx * tx + tz * tz) * 0.2F;
        natureBolt.setThrowableHeading(tx, ty + heightOffset, tz, 0.6F, 6.0F); // 0.6 speed, 6.0 inaccuracy
        this.worldObj.spawnEntityInWorld(natureBolt);

    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel() {
        boolean valid = false;
        int dx = MathHelper.floor_double(this.posX);
        int dy = MathHelper.floor_double(this.boundingBox.minY);
        int dz = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, dx, dy, dz) > this.rand.nextInt(32)) {
            valid = false;
        } else {
            int light = this.worldObj.getBlockLightValue(dx, dy, dz);

            valid = light <= this.rand.nextInt(12);

        }
        return valid;
    }
}
