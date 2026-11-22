package twilightforest.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

public class TFGenTreeOfTime extends TFGenHollowTree {

    private static final int TREE_HEIGHT = 8;
    private static final int TRUNK_RADIUS = 1;
    private static final int TINY_CROWN_RADIUS = 4;
    private static final int CROWN_HEIGHT_ALLOWANCE = TINY_CROWN_RADIUS + 2;
    private static final int MIN_CROWN_BRANCHES = 1;

    public TFGenTreeOfTime(boolean par1) {
        super(par1);

        this.treeBlock = TFBlocks.magicLog;
        this.treeMeta = 0;
        this.branchMeta = treeMeta | 12;
        this.leafBlock = TFBlocks.magicLeaves;
        this.leafMeta = 0;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int height = TREE_HEIGHT;
        int diameter = TRUNK_RADIUS;

        // do we have enough height?
        // crown branches and their leaf blobs can extend above the 8 block trunk
        if (y < 1 || (long) y + height + CROWN_HEIGHT_ALLOWANCE >= world.getHeight()) {
            // System.out.println("Failed with hollow tree of height " +
            // height);
            return false;
        }
        // make a tree!

        // check if we're on dirt or grass
        Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
        if (materialUnder != Material.grass && materialUnder != Material.ground) {
            return false;
        }

        // build the trunk
        buildTrunk(world, random, x, y, z, diameter, height);

        // build the crown
        buildTinyCrown(world, random, x, y, z, diameter, height);

        // 3-5 roots at the bottom
        buildBranchRing(world, random, x, y, z, diameter, 1, 0, 12, 0, 0.75D, 0, 3, 5, 3, false);

        // several more taproots
        buildBranchRing(world, random, x, y, z, diameter, 1, 2, 18, 0, 0.9D, 0, 3, 5, 3, false);

        // add clock block
        this.setBlockAndMetadata(world, x - 1, y + 2, z, TFBlocks.magicLogSpecial, 0);

        return true;
    }

    /**
     * Build the crown of the tree. This builds a smaller crown, since the large ones were causing some performance
     * issues
     */
    protected void buildTinyCrown(World world, Random random, int x, int y, int z, int diameter, int height) {
        int crownRadius = TINY_CROWN_RADIUS;
        int bvar = MIN_CROWN_BRANCHES;

        // 3-5 medium branches starting at the bottom of the crown
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - crownRadius,
                0,
                crownRadius,
                0,
                0.35D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // 3-5 medium branches at the crown middle
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height - (crownRadius / 2),
                0,
                crownRadius,
                0,
                0.28D,
                0,
                bvar,
                bvar + 2,
                1,
                true);

        // 2-4 medium branches at the crown top
        buildBranchRing(world, random, x, y, z, diameter, height, 0, crownRadius, 0, 0.15D, 0, 2, 4, 0, true);

        // 3-6 medium branches going straight up
        buildBranchRing(
                world,
                random,
                x,
                y,
                z,
                diameter,
                height,
                0,
                (crownRadius / 2),
                0,
                0.05D,
                0,
                bvar,
                bvar + 2,
                0,
                true);
    }
}
