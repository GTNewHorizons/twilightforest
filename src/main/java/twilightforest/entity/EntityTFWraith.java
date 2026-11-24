package twilightforest.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;

public class EntityTFWraith extends EntityFlying implements IMob {

    private static final double WANDER_DISTANCE = 16.0D;
    private static final double MAX_WAYPOINT_DISTANCE_SQ = 60.0D * 60.0D;
    private static final double MAX_TARGET_DISTANCE_SQ = 64.0D * 64.0D;
    private static final int TARGET_SEARCH_INTERVAL = 20;
    private static final int TARGET_LOST_TIMEOUT = 60;

    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    public int prevAttackCounter;
    public int attackCounter;

    private EntityLivingBase targetedEntity;
    private int aggroCooldown;
    private int unseenTargetTicks;

    public EntityTFWraith(World world) {
        super(world);
    }

    public EntityTFWraith(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);

        // wraiths extend EntityFlying, which doesnt register the attack attribute
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0D);
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void updateEntityActionState() {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            this.setDead();
            return;
        }

        this.despawnEntity();
        if (this.isDead) {
            return;
        }

        this.prevAttackCounter = this.attackCounter;
        this.updateTarget();
        this.updateWaypoint();
        this.moveTowardsWaypoint();
        this.updateCombat();
    }

    private void updateTarget() {
        if (this.targetedEntity != null) {
            if (!this.targetedEntity.isEntityAlive()
                    || this.targetedEntity.getDistanceSqToEntity(this) >= MAX_TARGET_DISTANCE_SQ
                    || this.targetedEntity instanceof EntityPlayer
                            && ((EntityPlayer) this.targetedEntity).capabilities.disableDamage) {
                this.targetedEntity = null;
            } else if (this.canEntityBeSeen(this.targetedEntity)) {
                this.unseenTargetTicks = 0;
            } else if (++this.unseenTargetTicks > TARGET_LOST_TIMEOUT) {
                this.targetedEntity = null;
            }
        }

        // player scans are relatively expensive, dont run them every idle tick
        if (this.targetedEntity == null && --this.aggroCooldown <= 0) {
            Entity candidate = this.findPlayerToAttack();
            this.targetedEntity = candidate instanceof EntityLivingBase ? (EntityLivingBase) candidate : null;
            this.aggroCooldown = TARGET_SEARCH_INTERVAL;
            this.unseenTargetTicks = 0;
        }
    }

    private void updateWaypoint() {
        double dx = this.waypointX - this.posX;
        double dy = this.waypointY - this.posY;
        double dz = this.waypointZ - this.posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq < 1.0D || distanceSq > MAX_WAYPOINT_DISTANCE_SQ) {
            this.waypointX = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * WANDER_DISTANCE;
            this.waypointY = Math
                    .max(1.0D, Math.min(250.0D, this.posY + (this.rand.nextDouble() * 2.0D - 1.0D) * WANDER_DISTANCE));
            this.waypointZ = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * WANDER_DISTANCE;
        }
    }

    private void moveTowardsWaypoint() {
        if (--this.courseChangeCooldown > 0) {
            return;
        }
        this.courseChangeCooldown = this.rand.nextInt(5) + 2;

        // recompute after waypoint changes, the old code divided an old (sometimes zero) distance
        double dx = this.waypointX - this.posX;
        double dy = this.waypointY - this.posY;
        double dz = this.waypointZ - this.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1.0E-4D) {
            return;
        }

        if (this.isCourseTraversable(distance)) {
            this.motionX += dx / distance * 0.1D;
            this.motionY += dy / distance * 0.1D;
            this.motionZ += dz / distance * 0.1D;
        } else {
            // a blocked route should be abandoned, not followed
            this.waypointX = this.posX;
            this.waypointY = this.posY;
            this.waypointZ = this.posZ;
        }
    }

    private void updateCombat() {
        if (this.targetedEntity == null) {
            this.renderYawOffset = this.rotationYaw = -(float) Math.toDegrees(Math.atan2(this.motionX, this.motionZ));
            if (this.attackCounter > 0) {
                --this.attackCounter;
            }
            return;
        }

        double dx = this.targetedEntity.posX - this.posX;
        double dz = this.targetedEntity.posZ - this.posZ;
        this.renderYawOffset = this.rotationYaw = -(float) Math.toDegrees(Math.atan2(dx, dz));

        if (!this.canEntityBeSeen(this.targetedEntity)) {
            this.attackBlockedEntity(this.targetedEntity, this.targetedEntity.getDistanceToEntity(this));
            if (this.attackCounter > 0) {
                --this.attackCounter;
            }
            return;
        }

        // melee contact is checked on every combat tick, even when retargeting
        this.attackEntity(this.targetedEntity, this.targetedEntity.getDistanceToEntity(this));
        if (++this.attackCounter == 20) {
            this.waypointX = this.targetedEntity.posX;
            this.waypointY = this.targetedEntity.posY + this.targetedEntity.height * 0.5D;
            this.waypointZ = this.targetedEntity.posZ;
            this.courseChangeCooldown = 0;
            this.attackCounter = -40;
        }
    }

    protected void attackEntity(Entity entity, float distance) {
        if (this.attackTime <= 0 && distance < 2.0F
                && entity.boundingBox.maxY > this.boundingBox.minY
                && entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        }
    }

    protected void attackBlockedEntity(Entity entity, float distance) {}

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!super.attackEntityFrom(source, damage)) {
            return false;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof EntityLivingBase && attacker != this
                && attacker != this.riddenByEntity
                && attacker != this.ridingEntity
                && !(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.disableDamage)) {
            this.targetedEntity = (EntityLivingBase) attacker;
            this.unseenTargetTicks = 0;
        }
        return true;
    }

    protected Entity findPlayerToAttack() {
        EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return player != null && this.canEntityBeSeen(player) ? player : null;
    }

    private boolean isCourseTraversable(double distance) {
        double dx = (this.waypointX - this.posX) / distance;
        double dy = (this.waypointY - this.posY) / distance;
        double dz = (this.waypointZ - this.posZ) / distance;
        AxisAlignedBB box = this.boundingBox.copy();

        for (int step = 1; step < distance; ++step) {
            box.offset(dx, dy, dz);
            if (!this.worldObj.getCollidingBoundingBoxes(this, box).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected String getLivingSound() {
        return TwilightForestMod.ID + ":mob.wraith.wraith";
    }

    @Override
    protected String getHurtSound() {
        return TwilightForestMod.ID + ":mob.wraith.wraith";
    }

    @Override
    protected String getDeathSound() {
        return TwilightForestMod.ID + ":mob.wraith.wraith";
    }

    @Override
    protected Item getDropItem() {
        return Items.glowstone_dust;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.worldObj.isRemote && damageSource.getEntity() instanceof EntityPlayer player
                && player.dimension == TwilightForestMod.dimensionID) {
            player.triggerAchievement(TFAchievementPage.twilightHunter);
            int chunkX = MathHelper.floor_double(this.posX) >> 4;
            int chunkZ = MathHelper.floor_double(this.posZ) >> 4;
            if (TFFeature.getNearestFeature(chunkX, chunkZ, this.worldObj) == TFFeature.hill3) {
                player.triggerAchievement(TFAchievementPage.twilightHill3);
            }
        }
    }

    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32)) {
            return false;
        }

        int blockLight = this.worldObj.getBlockLightValue(x, y, z);
        if (this.worldObj.isThundering()) {
            int previousSkylight = this.worldObj.skylightSubtracted;
            try {
                this.worldObj.skylightSubtracted = 10;
                blockLight = this.worldObj.getBlockLightValue(x, y, z);
            } finally {
                this.worldObj.skylightSubtracted = previousSkylight;
            }
        }
        return blockLight <= this.rand.nextInt(8);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL && this.isValidLightLevel()
                && super.getCanSpawnHere();
    }
}
