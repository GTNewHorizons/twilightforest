package twilightforest;

import static twilightforest.TwilightForestMod.enableEfRIntegration;
import static twilightforest.TwilightForestMod.shulkerSpawnInLichTower;
import static twilightforest.TwilightForestMod.shulkerSpawnInUrGhastTower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import ganymedes01.etfuturum.entities.EntityHusk;
import ganymedes01.etfuturum.entities.EntityShulker;
import ganymedes01.etfuturum.entities.EntityStray;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.compat.Mods;
import twilightforest.entity.EntityTFAdherent;
import twilightforest.entity.EntityTFArmoredGiant;
import twilightforest.entity.EntityTFBlockGoblin;
import twilightforest.entity.EntityTFDeathTome;
import twilightforest.entity.EntityTFFireBeetle;
import twilightforest.entity.EntityTFGiantMiner;
import twilightforest.entity.EntityTFGoblinKnightLower;
import twilightforest.entity.EntityTFHarbingerCube;
import twilightforest.entity.EntityTFHelmetCrab;
import twilightforest.entity.EntityTFIceExploder;
import twilightforest.entity.EntityTFIceShooter;
import twilightforest.entity.EntityTFKobold;
import twilightforest.entity.EntityTFMazeSlime;
import twilightforest.entity.EntityTFMiniGhast;
import twilightforest.entity.EntityTFMinotaur;
import twilightforest.entity.EntityTFPinchBeetle;
import twilightforest.entity.EntityTFRedcap;
import twilightforest.entity.EntityTFRedcapSapper;
import twilightforest.entity.EntityTFSlimeBeetle;
import twilightforest.entity.EntityTFSnowGuardian;
import twilightforest.entity.EntityTFSwarmSpider;
import twilightforest.entity.EntityTFTowerBroodling;
import twilightforest.entity.EntityTFTowerGhast;
import twilightforest.entity.EntityTFTowerGolem;
import twilightforest.entity.EntityTFTroll;
import twilightforest.entity.EntityTFWraith;
import twilightforest.entity.EntityTFYeti;
import twilightforest.world.TFWorld;
import twilightforest.world.TFWorldChunkManager;

public class TFFeature {

    private static final int FEATURE_LIST_SIZE = 256;
    private static final int CHUNK_SHIFT = 4;
    private static final int CHUNK_SIZE = 1 << CHUNK_SHIFT;
    private static final int FEATURE_GRID_SIZE = 16;
    private static final int FEATURE_GRID_SHIFT = 4;
    private static final int REGION_GRID_SIZE = 8;
    private static final int REGION_OFFSET = 64;
    private static final int MAX_NEARBY_FEATURE_RADIUS = 3;
    private static final int FEATURE_RANDOM_BOUND = 16;
    private static final long FEATURE_X_SEED_MULTIPLIER = 25117L;
    private static final long FEATURE_Z_SEED_MULTIPLIER = 151121L;
    private static final int HINT_SPAWN_ATTEMPTS = 20;
    private static final int HINT_SPAWN_HORIZONTAL_RANGE = 16;
    private static final int HINT_SPAWN_VERTICAL_RANGE = 4;
    private static final long HINT_SPAWN_COOLDOWN_TICKS = 1200L;
    private static final String HINT_BOOK_PREFIX = "progressionBook.";
    private static final String HINT_BOOK_PAGES_TAG = "pages";
    private static final String HINT_BOOK_AUTHOR_TAG = "author";
    private static final String HINT_BOOK_TITLE_TAG = "title";

    // preset layouts are reused instead of allocating 2D arrays on each lookup
    private static final int[][] PRESET_5X5 = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 19, 18, 8, 15, 14, 0 }, { 0, 0, 18, 18, 2, 3, 15, 0 }, { 0, 0, 4, 4, 5, 16, 9, 0 },
            { 0, 0, 13, 6, 1, 2, 17, 0 }, { 0, 0, 12, 13, 3, 17, 7, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
    private static final int[][] PRESET_6X6 = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 19, 19, 18, 15, 0, 0, 0 },
            { 0, 18, 18, 18, 0, 14, 0, 0 }, { 0, 0, 4, 1, 2, 3, 15, 0 }, { 0, 4, 1, 5, 16, 9, 17, 0 },
            { 0, 0, 13, 2, 3, 17, 17, 0 }, { 0, 0, 12, 13, 6, 17, 7, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };

    public static final TFFeature[] featureList = new TFFeature[FEATURE_LIST_SIZE];

    public static final TFFeature nothing = new TFFeature(0, 0, "No Feature").enableDecorations().disableStructure();
    public static final TFFeature hill1 = new TFFeature(1, 1, "Small Hollow Hill").enableDecorations()
            .enableTerrainAlterations();
    public static final TFFeature hill2 = new TFFeature(2, 2, "Medium Hollow Hill").enableDecorations()
            .enableTerrainAlterations();
    public static final TFFeature hill3 = new TFFeature(3, 3, "Large Hollow Hill").enableDecorations()
            .enableTerrainAlterations();
    public static final TFFeature hedgeMaze = new TFFeature(4, 2, "Hedge Maze").enableTerrainAlterations();
    public static final TFFeature nagaCourtyard = new TFFeature(5, 3, "Naga Courtyard").enableTerrainAlterations();
    public static final TFFeature lichTower = new TFFeature(6, 1, "Lich Tower")
            .setRequiredAchievement(TFAchievementPage.twilightKillNaga);
    public static final TFFeature iceTower = new TFFeature(7, 2, "Ice Tower")
            .setRequiredAchievement(TFAchievementPage.twilightProgressYeti);
    public static final TFFeature questIsland = new TFFeature(8, 1, "Quest Island").disableStructure();
    public static final TFFeature questGrove = new TFFeature(9, 1, "Quest Grove").enableTerrainAlterations();
    public static final TFFeature druidGrove = new TFFeature(10, 1, "Druid Grove").disableStructure();
    public static final TFFeature floatRuins = new TFFeature(11, 3, "Floating Ruins").disableStructure();
    public static final TFFeature hydraLair = new TFFeature(12, 2, "Hydra Lair")
            .setRequiredAchievement(TFAchievementPage.twilightProgressLabyrinth).enableTerrainAlterations();
    public static final TFFeature labyrinth = new TFFeature(13, 3, "Labyrinth").enableDecorations()
            .setRequiredAchievement(TFAchievementPage.twilightKillLich);
    public static final TFFeature darkTower = new TFFeature(14, 1, "Dark Tower")
            .setRequiredAchievement(TFAchievementPage.twilightProgressKnights);
    public static final TFFeature tfStronghold = new TFFeature(15, 3, "Knight Stronghold").enableDecorations()
            .setRequiredAchievement(TFAchievementPage.twilightProgressTrophyPedestal).disableProtectionAura();
    public static final TFFeature worldTree = new TFFeature(16, 3, "World Tree").disableStructure();
    public static final TFFeature yetiCave = new TFFeature(17, 2, "Yeti Lairs").enableDecorations()
            .enableTerrainAlterations().setRequiredAchievement(TFAchievementPage.twilightProgressUrghast);
    public static final TFFeature trollCave = new TFFeature(18, 3, "Troll Lairs").enableDecorations()
            .enableTerrainAlterations().setRequiredAchievement(TFAchievementPage.twilightProgressGlacier)
            .disableProtectionAura();
    public static final TFFeature finalCastle = new TFFeature(19, 3, "Final Castle");
    public static final TFFeature mushroomTower = new TFFeature(20, 2, "Mushroom Tower");

    ArrayList<SpawnListEntry> emptyList = new ArrayList<>();

    static {
        // spawn lists!
        if (Mods.EFR.isLoaded() && enableEfRIntegration && shulkerSpawnInLichTower) {
            lichTower.addMonster(EntityZombie.class, 6, 2, 4);
            lichTower.addMonster(EntitySkeleton.class, 6, 2, 4);
            lichTower.addMonster(EntityTFDeathTome.class, 6, 2, 4);
            lichTower.addMonster(EntityShulker.class, 2, 1, 2);
            lichTower.addMonster(EntityHusk.class, 1, 2, 4);
            lichTower.addMonster(EntityStray.class, 1, 2, 4);
        } else {
            lichTower.addMonster(EntityZombie.class, 10, 4, 4);
            lichTower.addMonster(EntitySkeleton.class, 10, 4, 4);
            lichTower.addMonster(EntityTFDeathTome.class, 10, 4, 4);
            lichTower.addMonster(EntityCreeper.class, 1, 4, 4);
            lichTower.addMonster(EntityWitch.class, 1, 1, 1);
        }
        lichTower.addMonster(EntityEnderman.class, 1, 1, 4);

        hill1.addMonster(EntitySpider.class, 10, 4, 4);
        hill1.addMonster(EntityZombie.class, 10, 4, 4);
        hill1.addMonster(EntityTFRedcap.class, 10, 4, 4);
        hill1.addMonster(EntityTFSwarmSpider.class, 10, 4, 4);
        hill1.addMonster(EntityTFKobold.class, 10, 4, 8);

        hill2.addMonster(EntityTFRedcap.class, 10, 4, 4);
        hill2.addMonster(EntityTFRedcapSapper.class, 1, 1, 4);
        hill2.addMonster(EntityTFKobold.class, 10, 4, 8);
        hill2.addMonster(EntitySkeleton.class, 10, 4, 4);
        hill2.addMonster(EntityTFSwarmSpider.class, 10, 4, 4);
        hill2.addMonster(EntitySpider.class, 10, 4, 4);
        hill2.addMonster(EntityCreeper.class, 10, 4, 4);
        hill2.addMonster(EntityTFFireBeetle.class, 5, 4, 4);
        hill2.addMonster(EntityTFSlimeBeetle.class, 5, 4, 4);
        hill2.addMonster(EntityWitch.class, 1, 1, 1);

        hill3.addMonster(EntityTFRedcap.class, 10, 4, 4);
        hill3.addMonster(EntityTFRedcapSapper.class, 2, 1, 4);
        hill3.addMonster(EntitySkeleton.class, 10, 4, 4);
        hill3.addMonster(EntityCaveSpider.class, 10, 4, 4);
        hill3.addMonster(EntityCreeper.class, 10, 4, 4);
        hill3.addMonster(EntityEnderman.class, 1, 1, 4);
        hill3.addMonster(EntityTFWraith.class, 2, 1, 4);
        hill3.addMonster(EntityTFFireBeetle.class, 10, 4, 4);
        hill3.addMonster(EntityTFSlimeBeetle.class, 10, 4, 4);
        hill3.addMonster(EntityTFPinchBeetle.class, 10, 2, 4);
        hill3.addMonster(EntityWitch.class, 1, 1, 1);

        labyrinth.addMonster(EntityTFMinotaur.class, 20, 2, 4);
        labyrinth.addMonster(EntityCaveSpider.class, 10, 4, 4);
        labyrinth.addMonster(EntityCreeper.class, 10, 4, 4);
        labyrinth.addMonster(EntityTFMazeSlime.class, 10, 4, 4);
        labyrinth.addMonster(EntityEnderman.class, 1, 1, 4);
        labyrinth.addMonster(EntityTFFireBeetle.class, 10, 4, 4);
        labyrinth.addMonster(EntityTFSlimeBeetle.class, 10, 4, 4);
        labyrinth.addMonster(EntityTFPinchBeetle.class, 10, 2, 4);

        darkTower.addMonster(EntityTFTowerGolem.class, 10, 4, 4);
        darkTower.addMonster(EntitySkeleton.class, 10, 4, 4);
        darkTower.addMonster(EntityCreeper.class, 10, 4, 4);
        darkTower.addMonster(EntityEnderman.class, 2, 1, 4);
        darkTower.addMonster(EntityWitch.class, 1, 1, 1);
        darkTower.addMonster(EntityTFMiniGhast.class, 10, 1, 4);
        darkTower.addMonster(EntityTFTowerBroodling.class, 10, 8, 8);
        darkTower.addMonster(EntityTFPinchBeetle.class, 10, 2, 4);
        // roof ghasts
        darkTower.addMonster(1, EntityTFTowerGhast.class, 10, 1, 4);
        if (Mods.EFR.isLoaded() && enableEfRIntegration && shulkerSpawnInUrGhastTower) {
            darkTower.addMonster(EntityShulker.class, 3, 1, 2);
            darkTower.addMonster(1, EntityShulker.class, 3, 1, 2);
        }
        // aquarium squids (only in aquariums between y = 35 and y = 64. :/
        darkTower.addWaterCreature(EntitySquid.class, 10, 4, 4);

        tfStronghold.addMonster(EntityTFBlockGoblin.class, 10, 4, 4);
        tfStronghold.addMonster(EntityTFGoblinKnightLower.class, 5, 1, 2);
        tfStronghold.addMonster(EntityTFHelmetCrab.class, 10, 4, 4);
        tfStronghold.addMonster(EntityTFSlimeBeetle.class, 10, 4, 4);
        tfStronghold.addMonster(EntityTFRedcapSapper.class, 2, 1, 4);
        tfStronghold.addMonster(EntityTFKobold.class, 10, 4, 8);
        tfStronghold.addMonster(EntityCreeper.class, 10, 4, 4);
        tfStronghold.addMonster(EntitySlime.class, 5, 4, 4);

        yetiCave.addMonster(EntityTFYeti.class, 10, 4, 4);

        iceTower.addMonster(EntityTFSnowGuardian.class, 10, 4, 4);
        iceTower.addMonster(EntityTFIceShooter.class, 10, 4, 4);
        iceTower.addMonster(EntityTFIceExploder.class, 5, 4, 4);

        trollCave.addMonster(EntityCreeper.class, 5, 4, 4);
        trollCave.addMonster(EntitySkeleton.class, 10, 4, 4);
        trollCave.addMonster(EntityTFTroll.class, 20, 4, 4);
        trollCave.addMonster(EntityWitch.class, 5, 1, 1);
        // cloud monsters
        trollCave.addMonster(1, EntityTFGiantMiner.class, 10, 1, 4);
        trollCave.addMonster(1, EntityTFArmoredGiant.class, 10, 1, 4);

        // plain parts of the castle, like the tower maze
        finalCastle.addMonster(EntityTFKobold.class, 10, 4, 4);
        finalCastle.addMonster(EntityTFAdherent.class, 10, 1, 1);
        finalCastle.addMonster(EntityTFHarbingerCube.class, 10, 1, 1);
        finalCastle.addMonster(EntityEnderman.class, 10, 1, 1);

        // internal castle
        finalCastle.addMonster(1, EntityTFKobold.class, 10, 4, 4);
        finalCastle.addMonster(1, EntityTFAdherent.class, 10, 1, 1);
        finalCastle.addMonster(1, EntityTFHarbingerCube.class, 10, 1, 1);
        finalCastle.addMonster(1, EntityTFArmoredGiant.class, 10, 1, 1);

        // dungeons
        finalCastle.addMonster(2, EntityTFAdherent.class, 10, 1, 1);

        // forge
        finalCastle.addMonster(3, EntityBlaze.class, 10, 1, 1);
    }

    public int featureID;
    public int size;
    public String name;
    public boolean areChunkDecorationsEnabled;
    public boolean isStructureEnabled;
    public boolean isTerrainAltered;
    protected List<List<SpawnListEntry>> spawnableMonsterLists;
    protected List<SpawnListEntry> ambientCreatureList;
    protected List<SpawnListEntry> waterCreatureList;
    protected Achievement requiredAchievement = null;
    public boolean hasProtectionAura;

    private long lastSpawnedHintMonsterTime;

    public TFFeature(int parID, int parSize, String parName) {
        this.featureID = parID;
        TFFeature.featureList[parID] = this;
        this.size = parSize;
        this.name = parName;
        this.areChunkDecorationsEnabled = false;
        this.isStructureEnabled = true;
        this.isTerrainAltered = false;
        this.spawnableMonsterLists = new ArrayList<>();
        this.ambientCreatureList = new ArrayList<>();
        this.waterCreatureList = new ArrayList<>();
        this.hasProtectionAura = true;

        ambientCreatureList.add(new SpawnListEntry(EntityBat.class, 10, 8, 8));
    }

    // Turns on biome-specific decorations like grass and trees near this feature.
    public TFFeature enableDecorations() {
        this.areChunkDecorationsEnabled = true;
        return this;
    }

    // Tell the chunkgenerator that we don't have an associated structure.
    public TFFeature disableStructure() {
        this.isStructureEnabled = false;
        return this;
    }

    // Tell the chunkgenerator that we want the terrain changed nearby.
    public TFFeature enableTerrainAlterations() {
        this.isTerrainAltered = true;
        return this;
    }

    public TFFeature disableProtectionAura() {
        this.hasProtectionAura = false;
        return this;
    }

    // Add a monster to spawn list 0
    public TFFeature addMonster(Class<? extends EntityLiving> monsterClass, int weight, int minGroup, int maxGroup) {
        this.addMonster(0, monsterClass, weight, minGroup, maxGroup);
        return this;
    }

    // Add a monster to a specific spawn list
    public TFFeature addMonster(int listIndex, Class<? extends EntityLiving> monsterClass, int weight, int minGroup,
            int maxGroup) {
        if (listIndex < 0) {
            throw new IllegalArgumentException("Spawn list index must be nonnegative: " + listIndex);
        }

        // integrations may register a higher index before the intermediate lists exist
        while (spawnableMonsterLists.size() <= listIndex) {
            spawnableMonsterLists.add(new ArrayList<>());
        }
        spawnableMonsterLists.get(listIndex).add(new SpawnListEntry(monsterClass, weight, minGroup, maxGroup));
        return this;
    }

    // Add a water creature
    public TFFeature addWaterCreature(Class<? extends EntityLiving> monsterClass, int weight, int minGroup,
            int maxGroup) {
        this.waterCreatureList.add(new SpawnListEntry(monsterClass, weight, minGroup, maxGroup));
        return this;
    }

    // @return The type of feature directly at the specified Chunk coordinates
    public static TFFeature getFeatureDirectlyAt(int chunkX, int chunkZ, World world) {

        if (world != null && world.getWorldChunkManager() instanceof TFWorldChunkManager tfManager) {

            if (tfManager.isInFeatureChunk(world, chunkX << CHUNK_SHIFT, chunkZ << CHUNK_SHIFT)) {
                return tfManager.getFeatureAt(chunkX << CHUNK_SHIFT, chunkZ << CHUNK_SHIFT, world);
            } else {
                return nothing;
            }
        } else {
            return nothing;
        }
    }

    /**
     * What feature would go in this chunk. Called when we know there is a feature, but there is no cache data, either
     * generating this chunk for the first time, or using the magic map to forecast beyond the edge of the world.
     */
    public static TFFeature generateFeatureForOldMapGen(int chunkX, int chunkZ, World world) {

        // what biome is at the center of the chunk?
        BiomeGenBase biomeAt = world.getBiomeGenForCoords((chunkX << CHUNK_SHIFT) + 8, (chunkZ << CHUNK_SHIFT) + 8);

        // get random value
        Random hillRNG = new Random(
                world.getSeed() + chunkX * FEATURE_X_SEED_MULTIPLIER + chunkZ * FEATURE_Z_SEED_MULTIPLIER);
        int randnum = hillRNG.nextInt(FEATURE_RANDOM_BOUND);

        // glaciers have ice towers
        if (biomeAt == TFBiomeBase.glacier) {
            return iceTower;
        }

        // lakes have quest islands
        if (biomeAt == TFBiomeBase.tfLake) {
            return questIsland;
        }

        // enchanted forests have groves
        if (biomeAt == TFBiomeBase.enchantedForest) {
            return questGrove;
        }

        // fire swamp has hydra lair
        if (biomeAt == TFBiomeBase.fireSwamp) {
            return hydraLair;
        }

        // temporary, clearing has maze ruins
        if (biomeAt == TFBiomeBase.clearing || biomeAt == TFBiomeBase.oakSavanna) {
            return labyrinth;
        }

        // dark forests have their own things
        if (biomeAt == TFBiomeBase.darkForest) {
            switch (randnum % 3) {
                case 0:
                    // return druidGrove;
                    break;
                case 1:
                    return darkTower;
                case 2:
                    return tfStronghold;
            }
        }

        // highlands center has castle
        if (biomeAt == TFBiomeBase.highlandsCenter) {
            return finalCastle;
        }
        // highlands has trolls
        if (biomeAt == TFBiomeBase.highlands) {
            return trollCave;
        }

        // deep mushrooms has mushroom tower
        if (biomeAt == TFBiomeBase.deepMushrooms) {
            return mushroomTower;
        }

        // okay, well that takes care of most special cases
        return switch (randnum) { // oops, I forgot about zero for a long time, now there are too many hill 1s
            default -> hill1;
            case 7, 8, 9 -> hill2;
            case 10 -> hill3;
            case 11, 12 -> hedgeMaze;
            case 13 -> (biomeAt != TFBiomeBase.tfSwamp) ? nagaCourtyard : hydraLair; // hydra in the swamp, naga
            // everywhere else
            case 14, 15 -> lichTower;
        };
    }

    public static TFFeature generateFeatureFor1Point7(int chunkX, int chunkZ, World world) {
        if (TwilightForestMod.oldMapGen) {
            return generateFeatureForOldMapGen(chunkX, chunkZ, world);
        }

        // set the chunkX and chunkZ to the center of the biome
        chunkX = Math.round(chunkX / (float) FEATURE_GRID_SIZE) * FEATURE_GRID_SIZE;
        chunkZ = Math.round(chunkZ / (float) FEATURE_GRID_SIZE) * FEATURE_GRID_SIZE;

        // what biome is at the center of the chunk?
        BiomeGenBase biomeAt = world.getBiomeGenForCoords((chunkX << CHUNK_SHIFT) + 8, (chunkZ << CHUNK_SHIFT) + 8);

        // get random value
        Random hillRNG = new Random(
                world.getSeed() + chunkX * FEATURE_X_SEED_MULTIPLIER + chunkZ * FEATURE_Z_SEED_MULTIPLIER);
        int randnum = hillRNG.nextInt(FEATURE_RANDOM_BOUND);

        // glaciers have ice towers
        if (biomeAt == TFBiomeBase.glacier) {
            return iceTower;
        }
        // snow has yeti lair
        if (biomeAt == TFBiomeBase.tfSnow) {
            return yetiCave;
        }

        // lakes have quest islands
        if (biomeAt == TFBiomeBase.tfLake) {
            return questIsland;
        }

        // enchanted forests have groves
        if (biomeAt == TFBiomeBase.enchantedForest) {
            return questGrove;
        }

        // fire swamp has hydra lair
        if (biomeAt == TFBiomeBase.fireSwamp) {
            return hydraLair;
        }
        // swamp has labyrinth
        if (biomeAt == TFBiomeBase.tfSwamp) {
            return labyrinth;
        }

        // dark forests have their own things
        if (biomeAt == TFBiomeBase.darkForest) {
            return tfStronghold;
        }
        if (biomeAt == TFBiomeBase.darkForestCenter) {
            return darkTower;
        }

        // highlands center has castle
        if (biomeAt == TFBiomeBase.highlandsCenter) {
            return finalCastle;
        }
        // highlands has trolls
        if (biomeAt == TFBiomeBase.highlands) {
            return trollCave;
        }

        // deep mushrooms has mushroom tower
        if (biomeAt == TFBiomeBase.deepMushrooms) {
            return mushroomTower;
        }

        int regionOffsetX = Math.abs(((chunkX + REGION_OFFSET) >> FEATURE_GRID_SHIFT) % REGION_GRID_SIZE);
        int regionOffsetZ = Math.abs(((chunkZ + REGION_OFFSET) >> FEATURE_GRID_SHIFT) % REGION_GRID_SIZE);

        // plant two lich towers near the center of each 2048x2048 map area
        if ((regionOffsetX == 4 && regionOffsetZ == 5) || (regionOffsetX == 4 && regionOffsetZ == 3)) {
            return lichTower;
        }

        // also two nagas
        if ((regionOffsetX == 5 && regionOffsetZ == 4) || (regionOffsetX == 3 && regionOffsetZ == 4)) {
            return nagaCourtyard;
        }

        // okay, well that takes care of most special cases
        return switch (randnum) {
            default -> hill1;
            case 6, 7, 8 -> hill2;
            case 9 -> hill3;
            case 10, 11 -> hedgeMaze;
            case 12, 13 -> nagaCourtyard;
            case 14, 15 -> lichTower;
        };
    }

    /**
     * What feature would go in this chunk. Called when we know there is a feature, but there is no cache data, either
     * generating this chunk for the first time, or using the magic map to forecast beyond the edge of the world.
     */
    public static TFFeature generateFeaturePreset5x5(int chunkX, int chunkZ, World world) {
        return getFeatureFromPreset(chunkX, chunkZ, PRESET_5X5, 4);
    }

    /**
     * What feature would go in this chunk. Called when we know there is a feature, but there is no cache data, either
     * generating this chunk for the first time, or using the magic map to forecast beyond the edge of the world.
     */
    public static TFFeature generateFeaturePreset6x6(int chunkX, int chunkZ, World world) {
        return getFeatureFromPreset(chunkX, chunkZ, PRESET_6X6, 3);
    }

    private static TFFeature getFeatureFromPreset(int chunkX, int chunkZ, int[][] preset, int originOffset) {
        if (chunkX % FEATURE_GRID_SIZE != 0 || chunkZ % FEATURE_GRID_SIZE != 0) {
            return nothing;
        }

        int mapX = chunkX / FEATURE_GRID_SIZE + originOffset;
        int mapZ = chunkZ / FEATURE_GRID_SIZE + originOffset;
        if (mapX < 0 || mapZ < 0 || mapZ >= preset.length || mapX >= preset[mapZ].length) {
            return nothing;
        }
        return featureList[preset[mapZ][mapX]];
    }

    // @return The feature nearest to the specified chunk coordinates
    public static TFFeature getNearestFeature(int cx, int cz, World world) {
        FeatureMatch match = findNearbyFeature(cx, cz, world);
        return match != null ? match.feature : nothing;
    }

    // preserve radius first and X then Z selection order but cache cells visited by earlier radii
    private static FeatureMatch findNearbyFeature(int cx, int cz, World world) {
        if (world == null || !(world.getWorldChunkManager() instanceof TFWorldChunkManager)) {
            return null;
        }

        int width = MAX_NEARBY_FEATURE_RADIUS * 2 + 1;
        TFFeature[] checked = new TFFeature[width * width];
        for (int radius = 1; radius <= MAX_NEARBY_FEATURE_RADIUS; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int index = (x + MAX_NEARBY_FEATURE_RADIUS) * width + z + MAX_NEARBY_FEATURE_RADIUS;
                    TFFeature feature = checked[index];
                    if (feature == null) {
                        feature = getFeatureDirectlyAt(x + cx, z + cz, world);
                        checked[index] = feature;
                    }
                    if (feature.size == radius) {
                        return new FeatureMatch(feature, x, z);
                    }
                }
            }
        }
        return null;
    }

    private static final class FeatureMatch {

        private final TFFeature feature;
        private final int chunkOffsetX;
        private final int chunkOffsetZ;

        private FeatureMatch(TFFeature feature, int chunkOffsetX, int chunkOffsetZ) {
            this.feature = feature;
            this.chunkOffsetX = chunkOffsetX;
            this.chunkOffsetZ = chunkOffsetZ;
        }
    }

    // @return The feature in the chunk "region"
    public static TFFeature getFeatureForRegion(int chunkX, int chunkZ, World world) {
        // just round to the nearest multiple of 16 chunks?
        int featureX = Math.round(chunkX / (float) FEATURE_GRID_SIZE) * FEATURE_GRID_SIZE;
        int featureZ = Math.round(chunkZ / (float) FEATURE_GRID_SIZE) * FEATURE_GRID_SIZE;

        return TFFeature.generateFeatureFor1Point7(featureX, featureZ, world);
    }

    /**
     * If we're near a hollow hill, this returns relative block coordinates indicating the center of that hill relative
     * to the current chunk block coordinate system.
     *
     * @param cx
     * @param cz
     * @param world
     * @return
     */
    public static int[] getNearestCenter(int cx, int cz, World world) {
        FeatureMatch match = findNearbyFeature(cx, cz, world);
        if (match == null) {
            return new int[] { 0, 0 };
        }
        return new int[] { match.chunkOffsetX * CHUNK_SIZE + 8, match.chunkOffsetZ * CHUNK_SIZE + 8 };
    }

    /**
     * Given some coordinates, return the center of the nearest feature.
     *
     * Feature centres are approximately one per 16x16-chunk region, with seed-derived offsets. The negative-coordinate
     * adjustment is retained for existing world compatibility.
     */
    public static ChunkCoordinates getNearestCenterXYZ(int cx, int cz, World world) {
        // legacy support
        if (TwilightForestMod.oldMapGen) {
            return getNearestCenterXYZOld(cx, cz);
        }

        int chunkX = cx;
        int chunkZ = cz;

        // generate random number for the whole biome area
        int regionX = (chunkX + 8) >> FEATURE_GRID_SHIFT;
        int regionZ = (chunkZ + 8) >> FEATURE_GRID_SHIFT;

        long seed = (regionX * 3129871L) ^ (long) regionZ * 116129781L;
        seed = seed * seed * 42317861L + seed * 7L;

        int num0 = (int) (seed >> 12 & 3L);
        int num1 = (int) (seed >> 15 & 3L);
        int num2 = (int) (seed >> 18 & 3L);
        int num3 = (int) (seed >> 21 & 3L);

        // slightly randomize center of biome (+/- 3)
        int centerX = 8 + num0 - num1;
        int centerZ = 8 + num2 - num3;

        // centers are offset strangely depending on +/-
        int ccz;
        if (regionZ >= 0) {
            ccz = (regionZ * FEATURE_GRID_SIZE + centerZ - 8) * CHUNK_SIZE + 8;
        } else {
            ccz = (regionZ * FEATURE_GRID_SIZE + (FEATURE_GRID_SIZE - centerZ) - 8) * CHUNK_SIZE + 9;
        }

        int ccx;
        if (regionX >= 0) {
            ccx = (regionX * FEATURE_GRID_SIZE + centerX - 8) * CHUNK_SIZE + 8;
        } else {
            ccx = (regionX * FEATURE_GRID_SIZE + (FEATURE_GRID_SIZE - centerX) - 8) * CHUNK_SIZE + 9;
        }

        return new ChunkCoordinates(ccx, TFWorld.SEALEVEL, ccz);
    }

    private static ChunkCoordinates getNearestCenterXYZOld(int cx, int cz) {
        int fx = (int) (Math.round(cx / (double) (FEATURE_GRID_SIZE * CHUNK_SIZE)) * (FEATURE_GRID_SIZE * CHUNK_SIZE)
                + 8);
        int fz = (int) (Math.round(cz / (double) (FEATURE_GRID_SIZE * CHUNK_SIZE)) * (FEATURE_GRID_SIZE * CHUNK_SIZE)
                + 8);

        return new ChunkCoordinates(fx, TFWorld.SEALEVEL, fz);
    }

    // Returns a list of hostile monsters. Are we ever going to need passive or water creatures?
    public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
        return switch (creatureType) {
            case monster -> this.getSpawnableList(EnumCreatureType.monster, 0);
            case ambient -> this.ambientCreatureList;
            case waterCreature -> this.waterCreatureList;
            default -> emptyList;
        };
    }

    // Returns a list of hostile monsters in the specified indexed category
    public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType, int index) {
        if (creatureType == EnumCreatureType.monster) {
            if (index >= 0 && index < this.spawnableMonsterLists.size()) {
                return this.spawnableMonsterLists.get(index);
            } else {
                return emptyList;
            }
        } else {
            return getSpawnableList(creatureType);
        }
    }

    private TFFeature setRequiredAchievement(Achievement required) {
        this.requiredAchievement = required;

        return this;
    }

    public boolean doesPlayerHaveRequiredAchievement(EntityPlayer player) {
        if (this.requiredAchievement != null) {
            // can we get the player's stats here at all?
            if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).func_147099_x() != null) {
                StatisticsFile stats = ((EntityPlayerMP) player).func_147099_x();

                return stats.hasAchievementUnlocked(this.requiredAchievement);
            } else {
                return false; // cannot get stats
            }
        } else {
            return true; // no required achievement
        }
    }

    // Try to spawn a hint monster near the specified player
    public void trySpawnHintMonster(World world, EntityPlayer player) {
        this.trySpawnHintMonster(
                world,
                player,
                MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ));
    }

    // Try several times to spawn a hint monster
    public void trySpawnHintMonster(World world, EntityPlayer player, int x, int y, int z) {
        // check if the timer is valid
        long currentTime = world.getTotalWorldTime();

        // if someone set the time backwards, fix the spawn timer
        if (currentTime < this.lastSpawnedHintMonsterTime) {
            this.lastSpawnedHintMonsterTime = 0;
        }

        if (currentTime - this.lastSpawnedHintMonsterTime > HINT_SPAWN_COOLDOWN_TICKS) {
            // okay, time is good, try several times to spawn one
            for (int i = 0; i < HINT_SPAWN_ATTEMPTS; i++) {
                if (didSpawnHintMonster(world, player, x, y, z)) {
                    this.lastSpawnedHintMonsterTime = currentTime;
                    break;
                }
            }
        }
    }

    /**
     * Try once to spawn a hint monster near the player. Return true if we did.
     *
     * We could change up the monster depending on what feature this is, but we currently are not doing that
     */
    private boolean didSpawnHintMonster(World world, EntityPlayer player, int x, int y, int z) {
        // find a target point
        int dx = x + world.rand.nextInt(HINT_SPAWN_HORIZONTAL_RANGE) - world.rand.nextInt(HINT_SPAWN_HORIZONTAL_RANGE);
        int dy = y + world.rand.nextInt(HINT_SPAWN_VERTICAL_RANGE) - world.rand.nextInt(HINT_SPAWN_VERTICAL_RANGE);
        int dz = z + world.rand.nextInt(HINT_SPAWN_HORIZONTAL_RANGE) - world.rand.nextInt(HINT_SPAWN_HORIZONTAL_RANGE);

        // make our hint monster
        EntityTFKobold hinty = new EntityTFKobold(world);
        hinty.setPosition(dx, dy, dz);

        // check if the bounding box is clear
        boolean isClearSpawn = world.checkNoEntityCollision(hinty.boundingBox)
                && world.getCollidingBoundingBoxes(hinty, hinty.boundingBox).isEmpty()
                && !world.isAnyLiquid(hinty.boundingBox);

        if (isClearSpawn && hinty.canEntityBeSeen(player)) {

            // add items and hint book
            ItemStack book = this.createHintBook();

            hinty.setCurrentItemOrArmor(0, book);
            hinty.setEquipmentDropChance(0, 1.0F);

            return world.spawnEntityInWorld(hinty);
        }
        return false;
    }

    private String formatBook(String address) {
        String page = StatCollector.translateToLocal(address);
        if (address.equals(page)) {
            page = StatCollector.translateToFallback(address);
        }
        return page.replace("\\n", "\n").replace("\\u00A78", "\u00A78").replace("\\u00A70", "\u00A70");
    }

    /**
     * Create a hint book for the specified feature. Only features with block protection will need this.
     */
    public ItemStack createHintBook() {
        String bookType;
        int pageCount;

        if (this == lichTower) {
            bookType = "lichTower";
            pageCount = 4;
        } else if (this == labyrinth) {
            bookType = "labyrinth";
            pageCount = 5;
        } else if (this == hydraLair) {
            bookType = "hydraLair";
            pageCount = 4;
        } else if (this == tfStronghold) {
            bookType = "stronghold";
            pageCount = 5;
        } else if (this == darkTower) {
            bookType = "darkTower";
            pageCount = 3;
        } else if (this == yetiCave) {
            bookType = "yetiCave";
            pageCount = 4;
        } else if (this == iceTower) {
            bookType = "iceTower";
            pageCount = 3;
        } else if (this == trollCave) {
            bookType = "trollCave";
            pageCount = 3;
        } else {
            bookType = "default";
            pageCount = 2;
        }

        ItemStack book = new ItemStack(Items.written_book);
        NBTTagList pages = new NBTTagList();
        String bookPrefix = HINT_BOOK_PREFIX + bookType + ".";
        for (int page = 0; page < pageCount; page++) {
            pages.appendTag(new NBTTagString(formatBook(bookPrefix + page)));
        }

        book.setTagInfo(HINT_BOOK_PAGES_TAG, pages);
        book.setTagInfo(HINT_BOOK_AUTHOR_TAG, new NBTTagString(formatBook(HINT_BOOK_PREFIX + "author")));
        book.setTagInfo(HINT_BOOK_TITLE_TAG, new NBTTagString(formatBook(bookPrefix + "title")));
        return book;
    }

}
