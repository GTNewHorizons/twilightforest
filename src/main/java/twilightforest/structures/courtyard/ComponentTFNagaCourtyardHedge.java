package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardHedge extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings("unused")
    public ComponentTFNagaCourtyardHedge() {
        super();
    }

    public ComponentTFNagaCourtyardHedge(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y + 1, z, x + 4, y + 6, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        adjustGroundHeight(world, structureBoundingBox);
        buildHedge(world, structureBoundingBox);
        replaceSurroundingGround(world, structureBoundingBox);
        addFoliage(world, random, structureBoundingBox);
        return true;
    }

    private void adjustGroundHeight(World world, StructureBoundingBox box) {
        int groundAir = 0;
        int undergroundAir = 0;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if (getBlockAtCurrentPosition(world, x, -1, z, box) == Blocks.air) groundAir++;
                if (getBlockAtCurrentPosition(world, x, -2, z, box) == Blocks.air) undergroundAir++;
            }
        }
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
                3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(
                world,
                box,
                1,
                0,
                0,
                4,
                0,
                0,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs1,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(
                world,
                box,
                4,
                0,
                1,
                4,
                0,
                4,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(
                world,
                box,
                0,
                0,
                4,
                3,
                0,
                4,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs0,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(world, box, 1, 0, 1, 3, 0, 3, TFBlocks.log, 12, TFBlocks.log, 12, false);
        fillWithMetadataBlocks(world, box, 1, 1, 1, 3, 4, 3, TFBlocks.leaves, 4, TFBlocks.leaves, 4, false);
        fillWithAir(world, box, 2, 1, 2, 2, 3, 2);
    }

    private void replaceSurroundingGround(World world, StructureBoundingBox box) {
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
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

    private void addFoliage(World world, Random random, StructureBoundingBox box) {
        partiallyFillWithMetadataBlocks(
                world,
                box,
                0,
                1,
                1,
                0,
                4,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                1,
                1,
                0,
                3,
                4,
                0,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                4,
                1,
                1,
                4,
                4,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                1,
                1,
                4,
                3,
                4,
                4,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        partiallyFillWithMetadataBlocks(
                world,
                box,
                1,
                5,
                1,
                3,
                5,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
    }
}
