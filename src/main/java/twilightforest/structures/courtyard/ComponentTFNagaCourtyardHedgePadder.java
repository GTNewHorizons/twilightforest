package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardHedgePadder extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings("unused")
    public ComponentTFNagaCourtyardHedgePadder() {
        super();
    }

    public ComponentTFNagaCourtyardHedgePadder(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y + 1, z - 1, x + 4, y + 6, z + 1);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        adjustGroundHeight(world, structureBoundingBox);
        buildHedge(world, structureBoundingBox);
        addFoliage(world, random, structureBoundingBox);
        replaceSurroundingGround(world, structureBoundingBox);
        return true;
    }

    private void adjustGroundHeight(World world, StructureBoundingBox box) {
        int groundAir = 0;
        int undergroundAir = 0;
        for (int x = 0; x < 5; x++) {
            if (getBlockAtCurrentPosition(world, x, -1, 1, box) == Blocks.air) groundAir++;
            if (getBlockAtCurrentPosition(world, x, -2, 1, box) == Blocks.air) undergroundAir++;
        }
        /*
         * || this.getBlockAtCurrentPosition(world, 0, -1, 1, structureBoundingBox) == TFBlocks.nagastoneStairsRight
         */
        if (groundAir > undergroundAir) boundingBox.minY--;
    }

    private void buildHedge(World world, StructureBoundingBox box) {
        fillWithMetadataBlocks(
                world,
                box,
                0,
                0,
                0,
                0,
                0,
                2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(
                world,
                box,
                4,
                0,
                0,
                4,
                0,
                2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(world, box, 1, 0, 0, 3, 0, 2, TFBlocks.log, 12, TFBlocks.log, 12, false);
        fillWithMetadataBlocks(world, box, 1, 1, 0, 3, 4, 2, TFBlocks.leaves, 4, TFBlocks.leaves, 4, false);
        fillWithAir(world, box, 2, 1, -1, 2, 3, 3);
    }

    private void addFoliage(World world, Random random, StructureBoundingBox box) {
        partiallyFillWithMetadataBlocks(
                world,
                box,
                0,
                1,
                0,
                0,
                4,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                4,
                1,
                0,
                4,
                4,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                1,
                5,
                0,
                3,
                5,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
    }

    private void replaceSurroundingGround(World world, StructureBoundingBox box) {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Block west = getBlockAtCurrentPosition(world, x - 1, 0, z, box);
                Block east = getBlockAtCurrentPosition(world, x + 1, 0, z, box);
                Block north = getBlockAtCurrentPosition(world, x, 0, z - 1, box);
                Block south = getBlockAtCurrentPosition(world, x, 0, z + 1, box);
                if (isGroundBlock(west) || isGroundBlock(east) || isGroundBlock(north) || isGroundBlock(south)) {
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, x, 0, z, box);
                }
            }
        }
    }

    private static boolean isGroundBlock(Block block) {
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.stonebrick;
    }
}
