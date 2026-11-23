package twilightforest.entity.boss;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import cpw.mods.fml.common.FMLLog;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TFTreasure;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFTowerDevice;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;
import twilightforest.entity.EntityTFTowerGhast;
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
    private static final int TRAP_SCAN_WIDTH = TRAP_SCAN_XZ_RANGE * 2 + 1;
    private static final int TRAP_SCAN_HEIGHT = TRAP_SCAN_Y_RANGE * 2 + 1;
    private static final int TRAP_SCAN_AREA = TRAP_SCAN_WIDTH * TRAP_SCAN_HEIGHT;
    private static final int TRAP_SCAN_VOLUME = TRAP_SCAN_WIDTH * TRAP_SCAN_AREA;
    private static final int TRAP_SCAN_BLOCKS_PER_TICK = 8192;
    private static final int TRAP_SCAN_COMPLETE = 2;

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

    int travelPathRepetitions;
    int desiredRepetitions;

    int nextTantrumCry;

    float damageUntilNextPhase; // how much damage can we take before we toggle tantrum mode

    boolean noTrapMode; // are there no traps nearby? just float around

    private int trapScanStage;
    private int trapScanIndex;
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

        this.noTrapMode = false;

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

    /**
     * Despawn the minoshroom, and restore the boss spawner at our home location, if set
     */
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
                    DamageSource.causeThrownDamage(source.getEntity(), source.getEntity()),
                    damage);
        } else {
            attackSuccessful = super.attackEntityFrom(source, damage);
        }

        if (!worldObj.isRemote && attackSuccessful) {
            float healthLost = Math.max(0.0F, healthBefore - this.getHealth());
            if (healthLost > 0.0F) {
                this.damageUntilNextPhase -= healthLost;
                if (this.damageUntilNextPhase <= 0.0F && this.isEntityAlive()) {
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
        if (worldObj.isRemote) {
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
        worldInfo.setRaining(true);
        worldInfo.setThundering(true);
        worldInfo.setRainTime(rainTime);
        worldInfo.setThunderTime(rainTime);

        spawnGhastsAtTraps();
    }

    /**
     * Spawn ghasts at two of the traps
     */
    protected void spawnGhastsAtTraps() {
        if (worldObj.isRemote || this.trapLocations.isEmpty()) {
            return;
        }

        // spawn ghasts around two of the traps, without picking the same trap twice
        ArrayList<ChunkCoordinates> ghastSpawns = new ArrayList<>(this.trapLocations);
        int numSpawns = Math.min(2, ghastSpawns.size());
        for (int i = 0; i < numSpawns; i++) {
            int index = this.rand.nextInt(ghastSpawns.size());
            ChunkCoordinates spawnCoord = ghastSpawns.remove(index);
            spawnMinionGhastsAt(spawnCoord.posX, spawnCoord.posY, spawnCoord.posZ);
        }
    }

    public void stopTantrum() {
        this.setInTantrum(false);

    }

    /**
     * Spawn up to 6 minon ghasts around the indicated area
     */
    private void spawnMinionGhastsAt(int x, int y, int z) {
        if (worldObj.isRemote) {
            return;
        }

        int tries = 24;
        int spawns = 0;
        int maxSpawns = 6;
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
            this.dataWatcher.updateObject(16, (byte) 0);
            return;
        }

        this.prevAttackCounter = this.attackCounter;
        if (!this.isInTantrum()) {
            // keep the 3 shots/volley
            updateFireballVolley();

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

        double targetRange = (this.aggroCounter > 0 || this.isAggressive) ? aggroRange : stareRange;
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
            this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;
            this.rotationPitch = 0;
        }

        if (this.attackCounter > 0 && !this.isAggressive) {
            --this.attackCounter;
        }
        byte currentAggroStatus = this.dataWatcher.getWatchableObjectByte(16);
        byte newAggroStatus = (byte) (this.attackCounter > 10 ? 2
                : (this.aggroCounter > 0 || this.isAggressive) ? 1 : 0);
        if (currentAggroStatus != newAggroStatus) {
            this.dataWatcher.updateObject(16, newAggroStatus);
        }
    }

    private void doTantrumDamageEffects() {
        // harm player below
        AxisAlignedBB below = this.boundingBox.getOffsetBoundingBox(0, -16, 0).expand(0, 16, 0);
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
     * Waypoints should be closer to our course than we are now. Try 50 times and take the closest
     */
    protected void makeNewWaypoint() {
        // a direct course waypoint guarantees progress if the random fails
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
            if (candidateDistanceSq < bestDistanceSq) {
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
                this.travelPathRepetitions++;

                // System.out.println("Tower boss has repeated path " + travelPathRepetitions + " times.");

                // when we're in tantrum mode, this is a good time to check if we need to spawn more ghasts
                if (isInTantrum() && !checkGhastsAtTraps()) {
                    this.spawnGhastsAtTraps();
                }
            }

            this.courseX = travelCoords.get(currentTravelCoordIndex).posX;
            this.courseY = travelCoords.get(currentTravelCoordIndex).posY + HOVER_ALTITUDE;
            this.courseZ = travelCoords.get(currentTravelCoordIndex).posZ;

            this.currentTravelCoordIndex++;
        }

    }

    /**
     * Check if there are at least 4 ghasts near at least 2 traps. Return false if not.
     */
    private boolean checkGhastsAtTraps() {
        int trapsWithEnoughGhasts = 0;

        for (ChunkCoordinates trap : this.trapLocations) {
            AxisAlignedBB aabb = AxisAlignedBB
                    .getBoundingBox(trap.posX, trap.posY, trap.posZ, trap.posX + 1, trap.posY + 1, trap.posZ + 1)
                    .expand(8D, 16D, 8D);

            List<EntityTFMiniGhast> nearbyGhasts = worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, aabb);

            // System.out.println("Checking for ghasts. There are " + nearbyGhasts.size() + " ghasts near this
            // trap.");

            if (nearbyGhasts.size() >= 4) {
                trapsWithEnoughGhasts++;
            }
        }

        // System.out.println("Checking traps for ghasts. There are " + trapsWithEnoughGhasts + " traps with
        // enough ghasts.");

        return trapsWithEnoughGhasts >= 1;

    }

    private void makeTravelPath() {
        ArrayList<ChunkCoordinates> potentialPoints;

        int px = MathHelper.floor_double(this.posX);
        int py = MathHelper.floor_double(this.posY);
        int pz = MathHelper.floor_double(this.posZ);

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

    /**
     * Scan a few chunks around us for ghast trap blocks and if we find any, add them to our list
     */
    private void updateTrapScan() {
        if (trapScanStage >= TRAP_SCAN_COMPLETE) {
            return;
        }
        if (!trapScanInitialized) {
            trapScanOriginX = MathHelper.floor_double(this.posX);
            trapScanOriginY = MathHelper.floor_double(this.posY);
            trapScanOriginZ = MathHelper.floor_double(this.posZ);
            trapScanInitialized = true;
        }

        for (int i = 0; i < TRAP_SCAN_BLOCKS_PER_TICK && trapScanStage < TRAP_SCAN_COMPLETE; ++i) {
            int index = trapScanIndex++;
            int sx = index / TRAP_SCAN_AREA - TRAP_SCAN_XZ_RANGE;
            int sz = (index / TRAP_SCAN_HEIGHT) % TRAP_SCAN_WIDTH - TRAP_SCAN_XZ_RANGE;
            int sy = index % TRAP_SCAN_HEIGHT - TRAP_SCAN_Y_RANGE;
            int x = trapScanOriginX + sx;
            int y = trapScanOriginY + sy;
            int z = trapScanOriginZ + sz;
            if (isTrapAt(x, y, z)) {
                if (!isTrapRecorded(x, y, z)) {
                    boolean firstTrap = trapLocations.isEmpty();
                    trapLocations.add(new ChunkCoordinates(x, y, z));
                    if (firstTrap) {
                        travelCoords.clear();
                        currentTravelCoordIndex = 0;
                    }
                }
            }

            if (trapScanIndex >= TRAP_SCAN_VOLUME) {
                completeTrapScanPass();
            }
        }
    }

    private void completeTrapScanPass() {
        trapScanIndex = 0;
        if (trapScanStage == 0 && !trapLocations.isEmpty()) {
            long ax = 0, ay = 0, az = 0;
            for (ChunkCoordinates trap : trapLocations) {
                ax += trap.posX;
                ay += trap.posY;
                az += trap.posZ;
            }
            trapScanOriginX = (int) (ax / trapLocations.size());
            trapScanOriginY = (int) (ay / trapLocations.size());
            trapScanOriginZ = (int) (az / trapLocations.size());
            trapScanStage = 1;
        } else {
            trapScanStage = TRAP_SCAN_COMPLETE;
            noTrapMode = trapLocations.isEmpty();
            if (noTrapMode) {
                FMLLog.info("[TwilightForest] Ur-ghast cannot find traps nearby, entering trap-less mode");
            }
            travelCoords.clear();
            currentTravelCoordIndex = 0;
        }
    }

    /**
     * Scan in the specified range for traps
     */
    protected void scanForTraps(int scanRangeXZ, int scanRangeY, int px, int py, int pz) {
        for (int sx = -scanRangeXZ; sx <= scanRangeXZ; sx++) {
            for (int sz = -scanRangeXZ; sz <= scanRangeXZ; sz++) {
                for (int sy = -scanRangeY; sy <= scanRangeY; sy++) {
                    int x = px + sx, y = py + sy, z = pz + sz;
                    if (isTrapAt(x, y, z)) {
                        if (!isTrapRecorded(x, y, z)) {
                            trapLocations.add(new ChunkCoordinates(x, y, z));
                        }
                    }
                }
            }
        }
    }

    private boolean isTrapRecorded(int x, int y, int z) {
        for (ChunkCoordinates existing : trapLocations) {
            if (existing.posX == x && existing.posY == y && existing.posZ == z) {
                return true;
            }
        }
        return false;
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

        nbttagcompound.setFloat("UrGhastPhaseDamageLeft", this.damageUntilNextPhase);
        nbttagcompound.setBoolean("UrGhastTrapsScanned", this.trapScanStage == TRAP_SCAN_COMPLETE);
        if (this.trapScanStage == TRAP_SCAN_COMPLETE) {
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
            this.setHomeArea(hx, hy, hz, 20);
        }
        if (!nbttagcompound.getBoolean("HasHome")) {
            this.detachHome();
        }
        this.setInTantrum(nbttagcompound.getBoolean("inTantrum"));
        this.damageUntilNextPhase = nbttagcompound.hasKey("UrGhastPhaseDamageLeft", 99)
                ? nbttagcompound.getFloat("UrGhastPhaseDamageLeft")
                : INITIAL_PHASE_DAMAGE;

        trapLocations.clear();
        trapScanStage = 0;
        trapScanIndex = 0;
        trapScanInitialized = false;
        if (nbttagcompound.getBoolean("UrGhastTrapsScanned")) {
            NBTTagList traps = nbttagcompound.getTagList("UrGhastTrapLocations", 10);
            for (int i = 0; i < traps.tagCount(); ++i) {
                NBTTagCompound entry = traps.getCompoundTagAt(i);
                trapLocations
                        .add(new ChunkCoordinates(entry.getInteger("X"), entry.getInteger("Y"), entry.getInteger("Z")));
            }
            trapScanStage = TRAP_SCAN_COMPLETE;
        }
        noTrapMode = trapScanStage == TRAP_SCAN_COMPLETE && trapLocations.isEmpty();
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
            TFTreasure.darktower_boss.generate(worldObj, null, chestCoords.posX, chestCoords.posY, chestCoords.posZ);
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
            ChunkCoordinates chestCoords = this.findChestCoords();
            int dx = chestCoords.posX;
            int dy = chestCoords.posY;
            int dz = chestCoords.posZ;

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
            // average the location of the traps we've found, and scan again from there
            int ax = 0, ay = 0, az = 0;

            for (ChunkCoordinates trapCoords : trapLocations) {
                ax += trapCoords.posX;
                ay += trapCoords.posY;
                az += trapCoords.posZ;
            }

            ax /= trapLocations.size();
            ay /= trapLocations.size();
            az /= trapLocations.size();

            return new ChunkCoordinates(ax, ay + 2, az);
        } else {
            return new ChunkCoordinates(
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ));
        }
    }
}
