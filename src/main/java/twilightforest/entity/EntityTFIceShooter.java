package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;

public class EntityTFIceShooter extends EntityMob implements IRangedAttackMob {

    private static final float PROJECTILE_SPEED = 0.6F;
    private static final float PROJECTILE_INACCURACY = 12.0F;

    public EntityTFIceShooter(World world) {
        super(world);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25D, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.setSize(0.8F, 1.8F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.6F;
    }

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    @Override
    protected Item getDropItem() {
        return Items.snowball;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            return;
        }
        // make snow particles
        for (int i = 0; i < 3; i++) {
            float px = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;
            float py = this.getEyeHeight() + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
            float pz = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F;

            TwilightForestMod.proxy.spawnParticle(
                    this.worldObj,
                    "snowguardian",
                    this.lastTickPosX + px,
                    this.lastTickPosY + py,
                    this.lastTickPosZ + pz,
                    0,
                    0,
                    0);
        }
    }

    @Override
    protected String getLivingSound() {
        return TwilightForestMod.ID + ":mob.ice.noise";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    protected String getHurtSound() {
        return TwilightForestMod.ID + ":mob.ice.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected String getDeathSound() {
        return TwilightForestMod.ID + ":mob.ice.death";
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
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        if (this.worldObj.isRemote || target == null || !target.isEntityAlive()) {
            return;
        }

        EntityTFIceSnowball snowball = new EntityTFIceSnowball(this.worldObj, this);
        double dx = target.posX - this.posX;
        double dy = target.posY + target.getEyeHeight() - 1.100000023841858D - snowball.posY;
        double dz = target.posZ - this.posZ;
        double arc = MathHelper.sqrt_double(dx * dx + dz * dz) * 0.2F;
        snowball.setThrowableHeading(dx, dy + arc, dz, PROJECTILE_SPEED, PROJECTILE_INACCURACY);

        if (this.worldObj.spawnEntityInWorld(snowball)) {
            this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        }
    }
}
