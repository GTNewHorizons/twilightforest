package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class TFGenFallenSmallLog extends TFGenerator {

    private static final int MIN_LENGTH = 3;
    private static final int LENGTH_VARIATION = 4;
    private static final int CROSS_SECTION_WIDTH = 3;
    private static final int CLEARANCE_HEIGHT = 2;

    private static final int LOG_X_META = 4;
    private static final int LOG_Z_META = 8;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // determine direction
        boolean goingX = rand.nextBoolean();

        // length
        int length = rand.nextInt(LENGTH_VARIATION) + MIN_LENGTH;

        // check area clear
        // include the side branch on either side of the log, as well as its moss
        int width = goingX ? length : CROSS_SECTION_WIDTH;
        int depth = goingX ? CROSS_SECTION_WIDTH : length;
        if (y < 1 || y + CLEARANCE_HEIGHT > world.getHeight()
                || !isAreaSuitable(world, rand, x, y, z, width, CLEARANCE_HEIGHT, depth)) {
            return false;
        }

        // determine wood type
        int woodVariant = rand.nextInt(7);
        Block logBlock = woodVariant < 3 ? TFBlocks.log : Blocks.log;
        int woodMeta = woodVariant < 3 ? woodVariant : woodVariant - 3;
        int logMeta = woodMeta | (goingX ? LOG_X_META : LOG_Z_META);
        int branchMeta = woodMeta | (goingX ? LOG_Z_META : LOG_X_META);

        // check biome

        // make log
        for (int along = 0; along < length; along++) {
            int logX = x + (goingX ? along : 1);
            int logZ = z + (goingX ? 1 : along);
            setBlockAndMetadata(world, logX, y, logZ, logBlock, logMeta);
            if (rand.nextInt(3) > 0) {
                setBlockAndMetadata(world, logX, y + 1, logZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
            }
        }

        // possibly make branch
        if (rand.nextInt(3) > 0) {
            makeSideBranch(world, rand, x, y, z, length, goingX, logBlock, branchMeta);
        }

        return true;
    }

    private void makeSideBranch(World world, Random rand, int x, int y, int z, int length, boolean goingX,
            Block logBlock, int branchMeta) {
        int along;
        int across;
        if (goingX) {
            along = rand.nextInt(length);
            across = rand.nextBoolean() ? 2 : 0;
        } else {
            // preserve the original draw order for Z oriented side branches
            across = rand.nextBoolean() ? 2 : 0;
            along = rand.nextInt(length);
        }

        int branchX = x + (goingX ? along : across);
        int branchZ = z + (goingX ? across : along);
        setBlockAndMetadata(world, branchX, y, branchZ, logBlock, branchMeta);
        if (rand.nextBoolean()) {
            setBlockAndMetadata(world, branchX, y + 1, branchZ, TFBlocks.plant, BlockTFPlant.META_MOSSPATCH);
        }
    }
}
