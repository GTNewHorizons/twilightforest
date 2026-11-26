package twilightforest.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.entity.ai.EntityAITFBreathAttack;
import twilightforest.item.TFItems;

public class EntityTFWinterWolf extends EntityTFHostileWolf implements IBreathAttacker {

    private static final int BREATH_FLAG = 21;
    private static final int BREATH_PARTICLES_PER_TICK = 10;
    private static final int BREATH_SOUND_INTERVAL = 10;
    private static final int BREATH_HIT_INTERVAL = 10;
    public static final int BREATH_DURATION = 10;
    public static final int BREATH_DAMAGE = 2;

    private int breathHitCooldown;
    private int breathSoundCooldown;

    public EntityTFWinterWolf(World world) {
        super(world);
        this.setSize(1.4F, 1.9F);

        this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAITFBreathAttack(this, 1.0F, 5.0F, 30, 0.1F));
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));

        this.targetTasks.taskEntries.clear();
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(BREATH_FLAG, (byte) 0);
    }

    public int getAttackStrength(Entity target) {
        return 6;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return !this.worldObj.isRemote
                && target.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength(target));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.worldObj.isRemote) {
            if (this.isBreathing()) {
                this.spawnBreathParticles();
            }
            return;
        }

        if (this.breathHitCooldown > 0) {
            --this.breathHitCooldown;
        }
        if (this.breathSoundCooldown > 0) {
            --this.breathSoundCooldown;
        }
        if (this.isBreathing()) {
            if (this.breathSoundCooldown == 0) {
                this.playBreathSound();
                this.breathSoundCooldown = BREATH_SOUND_INTERVAL;
            }
        } else {
            // a new breath attack should be audible without an old sound delay
            this.breathSoundCooldown = 0;
        }
    }

    private void spawnBreathParticles() {
        Vec3 look = this.getLookVec();
        double x = this.posX + look.xCoord * 0.5D;
        double y = this.posY + 1.25D + look.yCoord * 0.5D;
        double z = this.posZ + look.zCoord * 0.5D;

        for (int i = 0; i < BREATH_PARTICLES_PER_TICK; ++i) {
            double spread = 5.0D + this.rand.nextDouble() * 2.5D;
            double speed = 3.0D + this.rand.nextDouble() * 0.15D;
            double vx = (look.xCoord + this.rand.nextGaussian() * 0.0075D * spread) * speed;
            double vy = (look.yCoord + this.rand.nextGaussian() * 0.0075D * spread) * speed;
            double vz = (look.zCoord + this.rand.nextGaussian() * 0.0075D * spread) * speed;
            TwilightForestMod.proxy.spawnParticle(this.worldObj, "snowstuff", x, y, z, vx, vy, vz);
        }
    }

    public void playBreathSound() {
        if (!this.worldObj.isRemote) {
            this.worldObj.playSoundAtEntity(this, "mob.ghast.fireball", 0.5F, 0.8F + this.rand.nextFloat() * 0.4F);
        }
    }

    @Override
    protected float getSoundPitch() {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.6F;
    }

    @Override
    public boolean isBreathing() {
        return this.dataWatcher.getWatchableObjectByte(BREATH_FLAG) == 1;
    }

    @Override
    public void setBreathing(boolean breathing) {
        byte state = (byte) (breathing ? 1 : 0);
        if (!this.worldObj.isRemote && this.dataWatcher.getWatchableObjectByte(BREATH_FLAG) != state) {
            this.dataWatcher.updateObject(BREATH_FLAG, state);
        }
    }

    @Override
    public void doBreathAttack(Entity target) {
        if (this.worldObj.isRemote || !(target instanceof EntityLivingBase)
                || target == this
                || this.breathHitCooldown > 0) {
            return;
        }

        if (target.attackEntityFrom(DamageSource.causeMobDamage(this), BREATH_DAMAGE)) {
            ((EntityLivingBase) target)
                    .addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, BREATH_DURATION * 20, 0));
            this.breathHitCooldown = BREATH_HIT_INTERVAL;
        }
    }

    @Override
    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        return this.worldObj.getBiomeGenForCoords(x, z) == TFBiomeBase.tfSnow || super.isValidLightLevel();
    }

    @Override
    protected Item getDropItem() {
        return TFItems.arcticFur;
    }
}
