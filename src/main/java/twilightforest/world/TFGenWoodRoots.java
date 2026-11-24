package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class TFGenWoodRoots extends TFGenerator {

    private static final int MAX_HORIZONTAL_SPREAD = 6;

    private final Block rootBlock = TFBlocks.root;
    private final int rootMeta = BlockTFRoots.ROOT_META;
    private final Block oreBlock = TFBlocks.root;
    private final int oreMeta = BlockTFRoots.OREROOT_META;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // start must be in stone
        if (world.getBlock(x, y, z) != Blocks.stone) {
            return false;
        }

        float length = rand.nextFloat() * 6.0F + rand.nextFloat() * 6.0F + 4.0F;
        if (length > y) {
            length = y;
        }

        // tilt between 0.6 and 0.9
        float tilt = 0.6F + rand.nextFloat() * 0.3F;

        return drawRoot(world, rand, x, y, z, length, rand.nextFloat(), tilt);
    }

    private boolean drawRoot(World world, Random rand, int x, int y, int z, float length, float angle, float tilt) {
        // put origin at where we start
        return drawRoot(world, rand, x, z, x, y, z, length, angle, tilt);
    }

    private boolean drawRoot(World world, Random rand, int originX, int originZ, int x, int y, int z, float length,
            float angle, float tilt) {
        // generate a direction and a length
        int[] dest = translate(x, y, z, length, angle, tilt);

        // restrict both horizontal coordinates to 6 blocks from the original root
        dest[0] = Math.max(originX - MAX_HORIZONTAL_SPREAD, Math.min(originX + MAX_HORIZONTAL_SPREAD, dest[0]));
        dest[2] = Math.max(originZ - MAX_HORIZONTAL_SPREAD, Math.min(originZ + MAX_HORIZONTAL_SPREAD, dest[2]));

        // end must be in stone
        if (world.getBlock(dest[0], dest[1], dest[2]) != Blocks.stone) {
            return false;
        }

        // if both the start and the end are in stone, put a root there
        ChunkCoordinates[] lineArray = getBresehnamArrayCoords(x, y, z, dest[0], dest[1], dest[2]);
        for (ChunkCoordinates coord : lineArray) {
            this.placeRootBlock(world, coord.posX, coord.posY, coord.posZ, rootBlock, rootMeta);
        }

        // if we are long enough, make either another root or an oreball
        if (length > 8 && rand.nextInt(3) > 0) {
            // length > 8, usually split off into another root half as long
            int[] nextSrc = translate(x, y, z, length / 2, angle, tilt);
            float nextAngle = (angle + 0.25F + (rand.nextFloat() * 0.5F)) % 1.0F;
            float nextTilt = 0.6F + rand.nextFloat() * 0.3F;
            drawRoot(
                    world,
                    rand,
                    originX,
                    originZ,
                    nextSrc[0],
                    nextSrc[1],
                    nextSrc[2],
                    length / 2.0F,
                    nextAngle,
                    nextTilt);
        }

        if (length > 6 && rand.nextInt(4) == 0) {
            // length > 6, potentially make oreball
            int[] ballSrc = translate(x, y, z, length / 2, angle, tilt);
            int[] ballDest = translate(ballSrc[0], ballSrc[1], ballSrc[2], 1.5, (angle + 0.5F) % 1.0F, 0.75);

            placeRootBlock(world, ballSrc[0], ballSrc[1], ballSrc[2], oreBlock, oreMeta);
            placeRootBlock(world, ballSrc[0], ballSrc[1], ballDest[2], oreBlock, oreMeta);
            placeRootBlock(world, ballDest[0], ballSrc[1], ballSrc[2], oreBlock, oreMeta);
            placeRootBlock(world, ballDest[0], ballSrc[1], ballDest[2], oreBlock, oreMeta);
            placeRootBlock(world, ballSrc[0], ballDest[1], ballSrc[2], oreBlock, oreMeta);
            placeRootBlock(world, ballSrc[0], ballDest[1], ballDest[2], oreBlock, oreMeta);
            placeRootBlock(world, ballDest[0], ballDest[1], ballSrc[2], oreBlock, oreMeta);
            placeRootBlock(world, ballDest[0], ballDest[1], ballDest[2], oreBlock, oreMeta);
        }
        return true;
    }

    /**
     * Function used to actually place root blocks if they're not going to break anything important
     */
    protected void placeRootBlock(World world, int x, int y, int z, Block rootBlock2, int meta) {
        if (TFTreeGenerator.canRootGrowIn(world, x, y, z)) {
            this.setBlockAndMetadata(world, x, y, z, rootBlock2, meta);
        }
    }
}
