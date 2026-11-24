package twilightforest.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import twilightforest.block.BlockTFMagicLogSpecial;
import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class TFGenMinersTree extends TFTreeGenerator {

    public TFGenMinersTree() {
        this(false);
    }

    public TFGenMinersTree(boolean notify) {
        super(notify);
        this.treeBlock = TFBlocks.magicLog;
        this.treeMeta = 2;
        this.branchMeta = treeMeta | 12;
        this.leafBlock = TFBlocks.magicLeaves;
        this.leafMeta = 2;
        this.rootBlock = TFBlocks.root;
        this.rootMeta = BlockTFRoots.ROOT_META;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {

        // check soil
        Material materialUnder = world.getBlock(x, y - 1, z).getMaterial();
        if (y < 1 || (materialUnder != Material.grass && materialUnder != Material.ground)
                || y >= world.getHeight() - 12) {
            return false;
        }

        // 9 block high trunk
        for (int dy = 0; dy < 10; dy++) {
            setBlockAndMetadata(world, x, y + dy, z, treeBlock, branchMeta);
        }

        // branches with leaf blocks
        generateBranchSide(world, x, y, z, 1);
        generateBranchSide(world, x, y, z, -1);

        // place minewood core
        setBlockAndMetadata(world, x, y + 1, z, TFBlocks.magicLogSpecial, BlockTFMagicLogSpecial.META_MINE);
        world.scheduleBlockUpdate(x, y + 1, z, TFBlocks.magicLogSpecial, TFBlocks.magicLogSpecial.tickRate(world));

        // root bulb
        if (hasAirAround(world, x, y - 1, z)) {
            this.setBlockAndMetadata(world, x, y - 1, z, treeBlock, treeMeta);
        } else {
            this.setBlockAndMetadata(world, x, y - 1, z, rootBlock, rootMeta);
        }

        // roots!
        int numRoots = 3 + rand.nextInt(2);
        double offset = rand.nextDouble();
        for (int b = 0; b < numRoots; b++) {
            buildRoot(world, x, y, z, offset, b);
        }
        return true;
    }

    private void generateBranchSide(World world, int x, int y, int z, int direction) {
        // keep the upper to lower placement order for each side of the tree
        putBranchWithLeaves(world, x, y + 9, z + direction, true);
        putBranchWithLeaves(world, x, y + 9, z + 2 * direction, false);
        putBranchWithLeaves(world, x, y + 8, z + 3 * direction, false);
        putBranchWithLeaves(world, x, y + 7, z + 4 * direction, false);
        putBranchWithLeaves(world, x, y + 6, z + 5 * direction, false);
    }

    protected void putBranchWithLeaves(World world, int bx, int by, int bz, boolean bushy) {
        setBlockAndMetadata(world, bx, by, bz, treeBlock, branchMeta);

        for (int lx = -1; lx <= 1; lx++) {
            for (int ly = -1; ly <= 1; ly++) {
                for (int lz = -1; lz <= 1; lz++) {
                    if (!bushy && ly != 0 && lx != 0) {
                        continue;
                    }
                    putLeafBlock(world, bx + lx, by + ly, bz + lz, leafBlock, leafMeta);
                }
            }
        }
    }
}
