package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

public class TFGenBigMushgloom extends TFGenerator {

    private static final int CAP_RADIUS = 1;
    private static final int MIN_HEIGHT = 3;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = MIN_HEIGHT + rand.nextInt(2) + rand.nextInt(2);

        if (y < 1 || (long) y + height > world.getHeight()
                || !isAreaSuitable(world, rand, x - CAP_RADIUS, y, z - CAP_RADIUS, 3, height, 3)) {
            return false;
        }

        Block blockUnder = world.getBlock(x, y - 1, z);
        if (blockUnder != Blocks.dirt && blockUnder != Blocks.grass && blockUnder != Blocks.mycelium) {
            return false;
        }

        // generate!
        for (int dy = 0; dy < height - 2; dy++) {
            setBlockAndNotifyAdequately(world, x, y + dy, z, TFBlocks.hugeGloomBlock, 10);
        }

        makeMushroomCap(world, x, z, y + (height - 2));
        if (rand.nextBoolean()) {
            makeMushroomCap(world, x, z, y + (height - 1));
        }

        return true;
    }

    private void makeMushroomCap(World world, int x, int z, int y) {
        // metadata is row major, the northern row is 1-3 and the southern row is 7-9
        for (int dz = -CAP_RADIUS; dz <= CAP_RADIUS; dz++) {
            for (int dx = -CAP_RADIUS; dx <= CAP_RADIUS; dx++) {
                int metadata = (dz + CAP_RADIUS) * 3 + dx + CAP_RADIUS + 1;
                setBlockAndNotifyAdequately(world, x + dx, y, z + dz, TFBlocks.hugeGloomBlock, metadata);
            }
        }
    }
}
