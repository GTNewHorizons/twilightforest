package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import twilightforest.block.TFBlocks;

/**
 * This is a lame copy of WorldGenLargeTree, since I'm too lazy to make my own tree and all the methods in
 * WorldGenLargeTree are package-only
 *
 */
public class TFGenLargeRainboak extends TFTreeGenerator {

    private static final int COORDINATES = 3;
    private static final int MIN_UNOBSTRUCTED_HEIGHT = 6;

    /**
     * Contains three sets of two values that provide complimentary indices for a given 'major' index - 1 and 2 for 0, 0
     * and 2 for 1, and 0 and 1 for 2.
     */
    static final byte[] otherCoordPairs = new byte[] { (byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1 };

    /** random seed for GenBigTree */
    FastRandom rand = new FastRandom(0L);

    /** Reference to the World object. */
    World worldObj;
    int[] basePos = new int[] { 0, 0, 0 };
    int heightLimit = 0;
    int height;
    double heightAttenuation = 0.618D;
    double branchSlope = 0.381D;
    double scaleWidth = 1.0D;
    double leafDensity = 1.0D;

    /**
     * Currently always 1, can be set to 2 in the class constructor to generate a double-sized tree trunk for big trees.
     */
    int trunkSize = 1;

    /**
     * Sets the limit of the random value used to initialize the height limit.
     */
    int heightLimitLimit = 12;

    /**
     * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
     */
    int leafDistanceLimit = 4;

    /** Contains a list of a points at which to generate groups of leaves. */
    int[][] leafNodes;

    public TFGenLargeRainboak(boolean notify) {
        super(notify);
    }

    public TFGenLargeRainboak() {
        this(false);
    }

    /**
     * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
     */
    void generateLeafNodeList() {
        height = (int) ((double) heightLimit * heightAttenuation);
        if (height >= heightLimit) {
            height = heightLimit - 1;
        }

        int nodesPerLayer = Math.max(1, (int) (1.382D + Math.pow(leafDensity * (double) heightLimit / 13.0D, 2.0D)));
        int[][] candidates = new int[nodesPerLayer * heightLimit][4];
        int currentY = basePos[1] + heightLimit - leafDistanceLimit;
        int trunkTopY = basePos[1] + height;
        int layer = currentY - basePos[1];
        int nodeCount = 1;

        candidates[0][0] = basePos[0];
        candidates[0][1] = currentY;
        candidates[0][2] = basePos[2];
        candidates[0][3] = trunkTopY;
        --currentY;

        while (layer >= 0) {
            float radius = layerSize(layer);
            if (radius >= 0.0F) {
                for (int i = 0; i < nodesPerLayer; ++i) {
                    double horizontalOffset = scaleWidth * (double) radius * ((double) rand.nextFloat() + 0.328D);
                    double angle = (double) rand.nextFloat() * 2.0D * Math.PI;
                    int nodeX = MathHelper
                            .floor_double(horizontalOffset * Math.sin(angle) + (double) basePos[0] + 0.5D);
                    int nodeZ = MathHelper
                            .floor_double(horizontalOffset * Math.cos(angle) + (double) basePos[2] + 0.5D);
                    int[] node = new int[] { nodeX, currentY, nodeZ };
                    int[] nodeTop = new int[] { nodeX, currentY + leafDistanceLimit, nodeZ };

                    if (checkBlockLine(node, nodeTop) != -1) {
                        continue;
                    }

                    int[] branchBase = new int[] { basePos[0], basePos[1], basePos[2] };
                    double distance = Math.sqrt(
                            Math.pow(Math.abs(basePos[0] - node[0]), 2.0D)
                                    + Math.pow(Math.abs(basePos[2] - node[2]), 2.0D));
                    double drop = distance * branchSlope;
                    branchBase[1] = (double) node[1] - drop > (double) trunkTopY ? trunkTopY
                            : (int) ((double) node[1] - drop);

                    if (checkBlockLine(branchBase, node) == -1) {
                        candidates[nodeCount][0] = nodeX;
                        candidates[nodeCount][1] = currentY;
                        candidates[nodeCount][2] = nodeZ;
                        candidates[nodeCount][3] = branchBase[1];
                        ++nodeCount;
                    }
                }
            }
            --currentY;
            --layer;
        }

        leafNodes = new int[nodeCount][4];
        System.arraycopy(candidates, 0, leafNodes, 0, nodeCount);
    }

    void genTreeLayer(int x, int y, int z, float radius, byte axis, Block leaves, int meta) {
        int roundedRadius = (int) ((double) radius + 0.618D);
        byte firstAxis = otherCoordPairs[axis];
        byte secondAxis = otherCoordPairs[axis + 3];
        int[] center = new int[] { x, y, z };
        int[] position = new int[] { 0, 0, 0 };
        position[axis] = center[axis];

        for (int offsetA = -roundedRadius; offsetA <= roundedRadius; ++offsetA) {
            position[firstAxis] = center[firstAxis] + offsetA;
            for (int offsetB = -roundedRadius; offsetB <= roundedRadius; ++offsetB) {
                double distanceSquared = Math.pow((double) Math.abs(offsetA) + 0.5D, 2.0D)
                        + Math.pow((double) Math.abs(offsetB) + 0.5D, 2.0D);
                if (distanceSquared > (double) (radius * radius)) {
                    continue;
                }

                position[secondAxis] = center[secondAxis] + offsetB;
                Block current = worldObj.getBlock(position[0], position[1], position[2]);
                if (current == Blocks.air
                        || current.canBeReplacedByLeaves(worldObj, position[0], position[1], position[2])) {
                    setBlockAndMetadata(worldObj, position[0], position[1], position[2], leaves, meta);
                }
            }
        }
    }

    /**
     * Gets the rough size of a layer of the tree.
     */
    float layerSize(int layer) {
        if ((double) layer < (double) ((float) heightLimit) * 0.3D) {
            return -1.618F;
        }

        float halfHeight = (float) heightLimit / 2.0F;
        float distanceFromMiddle = (float) heightLimit / 2.0F - (float) layer;
        float radius;
        if (distanceFromMiddle == 0.0F) {
            radius = halfHeight;
        } else if (Math.abs(distanceFromMiddle) >= halfHeight) {
            radius = 0.0F;
        } else {
            radius = (float) Math
                    .sqrt(Math.pow(Math.abs(halfHeight), 2.0D) - Math.pow(Math.abs(distanceFromMiddle), 2.0D));
        }
        return radius * 0.5F;
    }

    float leafSize(int layer) {
        return layer >= 0 && layer < leafDistanceLimit ? (layer != 0 && layer != leafDistanceLimit - 1 ? 3.0F : 2.0F)
                : -1.0F;
    }

    /**
     * Generates the leaves surrounding an individual entry in the leafNodes list.
     */
    void generateLeafNode(int x, int y, int z) {
        for (int leafY = y; leafY < y + leafDistanceLimit; ++leafY) {
            genTreeLayer(x, leafY, z, leafSize(leafY - y), (byte) 1, TFBlocks.leaves, 3);
        }
    }

    /**
     * Places a line of the specified block ID into the world from the first coordinate triplet to the second.
     */
    void placeBlockLine(int[] start, int[] end, Block log) {
        int[] delta = new int[COORDINATES];
        byte majorAxis = getMajorAxis(start, end, delta);
        if (delta[majorAxis] == 0) {
            return;
        }

        byte firstAxis = otherCoordPairs[majorAxis];
        byte secondAxis = otherCoordPairs[majorAxis + 3];
        int direction = delta[majorAxis] > 0 ? 1 : -1;
        double firstSlope = (double) delta[firstAxis] / (double) delta[majorAxis];
        double secondSlope = (double) delta[secondAxis] / (double) delta[majorAxis];
        int[] position = new int[COORDINATES];

        for (int step = 0, endStep = delta[majorAxis] + direction; step != endStep; step += direction) {
            position[majorAxis] = MathHelper.floor_double((double) (start[majorAxis] + step) + 0.5D);
            position[firstAxis] = MathHelper
                    .floor_double((double) start[firstAxis] + (double) step * firstSlope + 0.5D);
            position[secondAxis] = MathHelper
                    .floor_double((double) start[secondAxis] + (double) step * secondSlope + 0.5D);

            int deltaX = Math.abs(position[0] - start[0]);
            int deltaZ = Math.abs(position[2] - start[2]);
            int largestHorizontalDelta = Math.max(deltaX, deltaZ);
            int metadata = 0;
            if (largestHorizontalDelta > 0) {
                if (deltaX == largestHorizontalDelta) {
                    metadata = 4;
                } else if (deltaZ == largestHorizontalDelta) {
                    metadata = 8;
                }
            }
            setBlockAndMetadata(worldObj, position[0], position[1], position[2], log, metadata);
        }
    }

    /**
     * Generates the leaf portion of the tree as specified by the leafNodes list.
     */
    void generateLeaves() {
        for (int[] node : leafNodes) {
            generateLeafNode(node[0], node[1], node[2]);
        }
    }

    /**
     * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
     */
    boolean leafNodeNeedsBase(int relativeHeight) {
        return (double) relativeHeight >= (double) heightLimit * 0.2D;
    }

    /**
     * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
     * field that is always 1 to 2.
     */
    void generateTrunk() {
        int[] start = new int[] { basePos[0], basePos[1], basePos[2] };
        int[] end = new int[] { basePos[0], basePos[1] + height, basePos[2] };
        placeBlockLine(start, end, TFBlocks.log);

        if (trunkSize == 2) {
            ++start[0];
            ++end[0];
            placeBlockLine(start, end, TFBlocks.log);
            ++start[2];
            ++end[2];
            placeBlockLine(start, end, TFBlocks.log);
            --start[0];
            --end[0];
            placeBlockLine(start, end, TFBlocks.log);
        }
    }

    /**
     * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
     */
    void generateLeafNodeBases() {
        int[] branchStart = new int[] { basePos[0], basePos[1], basePos[2] };
        for (int[] node : leafNodes) {
            int[] branchEnd = new int[] { node[0], node[1], node[2] };
            branchStart[1] = node[3];
            int relativeHeight = branchStart[1] - basePos[1];
            if (leafNodeNeedsBase(relativeHeight)) {
                placeBlockLine(branchStart, branchEnd, TFBlocks.log);
            }
        }
    }

    /**
     * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
     * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
     */
    int checkBlockLine(int[] start, int[] end) {
        int[] delta = new int[COORDINATES];
        byte majorAxis = getMajorAxis(start, end, delta);
        if (delta[majorAxis] == 0) {
            return -1;
        }

        byte firstAxis = otherCoordPairs[majorAxis];
        byte secondAxis = otherCoordPairs[majorAxis + 3];
        int direction = delta[majorAxis] > 0 ? 1 : -1;
        double firstSlope = (double) delta[firstAxis] / (double) delta[majorAxis];
        double secondSlope = (double) delta[secondAxis] / (double) delta[majorAxis];
        int[] position = new int[COORDINATES];
        int step = 0;
        int endStep = delta[majorAxis] + direction;

        for (; step != endStep; step += direction) {
            position[majorAxis] = start[majorAxis] + step;
            position[firstAxis] = MathHelper.floor_double((double) start[firstAxis] + (double) step * firstSlope);
            position[secondAxis] = MathHelper.floor_double((double) start[secondAxis] + (double) step * secondSlope);
            Block block = worldObj.getBlock(position[0], position[1], position[2]);

            // other leaves must also count as passable foliage
            if (block != Blocks.air && !block.isLeaves(worldObj, position[0], position[1], position[2])) {
                break;
            }
        }

        return step == endStep ? -1 : Math.abs(step);
    }

    private static byte getMajorAxis(int[] start, int[] end, int[] delta) {
        byte majorAxis = 0;
        for (byte axis = 0; axis < COORDINATES; ++axis) {
            delta[axis] = end[axis] - start[axis];
            if (Math.abs(delta[axis]) > Math.abs(delta[majorAxis])) {
                majorAxis = axis;
            }
        }
        return majorAxis;
    }

    /**
     * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
     * limit, is valid.
     */
    boolean validTreeLocation() {
        int[] start = new int[] { basePos[0], basePos[1], basePos[2] };
        int[] end = new int[] { basePos[0], basePos[1] + heightLimit - 1, basePos[2] };
        Material soil = worldObj.getBlock(basePos[0], basePos[1] - 1, basePos[2]).getMaterial();
        if (soil != Material.grass && soil != Material.ground) {
            return false;
        }

        int obstruction = checkBlockLine(start, end);
        if (obstruction == -1) {
            return true;
        }
        if (obstruction < MIN_UNOBSTRUCTED_HEIGHT) {
            return false;
        }
        heightLimit = obstruction;
        return true;
    }

    /**
     * Rescales the generator settings, only used in WorldGenBigTree
     */
    public void setScale(double heightScale, double widthScale, double leafScale) {
        heightLimitLimit = (int) (heightScale * 12.0D);
        if (heightScale > 0.5D) {
            leafDistanceLimit = 5;
        }
        scaleWidth = widthScale;
        leafDensity = leafScale;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        worldObj = world;
        long seed = random.nextLong();
        rand.setSeed(seed);
        basePos[0] = x;
        basePos[1] = y;
        basePos[2] = z;

        // a gen can be reused, each tree needs its own height roll
        heightLimit = 5 + rand.nextInt(heightLimitLimit);
        if (!validTreeLocation()) {
            return false;
        }

        generateLeafNodeList();
        generateLeaves();
        generateTrunk();
        generateLeafNodeBases();
        return true;
    }
}
