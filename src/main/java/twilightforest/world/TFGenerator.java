package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class TFGenerator extends WorldGenerator {

    // protected World worldObj;

    public TFGenerator() {
        this(false);
    }

    public TFGenerator(boolean par1) {
        super(par1);
    }

    /**
     * Moves distance along the vector.
     *
     * This goofy function takes a float between 0 and 1 for the angle, where 0 is 0 degrees, .5 is 180 degrees and 1
     * and 360 degrees. For the tilt, it takes a float between 0 and 1 where 0 is straight up, 0.5 is straight out and 1
     * is straight down.
     */
    public static int[] translate(int sx, int sy, int sz, double distance, double angle, double tilt) {
        int[] dest = { sx, sy, sz };

        double rangle = angle * 2.0D * Math.PI;
        double rtilt = tilt * Math.PI;

        dest[0] += Math.round(Math.sin(rangle) * Math.sin(rtilt) * distance);
        dest[1] += Math.round(Math.cos(rtilt) * distance);
        dest[2] += Math.round(Math.cos(rangle) * Math.sin(rtilt) * distance);

        return dest;
    }

    /**
     * Moves distance along the vector.
     *
     * This goofy function takes a float between 0 and 1 for the angle, where 0 is 0 degrees, .5 is 180 degrees and 1
     * and 360 degrees. For the tilt, it takes a float between 0 and 1 where 0 is straight up, 0.5 is straight out and 1
     * is straight down.
     */
    public static ChunkCoordinates translateCoords(int sx, int sy, int sz, double distance, double angle, double tilt) {
        ChunkCoordinates dest = new ChunkCoordinates(sx, sy, sz);

        double rangle = angle * 2.0D * Math.PI;
        double rtilt = tilt * Math.PI;

        dest.posX += Math.round(Math.sin(rangle) * Math.sin(rtilt) * distance);
        dest.posY += Math.round(Math.cos(rtilt) * distance);
        dest.posZ += Math.round(Math.cos(rangle) * Math.sin(rtilt) * distance);

        return dest;
    }

    /**
     * Draws a line from {x1, y1, z1} to {x2, y2, z2}
     */
    protected void drawBresehnam(World world, int x1, int y1, int z1, int x2, int y2, int z2, Block blockValue,
            int metaValue) {
        visitBresenham(
                x1,
                y1,
                z1,
                x2,
                y2,
                z2,
                (px, py, pz) -> setBlockAndMetadata(world, px, py, pz, blockValue, metaValue));
    }

    /**
     * Get an array of values that represent a line from point A to point B
     */
    public static ChunkCoordinates[] getBresehnamArrayCoords(ChunkCoordinates src, ChunkCoordinates dest) {
        return getBresehnamArrayCoords(src.posX, src.posY, src.posZ, dest.posX, dest.posY, dest.posZ);
    }

    /**
     * Get an array of values that represent a line from point A to point B
     */
    public static ChunkCoordinates[] getBresehnamArrayCoords(int x1, int y1, int z1, int x2, int y2, int z2) {
        int length = Math.max(Math.abs(x2 - x1), Math.max(Math.abs(y2 - y1), Math.abs(z2 - z1)));
        ChunkCoordinates[] line = new ChunkCoordinates[length + 1];
        final int[] index = { 0 };
        visitBresenham(x1, y1, z1, x2, y2, z2, (px, py, pz) -> line[index[0]++] = new ChunkCoordinates(px, py, pz));
        return line;
    }

    @FunctionalInterface
    private interface CoordinateVisitor {

        void accept(int x, int y, int z);
    }

    // preserve the original major axis tie breaking and error accumulator rules
    private static void visitBresenham(int x1, int y1, int z1, int x2, int y2, int z2, CoordinateVisitor visitor) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int dz = z2 - z1;
        int xStep = dx < 0 ? -1 : 1;
        int yStep = dy < 0 ? -1 : 1;
        int zStep = dz < 0 ? -1 : 1;
        int lengthX = Math.abs(dx);
        int lengthY = Math.abs(dy);
        int lengthZ = Math.abs(dz);
        int dx2 = lengthX << 1;
        int dy2 = lengthY << 1;
        int dz2 = lengthZ << 1;
        int x = x1;
        int y = y1;
        int z = z1;

        if (lengthX >= lengthY && lengthX >= lengthZ) {
            int errorY = dy2 - lengthX;
            int errorZ = dz2 - lengthX;
            for (int i = 0; i < lengthX; i++) {
                visitor.accept(x, y, z);
                if (errorY > 0) {
                    y += yStep;
                    errorY -= dx2;
                }
                if (errorZ > 0) {
                    z += zStep;
                    errorZ -= dx2;
                }
                errorY += dy2;
                errorZ += dz2;
                x += xStep;
            }
        } else if (lengthY >= lengthX && lengthY >= lengthZ) {
            int errorX = dx2 - lengthY;
            int errorZ = dz2 - lengthY;
            for (int i = 0; i < lengthY; i++) {
                visitor.accept(x, y, z);
                if (errorX > 0) {
                    x += xStep;
                    errorX -= dy2;
                }
                if (errorZ > 0) {
                    z += zStep;
                    errorZ -= dy2;
                }
                errorX += dx2;
                errorZ += dz2;
                y += yStep;
            }
        } else {
            int errorY = dy2 - lengthZ;
            int errorX = dx2 - lengthZ;
            for (int i = 0; i < lengthZ; i++) {
                visitor.accept(x, y, z);
                if (errorY > 0) {
                    y += yStep;
                    errorY -= dz2;
                }
                if (errorX > 0) {
                    x += xStep;
                    errorX -= dz2;
                }
                errorY += dy2;
                errorX += dx2;
                z += zStep;
            }
        }
        visitor.accept(x, y, z);
    }

    /**
     * Draw a flat blob (circle) of leaves
     */
    public void makeLeafCircle(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue) {
        this.makeLeafCircle(world, sx, sy, sz, rad, blockValue, metaValue, false);
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
                if (useHack && dx == 3 && dz == 3) {
                    dist = 6;
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
     * Put a leaf only in spots where leaves can go!
     */
    public void putLeafBlock(World world, int x, int y, int z, Block blockValue, int metaValue) {
        Block block = world.getBlock(x, y, z);
        if (block == null || block.canBeReplacedByLeaves(world, x, y, z)) {
            this.setBlockAndMetadata(world, x, y, z, blockValue, metaValue);
        }
    }

    /**
     * Gets either cobblestone or mossy cobblestone, randomly. Used for ruins.
     *
     * @param rand
     * @return
     */
    protected Block randStone(Random rand, int howMuch) {
        return rand.nextInt(howMuch) >= 1 ? Blocks.cobblestone : Blocks.mossy_cobblestone;
    }

    /**
     * Checks an area to see if it consists of flat natural ground below and air above
     *
     */
    protected boolean isAreaSuitable(World world, Random rand, int x, int y, int z, int width, int height, int depth) {
        // check if there's anything within the diameter
        for (int cx = 0; cx < width; cx++) {
            for (int cz = 0; cz < depth; cz++) {
                // check if the blocks even exist?
                if (world.blockExists(x + cx, y, z + cz)) {
                    // is there grass, dirt or stone below?
                    Material m = world.getBlock(x + cx, y - 1, z + cz).getMaterial();
                    if (m != Material.ground && m != Material.grass && m != Material.rock) {
                        return false;
                    }

                    for (int cy = 0; cy < height; cy++) {
                        // blank space above?
                        if (!world.isAirBlock(x + cx, y + cy, z + cz)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        // Okie dokie
        return true;

    }

    /**
     * Draw a giant blob of whatevs.
     */
    public void drawBlob(World world, int sx, int sy, int sz, int rad, Block blockValue, int metaValue) {
        // then trace out a quadrant
        for (int dx = 0; dx <= rad; dx++) {
            for (int dy = 0; dy <= rad; dy++) {
                for (int dz = 0; dz <= rad; dz++) {
                    // determine how far we are from the center.
                    int dist = 0;
                    if (dx >= dy && dx >= dz) {
                        dist = dx + (Math.max(dy, dz) >> 1) + (Math.min(dy, dz) >> 2);
                    } else if (dy >= dx && dy >= dz) {
                        dist = dy + (Math.max(dx, dz) >> 1) + (Math.min(dx, dz) >> 2);
                    } else {
                        dist = dz + (Math.max(dx, dy) >> 1) + (Math.min(dx, dy) >> 2);
                    }

                    // if we're inside the blob, fill it
                    if (dist <= rad) {
                        // do eight at a time for easiness!
                        setBlockAndMetadata(world, sx + dx, sy + dy, sz + dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx + dx, sy + dy, sz - dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx - dx, sy + dy, sz + dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx - dx, sy + dy, sz - dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx + dx, sy - dy, sz + dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx + dx, sy - dy, sz - dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx - dx, sy - dy, sz + dz, blockValue, metaValue);
                        setBlockAndMetadata(world, sx - dx, sy - dy, sz - dz, blockValue, metaValue);
                    }
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
                    int dist = 0;
                    if (dx >= dy && dx >= dz) {
                        dist = dx + (Math.max(dy, dz) >> 1) + (Math.min(dy, dz) >> 2);
                    } else if (dy >= dx && dy >= dz) {
                        dist = dy + (Math.max(dx, dz) >> 1) + (Math.min(dx, dz) >> 2);
                    } else {
                        dist = dz + (Math.max(dx, dy) >> 1) + (Math.min(dx, dy) >> 2);
                    }

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

    // Does the block have only air blocks adjacent
    protected static boolean surroundedByAir(IBlockAccess world, int bx, int by, int bz) {
        if (!world.isAirBlock(bx + 1, by, bz)) {
            return false;
        } else if (!world.isAirBlock(bx - 1, by, bz)) {
            return false;
        } else if (!world.isAirBlock(bx, by, bz + 1)) {
            return false;
        } else if (!world.isAirBlock(bx, by, bz - 1)) {
            return false;
        } else if (!world.isAirBlock(bx, by + 1, bz)) {
            return false;
        } else if (!world.isAirBlock(bx, by - 1, bz)) {
            return false;
        } else {
            return true;
        }
    }

    // Does the block have at least 1 air block adjacent
    protected static boolean hasAirAround(World world, int bx, int by, int bz) {
        if (world.isAirBlock(bx + 1, by, bz)) {
            return true;
        } else if (world.isAirBlock(bx - 1, by, bz)) {
            return true;
        } else if (world.isAirBlock(bx, by, bz + 1)) {
            return true;
        } else if (world.isAirBlock(bx, by, bz - 1)) {
            return true;
        } else if (world.isAirBlock(bx, by + 1, bz)) {
            return true;
        } else {
            return false;
        }
    }

    protected static boolean isNearSolid(World world, int bx, int by, int bz) {
        if (world.blockExists(bx + 1, by, bz) && world.getBlock(bx + 1, by, bz).getMaterial().isSolid()) {
            return true;
        } else if (world.blockExists(bx - 1, by, bz) && world.getBlock(bx - 1, by, bz).getMaterial().isSolid()) {
            return true;
        } else if (world.blockExists(bx, by, bz + 1) && world.getBlock(bx, by, bz + 1).getMaterial().isSolid()) {
            return true;
        } else if (world.blockExists(bx, by, bz - 1) && world.getBlock(bx, by, bz - 1).getMaterial().isSolid()) {
            return true;
        } else {
            return false;
        }
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
}
