package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.TFTreasure;

public class TFGenWell extends TFGenerator {

    private static final int SMALL_WELL_SIZE = 3;
    private static final int LARGE_WELL_SIZE = 4;
    private static final int WELL_HEIGHT = 4;
    private static final int SHAFT_DEPTH = 20;
    private static final int TREASURE_START_DEPTH = 15;
    private static final int TREASURE_CHANCE = 8;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        return rand.nextInt(4) == 0 ? generate4x4Well(world, rand, x, y, z) : generate3x3Well(world, rand, x, y, z);
    }

    /**
     * make a cute little well
     */
    public boolean generate3x3Well(World world, Random rand, int x, int y, int z) {
        if (!isAreaSuitable(world, rand, x, y, z, SMALL_WELL_SIZE, WELL_HEIGHT, SMALL_WELL_SIZE)) {
            return false;
        }

        buildWell(world, x, y, z, SMALL_WELL_SIZE);

        boolean madeTreasure = false;
        // now drill each the well square down 20 squares, or until we hit something
        for (int dy = -1; dy >= -SHAFT_DEPTH; dy--) {
            Block dblock = world.getBlock(x + 1, y + dy, z + 1);
            // we only drill through dirt, grass, gravel and stone
            if (!canExcavate(dblock)) {
                break;
            }
            // we also need a solid block under where we're digging
            if (!world.getBlock(x + 1, y + dy - 1, z + 1).getMaterial().isSolid()) {
                break;
            }

            // okay, we're good to dig.
            setBlock(world, x + 1, y + dy, z + 1, Blocks.water);

            // if we're below 15 squares, there's a small chance of treasure
            if (dy < -TREASURE_START_DEPTH && !madeTreasure && rand.nextInt(TREASURE_CHANCE) == 0) {
                // TODO: more directions
                setBlock(world, x + 2, y + dy, z + 1, Blocks.water);
                setBlock(world, x + 3, y + dy + 1, z + 1, Blocks.air);
                setBlock(world, x + 3, y + dy, z + 1, Blocks.air);

                // TODO: unique treasure table that is themed for underwater well exploration
                TFTreasure.basement.generate(world, rand, x + 3, y + dy, z + 1);

                // set flag so we only get one chest
                madeTreasure = true;
            }
        }
        return true;
    }

    /**
     * Make a larger well
     */
    public boolean generate4x4Well(World world, Random rand, int x, int y, int z) {
        if (!isAreaSuitable(world, rand, x, y, z, LARGE_WELL_SIZE, WELL_HEIGHT, LARGE_WELL_SIZE)) {
            return false;
        }

        buildWell(world, x, y, z, LARGE_WELL_SIZE);

        // now drill each of the 4 well squares down 20 squares, or until we hit something
        for (int dx = 1; dx <= 2; dx++) {
            for (int dz = 1; dz <= 2; dz++) {
                for (int dy = -1; dy >= -SHAFT_DEPTH; dy--) {
                    Block dblock = world.getBlock(x + dx, y + dy, z + dz);
                    // we only drill through dirt, grass, gravel and stone
                    if (!canExcavate(dblock)) {
                        break;
                    }
                    // we also need a solid block under where we're digging
                    if (!world.getBlock(x + dx, y + dy - 1, z + dz).getMaterial().isSolid()) {
                        break;
                    }
                    // okay, we're good to dig.
                    setBlock(world, x + dx, y + dy, z + dz, Blocks.water);
                }
            }
        }
        return true;
    }

    private static boolean canExcavate(Block block) {
        return block == Blocks.dirt || block == Blocks.grass || block == Blocks.gravel || block == Blocks.stone;
    }

    private void buildWell(World world, int x, int y, int z, int size) {
        int edge = size - 1;

        // make a cute well!
        // the perimeter, water, posts, roof and floor retain the original placement order
        for (int dx = 0; dx < size; dx++) {
            setBlock(world, x + dx, y, z, Blocks.mossy_cobblestone);
        }
        for (int dx = 0; dx < size; dx++) {
            setBlock(world, x + dx, y, z + edge, Blocks.mossy_cobblestone);
        }
        for (int dz = 1; dz < edge; dz++) {
            setBlock(world, x, y, z + dz, Blocks.mossy_cobblestone);
        }
        for (int dz = 1; dz < edge; dz++) {
            setBlock(world, x + edge, y, z + dz, Blocks.mossy_cobblestone);
        }

        for (int dz = 1; dz < edge; dz++) {
            for (int dx = 1; dx < edge; dx++) {
                setBlock(world, x + dx, y, z + dz, Blocks.water);
            }
        }

        for (int dy = 1; dy <= 2; dy++) {
            setBlock(world, x, y + dy, z, Blocks.fence);
            setBlock(world, x + edge, y + dy, z, Blocks.fence);
            setBlock(world, x, y + dy, z + edge, Blocks.fence);
            setBlock(world, x + edge, y + dy, z + edge, Blocks.fence);
        }

        for (int dx = 0; dx < size; dx++) {
            setBlockAndMetadata(world, x + dx, y + 3, z, Blocks.wooden_slab, 0);
        }
        for (int dx = 0; dx < size; dx++) {
            setBlockAndMetadata(world, x + dx, y + 3, z + edge, Blocks.wooden_slab, 0);
        }
        for (int dz = 1; dz < edge; dz++) {
            setBlockAndMetadata(world, x, y + 3, z + dz, Blocks.wooden_slab, 0);
        }
        for (int dz = 1; dz < edge; dz++) {
            setBlockAndMetadata(world, x + edge, y + 3, z + dz, Blocks.wooden_slab, 0);
        }

        for (int dz = 1; dz < edge; dz++) {
            for (int dx = 1; dx < edge; dx++) {
                setBlock(world, x + dx, y + 3, z + dz, Blocks.planks);
            }
        }
    }
}
