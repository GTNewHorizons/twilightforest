package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

/**
 * Makes large mushrooms with flat mushroom tops that provide a canopy for the forest
 *
 * @author Ben
 *
 */
public class TFGenCanopyMushroom extends TFTreeGenerator {

    private static final int BASE_HEIGHT = 12;
    private static final int HEIGHT_VARIATION = 5;
    private static final int BRANCH_SPREAD = 4;
    private static final int CAP_RADIUS = 4;
    private static final int CAP_INTERIOR_META = 5;
    private static final int CAP_CORNER_OFFSET = 3;
    private static final int CAP_CORNER_DISTANCE = 6;
    private static final int MAX_CAP_HEIGHT_ABOVE_TRUNK = 5;

    public TFGenCanopyMushroom() {
        this(false);
    }

    public TFGenCanopyMushroom(boolean notify) {
        super(notify);
        treeBlock = Blocks.red_mushroom_block;
        treeMeta = 10;
        branchMeta = 15;
        leafBlock = Blocks.red_mushroom_block;
        leafMeta = CAP_INTERIOR_META;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // determine a height
        int treeHeight = chooseTreeHeight(random);

        // check if we're on dirt or grass
        Block blockUnder = world.getBlock(x, y - 1, z);
        if ((blockUnder != Blocks.grass && blockUnder != Blocks.dirt && blockUnder != Blocks.mycelium)
                || y >= world.getHeight() - treeHeight - 1) {
            return false;
        }

        // the last branch can end 5 blocks above the cap
        if ((long) y + treeHeight + MAX_CAP_HEIGHT_ABOVE_TRUNK >= world.getHeight()) {
            return false;
        }

        treeBlock = random.nextInt(3) == 0 ? Blocks.red_mushroom_block : Blocks.brown_mushroom_block;
        leafBlock = treeBlock;

        // okay build a tree! Go up to the height
        buildBranch(world, x, y, z, 0, treeHeight, 0, 0, true, random);

        // make 3-4 branches
        int numBranches = 3 + random.nextInt(2);
        double offset = random.nextDouble();
        for (int branch = 0; branch < numBranches; branch++) {
            buildBranch(world, x, y, z, treeHeight - 5 + branch, 9, 0.3 * branch + offset, 0.2, false, random);
        }

        return true;
    }

    private static int chooseTreeHeight(Random random) {
        int treeHeight = BASE_HEIGHT;
        if (random.nextInt(3) == 0) {
            treeHeight += random.nextInt(HEIGHT_VARIATION);

            if (random.nextInt(8) == 0) {
                treeHeight += random.nextInt(HEIGHT_VARIATION);
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

        if (source.posX != destination.posX || source.posZ != destination.posZ) {
            // branch
            drawBresehnam(
                    world,
                    source.posX,
                    source.posY,
                    source.posZ,
                    destination.posX,
                    source.posY,
                    destination.posZ,
                    treeBlock,
                    branchMeta);
            drawBresehnam(
                    world,
                    destination.posX,
                    source.posY + 1,
                    destination.posZ,
                    destination.posX,
                    destination.posY - 1,
                    destination.posZ,
                    treeBlock,
                    treeMeta);
        } else {
            // trunk
            drawBresehnam(
                    world,
                    source.posX,
                    source.posY,
                    source.posZ,
                    destination.posX,
                    destination.posY - 1,
                    destination.posZ,
                    treeBlock,
                    treeMeta);
        }

        // do this here until that bug with the lighting is fixed
        if (trunk) {
            // add a firefly (torch) to the trunk
            addFirefly(world, x, y, z, 3 + treeRNG.nextInt(7), treeRNG.nextDouble());
        }

        drawMushroomCircle(world, destination.posX, destination.posY, destination.posZ, CAP_RADIUS, leafBlock);
    }

    private static int clampBranchCoordinate(int coordinate, int origin) {
        return Math.max(origin - BRANCH_SPREAD, Math.min(origin + BRANCH_SPREAD, coordinate));
    }

    /**
     * Draw a flat blob (a circle?) of whatevs (I'm guessing... leaves).
     *
     * @param sx
     * @param sy
     * @param sz
     * @param rad        radius
     * @param blockValue
     * @param metaValue
     * @param priority   do we overwrite what's there?
     */
    public void drawMushroomCircle(World world, int sx, int sy, int sz, int rad, Block blockValue) {
        // trace out a quadrant
        for (int dx = 0; dx <= rad; dx++) {
            for (int dz = 0; dz <= rad; dz++) {
                int distance = (int) (Math.max(dx, dz) + (Math.min(dx, dz) * 0.5));

                // hack! I keep getting failing leaves at a certain position.
                if (dx == CAP_CORNER_OFFSET && dz == CAP_CORNER_OFFSET) {
                    distance = CAP_CORNER_DISTANCE;
                }

                // if we're inside the blob, fill it
                if (dx == 0) {
                    // two!
                    placeCapZAxis(world, sx, sy, sz, dz, rad, blockValue);
                } else if (dz == 0) {
                    // two!
                    placeCapXAxis(world, sx, sy, sz, dx, rad, blockValue);
                } else if (distance < rad) {
                    // do four at a time for easiness!
                    placeCapQuadrant(world, sx, sy, sz, dx, dz, blockValue, false);
                } else if (distance == rad) {
                    // do four at a time for easiness!
                    placeCapQuadrant(world, sx, sy, sz, dx, dz, blockValue, true);
                }
            }
        }
    }

    private void placeCapZAxis(World world, int x, int y, int z, int offset, int radius, Block block) {
        int positiveMeta = offset < radius ? CAP_INTERIOR_META : 8;
        int negativeMeta = offset < radius ? CAP_INTERIOR_META : 2;
        setBlockAndMetadata(world, x, y, z + offset, block, positiveMeta);
        setBlockAndMetadata(world, x, y, z - offset, block, negativeMeta);
    }

    private void placeCapXAxis(World world, int x, int y, int z, int offset, int radius, Block block) {
        int positiveMeta = offset < radius ? CAP_INTERIOR_META : 6;
        int negativeMeta = offset < radius ? CAP_INTERIOR_META : 4;
        setBlockAndMetadata(world, x + offset, y, z, block, positiveMeta);
        setBlockAndMetadata(world, x - offset, y, z, block, negativeMeta);
    }

    private void placeCapQuadrant(World world, int x, int y, int z, int dx, int dz, Block block, boolean edge) {
        setBlockAndMetadata(world, x + dx, y, z + dz, block, edge ? 9 : CAP_INTERIOR_META);
        setBlockAndMetadata(world, x + dx, y, z - dz, block, edge ? 3 : CAP_INTERIOR_META);
        setBlockAndMetadata(world, x - dx, y, z + dz, block, edge ? 7 : CAP_INTERIOR_META);
        setBlockAndMetadata(world, x - dx, y, z - dz, block, edge ? 1 : CAP_INTERIOR_META);
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
