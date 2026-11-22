package twilightforest.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import twilightforest.block.TFBlocks;

/**
 * Generate huge lily pads
 *
 * @author Ben
 *
 */
public class TFGenHugeLilyPad extends WorldGenerator {

    private static final int GENERATION_ATTEMPTS = 10;
    private static final int HORIZONTAL_SPREAD = 8;
    private static final int VERTICAL_SPREAD = 4;
    private static final int PAD_WIDTH = 2;
    private static final long ORIENTATION_SEED = 8890919293L;

    private static final long ORIENTATION_X_FACTOR;
    private static final long ORIENTATION_Z_FACTOR;

    static {
        // these 2 draws originally repeated with the same seed for every placed pad
        FastRandom seedRandom = new FastRandom(ORIENTATION_SEED);
        ORIENTATION_X_FACTOR = seedRandom.nextLong();
        ORIENTATION_Z_FACTOR = seedRandom.nextLong();
    }

    private final FastRandom orientationRandom = new FastRandom(0L);

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int attempt = 0; attempt < GENERATION_ATTEMPTS; attempt++) {
            int padX = x + random.nextInt(HORIZONTAL_SPREAD) - random.nextInt(HORIZONTAL_SPREAD);
            int padY = y + random.nextInt(VERTICAL_SPREAD) - random.nextInt(VERTICAL_SPREAD);
            int padZ = z + random.nextInt(HORIZONTAL_SPREAD) - random.nextInt(HORIZONTAL_SPREAD);

            // align the 2x2 pad to even coords, including for -X and -Z
            padX &= ~1;
            padZ &= ~1;

            if (!shouldPlacePadAt(world, padX, padY, padZ)) {
                continue;
            }

            // this seems like a difficult way to generate 2 pseudorandom bits
            int orientation = getOrientation(padX, padZ);

            world.setBlock(padX, padY, padZ, TFBlocks.hugeLilyPad, orientation, 2);
            world.setBlock(padX + 1, padY, padZ, TFBlocks.hugeLilyPad, 1 | orientation, 2);
            world.setBlock(padX + 1, padY, padZ + 1, TFBlocks.hugeLilyPad, 2 | orientation, 2);
            world.setBlock(padX, padY, padZ + 1, TFBlocks.hugeLilyPad, 3 | orientation, 2);
        }
        return true;
    }

    private int getOrientation(int x, int z) {
        orientationRandom.setSeed((x * ORIENTATION_X_FACTOR) ^ (z * ORIENTATION_Z_FACTOR) ^ ORIENTATION_SEED);
        return orientationRandom.nextInt(4) << 2;
    }

    private static boolean shouldPlacePadAt(World world, int x, int y, int z) {
        if (y <= 0 || y >= world.getHeight()) {
            return false;
        }

        for (int offsetZ = 0; offsetZ < PAD_WIDTH; offsetZ++) {
            for (int offsetX = 0; offsetX < PAD_WIDTH; offsetX++) {
                int blockX = x + offsetX;
                int blockZ = z + offsetZ;
                if (!world.isAirBlock(blockX, y, blockZ)
                        || world.getBlock(blockX, y - 1, blockZ).getMaterial() != Material.water) {
                    return false;
                }
            }
        }
        return true;
    }
}
