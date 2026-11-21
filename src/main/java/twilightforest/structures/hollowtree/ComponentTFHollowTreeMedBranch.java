package twilightforest.structures.hollowtree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.world.TFGenerator;

public class ComponentTFHollowTreeMedBranch extends StructureTFComponent {

    private static final int LOG_METADATA = 12;
    private static final int LEAF_METADATA = 0;
    private static final int LEAF_UPDATE_FLAGS = 2;

    private static final int MAX_DECORATION_COUNT = 3;
    private static final int DECORATION_LENGTH_DIVISOR = 5;
    private static final double SHOOT_SPREAD = 0.8D;
    private static final double SHOOT_SPREAD_OFFSET = 0.4D;
    private static final double SHOOT_ORIGIN_MIN = 0.2D;
    private static final double SHOOT_ORIGIN_VARIATION = 0.8D;
    private static final double SHOOT_TILT_MIN = 0.15D;
    private static final double SHOOT_TILT_VARIATION = 0.75D;
    private static final float SHOOT_LENGTH_RATIO = 0.3F;
    private static final float MIN_SHOOT_LENGTH = 2.0F;
    private static final float LEAF_ORIGIN_MIN = 0.2F;
    private static final float LEAF_ORIGIN_VARIATION = 0.6F;
    private static final int SMALL_LEAF_RADIUS = 2;
    private static final int LARGE_LEAF_RADIUS = 3;
    private static final int BOUNDS_ROUNDING_MARGIN = 2;

    private static final double ORIGINAL_FORWARD_BOUNDS_OFFSET = 0.5F;
    private static final double ORIGINAL_SIDE_BOUNDS_OFFSET = 0.1F;
    private static final double ORIGINAL_SIDE_BOUNDS_ANGLE = 0.225D;

    private static final int DECORATION_SEED_X = 321534781;
    private static final int DECORATION_SEED_Z = 756839;

    private static final String NBT_SRC_X = "srcPosX";
    private static final String NBT_SRC_Y = "srcPosY";
    private static final String NBT_SRC_Z = "srcPosZ";
    private static final String NBT_DEST_X = "destPosX";
    private static final String NBT_DEST_Y = "destPosY";
    private static final String NBT_DEST_Z = "destPosZ";
    private static final String NBT_LENGTH = "branchLength";
    private static final String NBT_ANGLE = "branchAngle";
    private static final String NBT_TILT = "branchTilt";
    private static final String NBT_LEAFY = "branchLeafy";

    ChunkCoordinates src, dest; // source and destination of branch, array of 3 ints representing x, y, z
    double length;
    double angle;
    double tilt;
    boolean leafy;

    private int originalMinX;
    private int originalMinZ;

    public ComponentTFHollowTreeMedBranch() {
        super();
    }

    protected ComponentTFHollowTreeMedBranch(int i, int sx, int sy, int sz, double length, double angle, double tilt,
            boolean leafy) {
        super(i);

        this.src = new ChunkCoordinates(sx, sy, sz);
        this.dest = TFGenerator.translateCoords(sx, sy, sz, length, angle, tilt);
        this.length = length;
        this.angle = angle;
        this.tilt = tilt;
        this.leafy = leafy;

        setCoordBaseMode(0);

        this.boundingBox = makeOriginalBoundingBox();
        expandDecorationBounds();
    }

    private static StructureBoundingBox makeBoundingBox(ChunkCoordinates first, ChunkCoordinates second) {
        return new StructureBoundingBox(
                Math.min(first.posX, second.posX),
                Math.min(first.posY, second.posY),
                Math.min(first.posZ, second.posZ),
                Math.max(first.posX, second.posX),
                Math.max(first.posY, second.posY),
                Math.max(first.posZ, second.posZ));
    }

    private StructureBoundingBox makeOriginalBoundingBox() {
        StructureBoundingBox original = makeBoundingBox(src, dest);

        // preserve the original extents used by large branch subclasses
        original.expandTo(makeExpandedBB(ORIGINAL_FORWARD_BOUNDS_OFFSET, length, angle, tilt));
        original.expandTo(makeExpandedBB(ORIGINAL_SIDE_BOUNDS_OFFSET, length, ORIGINAL_SIDE_BOUNDS_ANGLE, tilt));
        original.expandTo(makeExpandedBB(ORIGINAL_SIDE_BOUNDS_OFFSET, length, -ORIGINAL_SIDE_BOUNDS_ANGLE, tilt));

        // the original seed must not depend on newly expanded decoration bounds
        originalMinX = original.minX;
        originalMinZ = original.minZ;
        return original;
    }

    private StructureBoundingBox makeExpandedBB(double outVar, double branchLength, double branchAngle,
            double branchTilt) {
        ChunkCoordinates branchSrc = TFGenerator
                .translateCoords(src.posX, src.posY, src.posZ, length * outVar, angle, tilt);
        ChunkCoordinates branchDest = TFGenerator
                .translateCoords(branchSrc.posX, branchSrc.posY, branchSrc.posZ, branchLength, branchAngle, branchTilt);
        return makeBoundingBox(branchSrc, branchDest);
    }

    private void expandDecorationBounds() {
        // shoot and leaf blobs can extend beyond the original line bounding box
        // include their full reach so neighbouring chunk passes do not clip them
        int padding = (int) Math.ceil(Math.max(length * SHOOT_LENGTH_RATIO, MIN_SHOOT_LENGTH)) + LARGE_LEAF_RADIUS
                + BOUNDS_ROUNDING_MARGIN;
        boundingBox.expandTo(
                new StructureBoundingBox(
                        Math.min(src.posX, dest.posX) - padding,
                        Math.min(src.posY, dest.posY) - padding,
                        Math.min(src.posZ, dest.posZ) - padding,
                        Math.max(src.posX, dest.posX) + padding,
                        Math.max(src.posY, dest.posY) + padding,
                        Math.max(src.posZ, dest.posZ) + padding));
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger(NBT_SRC_X, src.posX);
        par1NBTTagCompound.setInteger(NBT_SRC_Y, src.posY);
        par1NBTTagCompound.setInteger(NBT_SRC_Z, src.posZ);
        par1NBTTagCompound.setInteger(NBT_DEST_X, dest.posX);
        par1NBTTagCompound.setInteger(NBT_DEST_Y, dest.posY);
        par1NBTTagCompound.setInteger(NBT_DEST_Z, dest.posZ);
        par1NBTTagCompound.setDouble(NBT_LENGTH, length);
        par1NBTTagCompound.setDouble(NBT_ANGLE, angle);
        par1NBTTagCompound.setDouble(NBT_TILT, tilt);
        par1NBTTagCompound.setBoolean(NBT_LEAFY, leafy);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);

        src = new ChunkCoordinates(
                par1NBTTagCompound.getInteger(NBT_SRC_X),
                par1NBTTagCompound.getInteger(NBT_SRC_Y),
                par1NBTTagCompound.getInteger(NBT_SRC_Z));
        dest = new ChunkCoordinates(
                par1NBTTagCompound.getInteger(NBT_DEST_X),
                par1NBTTagCompound.getInteger(NBT_DEST_Y),
                par1NBTTagCompound.getInteger(NBT_DEST_Z));
        length = par1NBTTagCompound.getDouble(NBT_LENGTH);
        angle = par1NBTTagCompound.getDouble(NBT_ANGLE);
        tilt = par1NBTTagCompound.getDouble(NBT_TILT);
        leafy = par1NBTTagCompound.getBoolean(NBT_LEAFY);

        makeOriginalBoundingBox();
        expandDecorationBounds();
    }

    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random rand) {
        // medium branch decorations are placed directly during addComponentParts
    }

    public void makeSmallBranch(List list, Random rand, int index, int x, int y, int z, double branchLength,
            double branchRotation, double branchAngle, boolean leafy) {
        ComponentTFHollowTreeSmallBranch branch = new ComponentTFHollowTreeSmallBranch(
                index,
                x,
                y,
                z,
                branchLength,
                branchRotation,
                branchAngle,
                leafy);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    // keep the same decoration seed as the original generator, even when the bounding box grows
    protected final Random createDecorationRandom(World world) {
        return new Random(world.getSeed() + (originalMinX * DECORATION_SEED_X) ^ (originalMinZ * DECORATION_SEED_Z));
    }

    // convert a stored world coordinate to this components local coordinate space
    protected final ChunkCoordinates toLocalCoordinates(ChunkCoordinates point) {
        return new ChunkCoordinates(
                point.posX - boundingBox.minX,
                point.posY - boundingBox.minY,
                point.posZ - boundingBox.minZ);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
        ChunkCoordinates rSrc = toLocalCoordinates(src);
        ChunkCoordinates rDest = toLocalCoordinates(dest);

        drawBresehnam(
                world,
                sbb,
                rSrc.posX,
                rSrc.posY,
                rSrc.posZ,
                rDest.posX,
                rDest.posY,
                rDest.posZ,
                TFBlocks.log,
                LOG_METADATA);
        drawBresehnam(
                world,
                sbb,
                rSrc.posX,
                rSrc.posY + 1,
                rSrc.posZ,
                rDest.posX,
                rDest.posY,
                rDest.posZ,
                TFBlocks.log,
                LOG_METADATA);

        Random decoRNG = createDecorationRandom(world);

        // and several small branches
        int numShoots = Math.min(decoRNG.nextInt(MAX_DECORATION_COUNT) + 1, (int) (length / DECORATION_LENGTH_DIVISOR));
        double angleInc = numShoots > 0 ? SHOOT_SPREAD / numShoots : 0.0D;

        for (int i = 0; i < numShoots; i++) {
            double angleVar = angleInc * i - SHOOT_SPREAD_OFFSET;
            double outVar = decoRNG.nextDouble() * SHOOT_ORIGIN_VARIATION + SHOOT_ORIGIN_MIN;
            double tiltVar = decoRNG.nextDouble() * SHOOT_TILT_VARIATION + SHOOT_TILT_MIN;

            ChunkCoordinates bSrc = TFGenerator
                    .translateCoords(rSrc.posX, rSrc.posY, rSrc.posZ, length * outVar, angle, tilt);

            drawSmallBranch(
                    world,
                    sbb,
                    bSrc.posX,
                    bSrc.posY,
                    bSrc.posZ,
                    Math.max(length * SHOOT_LENGTH_RATIO, MIN_SHOOT_LENGTH),
                    angle + angleVar,
                    tilt * tiltVar,
                    leafy);
        }

        // with leaves!
        if (leafy) {
            int numLeafBalls = Math
                    .min(decoRNG.nextInt(MAX_DECORATION_COUNT) + 1, (int) (length / DECORATION_LENGTH_DIVISOR));
            for (int i = 0; i < numLeafBalls; i++) {
                double slength = (decoRNG.nextFloat() * LEAF_ORIGIN_VARIATION + LEAF_ORIGIN_MIN) * length;
                ChunkCoordinates bdst = TFGenerator
                        .translateCoords(rSrc.posX, rSrc.posY, rSrc.posZ, slength, angle, tilt);

                makeLeafBlob(
                        world,
                        sbb,
                        bdst.posX,
                        bdst.posY,
                        bdst.posZ,
                        decoRNG.nextBoolean() ? SMALL_LEAF_RADIUS : LARGE_LEAF_RADIUS);
            }

            makeLeafBlob(world, sbb, rDest.posX, rDest.posY, rDest.posZ, LARGE_LEAF_RADIUS);
        }

        return true;
    }

    /**
     * Draws a line
     */
    protected void drawBresehnam(World world, StructureBoundingBox sbb, int x1, int y1, int z1, int x2, int y2, int z2,
            Block blockValue, int metaValue) {
        ChunkCoordinates[] lineCoords = TFGenerator.getBresehnamArrayCoords(x1, y1, z1, x2, y2, z2);

        for (ChunkCoordinates coords : lineCoords) {
            placeBlockAtCurrentPosition(world, blockValue, metaValue, coords.posX, coords.posY, coords.posZ, sbb);
        }
    }

    /**
     * Make a leaf blob
     */
    protected void makeLeafBlob(World world, StructureBoundingBox sbb, int sx, int sy, int sz, int radius) {
        // then trace out a quadrant
        for (int dx = 0; dx <= radius; dx++) {
            for (int dy = 0; dy <= radius; dy++) {
                for (int dz = 0; dz <= radius; dz++) {
                    // determine how far we are from the center.
                    int largest = Math.max(dx, Math.max(dy, dz));
                    int smallest = Math.min(dx, Math.min(dy, dz));
                    int middle = dx + dy + dz - largest - smallest;
                    int dist = (int) (largest + ((middle * 0.5F) + (smallest * 0.25F)));

                    // if we're inside the blob, fill it
                    if (dist <= radius) {
                        // do eight at a time for easiness!
                        // avoid repeating positions along the three symmetry planes
                        placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx + dx, sy + dy, sz + dz, sbb);
                        if (dz != 0) {
                            placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx + dx, sy + dy, sz - dz, sbb);
                        }
                        if (dx != 0) {
                            placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx - dx, sy + dy, sz + dz, sbb);
                            if (dz != 0) {
                                placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx - dx, sy + dy, sz - dz, sbb);
                            }
                        }
                        if (dy != 0) {
                            placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx + dx, sy - dy, sz + dz, sbb);
                            if (dz != 0) {
                                placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx + dx, sy - dy, sz - dz, sbb);
                            }
                            if (dx != 0) {
                                placeLeafBlock(world, TFBlocks.leaves, LEAF_METADATA, sx - dx, sy - dy, sz + dz, sbb);
                                if (dz != 0) {
                                    placeLeafBlock(
                                            world,
                                            TFBlocks.leaves,
                                            LEAF_METADATA,
                                            sx - dx,
                                            sy - dy,
                                            sz - dz,
                                            sbb);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Puts a block only if leaves can go there.
     */
    protected void placeLeafBlock(World world, Block blockID, int meta, int x, int y, int z, StructureBoundingBox sbb) {
        int offX = getXWithOffset(x, z);
        int offY = getYWithOffset(y);
        int offZ = getZWithOffset(x, z);

        if (sbb.isVecInside(offX, offY, offZ)) {
            Block whatsThere = world.getBlock(offX, offY, offZ);
            if (whatsThere == null || whatsThere.canBeReplacedByLeaves(world, offX, offY, offZ)) {
                world.setBlock(offX, offY, offZ, blockID, meta, LEAF_UPDATE_FLAGS);
            }
        }
    }

    /**
     * This is like the small branch component, but we're just drawing it directly into the world
     */
    protected void drawSmallBranch(World world, StructureBoundingBox sbb, int sx, int sy, int sz, double branchLength,
            double branchAngle, double branchTilt, boolean leafy) {
        // draw a line
        ChunkCoordinates branchDest = TFGenerator.translateCoords(sx, sy, sz, branchLength, branchAngle, branchTilt);
        drawBresehnam(
                world,
                sbb,
                sx,
                sy,
                sz,
                branchDest.posX,
                branchDest.posY,
                branchDest.posZ,
                TFBlocks.log,
                LOG_METADATA);

        // leaf blob at the end
        if (leafy) {
            makeLeafBlob(world, sbb, branchDest.posX, branchDest.posY, branchDest.posZ, SMALL_LEAF_RADIUS);
        }
    }
}
