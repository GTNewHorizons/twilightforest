package twilightforest.world;

import java.util.Random;

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
        if (!world.isAirBlock(x, y, z) || !TFGenerator.surroundedByAir(world, x, y, z)) {
            return false;
        }

        // there should be a solid block or leaves within 7 blocks above
        if (!hasSupportAbove(world, x, y, z)) {
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

        for (int offset = 1; offset < MAX_HANG; offset++) {
            Material material = world.getBlock(x, y + offset, z).getMaterial();
            if (isHangingSupport(material)) {
                break;
            }
            world.setBlock(x, y + offset, z, Blocks.fence);
        }

        return true;
    }

    private boolean hasSupportAbove(World world, int x, int y, int z) {
        boolean hasSupport = false;
        for (int offset = 1; offset < MAX_HANG; offset++) {
            Material material = world.getBlock(x, y + offset, z).getMaterial();
            if (isHangingSupport(material)) {
                hasSupport = true;
            }
        }
        return hasSupport;
    }

    private boolean hasClearanceBelow(World world, int x, int y, int z) {
        boolean isClear = true;
        for (int offset = 1; offset < MIN_GROUND_CLEARANCE; offset++) {
            if (World.doesBlockHaveSolidTopSurface(world, x, y - offset, z)) {
                isClear = false;
            }
        }
        return isClear;
    }

    private static boolean isHangingSupport(Material material) {
        return material.isSolid() || material == Material.leaves;
    }
}
