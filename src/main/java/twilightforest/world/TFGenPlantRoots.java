package twilightforest.world;

import java.util.Random;

import net.minecraft.world.World;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class TFGenPlantRoots extends TFGenerator {

    private static final int MIN_GENERATION_Y = 5;
    private static final int OFFSET_SPREAD = 4;
    private static final int PLACEMENT_CHANCE = 6;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int originX = x;
        int originZ = z;

        for (; y > MIN_GENERATION_Y; --y) {
            if (world.isAirBlock(x, y, z) && BlockTFPlant.canPlaceRootBelow(world, x, y + 1, z)
                    && rand.nextInt(PLACEMENT_CHANCE) > 0) {
                world.setBlock(x, y, z, TFBlocks.plant, BlockTFPlant.META_ROOT_STRAND, 2);
            } else {
                x = originX + rand.nextInt(OFFSET_SPREAD) - rand.nextInt(OFFSET_SPREAD);
                z = originZ + rand.nextInt(OFFSET_SPREAD) - rand.nextInt(OFFSET_SPREAD);
            }
        }
        return true;
    }
}
