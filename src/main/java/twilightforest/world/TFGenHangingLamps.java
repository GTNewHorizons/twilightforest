package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

public class TFGenHangingLamps extends TFGenerator {

    private static final int MAX_HANG = 8;
    private static final int MIN_GROUND_CLEARANCE = 4;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // this must be an air block, surrounded by air
        if (y < MIN_GROUND_CLEARANCE - 1 || y >= world.getHeight()
                || !world.isAirBlock(x, y, z)
                || !TFGenerator.surroundedByAir(world, x, y, z)) {
            return false;
        }

        // there should be a solid block or leaves within 7 blocks above
        int supportDistance = findSupportDistance(world, x, y, z);
        if (supportDistance < 0) {
            return false;
        }

        // we need to be at least 4 above ground
        if (!hasClearanceBelow(world, x, y, z)) {
            return false;
        }

        // generate lamp
        if (!world.setBlock(x, y, z, TFBlocks.fireflyJar)) {
            return false;
        }

        for (int offset = 1; offset < supportDistance; offset++) {
            world.setBlock(x, y + offset, z, Blocks.fence);
        }

        return true;
    }

    private static int findSupportDistance(World world, int x, int y, int z) {
        for (int offset = 1; offset < MAX_HANG; offset++) {
            int blockY = y + offset;
            if (blockY >= world.getHeight()) {
                return -1;
            }

            Block block = world.getBlock(x, blockY, z);
            if (isHangingSupport(block.getMaterial())) {
                return offset;
            }

            // do not replace water, plants, or other non air blocks with fence posts
            if (!world.isAirBlock(x, blockY, z)) {
                return -1;
            }
        }

        return -1;
    }

    private static boolean hasClearanceBelow(World world, int x, int y, int z) {
        for (int offset = 1; offset < MIN_GROUND_CLEARANCE; offset++) {
            if (World.doesBlockHaveSolidTopSurface(world, x, y - offset, z)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isHangingSupport(Material material) {
        return material.isSolid() || material == Material.leaves;
    }
}
