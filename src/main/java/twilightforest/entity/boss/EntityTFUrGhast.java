package twilightforest.entity.boss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TFTreasure;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFTowerDevice;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;
import twilightforest.entity.EntityTFTowerGhast;
import twilightforest.tileentity.TileEntityTFGhastTrapActive;
import twilightforest.tileentity.TileEntityTFGhastTrapInactive;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class EntityTFUrGhast extends EntityTFTowerGhast implements IBossDisplayData {

    // private static final int DATA_BOSSHEALTH = 17;
    private static final int DATA_TANTRUM = 18;

    // private static final int CRUISING_ALTITUDE = 235; // absolute cruising altitude
    private static final int HOVER_ALTITUDE = 20; // how far, relatively, do we hover over ghast traps?

    private static final int TRAP_SCAN_XZ_RANGE = 48;
    private static final int TRAP_SCAN_Y_RANGE = 32;
    private static final int TRAP_SCAN_INTERVAL = 100;
    private static final int MINIONS_PER_TRAP = 4;

    private static final int FIREBALLS_PER_VOLLEY = 3;
    private static final int FIREBALL_SPACING_TICKS = 2;
    private static final int MINI_GHAST_ABSORB_INTERVAL = 3;
    private static final int WAYPOINT_ATTEMPTS = 12;
    private static final float INITIAL_PHASE_DAMAGE = 45.0F;
    private static final float DAMAGE_PER_PHASE = 48.0F;

    public double courseX;
    public double courseY;
    public double courseZ;

    ArrayList<ChunkCoordinates> trapLocations;
    ArrayList<ChunkCoordinates> travelCoords;

    int currentTravelCoordIndex;

    int nextTantrumCry;

    float damageUntilNextPhase; // how much damage can we take before we toggle tantrum mode

    private int trapScanCooldown;
    private int trapScanOriginX;
    private int trapScanOriginY;
    private int trapScanOriginZ;
    private boolean trapScanInitialized;

    private int queuedFireballs;
    private int fireballDelay;
    private double volleyAimX;
    private double volleyAimY;
    private double volleyAimZ;

    public EntityTFUrGhast(World par1World) {
        super(par1World);

        this.setSize(14.0F, 18.0F);
        this.aggroRange = 128.0F;
        this.wanderFactor = 32.0F;

        // this.texture = TwilightForestMod.MODEL_DIR + "towerboss.png";

        this.noClip = true;

        this.trapLocations = new ArrayList<>();
        this.travelCoords = new ArrayList<>();

        this.setInTantrum(false);

        this.damageUntilNextPhase = INITIAL_PHASE_DAMAGE;

        this.experienceValue = 317;

    }

    // public int getMaxHealth()
    // {
    // return 250;
    // }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250); // max health
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // this.dataWatcher.addObject(DATA_BOSSHEALTH, new Integer(this.getMaxHealth()));
        this.dataWatcher.addObject(DATA_TANTRUM, (byte) 0);
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
        if (this.isDead) {
            return;
        }
        super.onLivingUpdate();

        // boss tears are cosmetic and should never run on the dedicated server
        if (worldObj.isRemote && this.isInTantrum()) {
            shedTear();
        }
    }

    /**
     * Keep health updated
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        // extra death explosions
        if (worldObj.isRemote && deathTime > 0) {
            for (int k = 0; k < 5; k++) {
                double d = rand.nextGaussian() * 0.02D;
                double d1 = rand.nextGaussian() * 0.02D;
                double d2 = rand.nextGaussian() * 0.02D;
                String explosionType = rand.nextBoolean() ? "hugeexplosion" : "explode";

                worldObj.spawnParticle(
                        explosionType,
                        (posX + rand.nextFloat() * width * 2.0F) - width,
                        posY + rand.nextFloat() * height,
                        (posZ + rand.nextFloat() * width * 2.0F) - width,
                        d,
                        d1,
                        d2);
            }
        }
    }

    protected void despawnIfInvalid() {
        // check to see if we're valid
        if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            despawnMe();
        }
    }

    // restore the ur ghast spawner when switching to peaceful
    protected void despawnMe() {
        if (this.hasHome()) {
            ChunkCoordinates home = this.getHomePosition();
            if (worldObj.getBlock(home.posX, home.posY, home.posZ) != TFBlocks.bossSpawner) {
                worldObj.setBlock(home.posX, home.posY, home.posZ, TFBlocks.bossSpawner, 3, 2);
            }
        }
        setDead();
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        // ignore suffocation
        if (source == DamageSource.inWall) {
            return false;
        }

        float healthBefore = this.getHealth();

        // in tantrum mode take only 1/4 damage
        if (this.isInTantrum()) {
            damage /= 4.0F;
        }

        boolean attackSuccessful;
        if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
            // 'hide' fireball attacks so that we don't take 1000 damage.
            attackSuccessful = super.attackEntityFrom(
                    DamageSource.causeThrownDamage(source.getSourceOfDamage(), source.getEntity()),
                    damage);
        } else {
            attackSuccessful = super.attackEntityFrom(source, damage);
        }

        if (!worldObj.isRemote && attackSuccessful) {
            float healthLost = Math.max(0.0F, healthBefore - this.getHealth());
            if (healthLost > 0.0F) {
                this.damageUntilNextPhase -= healthLost;
                if (this.damageUntilNextPhase <= 0.0F && this.isEntityAlive() && inTrapCounter <= 0) {
                    this.switchPhase();
                }
            }
        }
        return attackSuccessful;
    }

    /**
     * Move to the next phase of our behavior
     */
    private void switchPhase() {
        if (this.isInTantrum()) {
            this.stopTantrum();
        } else {
            this.startTantrum();
        }

        this.damageUntilNextPhase = DAMAGE_PER_PHASE;
    }

    /**
     * Start throwing a tantrum
     */
    protected void startTantrum() {
        if (worldObj.isRemote || isInTantrum() || inTrapCounter > 0) {
            return;
        }
        this.setInTantrum(true);
        this.queuedFireballs = 0;
        this.attackCounter = 0;
        this.targetedEntity = null;
        this.isAggressive = false;

        // start raining in the world the boss actually occupies
        int rainTime = 300 * 20;
        WorldInfo worldInfo = worldObj.getWorldInfo();
        if (!(worldInfo instanceof DerivedWorldInfo)) {
            worldInfo.setRaining(true);
            worldInfo.setThundering(true);
            worldInfo.setRainTime(Math.max(worldInfo.getRainTime(), rainTime));
            worldInfo.setThunderTime(Math.max(worldInfo.getThunderTime(), rainTime));
        }

        spawnGhastsAtTraps();
    }

    /**
     * Spawn ghasts at two of the traps
     */
    protected void spawnGhastsAtTraps() {
        if (worldObj.isRemote || this.trapLocations.isEmpty()) {
            return;
        }

        List<ChunkCoordinates> candidates = new ArrayList<>(trapLocations);
        Collections.shuffle(candidates, rand);
        int refilled = 0;
        for (ChunkCoordinates trap : candidates) {
            if (!isTrapAt(trap.posX, trap.posY, trap.posZ)) {
                continue;
            }
            TileEntity tile = worldObj.getTileEntity(trap.posX, trap.posY, trap.posZ);
            if (!(tile instanceof TileEntityTFGhastTrapInactive inactive) || inactive.isCharged()) {
                continue;
            }
            int living = countLivingGhastsAt(trap);
            if (living >= MINIONS_PER_TRAP) {
                continue;
            }
            spawnMinionGhastsAt(trap.posX, trap.posY, trap.posZ, 6 - living);
            if (++refilled >= 2) {
                break;
            }
        }
    }

    @Override
    public void setInTrap() {
        super.setInTrap();
        queuedFireballs = 0;
    }

    public void stopTantrum() {
        if (!worldObj.isRemote && isInTantrum()) {
            setInTantrum(false);
            damageUntilNextPhase = DAMAGE_PER_PHASE;
        }
    }

    /**
     * Spawn up to 6 minon ghasts around the indicated area
     */
    private void spawnMinionGhastsAt(int x, int y, int z, int maxSpawns) {
        if (worldObj.isRemote) {
            return;
        }

        int tries = 24;
        int spawns = 0;
        int rangeXZ = 4;
        int rangeY = 8;

        // lightning strike
        this.worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, x, y + 4, z));

        for (int i = 0; i < tries && spawns < maxSpawns; i++) {
            EntityTFMiniGhast minion = new EntityTFMiniGhast(worldObj);
            double sx = x + ((rand.nextDouble() - rand.nextDouble()) * rangeXZ);
            double sy = y + (rand.nextDouble() * rangeY);
            double sz = z + ((rand.nextDouble() - rand.nextDouble()) * rangeXZ);

            minion.setLocationAndAngles(sx, sy, sz, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
            minion.makeBossMinion();
            if (minion.getCanSpawnHere() && this.worldObj.spawnEntityInWorld(minion)) {
                minion.spawnExplosionParticle();
                ++spawns;
            }
        }
    }

    /**
     * Altered Ghast AI
     */
    @Override
    protected void updateEntityActionState() {
        if (worldObj.isRemote) {
            return;
        }
        if (worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            despawnMe();
            return;
        }

        this.despawnEntity();
        updateTrapScan();

        if (this.ticksExisted % MINI_GHAST_ABSORB_INTERVAL == 0) {
            List<EntityTFMiniGhast> nearbyGhasts = worldObj
                    .getEntitiesWithinAABB(EntityTFMiniGhast.class, this.boundingBox.expand(1, 1, 1));
            for (EntityTFMiniGhast ghast : nearbyGhasts) {
                if (ghast.isEntityAlive() && !ghast.isDead) {
                    ghast.setDead();
                    this.heal(2);
                }
            }
        }

        if (this.inTrapCounter > 0) {
            this.inTrapCounter--;
            this.targetedEntity = null;
            this.isAggressive = false;
            this.aggroCounter = 0;
            this.attackCounter = 0;
            this.queuedFireballs = 0;
            if (dataWatcher.getWatchableObjectByte(AGGRO_STATUS) != 0) {
                dataWatcher.updateObject(AGGRO_STATUS, (byte) 0);
            }
            return;
        }

        if (damageUntilNextPhase <= 0.0F) {
            switchPhase();
        }
        this.prevAttackCounter = this.attackCounter;
        if (!this.isInTantrum()) {
            if (this.targetedEntity != null && (!this.targetedEntity.isEntityAlive()
                    || this.targetedEntity instanceof EntityPlayer player && player.capabilities.disableDamage)) {
                this.targetedEntity = null;
                this.isAggressive = false;
                this.aggroCounter = 0;
            }
            if (this.targetedEntity == null) {
                if (this.ticksExisted % 10 == 0) {
                    this.targetedEntity = findPlayerInRange();
                }
            } else if (!this.isAggressive && this.targetedEntity instanceof EntityPlayer) {
                checkToIncreaseAggro((EntityPlayer) this.targetedEntity);
            }
            // finish a volley only while its target is still valid and visible
            if (targetedEntity != null && targetedEntity.isEntityAlive()
                    && getDistanceSqToEntity(targetedEntity) < aggroRange * aggroRange
                    && canEntityBeSeen(targetedEntity)) {
                updateFireballVolley();
            } else {
                queuedFireballs = 0;
            }
        } else {
            this.targetedEntity = null;
            this.isAggressive = false;
            this.aggroCounter = 0;
            this.attackCounter = 0;
            this.queuedFireballs = 0;
            if (--this.nextTantrumCry <= 0) {
                this.playSound(getHurtSound(), this.getSoundVolume(), this.getSoundPitch());
                this.nextTantrumCry = 20 + rand.nextInt(30);
            }
            if (this.ticksExisted % 10 == 0) {
                doTantrumDamageEffects();
            }
        }

        checkAndChangeCourse();
        double offsetX = this.waypointX - this.posX;
        double offsetY = this.waypointY - this.posY;
        double offsetZ = this.waypointZ - this.posZ;
        double distanceSq = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;

        if (distanceSq < 1.0D || distanceSq > 3600.0D) {
            makeNewWaypoint();
            // always recalculate after choosing a new waypoint
            offsetX = this.waypointX - this.posX;
            offsetY = this.waypointY - this.posY;
            offsetZ = this.waypointZ - this.posZ;
            distanceSq = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = 2 + rand.nextInt(4);
            if (distanceSq > 1.0E-8D) {
                double factor = 0.05D / Math.sqrt(distanceSq);
                this.motionX += offsetX * factor;
                this.motionY += offsetY * factor;
                this.motionZ += offsetZ * factor;
            }
        }

        double targetRange = getCurrentTargetRange();
        if (!this.isInTantrum() && this.targetedEntity != null
                && this.targetedEntity.getDistanceSqToEntity(this) < targetRange * targetRange
                && this.canEntityBeSeen(this.targetedEntity)) {
            this.faceEntity(this.targetedEntity, 10F, this.getVerticalFaceSpeed());
            if (this.isAggressive) {
                if (this.attackCounter == 10) {
                    this.playSound("mob.ghast.charge", this.getSoundVolume(), this.getSoundPitch());
                }
                ++this.attackCounter;
                if (this.attackCounter == 20) {
                    spitFireball();
                    this.attackCounter = -40;
                }
            }
        } else {
            this.isAggressive = false;
            this.targetedEntity = null;
            this.aggroCounter = 0;
            this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;
            this.rotationPitch = 0;
        }

        if (this.attackCounter > 0 && !this.isAggressive) {
            --this.attackCounter;
        }
        byte currentAggroStatus = this.dataWatcher.getWatchableObjectByte(AGGRO_STATUS);
        byte newAggroStatus = (byte) (this.attackCounter > 10 ? 2
                : (this.aggroCounter > 0 || this.isAggressive) ? 1 : 0);
        if (currentAggroStatus != newAggroStatus) {
            this.dataWatcher.updateObject(AGGRO_STATUS, newAggroStatus);
        }
    }

    private void doTantrumDamageEffects() {
        // harm player below
        // only entities beneath the boss, not inside or above its body
        AxisAlignedBB below = AxisAlignedBB.getBoundingBox(
                boundingBox.minX,
                boundingBox.minY - 32.0D,
                boundingBox.minZ,
                boundingBox.maxX,
                boundingBox.minY,
                boundingBox.maxZ);
        List<EntityPlayer> playersBelow = worldObj.getEntitiesWithinAABB(EntityPlayer.class, below);
        for (EntityPlayer player : playersBelow) {
            if (!player.isEntityAlive() || player.capabilities.disableDamage) {
                continue;
            }
            int dx = MathHelper.floor_double(player.posX);
            int dy = MathHelper.floor_double(player.posY);
            int dz = MathHelper.floor_double(player.posZ);
            if (worldObj.canBlockSeeTheSky(dx, dy, dz)) {
                player.attackEntityFrom(DamageSource.anvil, 3);
            }
        }

        // also suck up mini ghasts
        List<EntityTFMiniGhast> ghastsBelow = worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, below);
        for (EntityTFMiniGhast ghast : ghastsBelow) {
            if (ghast.isEntityAlive()) {
                ghast.motionY += 1;
            }
        }
    }

    /**
     * Make a tear particle fall off the ghast
     */
    private void shedTear() {
        TwilightForestMod.proxy.spawnParticle(
                this.worldObj,
                "bosstear",
                this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                this.posY + this.rand.nextDouble() * (double) this.height - 0.25D,
                this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                0,
                0,
                0);
    }

    /**
     * Choose a waypoint closer to the course than our current position.
     */
    protected void makeNewWaypoint() {
        // A direct course waypoint guarantees progress if no candidate is closer than we are.
        this.waypointX = this.courseX;
        this.waypointY = this.courseY;
        this.waypointZ = this.courseZ;

        double bestDistanceSq = this.getDistanceSq(this.courseX, this.courseY, this.courseZ);
        for (int i = 0; i < WAYPOINT_ATTEMPTS; i++) {
            double potentialX = this.posX + (rand.nextFloat() * 2.0F - 1.0F) * wanderFactor;
            double potentialY = this.courseY + rand.nextFloat() * 8.0F - 4.0F;
            double potentialZ = this.posZ + (rand.nextFloat() * 2.0F - 1.0F) * wanderFactor;
            double dx = courseX - potentialX;
            double dy = courseY - potentialY;
            double dz = courseZ - potentialZ;
            double candidateDistanceSq = dx * dx + dy * dy + dz * dz;
            if (candidateDistanceSq < bestDistanceSq && getDistanceSq(potentialX, potentialY, potentialZ) >= 1.0D) {
                this.waypointX = potentialX;
                this.waypointY = potentialY;
                this.waypointZ = potentialZ;
                bestDistanceSq = candidateDistanceSq;
            }
        }
    }

    protected void checkAndChangeCourse() {
        // check course.
        if (courseX == 0 && courseY == 0 && courseZ == 0) {
            changeCourse();
        }

        double offsetX = this.courseX - this.posX;
        double offsetY = this.courseY - this.posY;
        double offsetZ = this.courseZ - this.posZ;
        double distanceToCourse = offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;

        if (distanceToCourse < 100.0D) {
            changeCourse();
        }
    }

    /**
     * Change to a new course target
     */
    private void changeCourse() {
        // System.out.println("Boss changing course");

        if (this.travelCoords.isEmpty()) {
            // System.out.println("Boss travel coords is empty, making a new list");
            makeTravelPath();
        }

        if (!this.travelCoords.isEmpty()) {
            if (this.currentTravelCoordIndex >= travelCoords.size()) {
                this.currentTravelCoordIndex = 0;
                // when we're in tantrum mode, this is a good time to check if we need to spawn more ghasts
                if (isInTantrum()) {
                    this.spawnGhastsAtTraps();
                }
            }

            this.courseX = travelCoords.get(currentTravelCoordIndex).posX;
            this.courseY = travelCoords.get(currentTravelCoordIndex).posY + HOVER_ALTITUDE;
            this.courseZ = travelCoords.get(currentTravelCoordIndex).posZ;

            this.currentTravelCoordIndex++;
            makeNewWaypoint();
        }

    }

    private int countLivingGhastsAt(ChunkCoordinates trap) {
        AxisAlignedBB area = AxisAlignedBB
                .getBoundingBox(trap.posX, trap.posY, trap.posZ, trap.posX + 1, trap.posY + 1, trap.posZ + 1)
                .expand(8D, 16D, 8D);
        List<EntityTFMiniGhast> ghasts = worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, area);
        int living = 0;
        for (EntityTFMiniGhast ghast : ghasts) {
            if (ghast.isEntityAlive()) {
                ++living;
            }
        }
        return living;
    }

    private void makeTravelPath() {
        ArrayList<ChunkCoordinates> potentialPoints;

        int px = trapScanOriginX;
        int py = trapScanOriginY;
        int pz = trapScanOriginZ;

        if (!this.trapLocations.isEmpty()) {
            // make a copy of the trap locations list
            potentialPoints = new ArrayList<>(this.trapLocations);
        } else {
            potentialPoints = new ArrayList<>();
            potentialPoints.add(new ChunkCoordinates(px + 20, py - HOVER_ALTITUDE, pz));
            potentialPoints.add(new ChunkCoordinates(px, py - HOVER_ALTITUDE, pz - 20));
            potentialPoints.add(new ChunkCoordinates(px - 20, py - HOVER_ALTITUDE, pz));
            potentialPoints.add(new ChunkCoordinates(px, py - HOVER_ALTITUDE, pz + 20));

            // System.out.println("Adding fake points to the potentials list");
        }

        // put the potential points on our travel coords list in random order
        travelCoords.clear();

        while (!potentialPoints.isEmpty()) {
            int index = rand.nextInt(potentialPoints.size());

            travelCoords.add(potentialPoints.get(index));

            potentialPoints.remove(index);
        }

        if (this.trapLocations.isEmpty()) {
            // if in no trap mode, head back to the middle when we're done
            travelCoords.add(new ChunkCoordinates(px, py - HOVER_ALTITUDE, pz));
        }
    }

    /**
     * Spit a fireball
     */
    @Override
    protected void spitFireball() {
        if (worldObj.isRemote || this.targetedEntity == null || !this.targetedEntity.isEntityAlive()) {
            return;
        }

        // preserve the original 3 projectile spread but dont spawn all
        // explosive entities in 1 tick
        this.volleyAimX = this.targetedEntity.posX;
        this.volleyAimY = this.targetedEntity.boundingBox.minY + this.targetedEntity.height * 0.5D;
        this.volleyAimZ = this.targetedEntity.posZ;
        this.queuedFireballs = FIREBALLS_PER_VOLLEY - 1;
        this.fireballDelay = FIREBALL_SPACING_TICKS;

        this.worldObj.playAuxSFXAtEntity(null, 1008, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
        spawnVolleyFireball(false);
    }

    private void updateFireballVolley() {
        if (this.queuedFireballs <= 0 || --this.fireballDelay > 0) {
            return;
        }
        spawnVolleyFireball(true);
        --this.queuedFireballs;
        this.fireballDelay = FIREBALL_SPACING_TICKS;
    }

    private void spawnVolleyFireball(boolean spread) {
        double offsetX = this.volleyAimX - this.posX;
        double offsetY = this.volleyAimY - (this.posY + this.height * 0.5D);
        double offsetZ = this.volleyAimZ - this.posZ;
        if (spread) {
            offsetX += (rand.nextFloat() - rand.nextFloat()) * 8.0D;
            offsetZ += (rand.nextFloat() - rand.nextFloat()) * 8.0D;
        }

        EntityTFUrGhastFireball fireball = new EntityTFUrGhastFireball(this.worldObj, this, offsetX, offsetY, offsetZ);
        fireball.field_92057_e = 1;
        Vec3 lookVec = this.getLook(1.0F);
        double shotSpawnDistance = 8.5D;
        fireball.setPosition(
                this.posX + lookVec.xCoord * shotSpawnDistance,
                this.posY + this.height * 0.5D + lookVec.yCoord * shotSpawnDistance,
                this.posZ + lookVec.zCoord * shotSpawnDistance);
        this.worldObj.spawnEntityInWorld(fireball);
    }

    private void updateTrapScan() {
        if (--trapScanCooldown > 0) {
            return;
        }
        trapScanCooldown = TRAP_SCAN_INTERVAL;
        if (!trapScanInitialized) {
            ChunkCoordinates origin = hasHome() ? getHomePosition()
                    : new ChunkCoordinates(
                            MathHelper.floor_double(posX),
                            MathHelper.floor_double(posY),
                            MathHelper.floor_double(posZ));
            trapScanOriginX = origin.posX;
            trapScanOriginY = origin.posY;
            trapScanOriginZ = origin.posZ;
            trapScanInitialized = true;
        }

        // an unloaded chunk isnt proof that a previously discovered trap was destroyed
        boolean changed = trapLocations.removeIf(
                trap -> worldObj.blockExists(trap.posX, trap.posY, trap.posZ)
                        && !isTrapAt(trap.posX, trap.posY, trap.posZ));
        for (Object entry : worldObj.loadedTileEntityList) {
            if (!(entry instanceof TileEntity tile) || tile.isInvalid()
                    || !(tile instanceof TileEntityTFGhastTrapInactive
                            || tile instanceof TileEntityTFGhastTrapActive)) {
                continue;
            }
            int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;
            if (Math.abs((long) x - trapScanOriginX) > TRAP_SCAN_XZ_RANGE
                    || Math.abs((long) y - trapScanOriginY) > TRAP_SCAN_Y_RANGE
                    || Math.abs((long) z - trapScanOriginZ) > TRAP_SCAN_XZ_RANGE
                    || !isTrapAt(x, y, z)) {
                continue;
            }
            if (!trapLocations.contains(new ChunkCoordinates(x, y, z))) {
                trapLocations.add(new ChunkCoordinates(x, y, z));
                changed = true;
            }
        }
        if (changed) {
            travelCoords.clear();
            currentTravelCoordIndex = 0;
            courseX = courseY = courseZ = 0.0D;
        }
        if (changed && !trapLocations.isEmpty() && isInTantrum()) {
            spawnGhastsAtTraps();
        }
    }

    private boolean isTrapAt(int x, int y, int z) {
        if (!worldObj.blockExists(x, y, z) || worldObj.getBlock(x, y, z) != TFBlocks.towerDevice) {
            return false;
        }
        int metadata = worldObj.getBlockMetadata(x, y, z);
        return metadata == BlockTFTowerDevice.META_GHASTTRAP_INACTIVE
                || metadata == BlockTFTowerDevice.META_GHASTTRAP_ACTIVE;
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    @Override
    public boolean isBurning() {
        // never burn
        return false;
    }

    /**
     * No point in ever being pushed
     */
    @Override
    public boolean canBePushed() {
        return false;
    }

    /**
     * Are we throwing a tantrum?
     */
    public boolean isInTantrum() {
        return this.dataWatcher.getWatchableObjectByte(DATA_TANTRUM) != (byte) 0;
    }

    /**
     * Sets whether we are throwing a tantrum
     */
    public void setInTantrum(boolean tantrum) {
        byte state = (byte) (tantrum ? -1 : 0);
        if (this.dataWatcher.getWatchableObjectByte(DATA_TANTRUM) != state) {
            this.dataWatcher.updateObject(DATA_TANTRUM, state);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 16F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch() {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 0.5F;
    }

    /**
     * Basically a public getter for living sounds
     */
    public String getTrophySound() {
        return this.getLivingSound();
    }

    /**
     * Needed for boss health bar on the client
     */
    // @Override
    // public int getBossHealth() {
    // return this.dataWatcher.getWatchableObjectInt(DATA_BOSSHEALTH);
    // }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("inTantrum", this.isInTantrum());
        ChunkCoordinates home = this.getHomePosition();
        nbttagcompound.setTag("Home", newDoubleNBTList(home.posX, home.posY, home.posZ));
        nbttagcompound.setBoolean("HasHome", this.hasHome());
        nbttagcompound.setInteger("TFHomeRadius", (int) getMaximumHomeDistance());

        nbttagcompound.setFloat("UrGhastPhaseDamageLeft", this.damageUntilNextPhase);
        nbttagcompound.setBoolean("UrGhastTrapsScanned", trapScanInitialized);
        if (trapScanInitialized) {
            nbttagcompound.setInteger("UrGhastTrapOriginX", trapScanOriginX);
            nbttagcompound.setInteger("UrGhastTrapOriginY", trapScanOriginY);
            nbttagcompound.setInteger("UrGhastTrapOriginZ", trapScanOriginZ);
            NBTTagList traps = new NBTTagList();
            for (ChunkCoordinates trap : this.trapLocations) {
                NBTTagCompound entry = new NBTTagCompound();
                entry.setInteger("X", trap.posX);
                entry.setInteger("Y", trap.posY);
                entry.setInteger("Z", trap.posZ);
                traps.appendTag(entry);
            }
            nbttagcompound.setTag("UrGhastTrapLocations", traps);
        }
        nbttagcompound.setInteger("UrGhastQueuedFireballs", this.queuedFireballs);
        nbttagcompound.setInteger("UrGhastFireballDelay", this.fireballDelay);
        nbttagcompound.setDouble("UrGhastVolleyX", this.volleyAimX);
        nbttagcompound.setDouble("UrGhastVolleyY", this.volleyAimY);
        nbttagcompound.setDouble("UrGhastVolleyZ", this.volleyAimZ);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        if (nbttagcompound.hasKey("Home", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Home", 6);
            int hx = (int) nbttaglist.func_150309_d(0);
            int hy = (int) nbttaglist.func_150309_d(1);
            int hz = (int) nbttaglist.func_150309_d(2);
            this.setHomeArea(
                    hx,
                    hy,
                    hz,
                    nbttagcompound.hasKey("TFHomeRadius", 99) ? nbttagcompound.getInteger("TFHomeRadius") : 46);
        }
        if (!nbttagcompound.getBoolean("HasHome")) {
            this.detachHome();
        }
        this.setInTantrum(nbttagcompound.getBoolean("inTantrum"));
        this.damageUntilNextPhase = nbttagcompound.hasKey("UrGhastPhaseDamageLeft", 99)
                ? nbttagcompound.getFloat("UrGhastPhaseDamageLeft")
                : INITIAL_PHASE_DAMAGE;

        trapLocations.clear();
        trapScanCooldown = 0;
        trapScanInitialized = false;
        if (nbttagcompound.getBoolean("UrGhastTrapsScanned")) {
            NBTTagList traps = nbttagcompound.getTagList("UrGhastTrapLocations", 10);
            for (int i = 0; i < traps.tagCount(); ++i) {
                NBTTagCompound entry = traps.getCompoundTagAt(i);
                trapLocations
                        .add(new ChunkCoordinates(entry.getInteger("X"), entry.getInteger("Y"), entry.getInteger("Z")));
            }
            if (nbttagcompound.hasKey("UrGhastTrapOriginX", 99)) {
                trapScanOriginX = nbttagcompound.getInteger("UrGhastTrapOriginX");
                trapScanOriginY = nbttagcompound.getInteger("UrGhastTrapOriginY");
                trapScanOriginZ = nbttagcompound.getInteger("UrGhastTrapOriginZ");
                trapScanInitialized = true;
            }
        }
        travelCoords.clear();
        currentTravelCoordIndex = 0;
        courseX = courseY = courseZ = 0.0D;
        waypointX = waypointY = waypointZ = 0.0D;

        this.queuedFireballs = Math
                .max(0, Math.min(FIREBALLS_PER_VOLLEY - 1, nbttagcompound.getInteger("UrGhastQueuedFireballs")));
        this.fireballDelay = Math.max(1, nbttagcompound.getInteger("UrGhastFireballDelay"));
        this.volleyAimX = nbttagcompound.getDouble("UrGhastVolleyX");
        this.volleyAimY = nbttagcompound.getDouble("UrGhastVolleyY");
        this.volleyAimZ = nbttagcompound.getDouble("UrGhastVolleyZ");
        if (isInTantrum()) {
            this.queuedFireballs = 0;
        }
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate() {
        super.onDeathUpdate();

        if (this.deathTime == 20 && !worldObj.isRemote) {
            // make chest
            ChunkCoordinates chestCoords = this.findChestCoords();
            TFTreasure.darktower_boss
                    .generateBossReward(worldObj, chestCoords.posX, chestCoords.posY, chestCoords.posZ);
        }
    }

    /**
     * Trigger achievement when killed
     */
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (!worldObj.isRemote && par1DamageSource.getEntity() instanceof EntityPlayer
                && ((EntityPlayer) par1DamageSource.getEntity()).dimension == TwilightForestMod.dimensionID) {
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightHunter);
            ((EntityPlayer) par1DamageSource.getEntity()).triggerAchievement(TFAchievementPage.twilightProgressUrghast);

        }

        // mark the tower as defeated
        if (!worldObj.isRemote && worldObj.provider instanceof WorldProviderTwilightForest) {
            // the tower is the boss home, the loot location can be outside
            ChunkCoordinates location = hasHome() ? getHomePosition() : findChestCoords();
            int dx = location.posX;
            int dy = location.posY;
            int dz = location.posZ;

            ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) worldObj.provider)
                    .getChunkProvider();
            TFFeature nearbyFeature = ((TFWorldChunkManager) worldObj.provider.worldChunkMgr)
                    .getFeatureAt(dx, dz, worldObj);

            if (nearbyFeature == TFFeature.darkTower) {
                chunkProvider.setStructureConquered(dx, dy, dz, true);
            }
        }
    }

    private ChunkCoordinates findChestCoords() {
        if (trapLocations.size() > 0) {
            // average the known trap positions to locate the centre of the arena
            long ax = 0, ay = 0, az = 0;

            for (ChunkCoordinates trapCoords : trapLocations) {
                ax += trapCoords.posX;
                ay += trapCoords.posY;
                az += trapCoords.posZ;
            }

            return new ChunkCoordinates(
                    (int) Math.floorDiv(ax, trapLocations.size()),
                    (int) Math.floorDiv(ay, trapLocations.size()) + 2,
                    (int) Math.floorDiv(az, trapLocations.size()));
        } else if (hasHome()) {
            ChunkCoordinates home = getHomePosition();
            for (int y = Math.min(home.posY, 255); y > Math.max(0, home.posY - 32); --y) {
                if (worldObj.isAirBlock(home.posX, y, home.posZ)
                        && World.doesBlockHaveSolidTopSurface(worldObj, home.posX, y - 1, home.posZ)) {
                    return new ChunkCoordinates(home.posX, y, home.posZ);
                }
            }
            return new ChunkCoordinates(home.posX, home.posY, home.posZ);
        } else {
            return new ChunkCoordinates(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ));
        }
    }
}
