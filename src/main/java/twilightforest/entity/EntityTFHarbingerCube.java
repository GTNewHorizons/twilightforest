package twilightforest.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class EntityTFHarbingerCube extends EntityMob {

    public EntityTFHarbingerCube(World world) {
        super(world);

        this.setSize(1.9F, 2.4F);

        this.tasks.addTask(1, new EntityAISwimming(this));

        this.tasks.addTask(1, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(2, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23D);
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
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void dropFewItems(boolean hitByPlayer, int looting) {
        if (hitByPlayer) {
            ItemStack item = new ItemStack(TFItems.metaItem, 1 + looting, 1);
            int dropCount = this.rand.nextInt(3);

            for (int i = 0; i < dropCount; ++i) {
                this.entityDropItem(item, 1f);
            }
        }
    }

}
