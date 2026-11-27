package twilightforest.biomes;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class TFBiomeTwilightForestVariant extends TFBiomeBase {

    private static final int TALL_FERN_TYPE = 3;
    private static final int TALL_FERN_ATTEMPTS = 7;

    public TFBiomeTwilightForestVariant(int biomeId) {
        super(biomeId);

        this.temperature = 0.7F;
        this.rainfall = 0.8F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.setTreesPerChunk(25);
        decorator.setGrassPerChunk(15);
        decorator.setFlowersPerChunk(8);
    }

    /**
     * Occasional shrub, no birches
     */
    @Override
    public WorldGenAbstractTree func_150567_a(Random random) {
        if (random.nextInt(5) == 0) {
            return new WorldGenShrub(3, 0);
        }
        if (random.nextInt(10) == 0) {
            return new WorldGenBigTree(false);
        }
        return worldGeneratorTrees;
    }

    /**
     * EVEN MOAR FERNZ!
     */
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        if (random.nextInt(4) != 0) {
            return new WorldGenTallGrass(Blocks.tallgrass, 2);
        }
        if (random.nextBoolean()) {
            return new WorldGenTallGrass(TFBlocks.plant, BlockTFPlant.META_MAYAPPLE);
        }
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }

    /**
     * Add tall ferns
     */
    @Override
    public void decorate(World world, Random random, int chunkX, int chunkZ) {
        genTallFlowers.func_150548_a(TALL_FERN_TYPE);

        for (int i = 0; i < TALL_FERN_ATTEMPTS; i++) {
            int x = chunkX + random.nextInt(16) + 8;
            int z = chunkZ + random.nextInt(16) + 8;
            int y = random.nextInt(world.getHeightValue(x, z) + 32);
            genTallFlowers.generate(world, random, x, y, z);
        }

        super.decorate(world, random, chunkX, chunkZ);
    }
}
