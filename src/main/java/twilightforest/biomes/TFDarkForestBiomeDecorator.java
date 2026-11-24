package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.TFFeature;
import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenDarkCanopyTree;
import twilightforest.world.TFGenTallGrass;
import twilightforest.world.TFTreeGenerator;
import twilightforest.world.TFWorld;

public class TFDarkForestBiomeDecorator extends TFBiomeDecorator {

    private static final int CHUNK_SIZE = 16;
    private static final int CHUNK_OFFSET = 8;
    private static final int RANDOM_PLANT_HEIGHT = 128;

    TFTreeGenerator darkCanopyTreeGen;
    TFGenTallGrass worldGenDeadBush;
    WorldGenTallGrass worldGenForestGrass;
    WorldGenTallGrass worldGenMushgloom;

    public TFDarkForestBiomeDecorator() {
        super();

        darkCanopyTreeGen = new TFGenDarkCanopyTree();
        worldGenDeadBush = new TFGenTallGrass(TFBlocks.plant, BlockTFPlant.META_DEADBUSH, 8);
        worldGenForestGrass = new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_FORESTGRASS);
        worldGenMushgloom = new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MUSHGLOOM);
    }

    /**
     * Decorates the world. Calls code that was formerly (pre-1.8) in ChunkProviderGenerate.populate
     */
    @Override
    public void decorateChunk(World world, Random rand, BiomeGenBase biome, int mapX, int mapZ) {
        // just decorate with what we need here
        TFFeature nearFeature = TFFeature.getNearestFeature(mapX >> 4, mapZ >> 4, world);
        if (nearFeature.areChunkDecorationsEnabled) {
            // add dark canopy trees
            generateDarkCanopyTrees(world, rand, mapX, mapZ);

            // regular trees
            generateRegularTrees(world, rand, biome, mapX, mapZ);

            // dead bushes
            generateRandomHeightPlants(world, rand, mapX, mapZ, deadBushPerChunk, worldGenDeadBush);

            // forest grass bushes
            generateRandomHeightPlants(world, rand, mapX, mapZ, deadBushPerChunk, worldGenForestGrass);

            // mushrooms
            generateMushrooms(world, rand, mapX, mapZ);

            // pumpkins
            generatePumpkins(world, rand, mapX, mapZ);
        }

        // do underground decorations
        decorateUnderground(world, rand, mapX, mapZ);
        decorateOnlyOres(world, rand, mapX, mapZ);
    }

    private void generateDarkCanopyTrees(World world, Random rand, int mapX, int mapZ) {
        int canopyCount = (int) canopyPerChunk + (rand.nextFloat() < canopyPerChunk - (int) canopyPerChunk ? 1 : 0);
        for (int i = 0; i < canopyCount; i++) {
            int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int y = world.getHeightValue(x, z);
            darkCanopyTreeGen.generate(world, rand, x, y, z);
        }
    }

    private void generateRegularTrees(World world, Random rand, BiomeGenBase biome, int mapX, int mapZ) {
        for (int i = 0; i < treesPerChunk; i++) {
            int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int y = getGroundLevel(world, x, z);
            WorldGenerator tree = biome.func_150567_a(rand);
            tree.setScale(1.0D, 1.0D, 1.0D);
            tree.generate(world, rand, x, y, z);
        }
    }

    private void generateRandomHeightPlants(World world, Random rand, int mapX, int mapZ, int count,
            WorldGenerator generator) {
        for (int i = 0; i < count; i++) {
            generateAtRandomHeight(world, rand, mapX, mapZ, generator);
        }
    }

    private void generateMushrooms(World world, Random rand, int mapX, int mapZ) {
        for (int i = 0; i < mushroomsPerChunk; i++) {
            if (rand.nextInt(8) == 0) {
                generateAtGroundLevel(world, rand, mapX, mapZ, mushroomBrownGen);
            }
            if (rand.nextInt(16) == 0) {
                generateAtGroundLevel(world, rand, mapX, mapZ, mushroomRedGen);
            }
            if (rand.nextInt(24) == 0) {
                // mushglooms
                generateAtGroundLevel(world, rand, mapX, mapZ, worldGenMushgloom);
            }
        }
        if (rand.nextInt(4) == 0) {
            generateAtRandomHeight(world, rand, mapX, mapZ, mushroomBrownGen);
        }
        if (rand.nextInt(8) == 0) {
            generateAtRandomHeight(world, rand, mapX, mapZ, mushroomRedGen);
        }
    }

    private void generatePumpkins(World world, Random rand, int mapX, int mapZ) {
        if (rand.nextInt(32) == 0) {
            int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
            int y = getGroundLevel(world, x, z);
            (new WorldGenPumpkin()).generate(world, rand, x, y, z);
        }
    }

    private void generateAtGroundLevel(World world, Random rand, int mapX, int mapZ, WorldGenerator generator) {
        int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
        int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
        int y = getGroundLevel(world, x, z);
        generator.generate(world, rand, x, y, z);
    }

    private void generateAtRandomHeight(World world, Random rand, int mapX, int mapZ, WorldGenerator generator) {
        int x = mapX + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
        int z = mapZ + rand.nextInt(CHUNK_SIZE) + CHUNK_OFFSET;
        int y = rand.nextInt(RANDOM_PLANT_HEIGHT);
        generator.generate(world, rand, x, y, z);
    }

    public int getGroundLevel(World world, int x, int z) {
        // go from sea level up. If we get grass, return that, otherwise return the last dirt, stone or
        // gravel we got
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        int lastDirt = TFWorld.SEALEVEL;
        for (int y = TFWorld.SEALEVEL; y < TFWorld.CHUNKHEIGHT - 1; y++) {
            Block blockID = chunk.getBlock(x & 15, y, z & 15);
            // grass = return immediately
            if (blockID == Blocks.grass) {
                return y + 1;
            } else if (blockID == Blocks.dirt || blockID == Blocks.stone || blockID == Blocks.gravel) {
                lastDirt = y + 1;
            }
        }

        return lastDirt;
    }
}
