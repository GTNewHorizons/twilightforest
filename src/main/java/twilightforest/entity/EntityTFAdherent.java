package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class EntityTFAdherent extends EntityMob implements IRangedAttackMob, ITFCharger {

    private static final int CHARGE_FLAG = 17;

    public EntityTFAdherent(World world) {
        super(world);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(4, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        this.setSize(0.8F, 2.2F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(CHARGE_FLAG, (byte) 0);
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

    @Override
    protected void dropFewItems(boolean hitByPlayer, int looting) {
        if (hitByPlayer) {
            ItemStack item = new ItemStack(TFItems.metaItem, 1 + looting, 0);
            int dropCount = this.rand.nextInt(3);

            for (int i = 0; i < dropCount; ++i) {
                this.entityDropItem(item, 1f);
            }
        }
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (damageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) damageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) damageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
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
     * In this case we're not charging like a bull, but charging up a ranged attack
     * 
     * @return
     */
    public boolean isCharging() {
        return dataWatcher.getWatchableObjectByte(CHARGE_FLAG) != 0;
    }

    public void setCharging(boolean flag) {
        if (flag) {
            dataWatcher.updateObject(CHARGE_FLAG, (byte) 127);
        } else {
            dataWatcher.updateObject(CHARGE_FLAG, (byte) 0);
        }
    }
}
