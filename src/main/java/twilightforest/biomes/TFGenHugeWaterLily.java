package twilightforest.biomes;

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
public class TFGenHugeWaterLily extends WorldGenerator {

    private static final int GENERATION_ATTEMPTS = 4;
    private static final int HORIZONTAL_SPREAD = 8;
    private static final int VERTICAL_SPREAD = 4;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int attempt = 0; attempt < GENERATION_ATTEMPTS; attempt++) {
            int dx = x + random.nextInt(HORIZONTAL_SPREAD) - random.nextInt(HORIZONTAL_SPREAD);
            int dy = y + random.nextInt(VERTICAL_SPREAD) - random.nextInt(VERTICAL_SPREAD);
            int dz = z + random.nextInt(HORIZONTAL_SPREAD) - random.nextInt(HORIZONTAL_SPREAD);

            if (shouldPlacePadAt(world, dx, dy, dz)) {
                world.setBlock(dx, dy, dz, TFBlocks.hugeWaterLily);
            }
        }

        return true;
    }

    private static boolean shouldPlacePadAt(World world, int x, int y, int z) {
        return y > 0 && y < world.getHeight()
                && world.isAirBlock(x, y, z)
                && world.getBlock(x, y - 1, z).getMaterial() == Material.water;
    }
}
