package twilightforest.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

public class TFGenLampposts extends TFGenerator {

    private static final int MAX_POST_HEIGHT = 4;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // we should start on a grass block
        if (y < 1 || y >= world.getHeight() || world.getBlock(x, y - 1, z) != Blocks.grass) {
            return false;
        }

        // generate a height
        int height = 1 + random.nextInt(MAX_POST_HEIGHT);

        // is it air or replaceable above our grass block
        if (y + height >= world.getHeight() || !hasRoomForLamppost(world, x, y, z, height)) {
            return false;
        }

        // generate lamp
        if (!world.setBlock(x, y + height, z, TFBlocks.fireflyJar)) {
            return false;
        }

        for (int offset = 0; offset < height; offset++) {
            world.setBlock(x, y + offset, z, Blocks.fence);
        }

        return true;
    }

    private static boolean hasRoomForLamppost(World world, int x, int y, int z, int height) {
        // include the jars position
        for (int offset = 0; offset <= height; offset++) {
            int blockY = y + offset;
            if (!world.isAirBlock(x, blockY, z) && !world.getBlock(x, blockY, z).isReplaceable(world, x, blockY, z)) {
                return false;
            }
        }

        return true;
    }
}
