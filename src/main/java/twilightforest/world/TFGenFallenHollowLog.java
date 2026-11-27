package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class TFGenFallenHollowLog extends TFGenerator {

    private static final int CORE_START = 3;
    private static final int CORE_LENGTH = 4;
    private static final int CROSS_SECTION_WIDTH = 4;
    private static final int CLEARANCE_LENGTH = 8;
    private static final int CLEARANCE_HEIGHT = 4;

    private static final int[] JAGGED_CROSS_POSITIONS = { 0, 3, 0, 3, 1, 2 };
    private static final int[] JAGGED_HEIGHTS = { 0, 0, 1, 1, 2, 2 };

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        return rand.nextBoolean() ? makeLog4Z(world, rand, x, y, z) : makeLog4X(world, rand, x, y, z);
    }

    private boolean makeLog4Z(World world, Random rand, int x, int y, int z) {
        // +Z 4x4 log
        return generateLog(world, rand, x, y, z, true);
    }

    /**
     * Make a 4x4 log in the +X direction
     */
    private boolean makeLog4X(World world, Random rand, int x, int y, int z) {
        // +X 4x4 log
        return generateLog(world, rand, x, y, z, false);
    }

    private boolean generateLog(World world, Random rand, int x, int y, int z, boolean alongZ) {
        // the ends reach one block past the core on either side, moss can reach y + 3.
        int clearanceX = alongZ ? x : x + 1;
        int clearanceZ = alongZ ? z + 1 : z;
        int clearanceWidth = alongZ ? CROSS_SECTION_WIDTH : CLEARANCE_LENGTH;
        int clearanceDepth = alongZ ? CLEARANCE_LENGTH : CROSS_SECTION_WIDTH;
        if (y < 1 || y + CLEARANCE_HEIGHT > world.getHeight()
                || !isAreaSuitable(
                        world,
                        rand,
                        clearanceX,
                        y,
                        clearanceZ,
                        clearanceWidth,
                        CLEARANCE_HEIGHT,
                        clearanceDepth)) {
            return false;
        }

        int logMeta = alongZ ? 8 : 4;

        // jaggy parts
        makeJaggedEnds(world, rand, x, y, z, alongZ, logMeta);

        // center
        for (int along = CORE_START; along < CORE_START + CORE_LENGTH; along++) {
            // floor
            makeFloorBlock(world, rand, x, y, z, along, 1, alongZ, logMeta);
            makeFloorBlock(world, rand, x, y, z, along, 2, alongZ, logMeta);

            // log part
            placeBlock(world, x, y, z, along, 0, 0, alongZ, TFBlocks.log, logMeta);
            placeBlock(world, x, y, z, along, 3, 0, alongZ, TFBlocks.log, logMeta);
            placeBlock(world, x, y, z, along, 0, 1, alongZ, TFBlocks.log, logMeta);
            placeBlock(world, x, y, z, along, 3, 1, alongZ, TFBlocks.log, logMeta);
            placeBlock(world, x, y, z, along, 1, 2, alongZ, TFBlocks.log, logMeta);
            placeBlock(world, x, y, z, along, 2, 2, alongZ, TFBlocks.log, logMeta);
            if (rand.nextBoolean()) {
                placeBlock(world, x, y, z, along, 1, 3, alongZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
            }
            if (rand.nextBoolean()) {
                placeBlock(world, x, y, z, along, 2, 3, alongZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
            }
        }

        // a few leaves?
        int leafOffset = rand.nextInt(3) + 2;
        boolean positiveSide = rand.nextBoolean();
        for (int along = leafOffset; along < leafOffset + 3; along++) {
            if (rand.nextBoolean()) {
                placeLeaf(world, x, y, z, along, positiveSide ? 3 : 0, 2, alongZ);
                if (rand.nextBoolean()) {
                    placeLeaf(world, x, y, z, along, positiveSide ? 3 : 0, 3, alongZ);
                }
                if (rand.nextBoolean()) {
                    placeLeaf(world, x, y, z, along, positiveSide ? 4 : -1, 2, alongZ);
                }
            }
        }

        // firefly
        placeBlock(world, x, y, z, rand.nextInt(4) + CORE_START, positiveSide ? 0 : 3, 2, alongZ, TFBlocks.firefly, 0);
        return true;
    }

    private void makeJaggedEnds(World world, Random rand, int x, int y, int z, boolean alongZ, int logMeta) {
        // keep the negative ends 6 rng draws before the positive ends 6 draws
        for (int end = 0; end < 2; end++) {
            boolean positiveEnd = end == 1;
            for (int i = 0; i < JAGGED_CROSS_POSITIONS.length; i++) {
                int length = rand.nextInt(3);
                int start = positiveEnd ? CORE_START + CORE_LENGTH : CORE_START - length;
                for (int along = start; along < start + length; along++) {
                    placeBlock(
                            world,
                            x,
                            y,
                            z,
                            along,
                            JAGGED_CROSS_POSITIONS[i],
                            JAGGED_HEIGHTS[i],
                            alongZ,
                            TFBlocks.log,
                            logMeta);
                }
            }
        }
    }

    private void makeFloorBlock(World world, Random rand, int x, int y, int z, int along, int across, boolean alongZ,
            int logMeta) {
        if (rand.nextBoolean()) {
            placeBlock(world, x, y, z, along, across, -1, alongZ, TFBlocks.log, logMeta);
            if (rand.nextBoolean()) {
                placeBlock(world, x, y, z, along, across, 0, alongZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
            }
        } else {
            placeBlock(world, x, y, z, along, across, -1, alongZ, Blocks.dirt, 0);
            placeBlock(world, x, y, z, along, across, 0, alongZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
        }
    }

    // local coordinates let both orientations share the same block and rng order
    private void placeBlock(World world, int x, int y, int z, int along, int across, int height, boolean alongZ,
            Block block, int metadata) {
        setBlockAndMetadata(
                world,
                x + (alongZ ? across : along),
                y + height,
                z + (alongZ ? along : across),
                block,
                metadata);
    }

    private void placeLeaf(World world, int x, int y, int z, int along, int across, int height, boolean alongZ) {
        putLeafBlock(
                world,
                x + (alongZ ? across : along),
                y + height,
                z + (alongZ ? along : across),
                TFBlocks.leaves,
                0);
    }
}
