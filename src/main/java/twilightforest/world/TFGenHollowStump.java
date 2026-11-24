package twilightforest.world;

import java.util.Random;

import net.minecraft.world.World;

/**
 * A stump from a hollow tree
 *
 * @author Ben
 *
 */
public class TFGenHollowStump extends TFGenHollowTree {

    private static final int STUMP_HEIGHT = 6;
    private static final int ROOT_DEPTH = 4;
    private static final int MIN_COLUMN_HEIGHT = 2;

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int radius = random.nextInt(2) + 2;

        // include both edges of the trunk and leave room for the four block deep base
        if (y < ROOT_DEPTH || y + STUMP_HEIGHT > Math.min(world.getHeight(), TFWorld.MAXHEIGHT)
                || !isAreaSuitable(
                        world,
                        random,
                        x - radius,
                        y,
                        z - radius,
                        2 * radius + 1,
                        STUMP_HEIGHT,
                        2 * radius + 1)) {
            return false;
        }

        buildTrunk(world, random, x, y, z, radius, STUMP_HEIGHT);

        // 4-5 roots at the bottom
        buildBranchRing(world, random, x, y, z, radius, 3, 2, 6, 0, 0.75D, 0, 3, 5, 3, false);

        // several more taproots
        buildBranchRing(world, random, x, y, z, radius, 1, 2, 8, 0, 0.9D, 0, 3, 5, 3, false);

        return true;
    }

    /**
     * This function builds the hollow trunk of the tree
     */
    @Override
    protected void buildTrunk(World world, Random random, int x, int y, int z, int diameter, int maxHeight) {
        buildTrunkBase(world, x, y, z, diameter);

        int hollow = diameter / 2;

        // build the trunk upwards
        for (int dx = -diameter; dx <= diameter; dx++) {
            for (int dz = -diameter; dz <= diameter; dz++) {
                // keep both random rolls for every column, including those outside the hollow trunk
                int height = Math.min(maxHeight - 1, MIN_COLUMN_HEIGHT + random.nextInt(3) + random.nextInt(2));

                // determine how far we are from the center.
                int dist = trunkDistance(dx, dz);

                // make a trunk!
                if (dist <= diameter && dist > hollow) {
                    for (int dy = 0; dy <= height; dy++) {
                        placeRootOrTrunkBlock(world, x + dx, y + dy, z + dz, treeBlock, treeMeta);
                    }
                }
            }
        }
    }
}
