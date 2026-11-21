package twilightforest.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import cpw.mods.fml.common.FMLLog;
import twilightforest.TwilightForestMod;
import twilightforest.entity.EntityTFKobold;
import twilightforest.entity.passive.EntityTFMobileFirefly;

public abstract class TFBiomeBase extends BiomeGenBase {

    private static final int LEGACY_BIOME_ID_LIMIT = 256;
    private static final int SEA_LEVEL = 32;
    private static final int WORLD_HEIGHT = 256;
    private static final int CHUNK_WIDTH = 16;
    private static final int BEDROCK_DEPTH_VARIATION = 5;
    private static final float SPAWN_CHANCE = 0.12F;

    static {
        assignBlankBiomeIds();
        areThereBiomeIdConflicts();
    }

    public static final BiomeGenBase tfLake = (new TFBiomeTwilightLake(TwilightForestMod.idBiomeLake))
            .setColor(0x0000ff).setBiomeName("Twilight Lake").setHeight(height_DeepOceans);
    public static final BiomeGenBase twilightForest = (new TFBiomeTwilightForest(
            TwilightForestMod.idBiomeTwilightForest)).setColor(0x005500).setBiomeName("Twilight Forest");
    public static final BiomeGenBase twilightForest2 = (new TFBiomeTwilightForestVariant(
            TwilightForestMod.idBiomeTwilightForestVariant)).setColor(0x005522).setBiomeName("Dense Twilight Forest")
                    .setHeight(height_MidPlains);
    public static final BiomeGenBase highlands = (new TFBiomeHighlands(TwilightForestMod.idBiomeHighlands))
            .setColor(0x556644).setBiomeName("Twilight Highlands").setHeight(new Height(3.5F, 0.05F));
    public static final BiomeGenBase mushrooms = (new TFBiomeMushrooms(TwilightForestMod.idBiomeMushrooms))
            .setColor(0x226622).setBiomeName("Mushroom Forest");
    public static final BiomeGenBase tfSwamp = (new TFBiomeSwamp(TwilightForestMod.idBiomeSwamp)).setColor(0x334422)
            .setBiomeName("Twilight Swamp").setHeight(new Height(-0.125F, 0.125F));
    public static final BiomeGenBase stream = (new TFBiomeStream(TwilightForestMod.idBiomeStream)).setColor(0x3253b7)
            .setBiomeName("Twilight Stream").setHeight(height_ShallowWaters);
    public static final BiomeGenBase tfSnow = (new TFBiomeSnow(TwilightForestMod.idBiomeSnowfield)).setColor(0xccffff)
            .setBiomeName("Snowy Forest").setHeight(height_MidPlains);
    public static final BiomeGenBase glacier = (new TFBiomeGlacier(TwilightForestMod.idBiomeGlacier)).setColor(0x7777bb)
            .setBiomeName("Twilight Glacier");
    public static final BiomeGenBase clearing = (new TFBiomeClearing(TwilightForestMod.idBiomeClearing))
            .setColor(0x349b34).setBiomeName("Twilight Clearing").setHeight(height_LowPlains);
    public static final BiomeGenBase oakSavanna = (new TFBiomeOakSavanna(TwilightForestMod.idBiomeOakSavanna))
            .setColor(0x446622).setBiomeName("Oak Savanna").setHeight(height_MidPlains);
    public static final BiomeGenBase fireflyForest = (new TFBiomeFireflyForest(TwilightForestMod.idBiomeFireflyForest))
            .setColor(0x006633).setBiomeName("Firefly Forest").setHeight(height_LowPlains);
    public static final BiomeGenBase deepMushrooms = (new TFBiomeDeepMushrooms(TwilightForestMod.idBiomeDeepMushrooms))
            .setColor(0x663322).setBiomeName("Deep Mushroom Forest").setHeight(height_LowPlains);
    public static final BiomeGenBase darkForest = (new TFBiomeDarkForest(TwilightForestMod.idBiomeDarkForest))
            .setColor(0x003311).setBiomeName("Dark Forest").setHeight(height_LowPlains);
    public static final BiomeGenBase enchantedForest = (new TFBiomeEnchantedForest(
            TwilightForestMod.idBiomeEnchantedForest)).setColor(0x115566).setBiomeName("Enchanted Forest");
    public static final BiomeGenBase fireSwamp = (new TFBiomeFireSwamp(TwilightForestMod.idBiomeFireSwamp))
            .setColor(0x42231a).setBiomeName("Fire Swamp").setHeight(height_Default);
    public static final BiomeGenBase darkForestCenter = (new TFBiomeDarkForestCenter(
            TwilightForestMod.idBiomeDarkForestCenter)).setColor(0x002200).setBiomeName("Dark Forest Center")
                    .setHeight(height_LowPlains);
    public static final BiomeGenBase highlandsCenter = (new TFBiomeFinalPlateau(
            TwilightForestMod.idBiomeHighlandsCenter)).setColor(0x334422).setBiomeName("Highlands Center")
                    .setHeight(new Height(10.5F, 0.025F));
    public static final BiomeGenBase thornlands = (new TFBiomeThornlands(TwilightForestMod.idBiomeThornlands))
            .setColor(0x223322).setBiomeName("Thornlands").setHeight(new Height(6F, 0.1F));

    protected WorldGenBigMushroom bigMushroomGen;
    protected WorldGenForest birchGen;

    /**
     * Holds the classes of IMobs (hostile mobs) that can be spawned in the biome.
     */
    protected List<SpawnListEntry> undergroundMonsterList;

    public TFBiomeBase(int id) {
        super(id);

        // Set default filler block meta to 0
        this.field_76754_C = 0;

        bigMushroomGen = new WorldGenBigMushroom();
        birchGen = new WorldGenForest(false, false);

        // try to stop world leaks
        this.worldGeneratorBigTree = null;

        configureCreatureSpawns();
        configureUndergroundSpawns();
        configureCaveSpawns();

        getTFBiomeDecorator().setTreesPerChunk(10);
        getTFBiomeDecorator().setGrassPerChunk(2);
    }

    private void configureCreatureSpawns() {
        // remove normal monster spawns
        spawnableMonsterList.clear();
        // remove squids
        spawnableWaterCreatureList.clear();
        // custom creature list.
        spawnableCreatureList.clear();
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBighorn.class, 12, 4, 4));
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBoar.class, 10, 4, 4));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityChicken.class, 10, 4, 4));
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFDeer.class, 15, 4, 5));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityWolf.class, 5, 4, 4));

        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFTinyBird.class, 15, 4, 8));
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFSquirrel.class, 10, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBunny.class, 10, 4, 5));
        spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFRaven.class, 10, 1, 2));
    }

    private void configureUndergroundSpawns() {
        undergroundMonsterList = new ArrayList<>();
        undergroundMonsterList.add(new SpawnListEntry(EntitySpider.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityTFKobold.class, 10, 4, 8));
    }

    private void configureCaveSpawns() {
        spawnableCaveCreatureList.clear();
        spawnableCaveCreatureList.add(new SpawnListEntry(EntityBat.class, 10, 8, 8));
        spawnableCaveCreatureList.add(new SpawnListEntry(EntityTFMobileFirefly.class, 10, 8, 8));
    }

    @Override
    public TFBiomeBase setColor(int par1) {
        return (TFBiomeBase) super.setColor(par1);
    }

    /**
     * returns the chance a creature has to spawn.
     */
    @Override
    public float getSpawningChance() {
        // okay, 20% more animals
        return SPAWN_CHANCE;
    }

    /**
     * Allocate a new BiomeDecorator for this BiomeGenBase
     */
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return new TFBiomeDecorator();
    }

    protected TFBiomeDecorator getTFBiomeDecorator() {
        return (TFBiomeDecorator) this.theBiomeDecorator;
    }

    /**
     * Forest trees all over
     *
     * used to be getRandomWorldGenForTrees()
     */
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(5) == 0) {
            return birchGen;
        }
        return random.nextInt(10) == 0 ? new WorldGenBigTree(false) : worldGeneratorTrees;
    }

    /**
     * Ferns!
     */
    public WorldGenerator getRandomWorldGenForGrass(Random par1Random) {
        return new WorldGenTallGrass(Blocks.tallgrass, par1Random.nextInt(4) == 0 ? 2 : 1);
    }

    /**
     * If any biome IDs are -1, find an open ID for them.
     */
    public static boolean assignBlankBiomeIds() {
        BiomeGenBase[] registeredBiomes = BiomeGenBase.getBiomeGenArray();
        boolean[] usedIDs = new boolean[registeredBiomes.length];
        checkUsedIDs(usedIDs, registeredBiomes);

        int[] biomeIDs = getConfiguredBiomeIds();

        // reserve explicit IDs before auto assigning blanks, even if their biomes arent registered yet
        for (int biomeID : biomeIDs) {
            if (biomeID >= 0 && biomeID < usedIDs.length) {
                usedIDs[biomeID] = true;
            }
        }

        boolean assigned = false;
        for (int i = 0; i < biomeIDs.length; i++) {
            if (biomeIDs[i] == -1) {
                biomeIDs[i] = findNextOpenBiomeId(usedIDs);
                assigned |= biomeIDs[i] != -1;
            }
        }

        setConfiguredBiomeIds(biomeIDs);
        TwilightForestMod.hasAssignedBiomeID |= assigned;
        return assigned;
    }

    private static int[] getConfiguredBiomeIds() {
        return new int[] { TwilightForestMod.idBiomeLake, TwilightForestMod.idBiomeTwilightForest,
                TwilightForestMod.idBiomeTwilightForestVariant, TwilightForestMod.idBiomeHighlands,
                TwilightForestMod.idBiomeMushrooms, TwilightForestMod.idBiomeSwamp, TwilightForestMod.idBiomeStream,
                TwilightForestMod.idBiomeSnowfield, TwilightForestMod.idBiomeGlacier, TwilightForestMod.idBiomeClearing,
                TwilightForestMod.idBiomeOakSavanna, TwilightForestMod.idBiomeFireflyForest,
                TwilightForestMod.idBiomeDeepMushrooms, TwilightForestMod.idBiomeDarkForestCenter,
                TwilightForestMod.idBiomeHighlandsCenter, TwilightForestMod.idBiomeDarkForest,
                TwilightForestMod.idBiomeEnchantedForest, TwilightForestMod.idBiomeFireSwamp,
                TwilightForestMod.idBiomeThornlands };
    }

    private static void setConfiguredBiomeIds(int[] ids) {
        TwilightForestMod.idBiomeLake = ids[0];
        TwilightForestMod.idBiomeTwilightForest = ids[1];
        TwilightForestMod.idBiomeTwilightForestVariant = ids[2];
        TwilightForestMod.idBiomeHighlands = ids[3];
        TwilightForestMod.idBiomeMushrooms = ids[4];
        TwilightForestMod.idBiomeSwamp = ids[5];
        TwilightForestMod.idBiomeStream = ids[6];
        TwilightForestMod.idBiomeSnowfield = ids[7];
        TwilightForestMod.idBiomeGlacier = ids[8];
        TwilightForestMod.idBiomeClearing = ids[9];
        TwilightForestMod.idBiomeOakSavanna = ids[10];
        TwilightForestMod.idBiomeFireflyForest = ids[11];
        TwilightForestMod.idBiomeDeepMushrooms = ids[12];
        TwilightForestMod.idBiomeDarkForestCenter = ids[13];
        TwilightForestMod.idBiomeHighlandsCenter = ids[14];
        TwilightForestMod.idBiomeDarkForest = ids[15];
        TwilightForestMod.idBiomeEnchantedForest = ids[16];
        TwilightForestMod.idBiomeFireSwamp = ids[17];
        TwilightForestMod.idBiomeThornlands = ids[18];
    }

    /**
     * Populate the array with whether there is a biome using that ID
     */
    private static void checkUsedIDs(boolean[] usedIDs, BiomeGenBase[] registeredBiomes) {
        for (int i = 0; i < usedIDs.length; i++) {
            usedIDs[i] = registeredBiomes[i] != null;
        }
    }

    /**
     * Find an open biome ID
     */
    private static int findNextOpenBiomeId(boolean[] usedIDs) {
        for (int i = 0; i < Math.min(LEGACY_BIOME_ID_LIMIT, usedIDs.length); i++) {
            if (!usedIDs[i]) {
                usedIDs[i] = true;
                return i;
            }
        }

        // Uh oh...
        FMLLog.warning(
                "[TwilightForest] Could not find open biome ID.  Edit the Twilight Forest config to give all biomes unique IDs.");
        return -1;
    }

    /**
     * Check all of our biome IDs. If there is a biome in them that is not null and does not inherit from TFBiomeBase,
     * make a warning.
     */
    public static boolean areThereBiomeIdConflicts() {
        boolean conflict = TwilightForestMod.hasBiomeIdConflicts;
        boolean[] seenIDs = new boolean[BiomeGenBase.getBiomeGenArray().length];

        for (int id : getConfiguredBiomeIds()) {
            if (id < 0 || id >= seenIDs.length) {
                FMLLog.warning("[TwilightForest] Invalid biome ID %d in the Twilight Forest config.", id);
                conflict = true;
                continue;
            }
            if (seenIDs[id]) {
                FMLLog.warning("[TwilightForest] Multiple Twilight Forest biomes are configured with ID %d.", id);
                conflict = true;
            }
            seenIDs[id] = true;
            conflict |= isConflictAtBiomeID(id);
        }

        if (conflict) {
            FMLLog.warning(
                    "[TwilightForest] Biome ID conflict detected.  Edit the Twilight Forest config to give all biomes unique IDs.");
        }

        TwilightForestMod.hasBiomeIdConflicts |= conflict;
        return conflict;
    }

    /**
     * Check the ID for conflicts
     */
    public static boolean isConflictAtBiomeID(int id) {
        if (id < 0 || id >= BiomeGenBase.getBiomeGenArray().length) {
            FMLLog.warning("[TwilightForest] Invalid biome ID %d in the Twilight Forest config.", id);
            return true;
        }

        BiomeGenBase biomeAt = BiomeGenBase.getBiome(id);
        if (biomeAt == null || biomeAt instanceof TFBiomeBase) {
            return false;
        }

        FMLLog.warning(
                "[TwilightForest] Biome ID conflict.  Biome ID %d contains a biome named %s, but Twilight Forest is set to use that ID.",
                id,
                biomeAt.biomeName);
        return true;
    }

    /**
     * Register our biomes with the Forge biome dictionary
     */
    public static void registerWithBiomeDictionary() {
        BiomeDictionary.registerBiomeType(tfLake, Type.OCEAN);
        BiomeDictionary.registerBiomeType(twilightForest, Type.FOREST);
        BiomeDictionary.registerBiomeType(twilightForest2, Type.FOREST, Type.DENSE);
        BiomeDictionary.registerBiomeType(highlands, Type.FOREST, Type.MOUNTAIN, Type.CONIFEROUS);
        BiomeDictionary.registerBiomeType(mushrooms, Type.FOREST, Type.MUSHROOM);
        BiomeDictionary.registerBiomeType(tfSwamp, Type.SWAMP, Type.WET);
        BiomeDictionary.registerBiomeType(stream, Type.RIVER);
        BiomeDictionary.registerBiomeType(tfSnow, Type.FOREST, Type.SNOWY, Type.COLD, Type.CONIFEROUS);
        BiomeDictionary.registerBiomeType(glacier, Type.COLD, Type.SNOWY, Type.WASTELAND);
        BiomeDictionary.registerBiomeType(clearing, Type.PLAINS, Type.SPARSE);
        BiomeDictionary.registerBiomeType(oakSavanna, Type.FOREST, Type.SPARSE);
        BiomeDictionary.registerBiomeType(fireflyForest, Type.FOREST, Type.LUSH);
        BiomeDictionary.registerBiomeType(deepMushrooms, Type.FOREST, Type.MUSHROOM);
        BiomeDictionary.registerBiomeType(darkForest, Type.FOREST, Type.DENSE, Type.SPOOKY);
        BiomeDictionary.registerBiomeType(enchantedForest, Type.FOREST, Type.MAGICAL);
        BiomeDictionary.registerBiomeType(fireSwamp, Type.SWAMP, Type.WASTELAND, Type.HOT);
        BiomeDictionary.registerBiomeType(darkForestCenter, Type.FOREST, Type.DENSE, Type.SPOOKY, Type.MAGICAL);
        BiomeDictionary.registerBiomeType(highlandsCenter, Type.MESA, Type.DEAD, Type.DRY, Type.WASTELAND);
        BiomeDictionary.registerBiomeType(thornlands, Type.HILLS, Type.DEAD, Type.DRY, Type.WASTELAND);
    }

    public void genTerrainBlocks(World world, Random rand, Block[] blockStorage, byte[] metaStorage, int x, int z,
            double stoneNoise) {
        this.genTwilightBiomeTerrain(world, rand, blockStorage, metaStorage, x, z, stoneNoise);
    }

    /**
     * This is just the biome terrain gen function modified to have sea level at 32 instead of 64
     */
    public void genTwilightBiomeTerrain(World world, Random rand, Block[] blockStorage, byte[] metaStorage, int x,
            int z, double stoneNoise) {
        Block topBlock = this.topBlock;
        byte topMeta = (byte) (this.field_150604_aj & 255);
        Block fillerBlock = this.fillerBlock;
        byte fillerMeta = (byte) (this.field_76754_C & 255);
        int currentFillerDepth = -1;
        int maxFillerDepth = (int) (stoneNoise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int maskX = x & (CHUNK_WIDTH - 1);
        int maskZ = z & (CHUNK_WIDTH - 1);
        int worldHeight = blockStorage.length / (CHUNK_WIDTH * CHUNK_WIDTH);
        int columnOffset = (maskZ * CHUNK_WIDTH + maskX) * worldHeight;

        for (int y = WORLD_HEIGHT - 1; y >= 0; --y) {
            int index = columnOffset + y;

            if (y <= rand.nextInt(BEDROCK_DEPTH_VARIATION)) {
                blockStorage[index] = Blocks.bedrock;
            } else {
                Block currentBlock = blockStorage[index];

                if (currentBlock != null && currentBlock.getMaterial() != Material.air) {
                    if (currentBlock == Blocks.stone) {
                        // ADDED STONE REPLACEMENT
                        if (this.getStoneReplacementBlock() != null) {
                            blockStorage[index] = this.getStoneReplacementBlock();
                            metaStorage[index] = this.getStoneReplacementMeta();
                        }

                        if (currentFillerDepth == -1) {
                            if (maxFillerDepth <= 0) {
                                topBlock = null;
                                topMeta = 0;
                                fillerBlock = Blocks.stone;
                                fillerMeta = 0;
                            } else if (y >= (SEA_LEVEL - 5) && y <= SEA_LEVEL) {
                                topBlock = this.topBlock;
                                topMeta = (byte) (this.field_150604_aj & 255);
                                fillerBlock = this.fillerBlock;
                                fillerMeta = (byte) (this.field_76754_C & 255);
                            }

                            if (y < (SEA_LEVEL - 1) && (topBlock == null || topBlock.getMaterial() == Material.air)) {
                                if (this.getFloatTemperature(x, y, z) < 0.15F) {
                                    topBlock = Blocks.ice;
                                    topMeta = 0;
                                } else {
                                    topBlock = Blocks.water;
                                    topMeta = 0;
                                }
                            }

                            currentFillerDepth = maxFillerDepth;

                            if (y >= (SEA_LEVEL - 2)) {
                                blockStorage[index] = topBlock;
                                metaStorage[index] = topMeta;
                            } else if (y < (SEA_LEVEL - 8) - maxFillerDepth) {
                                topBlock = null;
                                fillerBlock = Blocks.stone;
                                fillerMeta = 0;
                                blockStorage[index] = Blocks.gravel;
                            } else {
                                blockStorage[index] = fillerBlock;
                                metaStorage[index] = fillerMeta;
                            }
                        } else if (currentFillerDepth > 0) {
                            --currentFillerDepth;
                            blockStorage[index] = fillerBlock;
                            metaStorage[index] = fillerMeta;

                            if (currentFillerDepth == 0 && fillerBlock == Blocks.sand) {
                                currentFillerDepth = rand.nextInt(4) + Math.max(0, y - (SEA_LEVEL - 1));
                                fillerBlock = Blocks.sandstone;
                                fillerMeta = 0;
                            }
                        }
                    }
                } else {
                    // air pockets end the surface layer, dont continue filler below caves
                    currentFillerDepth = -1;
                }
            }
        }
    }

    /**
     * Return a block if you want it to replace stone in the terrain generation
     */
    public Block getStoneReplacementBlock() {
        return null;
    }

    /**
     * Metadata for the stone replacement block
     */
    public byte getStoneReplacementMeta() {
        return 0;
    }

    /**
     * Does the player have the achievement needed to be in this biome? Defaults to true for no achievement required
     * biomes.
     */
    public boolean doesPlayerHaveRequiredAchievement(EntityPlayer player) {
        Achievement requiredAchievement = getRequiredAchievement();
        if (requiredAchievement == null) {
            return true;
        }

        if (player instanceof EntityPlayerMP) {
            StatisticsFile stats = ((EntityPlayerMP) player).func_147099_x();
            return stats == null || stats.hasAchievementUnlocked(requiredAchievement);
        }
        if (player instanceof EntityClientPlayerMP) {
            StatFileWriter stats = ((EntityClientPlayerMP) player).getStatFileWriter();
            return stats == null || stats.hasAchievementUnlocked(requiredAchievement);
        }
        return true;
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    protected Achievement getRequiredAchievement() {
        return null;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    public void enforceProgession(EntityPlayer player, World world) {}

    /**
     * Returns the list of underground creatures.
     */
    public List<SpawnListEntry> getUndergroundSpawnableList() {
        return this.undergroundMonsterList;
    }
}
