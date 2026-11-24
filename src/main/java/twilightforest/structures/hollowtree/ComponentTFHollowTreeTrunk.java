package twilightforest.structures.hollowtree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.world.TFGenerator;

public class ComponentTFHollowTreeTrunk extends StructureTFComponent {

    private static final int MIN_HEIGHT = 32;
    private static final int HEIGHT_VARIATION = 64;
    private static final int MIN_RADIUS = 1;
    private static final int RADIUS_VARIATION = 4;
    private static final int BOUNDING_BOX_PADDING = 2;
    private static final int TRUNK_BLOCK_OFFSET = 1;
    private static final int TRUNK_HOLLOW_DIVISOR = 2;
    private static final double TRUNK_DIAGONAL_WEIGHT = 0.5D;
    private static final int VINE_METADATA = 8;
    private static final int GROUND_LEVEL_UNSET = -1;

    private static final int SMALL_BRANCH_MIN_COUNT = 3;
    private static final int SMALL_BRANCH_COUNT_VARIATION = 3;
    private static final int SMALL_BRANCH_LENGTH = 4;
    private static final double SMALL_BRANCH_TILT = 0.35D;
    private static final double BRANCH_HEIGHT_RANGE = 0.9D;
    private static final int BRANCH_MIN_HEIGHT_DIVISOR = 10;

    private static final int CROWN_RADIUS_MULTIPLIER = 4;
    private static final int CROWN_RADIUS_OFFSET = 4;
    private static final int CROWN_BRANCH_VARIATION = 2;
    private static final int CROWN_RADIUS_DIVISOR = 2;
    private static final int CROWN_TOP_MIN_BRANCHES = 2;
    private static final int CROWN_TOP_MAX_BRANCHES = 4;
    private static final double CROWN_LOWER_TILT = 0.35D;
    private static final double CROWN_MIDDLE_TILT = 0.28D;
    private static final double CROWN_UPPER_TILT = 0.15D;
    private static final double CROWN_VERTICAL_TILT = 0.05D;

    private static final int ROOT_MIN_COUNT = 3;
    private static final int ROOT_MAX_COUNT = 5;
    private static final int ROOT_HEIGHT_VARIATION = 2;
    private static final int ROOT_RING_HEIGHT = 3;
    private static final int ROOT_RING_LENGTH = 6;
    private static final double ROOT_RING_TILT = 0.75D;
    private static final int TAPROOT_RING_HEIGHT = 1;
    private static final int TAPROOT_RING_LENGTH = 8;
    private static final double TAPROOT_RING_TILT = 0.9D;

    private static final int BRANCH_SIZE_MEDIUM = 1;
    private static final int BRANCH_SIZE_LARGE = 2;
    private static final int BRANCH_SIZE_ROOT = 3;
    private static final double BRANCH_ORIGIN_TILT = 0.5D;

    private static final int INSECTS_PER_RADIUS = 3;
    private static final int MIN_INSECT_COUNT = 10;
    private static final double INSECT_ANGLE_FIRST_BOUNDARY = 0.125D;
    private static final double INSECT_ANGLE_SECOND_BOUNDARY = 0.375D;
    private static final double INSECT_ANGLE_THIRD_BOUNDARY = 0.625D;
    private static final double INSECT_ANGLE_FOURTH_BOUNDARY = 0.875D;
    private static final int INSECT_META_FIRST_FACE = 3;
    private static final int INSECT_META_SECOND_FACE = 1;
    private static final int INSECT_META_THIRD_FACE = 4;
    private static final int INSECT_META_FOURTH_FACE = 2;
    private static final int INSECT_BLOCK_UPDATE_FLAGS = 2;

    private static final String NBT_RADIUS = "trunkRadius";
    private static final String NBT_HEIGHT = "trunkHeight";
    private static final String NBT_GROUND_LEVEL = "trunkGroundLevel";

    int radius;
    int height;
    int groundLevel = GROUND_LEVEL_UNSET;

    public ComponentTFHollowTreeTrunk() {
        super();
    }

    public ComponentTFHollowTreeTrunk(World world, Random rand, int index, int x, int y, int z) {
        super(index);

        height = rand.nextInt(HEIGHT_VARIATION) + MIN_HEIGHT;
        radius = rand.nextInt(RADIUS_VARIATION) + MIN_RADIUS;

        this.setCoordBaseMode(0);

        boundingBox = new StructureBoundingBox(
                x,
                y,
                z,
                x + radius * 2 + BOUNDING_BOX_PADDING,
                y + height,
                z + radius * 2 + BOUNDING_BOX_PADDING);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger(NBT_RADIUS, this.radius);
        par1NBTTagCompound.setInteger(NBT_HEIGHT, this.height);
        par1NBTTagCompound.setInteger(NBT_GROUND_LEVEL, this.groundLevel);

    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);

        this.radius = par1NBTTagCompound.getInteger(NBT_RADIUS);
        this.height = par1NBTTagCompound.getInteger(NBT_HEIGHT);
        this.groundLevel = par1NBTTagCompound.hasKey(NBT_GROUND_LEVEL) ? par1NBTTagCompound.getInteger(NBT_GROUND_LEVEL)
                : GROUND_LEVEL_UNSET;
    }

    /**
     * Add on the various bits and doo-dads we need to succeed
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random rand) {
        int index = getComponentType();

        // 3-5 couple branches on the way up...
        int numBranches = rand.nextInt(SMALL_BRANCH_COUNT_VARIATION) + SMALL_BRANCH_MIN_COUNT;
        for (int i = 0; i < numBranches; i++) {
            int branchHeight = (int) (height * rand.nextDouble() * BRANCH_HEIGHT_RANGE)
                    + height / BRANCH_MIN_HEIGHT_DIVISOR;
            double branchRotation = rand.nextDouble();

            makeSmallBranch(
                    list,
                    rand,
                    index + i + 1,
                    branchHeight,
                    SMALL_BRANCH_LENGTH,
                    branchRotation,
                    SMALL_BRANCH_TILT,
                    true);
        }

        // build the crown
        buildFullCrown(list, rand, index + numBranches + 1);

        // roots
        // 3-5 roots at the bottom
        buildBranchRing(
                list,
                rand,
                index,
                ROOT_RING_HEIGHT,
                ROOT_HEIGHT_VARIATION,
                ROOT_RING_LENGTH,
                0,
                ROOT_RING_TILT,
                0,
                ROOT_MIN_COUNT,
                ROOT_MAX_COUNT,
                BRANCH_SIZE_ROOT,
                false);

        // several more taproots
        buildBranchRing(
                list,
                rand,
                index,
                TAPROOT_RING_HEIGHT,
                ROOT_HEIGHT_VARIATION,
                TAPROOT_RING_LENGTH,
                0,
                TAPROOT_RING_TILT,
                0,
                ROOT_MIN_COUNT,
                ROOT_MAX_COUNT,
                BRANCH_SIZE_ROOT,
                false);

    }

    /**
     * Build the crown of the tree
     */
    protected void buildFullCrown(List list, Random rand, int index) {
        int crownRadius = radius * CROWN_RADIUS_MULTIPLIER + CROWN_RADIUS_OFFSET;
        int bvar = radius + CROWN_BRANCH_VARIATION;

        // okay, let's do 3-5 main branches starting at the bottom of the crown
        index += buildBranchRing(
                list,
                rand,
                index,
                height - crownRadius,
                0,
                crownRadius,
                0,
                CROWN_LOWER_TILT,
                0,
                bvar,
                bvar + CROWN_BRANCH_VARIATION,
                BRANCH_SIZE_LARGE,
                true);

        // then, let's do 3-5 medium branches at the crown middle
        index += buildBranchRing(
                list,
                rand,
                index,
                height - (crownRadius / CROWN_RADIUS_DIVISOR),
                0,
                crownRadius,
                0,
                CROWN_MIDDLE_TILT,
                0,
                bvar,
                bvar + CROWN_BRANCH_VARIATION,
                BRANCH_SIZE_MEDIUM,
                true);

        // finally, let's do 2-4 main branches at the crown top
        index += buildBranchRing(
                list,
                rand,
                index,
                height,
                0,
                crownRadius,
                0,
                CROWN_UPPER_TILT,
                0,
                CROWN_TOP_MIN_BRANCHES,
                CROWN_TOP_MAX_BRANCHES,
                BRANCH_SIZE_LARGE,
                true);

        // and extra finally, let's do 3-6 medium branches going straight up
        buildBranchRing(
                list,
                rand,
                index,
                height,
                0,
                crownRadius / CROWN_RADIUS_DIVISOR,
                0,
                CROWN_VERTICAL_TILT,
                0,
                bvar,
                bvar + CROWN_BRANCH_VARIATION,
                BRANCH_SIZE_MEDIUM,
                true);
    }

    /**
     * Build a ring of branches around the tree size 0 = small, 1 = med, 2 = large, 3 = root
     */
    protected int buildBranchRing(List list, Random rand, int index, int branchHeight, int heightVar, int length,
            int lengthVar, double tilt, double tiltVar, int minBranches, int maxBranches, int size, boolean leafy) {
        // let's do this!
        int numBranches = rand.nextInt(maxBranches - minBranches + 1) + minBranches;
        double branchRotation = 1.0 / numBranches;
        double branchOffset = rand.nextDouble();

        for (int i = 0; i < numBranches; i++) {
            int dHeight = heightVar > 0 ? branchHeight - heightVar + rand.nextInt(2 * heightVar) : branchHeight;

            if (size == BRANCH_SIZE_LARGE) {
                makeLargeBranch(list, rand, index, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
            } else if (size == BRANCH_SIZE_MEDIUM) {
                makeMedBranch(list, rand, index, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
            } else if (size == BRANCH_SIZE_ROOT) {
                makeRoot(list, rand, index, dHeight, length, i * branchRotation + branchOffset, tilt);
            } else {
                makeSmallBranch(list, rand, index, dHeight, length, i * branchRotation + branchOffset, tilt, leafy);
            }
        }

        return numBranches;
    }

    public void makeSmallBranch(List list, Random rand, int index, int branchHeight, int branchLength,
            double branchRotation, double branchAngle, boolean leafy) {
        ChunkCoordinates bSrc = getBranchSrc(branchHeight, branchRotation);
        ComponentTFHollowTreeSmallBranch branch = new ComponentTFHollowTreeSmallBranch(
                index,
                bSrc.posX,
                bSrc.posY,
                bSrc.posZ,
                branchLength,
                branchRotation,
                branchAngle,
                leafy);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    public void makeMedBranch(List list, Random rand, int index, int branchHeight, int branchLength,
            double branchRotation, double branchAngle, boolean leafy) {
        ChunkCoordinates bSrc = getBranchSrc(branchHeight, branchRotation);
        ComponentTFHollowTreeMedBranch branch = new ComponentTFHollowTreeMedBranch(
                index,
                bSrc.posX,
                bSrc.posY,
                bSrc.posZ,
                branchLength,
                branchRotation,
                branchAngle,
                leafy);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    public void makeLargeBranch(List list, Random rand, int index, int branchHeight, int branchLength,
            double branchRotation, double branchAngle, boolean leafy) {
        ChunkCoordinates bSrc = getBranchSrc(branchHeight, branchRotation);
        ComponentTFHollowTreeLargeBranch branch = new ComponentTFHollowTreeLargeBranch(
                index,
                bSrc.posX,
                bSrc.posY,
                bSrc.posZ,
                branchLength,
                branchRotation,
                branchAngle,
                leafy);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    public void makeRoot(List list, Random rand, int index, int branchHeight, int branchLength, double branchRotation,
            double branchAngle) {
        ChunkCoordinates bSrc = getBranchSrc(branchHeight, branchRotation);
        ComponentTFHollowTreeRoot branch = new ComponentTFHollowTreeRoot(
                index,
                bSrc.posX,
                bSrc.posY,
                bSrc.posZ,
                branchLength,
                branchRotation,
                branchAngle,
                false);
        list.add(branch);
        branch.buildComponent(this, list, rand);
    }

    /**
     * Where should we start this branch?
     */
    private ChunkCoordinates getBranchSrc(int branchHeight, double branchRotation) {
        return TFGenerator.translateCoords(
                boundingBox.minX + radius + TRUNK_BLOCK_OFFSET,
                boundingBox.minY + branchHeight,
                boundingBox.minZ + radius + TRUNK_BLOCK_OFFSET,
                radius,
                branchRotation,
                BRANCH_ORIGIN_TILT);
    }

    /**
     * Generate the tree trunk
     */
    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {

        // offset bounding box to average ground level
        if (this.groundLevel < 0) {
            this.groundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.groundLevel < 0) {
                return true;
            }

            this.height = this.boundingBox.maxY - this.groundLevel;
            this.boundingBox.minY = this.groundLevel;
        }

        int hollow = radius / TRUNK_HOLLOW_DIVISOR;

        for (int dx = 0; dx <= 2 * radius; dx++) {
            for (int dz = 0; dz <= 2 * radius; dz++) {
                // determine how far we are from the center.
                int ax = Math.abs(dx - radius);
                int az = Math.abs(dz - radius);
                int dist = (int) (Math.max(ax, az) + (Math.min(ax, az) * TRUNK_DIAGONAL_WEIGHT));

                // fill the body of the trunk
                if (dist <= radius && dist > hollow) {
                    for (int dy = 0; dy <= height; dy++) {
                        this.placeBlockAtCurrentPosition(
                                world,
                                TFBlocks.log,
                                0,
                                dx + TRUNK_BLOCK_OFFSET,
                                dy,
                                dz + TRUNK_BLOCK_OFFSET,
                                sbb); // offset
                        // since our
                        // BB is
                        // slightly
                        // larger
                        // than the
                        // trunk
                    }
                }

                // fill to ground
                if (dist <= radius) {
                    this.func_151554_b(
                            world,
                            TFBlocks.log,
                            0,
                            dx + TRUNK_BLOCK_OFFSET,
                            -1,
                            dz + TRUNK_BLOCK_OFFSET,
                            sbb);
                }

                // add vines
                if (dist == hollow && dx == hollow + radius) {
                    this.func_151554_b(
                            world,
                            Blocks.vine,
                            VINE_METADATA,
                            dx + TRUNK_BLOCK_OFFSET,
                            height,
                            dz + TRUNK_BLOCK_OFFSET,
                            sbb);
                }
            }
        }

        // fireflies & cicadas
        int numInsects = random.nextInt(INSECTS_PER_RADIUS * radius) + random.nextInt(INSECTS_PER_RADIUS * radius)
                + MIN_INSECT_COUNT;
        for (int i = 0; i < numInsects; i++) {
            int fHeight = (int) (height * random.nextDouble() * BRANCH_HEIGHT_RANGE)
                    + height / BRANCH_MIN_HEIGHT_DIVISOR;
            double fAngle = random.nextDouble();
            addInsect(world, fHeight, fAngle, random, sbb);
        }

        return true;
    }

    /**
     * Add a random insect
     */
    protected void addInsect(World world, int fHeight, double fAngle, Random random, StructureBoundingBox sbb) {
        ChunkCoordinates bugSpot = TFGenerator.translateCoords(
                this.radius + TRUNK_BLOCK_OFFSET,
                fHeight,
                this.radius + TRUNK_BLOCK_OFFSET,
                this.radius + TRUNK_BLOCK_OFFSET,
                fAngle,
                BRANCH_ORIGIN_TILT);

        fAngle = fAngle % 1.0;
        int insectMeta = 0;

        if (fAngle > INSECT_ANGLE_FOURTH_BOUNDARY || fAngle <= INSECT_ANGLE_FIRST_BOUNDARY) {
            insectMeta = INSECT_META_FIRST_FACE;
        } else if (fAngle <= INSECT_ANGLE_SECOND_BOUNDARY) {
            insectMeta = INSECT_META_SECOND_FACE;
        } else if (fAngle <= INSECT_ANGLE_THIRD_BOUNDARY) {
            insectMeta = INSECT_META_THIRD_FACE;
        } else if (fAngle <= INSECT_ANGLE_FOURTH_BOUNDARY) {
            insectMeta = INSECT_META_FOURTH_FACE;
        }

        addInsect(
                world,
                random.nextBoolean() ? TFBlocks.firefly : TFBlocks.cicada,
                insectMeta,
                bugSpot.posX,
                bugSpot.posY,
                bugSpot.posZ,
                sbb);
    }

    /**
     * Add an insect if we can at the position specified
     */
    private void addInsect(World world, Block blockID, int insectMeta, int posX, int posY, int posZ,
            StructureBoundingBox sbb) {
        int ox = this.getXWithOffset(posX, posZ);
        int oy = this.getYWithOffset(posY);
        int oz = this.getZWithOffset(posX, posZ);

        if (sbb.isVecInside(ox, oy, oz) && blockID != null && blockID.canPlaceBlockAt(world, ox, oy, oz)) {
            world.setBlock(ox, oy, oz, blockID, insectMeta, INSECT_BLOCK_UPDATE_FLAGS);
        }
    }

}
