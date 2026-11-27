package twilightforest.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;

import twilightforest.TFFeature;
import twilightforest.TwilightForestMod;
import twilightforest.world.TFGenCanopyMushroom;
import twilightforest.world.TFGenCanopyTree;
import twilightforest.world.TFGenFallenHollowLog;
import twilightforest.world.TFGenFallenSmallLog;
import twilightforest.world.TFGenFoundation;
import twilightforest.world.TFGenGroveRuins;
import twilightforest.world.TFGenHollowStump;
import twilightforest.world.TFGenHollowTree;
import twilightforest.world.TFGenMangroveTree;
import twilightforest.world.TFGenMonolith;
import twilightforest.world.TFGenMyceliumBlob;
import twilightforest.world.TFGenOutsideStalagmite;
import twilightforest.world.TFGenPlantRoots;
import twilightforest.world.TFGenStoneCircle;
import twilightforest.world.TFGenTorchBerries;
import twilightforest.world.TFGenWell;
import twilightforest.world.TFGenWitchHut;
import twilightforest.world.TFGenWoodRoots;
import twilightforest.world.TFGenerator;
import twilightforest.world.TFTreeGenerator;

public class TFBiomeDecorator extends BiomeDecorator {

    private static final int CHUNK_SIZE = 16;
    private static final int DECORATION_OFFSET = 8;
    private static final int RANDOM_FEATURE_CHANCE = 6;
    private static final int MAX_RANDOM_FEATURE_HEIGHT = 75;
    private static final int PLANT_ROOTS_PER_CHUNK = 12;
    private static final int WOOD_ROOTS_PER_CHUNK = 20;
    private static final int CAVE_WATER_SOURCES_PER_CHUNK = 50;
    private static final int TORCH_BERRIES_PER_CHUNK = 3;

    TFGenCanopyTree canopyTreeGen;
    TFTreeGenerator alternateCanopyGen;
    TFGenHollowTree hollowTreeGen;
    TFGenMyceliumBlob myceliumBlobGen;
    WorldGenLakes extraLakeGen;
    WorldGenLakes extraLavaPoolGen;
    TFGenMangroveTree mangroveTreeGen;

    TFGenPlantRoots plantRootGen;
    TFGenWoodRoots woodRootGen;
    WorldGenLiquids caveWaterGen;
    TFGenTorchBerries torchBerryGen;

    public float canopyPerChunk;
    public float alternateCanopyChance;
    public int myceliumPerChunk;
    public int mangrovesPerChunk;
    public int lakesPerChunk;
    public float lavaPoolChance;

    static final List<RuinEntry> ruinList = new ArrayList<>();
    static {
        // make list of ruins
        ruinList.add(new RuinEntry(new TFGenStoneCircle(), 10));
        ruinList.add(new RuinEntry(new TFGenWell(), 10));
        ruinList.add(new RuinEntry(new TFGenWitchHut(), 5));
        ruinList.add(new RuinEntry(new TFGenOutsideStalagmite(), 12));
        ruinList.add(new RuinEntry(new TFGenFoundation(), 10));
        ruinList.add(new RuinEntry(new TFGenMonolith(), 10));
        ruinList.add(new RuinEntry(new TFGenGroveRuins(), 5));
        ruinList.add(new RuinEntry(new TFGenHollowStump(), 12));
        ruinList.add(new RuinEntry(new TFGenFallenHollowLog(), 10));
        ruinList.add(new RuinEntry(new TFGenFallenSmallLog(), 10));
    }

    /**
     * WeightedRandomItem for making the minor features
     */
    static class RuinEntry extends WeightedRandom.Item {

        public final TFGenerator generator;

        public RuinEntry(TFGenerator generator, int weight) {
            super(weight);
            this.generator = generator;
        }
    }

    public TFBiomeDecorator() {
        super();

        canopyTreeGen = new TFGenCanopyTree();
        alternateCanopyGen = new TFGenCanopyMushroom();
        mangroveTreeGen = new TFGenMangroveTree();
        myceliumBlobGen = new TFGenMyceliumBlob(5);
        hollowTreeGen = new TFGenHollowTree();
        extraLakeGen = new WorldGenLakes(Blocks.water);
        extraLavaPoolGen = new WorldGenLakes(Blocks.lava);

        plantRootGen = new TFGenPlantRoots();
        woodRootGen = new TFGenWoodRoots();
        caveWaterGen = new WorldGenLiquids(Blocks.flowing_water);
        torchBerryGen = new TFGenTorchBerries();

        canopyPerChunk = TwilightForestMod.canopyCoverage;
        alternateCanopyChance = 0;
        myceliumPerChunk = 0;
        lakesPerChunk = 0;
        lavaPoolChance = 0;
        mangrovesPerChunk = 0;
    }

    /**
     * Decorates the world. Calls code that was formerly (pre-1.8) in ChunkProviderGenerate.populate
     */
    @Override
    public void decorateChunk(World world, Random rand, BiomeGenBase biome, int mapX, int mapZ) {
        // check for features
        TFFeature nearFeature = TFFeature.getNearestFeature(mapX >> 4, mapZ >> 4, world);

        if (!nearFeature.areChunkDecorationsEnabled) {
            // no normal decorations here, these parts supply their own decorations.
            decorateUnderground(world, rand, mapX, mapZ);
            decorateOnlyOres(world, rand, mapX, mapZ);
            return;
        }

        final World oldWorld = this.currentWorld;
        final Random oldRandom = this.randomGenerator;
        final int oldX = this.chunk_X;
        final int oldZ = this.chunk_Z;

        if (world != null) {
            this.currentWorld = world;
        }
        if (rand != null) {
            this.randomGenerator = rand;
        }
        this.chunk_X = mapX;
        this.chunk_Z = mapZ;

        try {
            genDecorations(biome);
        } finally {
            // Restore old values to prevent cascading generation issues.
            this.currentWorld = oldWorld;
            this.randomGenerator = oldRandom;
            this.chunk_X = oldX;
            this.chunk_Z = oldZ;
        }
    }

    /**
     * The method that does the work of actually decorating chunks
     */
    @Override
    protected void genDecorations(BiomeGenBase biome) {
        final Random randomGenerator = this.randomGenerator;

        if (randomGenerator == null) {
            throw new NullPointerException("TFBiomeDecorator#genDecorations randomGenerator is null");
        }
        if (currentWorld == null) {
            throw new NullPointerException("TFBiomeDecorator#genDecorations currentWorld is null");
        }

        // random features!
        generateRandomFeature(randomGenerator);

        // add canopy trees
        generateCanopyTrees(randomGenerator);

        // mangrove trees
        generateMangroves(randomGenerator);

        // add extra lakes for swamps
        generateExtraLakes(randomGenerator);

        // add extra lava for fire swamps
        generateLavaPool(randomGenerator);

        // mycelium blobs
        generateMycelium(randomGenerator);

        super.genDecorations(biome);

        decorateUnderground(currentWorld, randomGenerator, chunk_X, chunk_Z);
    }

    private void generateRandomFeature(Random rand) {
        if (rand.nextInt(RANDOM_FEATURE_CHANCE) == 0) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            if (y < MAX_RANDOM_FEATURE_HEIGHT) {
                randomFeature(rand).generate(currentWorld, rand, x, y, z);
            }
        }
    }

    private void generateCanopyTrees(Random rand) {
        // always roll the chance even if the configured coverage is an integer
        int treeCount = (int) canopyPerChunk + (rand.nextFloat() < canopyPerChunk - (int) canopyPerChunk ? 1 : 0);
        for (int i = 0; i < treeCount; i++) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            if (alternateCanopyChance > 0 && rand.nextFloat() <= alternateCanopyChance) {
                alternateCanopyGen.generate(currentWorld, rand, x, y, z);
            } else {
                canopyTreeGen.generate(currentWorld, rand, x, y, z);
            }
        }
    }

    private void generateMangroves(Random rand) {
        for (int i = 0; i < mangrovesPerChunk; i++) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            mangroveTreeGen.generate(currentWorld, rand, x, y, z);
        }
    }

    private void generateExtraLakes(Random rand) {
        for (int i = 0; i < lakesPerChunk; i++) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            extraLakeGen.generate(currentWorld, rand, x, y, z);
        }
    }

    private void generateLavaPool(Random rand) {
        // keep this rng draw even when the chance is zero
        if (rand.nextFloat() <= lavaPoolChance) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            extraLavaPoolGen.generate(currentWorld, rand, x, y, z);
        }
    }

    private void generateMycelium(Random rand) {
        for (int i = 0; i < myceliumPerChunk; i++) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int y = currentWorld.getHeightValue(x, z);
            myceliumBlobGen.generate(currentWorld, rand, x, y, z);
        }
    }

    /**
     * Generate the Twilight Forest underground decorations
     */
    protected void decorateUnderground(World world, Random rand, int mapX, int mapZ) {
        // generate roots
        for (int i = 0; i < PLANT_ROOTS_PER_CHUNK; ++i) {
            int rx = mapX + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            byte ry = 64;
            int rz = mapZ + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            plantRootGen.generate(world, rand, rx, ry, rz);
        }

        // generate roots
        for (int i = 0; i < WOOD_ROOTS_PER_CHUNK; ++i) {
            int rx = mapX + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int ry = rand.nextInt(64);
            int rz = mapZ + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            woodRootGen.generate(world, rand, rx, ry, rz);
        }

        // extra underground water sources
        if (this.generateLakes) {
            for (int i = 0; i < CAVE_WATER_SOURCES_PER_CHUNK; ++i) {
                int rx = mapX + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
                int ry = rand.nextInt(24) + 4;
                int rz = mapZ + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
                caveWaterGen.generate(world, rand, rx, ry, rz);
            }
        }

        // torch berries are almost guaranteed to spawn so we don't need many
        for (int i = 0; i < TORCH_BERRIES_PER_CHUNK; ++i) {
            int rx = mapX + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            int ry = 64;
            int rz = mapZ + rand.nextInt(CHUNK_SIZE) + DECORATION_OFFSET;
            torchBerryGen.generate(world, rand, rx, ry, rz);
        }
    }

    /**
     * Generates ores only
     */
    public void decorateOnlyOres(World world, Random rand, int mapX, int mapZ) {
        final World oldWorld = this.currentWorld;
        final Random oldRandom = this.randomGenerator;
        final int oldX = this.chunk_X;
        final int oldZ = this.chunk_Z;

        if (world != null) {
            this.currentWorld = world;
        }
        if (rand != null) {
            this.randomGenerator = rand;
        }
        this.chunk_X = mapX;
        this.chunk_Z = mapZ;

        try {
            this.generateOres();
        } finally {
            // restore old values to prevent cascading gen issues
            this.currentWorld = oldWorld;
            this.randomGenerator = oldRandom;
            this.chunk_X = oldX;
            this.chunk_Z = oldZ;
        }
    }

    /**
     * Gets a random feature suitible to the current biome.
     */
    public TFGenerator randomFeature(Random rand) {
        return ((RuinEntry) WeightedRandom.getRandomItem(rand, ruinList)).generator;
    }

    public void setTreesPerChunk(int treesPerChunk) {
        this.treesPerChunk = treesPerChunk;
    }

    public void setBigMushroomsPerChunk(int bigMushroomsPerChunk) {
        this.bigMushroomsPerChunk = bigMushroomsPerChunk;
    }

    public void setClayPerChunk(int clayPerChunk) {
        this.clayPerChunk = clayPerChunk;
    }

    public void setDeadBushPerChunk(int deadBushPerChunk) {
        this.deadBushPerChunk = deadBushPerChunk;
    }

    public void setMushroomsPerChunk(int mushroomsPerChunk) {
        this.mushroomsPerChunk = mushroomsPerChunk;
    }

    public void setFlowersPerChunk(int flowersPerChunk) {
        this.flowersPerChunk = flowersPerChunk;
    }

    public void setReedsPerChunk(int reedsPerChunk) {
        this.reedsPerChunk = reedsPerChunk;
    }

    public void setWaterlilyPerChunk(int waterlilyPerChunk) {
        this.waterlilyPerChunk = waterlilyPerChunk;
    }

    public void setGrassPerChunk(int grassPerChunk) {
        this.grassPerChunk = grassPerChunk;
    }
}
