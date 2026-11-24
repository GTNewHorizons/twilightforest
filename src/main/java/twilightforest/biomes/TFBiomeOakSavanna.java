package twilightforest.biomes;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenCanopyOak;
import twilightforest.world.TFGenNoTree;

public class TFBiomeOakSavanna extends TFBiomeTwilightForest {

    private static final int TALL_GRASS_ATTEMPTS = 7;
    private static final int CHUNK_WIDTH = 16;
    private static final int CHUNK_OFFSET = 8;
    private static final int HEIGHT_RANDOM_OFFSET = 32;

    public TFBiomeOakSavanna(int biomeId) {
        super(biomeId);

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.canopyTreeGen = new TFGenCanopyOak();
        decorator.alternateCanopyChance = 0.8F;
        decorator.alternateCanopyGen = new TFGenNoTree();

        this.temperature = 0.9F;
        this.rainfall = 0.0F;

        decorator.treesPerChunk = 1;
        decorator.flowersPerChunk = 4;
        decorator.grassPerChunk = 20;
    }

    /**
     * Oak trees all over
     */
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        return random.nextInt(10) == 0 ? new WorldGenBigTree(false) : worldGeneratorTrees;
    }

    /**
     * Oak savanna fern mix
     */
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        if (random.nextInt(10) == 0) {
            return new WorldGenTallGrass(Blocks.tallgrass, 2);
        }
        if (random.nextInt(10) == 0) {
            return new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MAYAPPLE);
        }
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    /**
     * Multi-color flowers!
     */
    @Override
    public String func_150572_a(Random random, int x, int y, int z) {
        double noise = plantNoise.func_151601_a(x / 200.0D, z / 200.0D);
        if (noise < -0.8D) {
            return BlockFlower.field_149859_a[4 + random.nextInt(4)];
        }
        if (random.nextInt(3) == 0) {
            return BlockFlower.field_149858_b[0];
        }

        switch (random.nextInt(3)) {
            case 0:
                return BlockFlower.field_149859_a[0];
            case 1:
                return BlockFlower.field_149859_a[3];
            default:
                return BlockFlower.field_149859_a[8];
        }
    }

    @Override
    public void decorate(World world, Random random, int mapX, int mapZ) {
        generateTallGrass(world, random, mapX, mapZ);
        super.decorate(world, random, mapX, mapZ);
    }

    private void generateTallGrass(World world, Random random, int mapX, int mapZ) {
        // 2-tall grass!
        genTallFlowers.func_150548_a(2);

        for (int i = 0; i < TALL_GRASS_ATTEMPTS; i++) {
            int x = mapX + random.nextInt(CHUNK_WIDTH) + CHUNK_OFFSET;
            int z = mapZ + random.nextInt(CHUNK_WIDTH) + CHUNK_OFFSET;
            int y = random.nextInt(world.getHeightValue(x, z) + HEIGHT_RANDOM_OFFSET);
            genTallFlowers.generate(world, random, x, y, z);
        }
    }
}
