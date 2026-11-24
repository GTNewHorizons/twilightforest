package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFTroll;
import twilightforest.world.TFGenTrollRoots;

public class TFBiomeHighlands extends TFBiomeBase {

    private static final int CHUNK_SIZE = 16;
    private static final int DECORATION_OFFSET = 8;
    private static final int FERN_ATTEMPTS = 7;
    private static final int TROLL_ROOT_ATTEMPTS = 24;
    private static final int TROLL_ROOT_HEIGHT = 64;
    private static final int MUSHGLOOM_HEIGHT = 64;
    private static final int TALL_FERN_METADATA = 3;

    private static final WorldGenTaiga1 TAIGA_GEN_1 = new WorldGenTaiga1();
    private static final WorldGenTaiga2 TAIGA_GEN_2 = new WorldGenTaiga2(false);
    private static final WorldGenMegaPineTree MEGA_PINE_GEN_1 = new WorldGenMegaPineTree(false, false);
    private static final WorldGenMegaPineTree MEGA_PINE_GEN_2 = new WorldGenMegaPineTree(false, true);
    private static final WorldGenBlockBlob BOULDER_GEN = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);
    private static final TFGenTrollRoots TROLL_ROOT_GEN = new TFGenTrollRoots();
    private static final WorldGenTallGrass MUSHGLOOM_GEN = new WorldGenTallGrass(
            TFBlocks.plant,
            BlockTFPlant.META_MUSHGLOOM);

    public TFBiomeHighlands(int i) {
        super(i);

        temperature = 0.4F;
        rainfall = 0.7F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        // a negative canopy count disables canopy trees without changing the decorators rng calls
        decorator.canopyPerChunk = -999;
        decorator.setGrassPerChunk(7);
        decorator.setDeadBushPerChunk(1);
        decorator.generateLakes = false; // actually underground water sources

        undergroundMonsterList.clear();
        undergroundMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 4, 4));
        undergroundMonsterList.add(new SpawnListEntry(EntityTFTroll.class, 10, 4, 4));
    }

    // spruce, mega pine and birch trees
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(4) == 0) {
            return TAIGA_GEN_1;
        }
        if (random.nextInt(10) == 0) {
            return TAIGA_GEN_2;
        }
        if (random.nextInt(3) == 0) {
            return MEGA_PINE_GEN_1;
        }
        if (random.nextInt(13) == 0) {
            return MEGA_PINE_GEN_2;
        }
        return birchGen;
    }

    // Gets a WorldGen appropriate for this biome.
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return new WorldGenTallGrass(Blocks.tallgrass, random.nextInt(5) > 0 ? 2 : 1);
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, Block[] blockStorage, byte[] metaStorage, int x, int z,
            double noiseVal) {
        topBlock = Blocks.grass;
        field_150604_aj = 0;
        fillerBlock = Blocks.dirt;

        if (noiseVal > 1.75D) {
            topBlock = Blocks.dirt;
            field_150604_aj = 1;
        } else if (noiseVal > -0.95D) {
            topBlock = Blocks.dirt;
            field_150604_aj = 2;
        }

        genTwilightBiomeTerrain(world, rand, blockStorage, metaStorage, x, z, noiseVal);
    }

    /**
     * Add extra decorations
     */
    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        generateBoulders(world, random, mapX, mapZ);
        generateTallFerns(world, random, mapX, mapZ);
        generateMushgloom(world, random, mapX, mapZ);
        generateTrollRoots(world, random, mapX, mapZ);

        super.decorate(world, random, mapX, mapZ);
    }

    private static void generateBoulders(World world, Random random, int mapX, int mapZ) {
        // boulders
        int maxBoulders = random.nextInt(2);
        for (int i = 0; i < maxBoulders; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = world.getHeightValue(x, z);
            BOULDER_GEN.generate(world, random, x, y, z);
        }
    }

    private void generateTallFerns(World world, Random random, int mapX, int mapZ) {
        // giant ferns
        genTallFlowers.func_150548_a(TALL_FERN_METADATA);
        for (int i = 0; i < FERN_ATTEMPTS; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = random.nextInt(world.getHeightValue(x, z) + 32);
            genTallFlowers.generate(world, random, x, y, z);
        }
    }

    private static void generateMushgloom(World world, Random random, int mapX, int mapZ) {
        // mushglooms
        int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
        int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
        int y = random.nextInt(MUSHGLOOM_HEIGHT);
        MUSHGLOOM_GEN.generate(world, random, x, y, z);
    }

    private static void generateTrollRoots(World world, Random random, int mapX, int mapZ) {
        // generate roots
        for (int i = 0; i < TROLL_ROOT_ATTEMPTS; i++) {
            int x = mapX + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            TROLL_ROOT_GEN.generate(world, random, x, TROLL_ROOT_HEIGHT, z);
        }
    }

    /**
     * Flowers
     */
    @Override
    public String func_150572_a(Random rand, int x, int y, int z) {
        return rand.nextBoolean() ? BlockFlower.field_149858_b[0] : BlockFlower.field_149859_a[8];
    }

    /**
     * If there is a required achievement to be here, return it, otherwise return null
     */
    @Override
    protected Achievement getRequiredAchievement() {
        return TFAchievementPage.twilightProgressGlacier;
    }

    /**
     * Do something bad to a player in the wrong biome.
     */
    @Override
    public void enforceProgession(EntityPlayer player, World world) {
        if (!world.isRemote && world.getWorldTime() % 5 == 0) {
            player.attackEntityFrom(DamageSource.magic, 0.5F);
            world.playSoundAtEntity(player, "random.fizz", 1.0F, 1.0F);

            // hint monster?
            if (world.rand.nextInt(4) == 0) {
                TFFeature.trollCave.trySpawnHintMonster(world, player);
            }
        }
    }
}
