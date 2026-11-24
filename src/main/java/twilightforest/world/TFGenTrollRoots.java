package twilightforest.world;

import java.util.Random;

import net.minecraft.world.World;

import twilightforest.block.BlockTFTrollRoot;
import twilightforest.block.TFBlocks;

public class TFGenTrollRoots extends TFGenerator {

    private static final int MIN_ROOT_Y = 5;
    private static final int ROOT_PLACEMENT_CHANCE = 6;
    private static final int RIPE_BERRY_CHANCE = 10;
    private static final int ROOT_HORIZONTAL_VARIATION = 4;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int originX = x;
        int originZ = z;

        for (; y > MIN_ROOT_Y; --y) {
            if (world.isAirBlock(x, y, z) && BlockTFTrollRoot.canPlaceRootBelow(world, x, y + 1, z)
                    && random.nextInt(ROOT_PLACEMENT_CHANCE) > 0) {
                if (random.nextInt(RIPE_BERRY_CHANCE) == 0) {
                    world.setBlock(x, y, z, TFBlocks.unripeTrollBer);
                } else {
                    world.setBlock(x, y, z, TFBlocks.trollVidr);
                }
            } else {
                x = originX + random.nextInt(ROOT_HORIZONTAL_VARIATION) - random.nextInt(ROOT_HORIZONTAL_VARIATION);
                z = originZ + random.nextInt(ROOT_HORIZONTAL_VARIATION) - random.nextInt(ROOT_HORIZONTAL_VARIATION);
            }
        }
        return true;
    }
}
