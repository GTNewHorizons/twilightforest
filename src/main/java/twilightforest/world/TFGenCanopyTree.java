package twilightforest.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

/**
 * Makes large trees with flat leaf ovals that provide a canopy for the forest
 *
 * @author Ben
 *
 */
public class TFGenCanopyTree extends TFTreeGenerator {

    private static final int BRANCH_SPREAD = 4;
    private static final int LEAF_CANOPY_ABOVE_TRUNK = 1;
    private static final int MIN_CEILING_CLEARANCE = 12;

    protected int minHeight = 20;
    protected int chanceAddFirstFive = 3;
    protected int chanceAddSecondFive = 8;

    public TFGenCanopyTree() {
        this(false);
    }

    public TFGenCanopyTree(boolean notify) {
        super(notify);
        treeBlock = TFBlocks.log;
        treeMeta = 1;
        branchMeta = 13;
        leafBlock = TFBlocks.leaves;
        leafMeta = 1;
        rootBlock = TFBlocks.root;
        rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // check if we're on dirt or grass
        Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
        if ((materialUnder != Material.grass && materialUnder != Material.ground)
                || y >= world.getHeight() - MIN_CEILING_CLEARANCE) {
            return false;
        }

        // determine a height
        int treeHeight = chooseTreeHeight(random);

        // The trunk canopy extends one block beyond the generated trunk height.
        if ((long) y + treeHeight + LEAF_CANOPY_ABOVE_TRUNK >= world.getHeight()) {
            return false;
        }

        // okay build a tree! Go up to the height
        buildBranch(world, x, y, z, 0, treeHeight, 0, 0, true, random);

        // make 3-4 branches
        int numBranches = 3 + random.nextInt(2);
        float offset = random.nextFloat();
        for (int branch = 0; branch < numBranches; branch++) {
            buildBranch(world, x, y, z, treeHeight - 10 + branch, 9, 0.3 * branch + offset, 0.2, false, random);
        }

        // root bulb
        if (hasAirAround(world, x, y - 1, z)) {
            setBlockAndMetadata(world, x, y - 1, z, treeBlock, treeMeta);
        } else {
            setBlockAndMetadata(world, x, y - 1, z, rootBlock, rootMeta);
        }

        // roots!
        int numRoots = 3 + random.nextInt(2);
        offset = random.nextFloat();
        for (int root = 0; root < numRoots; root++) {
            buildRoot(world, x, y, z, offset, root);
        }

        return true;
    }

    private int chooseTreeHeight(Random random) {
        int treeHeight = minHeight;
        if (random.nextInt(chanceAddFirstFive) == 0) {
            treeHeight += random.nextInt(5);

            if (random.nextInt(chanceAddSecondFive) == 0) {
                treeHeight += random.nextInt(5);
            }
        }
        return treeHeight;
    }

    /**
     * Build a branch with a flat blob of leaves at the end.
     *
     * @param height
     * @param length
     * @param angle
     * @param tilt
     */
    void buildBranch(World world, int x, int y, int z, int height, double length, double angle, double tilt,
            boolean trunk, Random treeRNG) {
        ChunkCoordinates source = new ChunkCoordinates(x, y + height, z);
        ChunkCoordinates destination = translateCoords(source.posX, source.posY, source.posZ, length, angle, tilt);

        // constrain branch spread
        destination.posX = clampBranchCoordinate(destination.posX, x);
        destination.posZ = clampBranchCoordinate(destination.posZ, z);

        drawBresehnam(
                world,
                source.posX,
                source.posY,
                source.posZ,
                destination.posX,
                destination.posY,
                destination.posZ,
                treeBlock,
                trunk ? treeMeta : branchMeta);

        // do this here until that bug with the lighting is fixed
        if (trunk) {
            // add a firefly (torch) to the trunk
            addFirefly(world, x, y, z, 3 + treeRNG.nextInt(7), treeRNG.nextDouble());
        }

        makeLeafCircle(world, destination.posX, destination.posY - 1, destination.posZ, 3, leafBlock, leafMeta, true);
        makeLeafCircle(world, destination.posX, destination.posY, destination.posZ, 4, leafBlock, leafMeta, true);
        makeLeafCircle(world, destination.posX, destination.posY + 1, destination.posZ, 2, leafBlock, leafMeta, true);

        setBlockAndMetadata(world, destination.posX + 1, destination.posY, destination.posZ, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX - 1, destination.posY, destination.posZ, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ + 1, treeBlock, branchMeta);
        setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ - 1, treeBlock, branchMeta);
    }

    private static int clampBranchCoordinate(int coordinate, int origin) {
        return Math.max(origin - BRANCH_SPREAD, Math.min(origin + BRANCH_SPREAD, coordinate));
    }

    /**
     * Add a firefly at the specified height and angle
     *
     * @param height how far up the tree
     * @param angle  from 0 - 1 rotation around the tree
     */
    protected void addFirefly(World world, int x, int y, int z, int height, double angle) {
        int direction = (int) (angle * 4.0);
        switch (direction) {
            case 0 -> setBlockAndMetadata(world, x + 1, y + height, z, TFBlocks.firefly, 0);
            case 1 -> setBlockAndMetadata(world, x - 1, y + height, z, TFBlocks.firefly, 0);
            case 2 -> setBlockAndMetadata(world, x, y + height, z + 1, TFBlocks.firefly, 0);
            case 3 -> setBlockAndMetadata(world, x, y + height, z - 1, TFBlocks.firefly, 0);
        }
    }
}
