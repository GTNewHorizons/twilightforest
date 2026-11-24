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
public class TFGenDarkCanopyTree extends TFTreeGenerator {

    private static final int MAX_BRANCH_SPREAD = 4;
    private static final int BRANCH_COUNT = 4;

    public TFGenDarkCanopyTree() {
        this(false);
    }

    public TFGenDarkCanopyTree(boolean par1) {
        super(par1);
        treeBlock = TFBlocks.log;
        treeMeta = 3;
        branchMeta = 15;
        leafBlock = TFBlocks.darkleaves;
        leafMeta = 0;
        rootBlock = TFBlocks.root;
        rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        // if we are given leaves as a starting position, seek dirt or grass underneath
        boolean foundDirt = false;
        Material materialUnder;
        for (int dy = y; dy >= TFWorld.SEALEVEL; dy--) {
            materialUnder = world.getBlock(x, dy - 1, z).getMaterial();
            if (materialUnder == Material.grass || materialUnder == Material.ground) {
                // yes!
                foundDirt = true;
                y = dy;
                break;
            } else if (materialUnder == Material.rock || materialUnder == Material.sand) {
                // nope
                break;
            }
        }

        if (!foundDirt) {
            return false;
        }

        // do not grow next to another tree
        if (world.getBlock(x + 1, y, z + 0).getMaterial() == Material.wood
                || world.getBlock(x - 1, y, z + 0).getMaterial() == Material.wood
                || world.getBlock(x + 0, y, z + 1).getMaterial() == Material.wood
                || world.getBlock(x + 0, y, z - 1).getMaterial() == Material.wood) {
            return false;
        }

        // determine a height
        int treeHeight = 6 + random.nextInt(5);

        // allow room for the upper leaf crown and the tilted side branches
        if ((long) y + treeHeight + 6 >= world.getHeight()) {
            return false;
        }

        // okay build a tree! trunk here
        drawBresehnam(world, x, y, z, x, y + treeHeight, z, treeBlock, treeMeta);
        leafAround(world, x, y + treeHeight, z);

        // make 4 branches
        double offset = random.nextFloat();
        for (int b = 0; b < BRANCH_COUNT; b++) {
            buildBranch(
                    world,
                    x,
                    y,
                    z,
                    treeHeight - 3 - BRANCH_COUNT + (b / 2),
                    10 + random.nextInt(4),
                    0.23 * b + offset,
                    0.23,
                    random);
        }

        // root bulb
        if (hasAirAround(world, x, y - 1, z)) {
            this.setBlockAndMetadata(world, x, y - 1, z, treeBlock, treeMeta);
        } else {
            this.setBlockAndMetadata(world, x, y - 1, z, rootBlock, rootMeta);
        }

        // roots!
        int numRoots = 3 + random.nextInt(2);
        offset = random.nextDouble();
        for (int b = 0; b < numRoots; b++) {
            buildRoot(world, x, y, z, offset, b);
        }

        return true;
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
            Random random) {
        ChunkCoordinates src = new ChunkCoordinates(x, y + height, z);
        ChunkCoordinates dest = translateCoords(src.posX, src.posY, src.posZ, length, angle, tilt);

        // constrain branch spread
        dest.posX = clampBranchCoordinate(dest.posX, x);
        dest.posZ = clampBranchCoordinate(dest.posZ, z);

        drawBresehnam(world, src.posX, src.posY, src.posZ, dest.posX, dest.posY, dest.posZ, treeBlock, branchMeta);

        leafAround(world, dest.posX, dest.posY, dest.posZ);
    }

    private static int clampBranchCoordinate(int coordinate, int origin) {
        return Math.max(origin - MAX_BRANCH_SPREAD, Math.min(origin + MAX_BRANCH_SPREAD, coordinate));
    }

    /**
     * Make our leaf pattern
     */
    private void leafAround(World world, int dx, int dy, int dz) {
        int leafSize = 4;

        // only leaf if there are no leaves by where we are thinking of leafing
        if (hasAirAround(world, dx, dy, dz)) {
            makeLeafCircle(world, dx, dy - 1, dz, leafSize, leafBlock, leafMeta);
            makeLeafCircle(world, dx, dy, dz, leafSize + 1, leafBlock, leafMeta);
            makeLeafCircle(world, dx, dy + 1, dz, leafSize, leafBlock, leafMeta);
            makeLeafCircle(world, dx, dy + 2, dz, leafSize - 2, leafBlock, leafMeta);
        }
    }

}
