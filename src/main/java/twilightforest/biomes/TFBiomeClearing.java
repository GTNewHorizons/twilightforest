package twilightforest.biomes;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class TFBiomeClearing extends TFBiomeBase {

    public TFBiomeClearing(int biomeId) {
        super(biomeId);

        this.temperature = 0.8F;
        this.rainfall = 0.4F;

        TFBiomeDecorator decorator = getTFBiomeDecorator();
        decorator.canopyPerChunk = -999;
        decorator.setTreesPerChunk(-999);
        decorator.setFlowersPerChunk(4);
        decorator.setGrassPerChunk(10);
    }

    // No ferns
    @Override
    public WorldGenerator getRandomWorldGenForGrass(Random random) {
        return new WorldGenTallGrass(Blocks.tallgrass, 1);
    }
}
