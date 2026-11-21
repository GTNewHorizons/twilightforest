package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class TFGenMangroveTree extends TFTreeGenerator {

    private static final int MIN_CEILING_CLEARANCE = 19;
    private static final int TRUNK_BASE_HEIGHT = 5;
    private static final int TRUNK_LENGTH = 6;
    private static final int BRANCH_BASE_HEIGHT = 7;
    private static final int BRANCH_LENGTH = 6;
    private static final int BRANCH_SPREAD = 4;
    private static final int ROOT_LENGTH = 12;
    private static final int MIN_LEAF_RADIUS = 2;

    boolean checkForWater;

    public TFGenMangroveTree() {
        this(false);
    }

    public TFGenMangroveTree(boolean notify) {
        super(notify);
        checkForWater = !notify;

        treeBlock = TFBlocks.log;
        treeMeta = 2;
        branchMeta = 14;
        leafBlock = TFBlocks.leaves;
        leafMeta = 2;
        rootBlock = TFBlocks.root;
        rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // natural generation only starts over water
        if ((checkForWater && world.getBlock(x, y - 1, z) != Blocks.water)
                || y >= world.getHeight() - MIN_CEILING_CLEARANCE) {
            return false;
        }

        // okay build a trunk!
        buildBranch(world, random, x, y, z, TRUNK_BASE_HEIGHT, TRUNK_LENGTH + random.nextInt(3), 0, 0, true);

        generateBranches(world, random, x, y, z);
        generateRoots(world, random, x, y, z);

        // add a firefly (torch) to the trunk
        addFirefly(world, x, y, z, TRUNK_BASE_HEIGHT + random.nextInt(5), random.nextDouble());

        return true;
    }

    private void generateBranches(World world, Random random, int x, int y, int z) {
        // make 0-2 branches
        int branchCount = random.nextInt(3);
        double offset = random.nextDouble();
        for (int branch = 0; branch < branchCount; branch++) {
            buildBranch(
                    world,
                    random,
                    x,
                    y,
                    z,
                    BRANCH_BASE_HEIGHT + branch,
                    BRANCH_LENGTH + random.nextInt(2),
                    0.3 * branch + offset,
                    0.25,
                    false);
        }
    }

    private void generateRoots(World world, Random random, int x, int y, int z) {
        // make 3-4 roots
        int rootCount = 3 + random.nextInt(2);
        double offset = random.nextDouble();
        for (int root = 0; root < rootCount; root++) {
            double rootTilt = 0.75 + random.nextDouble() * 0.1;
            buildRoot(world, x, y, z, TRUNK_BASE_HEIGHT, ROOT_LENGTH, 0.4 * root + offset, rootTilt);
        }
    }

    /**
     * Build a branch with a flat blob of leaves at the end.
     *
     * @param height
     * @param length
     * @param angle
     * @param tilt
     */
    void buildBranch(World world, Random random, int x, int y, int z, int height, double length, double angle,
            double tilt, boolean trunk) {
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

        // variable size leaves
        int leafRadius = MIN_LEAF_RADIUS + random.nextInt(3);

        // we only need these side blocks if the size is > 2
        if (leafRadius > MIN_LEAF_RADIUS) {
            setBlockAndMetadata(world, destination.posX + 1, destination.posY, destination.posZ, treeBlock, branchMeta);
            setBlockAndMetadata(world, destination.posX - 1, destination.posY, destination.posZ, treeBlock, branchMeta);
            setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ + 1, treeBlock, branchMeta);
            setBlockAndMetadata(world, destination.posX, destination.posY, destination.posZ - 1, treeBlock, branchMeta);
        }

        // leaves!
        makeLeafCircle(
                world,
                destination.posX,
                destination.posY - 1,
                destination.posZ,
                leafRadius - 1,
                leafBlock,
                leafMeta);
        makeLeafCircle(world, destination.posX, destination.posY, destination.posZ, leafRadius, leafBlock, leafMeta);
        makeLeafCircle(
                world,
                destination.posX,
                destination.posY + 1,
                destination.posZ,
                leafRadius - 2,
                leafBlock,
                leafMeta);
    }

    private static int clampBranchCoordinate(int coordinate, int origin) {
        return Math.max(origin - BRANCH_SPREAD, Math.min(origin + BRANCH_SPREAD, coordinate));
    }

    /**
     * Build a root. (Which is really like a branch without the leaves)
     *
     * @param height
     * @param length
     * @param angle
     * @param tilt
     */
    void buildRoot(World world, int x, int y, int z, int height, double length, double angle, double tilt) {
        ChunkCoordinates source = new ChunkCoordinates(x, y + height, z);
        ChunkCoordinates destination = translateCoords(source.posX, source.posY, source.posZ, length, angle, tilt);

        ChunkCoordinates[] rootLine = getBresehnamArrayCoords(source, destination);
        boolean aboveGround = true;
        for (ChunkCoordinates coordinate : rootLine) {
            if (aboveGround && hasAirAround(world, coordinate.posX, coordinate.posY, coordinate.posZ)) {
                placeExposedRootBlock(world, coordinate.posX, coordinate.posY, coordinate.posZ);
                placeExposedRootBlock(world, coordinate.posX, coordinate.posY - 1, coordinate.posZ);
            } else {
                placeRootBlock(world, coordinate.posX, coordinate.posY, coordinate.posZ, rootBlock, rootMeta);
                placeRootBlock(world, coordinate.posX, coordinate.posY - 1, coordinate.posZ, rootBlock, rootMeta);
                aboveGround = false;
            }
        }
    }

    private void placeExposedRootBlock(World world, int x, int y, int z) {
        // above ground roots shouldnt replace the same protected blocks as buried roots
        Block existing = world.getBlock(x, y, z);
        if (existing != Blocks.bedrock && existing != Blocks.obsidian && existing != TFBlocks.shield) {
            setBlockAndMetadata(world, x, y, z, treeBlock, branchMeta);
        }
    }

    /**
     * Add a firefly at the specified height and angle.
     *
     * @param height how far up the tree
     * @param angle  from 0 - 1 rotation around the tree
     */
    private void addFirefly(World world, int x, int y, int z, int height, double angle) {
        int direction = (int) (angle * 4.0);
        switch (direction) {
            case 0 -> setBlockAndMetadata(world, x + 1, y + height, z, TFBlocks.firefly, 0);
            case 1 -> setBlockAndMetadata(world, x - 1, y + height, z, TFBlocks.firefly, 0);
            case 2 -> setBlockAndMetadata(world, x, y + height, z + 1, TFBlocks.firefly, 0);
            case 3 -> setBlockAndMetadata(world, x, y + height, z - 1, TFBlocks.firefly, 0);
        }
    }
}
