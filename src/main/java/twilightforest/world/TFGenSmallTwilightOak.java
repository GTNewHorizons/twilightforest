package twilightforest.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class TFGenSmallTwilightOak extends TFTreeGenerator {

    private static final int HEIGHT_VARIATION = 3;
    private static final int CANOPY_DEPTH = 3;
    private static final int WORLD_HEIGHT_LIMIT = 256;

    /** The minimum height of a generated tree. */
    protected final int minTreeHeight;

    public TFGenSmallTwilightOak(boolean notify) {
        this(notify, 4);
    }

    public TFGenSmallTwilightOak(boolean notify, int minTreeHeight) {
        super(notify);
        this.minTreeHeight = minTreeHeight;

        treeBlock = TFBlocks.log;
        treeMeta = 0;
        branchMeta = treeMeta | 12;
        leafBlock = TFBlocks.leaves;
        leafMeta = 0;
        rootBlock = TFBlocks.root;
        rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = rand.nextInt(HEIGHT_VARIATION) + minTreeHeight;

        if (y < 1 || y + height + 1 > world.getHeight() || !hasRoomForTree(world, x, y, z, height)) {
            return false;
        }

        Block soil = world.getBlock(x, y - 1, z);
        Material soilMaterial = soil.getMaterial();
        if ((soilMaterial != Material.grass && soilMaterial != Material.ground)
                || y >= world.getHeight() - height - 1) {
            return false;
        }

        soil.onPlantGrow(world, x, y - 1, z, x, y, z);
        generateCanopy(world, rand, x, y, z, height);
        generateTrunk(world, x, y, z, height);
        return true;
    }

    private boolean hasRoomForTree(World world, int x, int y, int z, int height) {
        for (int blockY = y; blockY <= y + height + 1; blockY++) {
            int radius = 1;
            if (blockY == y) {
                radius = 0;
            }
            if (blockY >= y + height - 1) {
                radius = 2;
            }

            for (int blockX = x - radius; blockX <= x + radius; blockX++) {
                for (int blockZ = z - radius; blockZ <= z + radius; blockZ++) {
                    if (blockY < 0 || blockY >= WORLD_HEIGHT_LIMIT) {
                        return false;
                    }

                    Block block = world.getBlock(blockX, blockY, blockZ);
                    if (block != Blocks.air && !block.isLeaves(world, blockX, blockY, blockZ)
                            && block != Blocks.grass
                            && block != Blocks.dirt
                            && !block.isWood(world, blockX, blockY, blockZ)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void generateCanopy(World world, Random rand, int x, int y, int z, int height) {
        for (int leafY = y + height - CANOPY_DEPTH; leafY <= y + height; leafY++) {
            int layer = leafY - (y + height);
            int radius = 1 - layer / 2;

            for (int leafX = x - radius; leafX <= x + radius; leafX++) {
                int offsetX = leafX - x;
                for (int leafZ = z - radius; leafZ <= z + radius; leafZ++) {
                    int offsetZ = leafZ - z;
                    Block block = world.getBlock(leafX, leafY, leafZ);

                    // keep the random roll before the layer check so the original rng sequence is kept
                    if ((Math.abs(offsetX) != radius || Math.abs(offsetZ) != radius
                            || rand.nextInt(2) != 0 && layer != 0)
                            && (block == null || block.canBeReplacedByLeaves(world, leafX, leafY, leafZ))) {
                        setBlockAndMetadata(world, leafX, leafY, leafZ, leafBlock, leafMeta);
                    }
                }
            }
        }
    }

    private void generateTrunk(World world, int x, int y, int z, int height) {
        for (int offsetY = 0; offsetY < height; offsetY++) {
            Block block = world.getBlock(x, y + offsetY, z);
            if (block == Blocks.air || block == null || block.isLeaves(world, x, y + offsetY, z)) {
                setBlockAndMetadata(world, x, y + offsetY, z, treeBlock, treeMeta);
            }
        }
    }
}
