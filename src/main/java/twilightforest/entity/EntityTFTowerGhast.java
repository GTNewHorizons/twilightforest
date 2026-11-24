package twilightforest.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import twilightforest.TFFeature;

public class EntityTFTowerGhast extends EntityGhast {

    protected static final int AGGRO_STATUS = 16;
    protected EntityLivingBase targetedEntity;
    protected boolean isAggressive;
    protected int aggroCooldown;
    protected int explosionPower;
    protected int aggroCounter;

    protected float aggroRange;
    protected float stareRange;

    protected float wanderFactor;

    protected int inTrapCounter;

    private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
    /** If -1 there is no maximum distance */
    private float maximumHomeDistance = -1.0F;

    public EntityTFTowerGhast(World par1World) {
        super(par1World);
        // this.texture = TwilightForestMod.MODEL_DIR + "towerghast.png";

        this.setSize(4.0F, 6.0F);

        this.aggroRange = 64.0F;
        this.stareRange = 32.0F;

        this.wanderFactor = 16.0F;

        this.inTrapCounter = 0;
    }
    //
    // public int getMaxHealth()
    // {
    // return 30;
    // }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D); // max health
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.5F;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval() {
        return 160;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // byte aggroStatus = this.dataWatcher.getWatchableObjectByte(AGGRO_STATUS);

        // switch (aggroStatus)
        // {
        // case 0:
        // default:
        // this.texture = TwilightForestMod.MODEL_DIR + "towerghast.png";
        // break;
        // case 1:
        // this.texture = TwilightForestMod.MODEL_DIR + "towerghast_openeyes.png";
        // break;
        // case 2:
        // this.texture = TwilightForestMod.MODEL_DIR + "towerghast_fire.png";
        // break;
        // }
    }

    public int getAttackStatus() {
        return this.dataWatcher.getWatchableObjectByte(AGGRO_STATUS);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        // age when in light, like mobs
        float var1 = this.getBrightness(1.0F);

        if (var1 > 0.5F) {
            this.entityAge += 2;
        }

        if (worldObj.isRemote && this.rand.nextBoolean()) {
            this.worldObj.spawnParticle(
                    "reddust",
                    this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                    this.posY + this.rand.nextDouble() * (double) this.height - 0.25D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                    0,
                    0,
                    0);
        }

        super.onLivingUpdate();
    }

    /**
     * Altered Ghast AI
     */
    protected void updateEntityActionState() {
        if (worldObj.isRemote) {
            return;
        }
        if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            setDead();
            return;
        }

        this.despawnEntity();

        // tower home
        checkForTowerHome();

        // check if in trap
        if (this.inTrapCounter > 0) {
            this.inTrapCounter--;
            this.targetedEntity = null;
            this.isAggressive = false;
            this.aggroCounter = 0;
            this.attackCounter = 0;
            if (dataWatcher.getWatchableObjectByte(AGGRO_STATUS) != 0) {
                dataWatcher.updateObject(AGGRO_STATUS, (byte) 0);
            }
            return; // do nothing if in trap
        }

        this.prevAttackCounter = this.attackCounter;

        // target
        if (this.targetedEntity != null && (!this.targetedEntity.isEntityAlive()
                || this.targetedEntity instanceof EntityPlayer player && player.capabilities.disableDamage)) {
            this.targetedEntity = null;
            this.isAggressive = false;
            this.aggroCounter = 0;
        }

        if (this.targetedEntity == null) {
            // target scans are considerably more expensive than movement updates
            if (ticksExisted % 10 == 0) {
                this.targetedEntity = findPlayerInRange();
            }
        } else if (!this.isAggressive && this.targetedEntity instanceof EntityPlayer) {
            checkToIncreaseAggro((EntityPlayer) this.targetedEntity);
        }

        // choose a waypoint before measuring its offset,
        // offsets after replacing a waypoint and could fly in the wrong direction
        double dx = waypointX - posX;
        double dy = waypointY - posY;
        double dz = waypointZ - posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;
        if ((distanceSq < 1.0D || distanceSq > 3600.0D) && wanderFactor > 0) {
            waypointX = posX + (rand.nextFloat() * 2.0F - 1.0F) * wanderFactor;
            waypointY = posY + (rand.nextFloat() * 2.0F - 1.0F) * wanderFactor;
            waypointZ = posZ + (rand.nextFloat() * 2.0F - 1.0F) * wanderFactor;
        }

        if (targetedEntity == null && wanderFactor > 0) {
            if (courseChangeCooldown-- <= 0) {
                courseChangeCooldown += rand.nextInt(20) + 20;

                if (!isWithinHomeDistance(
                        MathHelper.floor_double(waypointX),
                        MathHelper.floor_double(waypointY),
                        MathHelper.floor_double(waypointZ))) {
                    // guide wandering ghasts towards the tower rather than searching for its centre again
                    ChunkCoordinates home = getHomePosition();
                    Vec3 homeVector = Vec3.createVectorHelper(home.posX - posX, home.posY - posY, home.posZ - posZ)
                            .normalize();
                    waypointX = posX + homeVector.xCoord * 16.0D + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
                    waypointY = posY + homeVector.yCoord * 16.0D + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
                    waypointZ = posZ + homeVector.zCoord * 16.0D + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
                }

                dx = waypointX - posX;
                dy = waypointY - posY;
                dz = waypointZ - posZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (distance > 1.0E-6D && isCourseTraversable(waypointX, waypointY, waypointZ, distance)) {
                    // tiny minion waypoints should produce tiny acceleration, full
                    double acceleration = 0.1D / Math.max(1.0D, distance);
                    motionX += dx * acceleration;
                    motionY += dy * acceleration;
                    motionZ += dz * acceleration;
                } else {
                    waypointX = posX;
                    waypointY = posY;
                    waypointZ = posZ;
                }
            }
        } else {
            motionX *= 0.75D;
            motionY *= 0.75D;
            motionZ *= 0.75D;
        }

        // check if our target is still within range
        double targetRange = getCurrentTargetRange();

        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < targetRange * targetRange
                && this.canEntityBeSeen(this.targetedEntity)) {
            // turn towards target
            this.faceEntity(this.targetedEntity, 10F, this.getVerticalFaceSpeed());

            // attack if aggressive
            if (this.isAggressive) {
                if (this.attackCounter == 10) {
                    this.worldObj.playAuxSFXAtEntity(null, 1007, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                }

                ++this.attackCounter;

                if (this.attackCounter == 20) {
                    spitFireball();
                    this.attackCounter = -40;
                }
            }
        } else {
            // ignore player, move normally
            this.isAggressive = false;
            this.targetedEntity = null;
            this.aggroCounter = 0;
            this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F
                    / (float) Math.PI;

            // changing the pitch with movement looks goofy and un-ghast-like
            // double dist = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationPitch = 0;// (float) (-((Math.atan2(this.motionY, dist) * 180D) / Math.PI));;

        }

        if (this.attackCounter > 0 && !this.isAggressive) {
            --this.attackCounter;
        }

        // set aggro status
        byte currentAggroStatus = this.dataWatcher.getWatchableObjectByte(AGGRO_STATUS);
        byte newAggroStatus = (byte) (this.attackCounter > 10 ? 2
                : (this.aggroCounter > 0 || this.isAggressive) ? 1 : 0);

        if (currentAggroStatus != newAggroStatus) {
            this.dataWatcher.updateObject(AGGRO_STATUS, newAggroStatus);
        }
    }

    /**
     * Something is deeply wrong with the calculations based off of this value, so let's set it high enough that it's
     * ignored.
     */
    @Override
    public int getVerticalFaceSpeed() {
        return 500;
    }

    /**
     * Spit a fireball
     */
    protected void spitFireball() {
        double offsetX = this.targetedEntity.posX - this.posX;
        double offsetY = this.targetedEntity.boundingBox.minY + (double) (this.targetedEntity.height / 2.0F)
                - (this.posY + (double) (this.height / 2.0F));
        double offsetZ = this.targetedEntity.posZ - this.posZ;

        // fireball sound effect
        this.worldObj.playAuxSFXAtEntity(null, 1008, (int) this.posX, (int) this.posY, (int) this.posZ, 0);

        EntityLargeFireball entityFireball = new EntityLargeFireball(this.worldObj, this, offsetX, offsetY, offsetZ);
        // var17.field_92012_e = this.explosionPower;
        double shotSpawnDistance = 0.5D;
        Vec3 lookVec = this.getLook(1.0F);
        entityFireball.setPosition(
                this.posX + lookVec.xCoord * shotSpawnDistance,
                this.posY + this.height / 2.0F + lookVec.yCoord * shotSpawnDistance,
                this.posZ + lookVec.zCoord * shotSpawnDistance);
        this.worldObj.spawnEntityInWorld(entityFireball);

        // when we attack, there is a 1-in-6 chance we decide to stop attacking
        if (rand.nextInt(6) == 0) {
            this.isAggressive = false;
        }
    }

    /**
     * Find a player in our aggro range. If we feel the need to become aggressive towards that player, or it is within
     * our stare range, return
     *
     * @return
     */
    protected double getCurrentTargetRange() {
        // a new exposed player must survive the first tick to build aggro
        if (isAggressive || aggroCounter > 0
                || targetedEntity instanceof EntityPlayer player && shouldAttackPlayer(player)) {
            return aggroRange;
        }
        return stareRange;
    }

    protected EntityLivingBase findPlayerInRange() {
        EntityPlayer best = null;
        double bestDistanceSq = aggroRange * aggroRange;

        // dont let a nearby hidden player prevent from noticing a visible one
        @SuppressWarnings("unchecked")
        java.util.List<EntityPlayer> players = worldObj
                .getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(aggroRange, aggroRange, aggroRange));
        for (EntityPlayer player : players) {
            if (!player.isEntityAlive() || player.capabilities.disableDamage || !canEntityBeSeen(player)) {
                continue;
            }
            double distanceSq = getDistanceSqToEntity(player);
            if (distanceSq < bestDistanceSq && (distanceSq < stareRange * stareRange || shouldAttackPlayer(player))) {
                best = player;
                bestDistanceSq = distanceSq;
            }
        }
        return best;
    }

    /**
     * Check if we should become aggressive towards the player.
     *
     * @param par1EntityPlayer
     */
    protected void checkToIncreaseAggro(EntityPlayer par1EntityPlayer) {
        if (this.shouldAttackPlayer(par1EntityPlayer)) {
            if (this.aggroCounter == 0) {
                this.worldObj.playSoundAtEntity(this, "mob.ghast.moan", 1.0F, 1.0F);
            }

            if (this.aggroCounter++ >= 20) {
                this.aggroCounter = 0;
                this.isAggressive = true;
            }
        } else {
            this.aggroCounter = 0;
        }
    }

    /**
     * Checks to see if this tower ghast should be attacking this player. Tower ghasts attack if the block above the
     * player is not Tower wood
     */
    protected boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer) {
        int dx = MathHelper.floor_double(par1EntityPlayer.posX);
        int dy = MathHelper.floor_double(par1EntityPlayer.posY);
        int dz = MathHelper.floor_double(par1EntityPlayer.posZ);

        return worldObj.canBlockSeeTheSky(dx, dy, dz) && par1EntityPlayer.canEntityBeSeen(this);
    }

    /**
     * True if the ghast has an unobstructed line of travel to the waypoint.
     */
    protected boolean isCourseTraversable(double targetX, double targetY, double targetZ, double distance) {
        if (distance <= 0.0D) {
            return false;
        }
        double stepX = (targetX - posX) / distance;
        double stepY = (targetY - posY) / distance;
        double stepZ = (targetZ - posZ) / distance;
        AxisAlignedBB testBox = boundingBox.copy();
        for (int step = 1; step < distance; step++) {
            testBox.offset(stepX, stepY, stepZ);
            if (!worldObj.getCollidingBoundingBoxes(this, testBox).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        boolean wasAttacked = super.attackEntityFrom(par1DamageSource, par2);

        if (!worldObj.isRemote && wasAttacked && par1DamageSource.getEntity() instanceof EntityLivingBase) {
            this.targetedEntity = (EntityLivingBase) par1DamageSource.getEntity();

            this.isAggressive = true;

            return true;
        } else {
            return wasAttacked;
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere() {
        return this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox)
                && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
                && this.isValidLightLevel();
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel() {
        return true;
    }

    /**
     * Check for our tower home and if we find it, set it
     */
    protected void checkForTowerHome() {
        if (!this.hasHome()) {
            // check if we're near a dark forest tower and if so, set that as home
            int chunkX = MathHelper.floor_double(this.posX) >> 4;
            int chunkZ = MathHelper.floor_double(this.posZ) >> 4;

            TFFeature nearFeature = TFFeature.getFeatureForRegion(chunkX, chunkZ, this.worldObj);

            if (nearFeature != TFFeature.darkTower) {
                this.detachHome();

                this.entityAge += 5;

                // System.out.println("Tower ghast is lost");

            } else {
                // set our home position to the center of the tower
                ChunkCoordinates cc = TFFeature.getNearestCenterXYZ(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posZ),
                        worldObj);
                this.setHomeArea(cc.posX, cc.posY + 128, cc.posZ, 64);

                // System.out.println("Ghast is at " + this.posX + ", " + this.posY + ", " + this.posZ);
                // System.out.println("Set home area to " + cc.posX + ", " + (cc.posY + 128) + ", " + cc.posZ);
            }
        }
    }

    /**
     * Towers are so large, a simple radius doesn't really work, so we make it more of a cylinder
     */
    public boolean isWithinHomeDistance(int x, int y, int z) {
        if (this.getMaximumHomeDistance() == -1.0F) {
            return true;
        } else {
            ChunkCoordinates home = this.getHomePosition();

            // System.out.println("Checking home for " + x + ", " + y + ", " + z + " and home is " + home.posX + ", " +
            // home.posY + ", " + home.posZ);
            // System.out.println("home.getDistanceSquared(x, home.posY, z) = " + home.getDistanceSquared(x, home.posY,
            // z) + " compared to " + (this.getMaximumHomeDistance() * this.getMaximumHomeDistance()));

            return y > 64 && y < 210
                    && home.getDistanceSquared(x, home.posY, z)
                            < this.getMaximumHomeDistance() * this.getMaximumHomeDistance();
        }
    }

    public void setInTrap() {
        this.inTrapCounter = 10;
        this.isAggressive = false;
        this.aggroCounter = 0;
        this.attackCounter = 0;
        this.targetedEntity = null;
        if (!worldObj.isRemote) {
            dataWatcher.updateObject(AGGRO_STATUS, (byte) 0);
        }
    }

    public void setHomeArea(int par1, int par2, int par3, int par4) {
        this.homePosition.set(par1, par2, par3);
        this.maximumHomeDistance = (float) par4;
    }

    public ChunkCoordinates getHomePosition() {
        return this.homePosition;
    }

    public float getMaximumHomeDistance() {
        return this.maximumHomeDistance;
    }

    public void detachHome() {
        this.maximumHomeDistance = -1.0F;
    }

    public boolean hasHome() {
        return this.maximumHomeDistance != -1.0F;
    }
}
