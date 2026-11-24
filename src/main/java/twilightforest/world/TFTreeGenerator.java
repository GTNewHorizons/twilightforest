package twilightforest.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public abstract class TFTreeGenerator extends WorldGenAbstractTree {

    private static final int LEAF_CORNER_HACK_COORDINATE = 3;
    private static final int LEAF_CORNER_HACK_DISTANCE = 6;

    protected Block treeBlock = TFBlocks.log;
    protected int treeMeta = 3;
    protected int branchMeta = 15;
    protected Block leafBlock = TFBlocks.hedge;
    protected int leafMeta = 1;
    protected Block rootBlock = TFBlocks.root;
    protected int rootMeta = BlockTFRoots.ROOT_META;

    public TFTreeGenerator() {
        this(false);
    }

    public TFTreeGenerator(boolean notify) {
        super(notify);
    }

    /**
     * Build a root, but don't let it stick out too far into thin air because that's weird
     */
    protected void buildRoot(World world, int x, int y, int z, double offset, int b) {
        ChunkCoordinates destination = translateCoords(x, y - b - 2, z, 5, 0.3 * b + offset, 0.8);

        // go through block by block and stop drawing when we head too far into open air
        ChunkCoordinates[] rootLine = getBresehnamArrayCoords(
                x,
                y - b - 2,
                z,
                destination.posX,
                destination.posY,
                destination.posZ);
        for (ChunkCoordinates coordinate : rootLine) {
            placeRootBlock(world, coordinate.posX, coordinate.posY, coordinate.posZ, rootBlock, rootMeta);
        }
    }

    /**
     * Function used to actually place root blocks if they're not going to break anything important
     */
    protected void placeRootBlock(World world, int x, int y, int z, Block rootBlock, int meta) {
        if (canRootGrowIn(world, x, y, z)) {
            setBlockAndMetadata(world, x, y, z, rootBlock, meta);
        }
    }

    public static boolean canRootGrowIn(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.air) {
            // roots can grow through air if they are near a solid block
            return isNearSolid(world, x, y, z);
        }

        return block != Blocks.bedrock && block != Blocks.obsidian && block != TFBlocks.shield;
    }

    /**
     * Moves distance along the vector.
     *
     * This goofy function takes a float between 0 and 1 for the angle, where 0 is 0 degrees, .5 is 180 degrees and 1
     * and 360 degrees. For the tilt, it takes a float between 0 and 1 where 0 is straight up, 0.5 is straight out and 1
     * is straight down.
     */
    public static int[] translate(int sx, int sy, int sz, double distance, double angle, double tilt) {
        return TFGenerator.translate(sx, sy, sz, distance, angle, tilt);
    }

    protected static ChunkCoordinates translateCoords(int sx, int sy, int sz, double length, double angle,
            double tilt) {
        return TFGenerator.translateCoords(sx, sy, sz, length, angle, tilt);
    }

    /**
     * Get an array of values that represent a line from point A to point B
     */
    public static ChunkCoordinates[] getBresehnamArrayCoords(ChunkCoordinates src, ChunkCoordinates dest) {
        return TFGenerator.getBresehnamArrayCoords(src.posX, src.posY, src.posZ, dest.posX, dest.posY, dest.posZ);
    }

    /**
     * Get an array of values that represent a line from point A to point B
     */
    public static ChunkCoordinates[] getBresehnamArrayCoords(int x1, int y1, int z1, int x2, int y2, int z2) {
        return TFGenerator.getBresehnamArrayCoords(x1, y1, z1, x2, y2, z2);
    }

    protected static boolean isNearSolid(World world, int bx, int by, int bz) {
        return TFGenerator.isNearSolid(world, bx, by, bz);
    }

    protected static boolean hasAirAround(World world, int bx, int by, int bz) {
        return TFGenerator.hasAirAround(world, bx, by, bz);
    }

    /**
     * Temporary override
     */
    protected void setBlock(World world, int x, int y, int z, Block block) {
        this.func_150515_a(world, x, y, z, block);
    }

    /**
     * Temporary override
     */
    protected void setBlockAndMetadata(World world, int x, int y, int z, Block block, int meta) {
        this.setBlockAndNotifyAdequately(world, x, y, z, block, meta);
    }

    /**
     * Draw a flat blob (circle) of leaves
     */
    public void makeLeafCircle(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue) {
        this.makeLeafCircle(world, sx, sy, sz, rad, blockValue, metaValue, false);
    }

    /*
     * Fully duplicated code from TFGenerator below. Where is my multiple inheritance?
     */

    /**
     * Draws a line from {x1, y1, z1} to {x2, y2, z2}
     */
    protected void drawBresehnam(World world, int x1, int y1, int z1, int x2, int y2, int z2, Block blockValue,
            int metaValue) {
        // draw in the same order as getBresehnamArrayCoords without allocating an array
        int dx = x2 - x1;
        int dy = y2 - y1;
        int dz = z2 - z1;
        int stepX = dx < 0 ? -1 : 1;
        int stepY = dy < 0 ? -1 : 1;
        int stepZ = dz < 0 ? -1 : 1;
        int lengthX = Math.abs(dx);
        int lengthY = Math.abs(dy);
        int lengthZ = Math.abs(dz);
        int doubleX = lengthX << 1;
        int doubleY = lengthY << 1;
        int doubleZ = lengthZ << 1;

        if (lengthX >= lengthY && lengthX >= lengthZ) {
            int errorY = doubleY - lengthX;
            int errorZ = doubleZ - lengthX;
            for (int step = 0; step < lengthX; step++) {
                setBlockAndMetadata(world, x1, y1, z1, blockValue, metaValue);
                if (errorY > 0) {
                    y1 += stepY;
                    errorY -= doubleX;
                }
                if (errorZ > 0) {
                    z1 += stepZ;
                    errorZ -= doubleX;
                }
                errorY += doubleY;
                errorZ += doubleZ;
                x1 += stepX;
            }
        } else if (lengthY >= lengthX && lengthY >= lengthZ) {
            int errorX = doubleX - lengthY;
            int errorZ = doubleZ - lengthY;
            for (int step = 0; step < lengthY; step++) {
                setBlockAndMetadata(world, x1, y1, z1, blockValue, metaValue);
                if (errorX > 0) {
                    x1 += stepX;
                    errorX -= doubleY;
                }
                if (errorZ > 0) {
                    z1 += stepZ;
                    errorZ -= doubleY;
                }
                errorX += doubleX;
                errorZ += doubleZ;
                y1 += stepY;
            }
        } else {
            int errorY = doubleY - lengthZ;
            int errorX = doubleX - lengthZ;
            for (int step = 0; step < lengthZ; step++) {
                setBlockAndMetadata(world, x1, y1, z1, blockValue, metaValue);
                if (errorY > 0) {
                    y1 += stepY;
                    errorY -= doubleZ;
                }
                if (errorX > 0) {
                    x1 += stepX;
                    errorX -= doubleZ;
                }
                errorY += doubleY;
                errorX += doubleX;
                z1 += stepZ;
            }
        }
        setBlockAndMetadata(world, x1, y1, z1, blockValue, metaValue);
    }

    /**
     * Draw a flat blob (circle) of leaves
     */
    public void makeLeafCircle(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue,
            boolean useHack) {
        // trace out a quadrant
        for (int dx = 0; dx <= rad; dx++) {
            for (int dz = 0; dz <= rad; dz++) {
                int dist = Math.max(dx, dz) + (Math.min(dx, dz) >> 1);

                // hack! I keep getting failing leaves at a certain position.
                if (useHack && dx == LEAF_CORNER_HACK_COORDINATE && dz == LEAF_CORNER_HACK_COORDINATE) {
                    dist = LEAF_CORNER_HACK_DISTANCE;
                }

                // if we're inside the blob, fill it
                if (dist <= rad) {
                    // do four at a time for easiness!
                    putLeafBlock(world, sx + dx, sy, sz + dz, blockValue, metaValue);
                    putLeafBlock(world, sx + dx, sy, sz - dz, blockValue, metaValue);
                    putLeafBlock(world, sx - dx, sy, sz + dz, blockValue, metaValue);
                    putLeafBlock(world, sx - dx, sy, sz - dz, blockValue, metaValue);
                }
            }
        }
    }

    /**
     * Draw a flat blob (circle) of leaves. This one makes it offset to surround a 2x2 area instead of a 1 block area
     */
    public void makeLeafCircle2(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue,
            boolean useHack) {
        // trace out a quadrant
        for (int dx = 0; dx <= rad; dx++) {
            for (int dz = 0; dz <= rad; dz++) {
                // int dist = Math.max(dx, dz) + (int)(Math.min(dx, dz) * 0.6F);
                //
                // //hack! I keep getting failing leaves at a certain position.
                // if (useHack && dx == 3 && dz == 3) {
                // dist = 6;
                // }

                // if we're inside the blob, fill it
                if (dx * dx + dz * dz <= rad * rad) {
                    // do four at a time for easiness!
                    putLeafBlock(world, sx + 1 + dx, sy, sz + 1 + dz, blockValue, metaValue);
                    putLeafBlock(world, sx + 1 + dx, sy, sz - dz, blockValue, metaValue);
                    putLeafBlock(world, sx - dx, sy, sz + 1 + dz, blockValue, metaValue);
                    putLeafBlock(world, sx - dx, sy, sz - dz, blockValue, metaValue);
                }
            }
        }
    }

    /**
     * Draw a giant blob of leaves.
     */
    public void drawLeafBlob(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue) {
        // then trace out a quadrant
        for (int dx = 0; dx <= rad; dx++) {
            for (int dy = 0; dy <= rad; dy++) {
                for (int dz = 0; dz <= rad; dz++) {
                    // determine how far we are from the center.
                    int dist = approximateBlobDistance(dx, dy, dz);

                    // if we're inside the blob, fill it
                    if (dist <= rad) {
                        // do eight at a time for easiness!
                        putLeafBlock(world, sx + dx, sy + dy, sz + dz, blockValue, metaValue);
                        putLeafBlock(world, sx + dx, sy + dy, sz - dz, blockValue, metaValue);
                        putLeafBlock(world, sx - dx, sy + dy, sz + dz, blockValue, metaValue);
                        putLeafBlock(world, sx - dx, sy + dy, sz - dz, blockValue, metaValue);
                        putLeafBlock(world, sx + dx, sy - dy, sz + dz, blockValue, metaValue);
                        putLeafBlock(world, sx + dx, sy - dy, sz - dz, blockValue, metaValue);
                        putLeafBlock(world, sx - dx, sy - dy, sz + dz, blockValue, metaValue);
                        putLeafBlock(world, sx - dx, sy - dy, sz - dz, blockValue, metaValue);
                    }
                }
            }
        }
    }

    private static int approximateBlobDistance(int dx, int dy, int dz) {
        int largest = Math.max(dx, Math.max(dy, dz));
        int smallest = Math.min(dx, Math.min(dy, dz));
        int middle = dx + dy + dz - largest - smallest;
        return largest + (middle >> 1) + (smallest >> 2);
    }

    /**
     * Put a leaf only in spots where leaves can go!
     */
    public void putLeafBlock(World world, int x, int y, int z, Block blockValue, int metaValue) {
        Block existing = world.getBlock(x, y, z);
        if (existing == null || existing.canBeReplacedByLeaves(world, x, y, z)) {
            setBlockAndMetadata(world, x, y, z, blockValue, metaValue);
        }
    }
}
