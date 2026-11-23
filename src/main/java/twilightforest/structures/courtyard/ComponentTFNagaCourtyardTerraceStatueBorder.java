package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceStatueBorder extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardTerraceStatueBorder() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceStatueBorder(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y - 1, z - 1, x + 5, y + 5, z + 7);
        normalizeRotatedFootprint();
    }

    @Override
    protected void normalizeRotatedFootprint() {
        fitRotatedFootprint(6, 9);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        buildGroundStairs(world, box);
        buildBorders(world, box);
        buildColumns(world, box);
        return true;
    }

    private void buildGroundStairs(World world, StructureBoundingBox box) {
        // ground stairs
        fillWithMetadataBlocks(
                world,
                box,
                4,
                0,
                1,
                4,
                0,
                7,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                true);
        fillWithMetadataBlocks(
                world,
                box,
                4,
                0,
                2,
                4,
                0,
                3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                true);
        fillWithMetadataBlocks(
                world,
                box,
                4,
                0,
                5,
                4,
                0,
                6,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs3,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs3,
                true);
    }

    private void buildBorders(World world, StructureBoundingBox box) {
        // borders
        fillWithMetadataBlocks(
                world,
                box,
                0,
                1,
                1,
                0,
                1,
                7,
                TFBlocks.nagastoneBody,
                1,
                TFBlocks.nagastoneBody,
                1,
                true);

        int westMetadata = BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest);
        for (int i = 1; i <= 7; i++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, westMetadata, 1, 1, i, box);
        }

        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 1, 1, 0, box);
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                1,
                1,
                1,
                box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 1, 1, 2, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 1, 1, 6, box);
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                1,
                1,
                7,
                box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 1, 1, 8, box);
    }

    private void buildColumns(World world, StructureBoundingBox box) {
        // columns
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, 4, 0, 0, box);
        fillWithMetadataBlocks(
                world,
                box,
                4,
                1,
                0,
                4,
                5,
                0,
                TFBlocks.nagastonePillar,
                0,
                TFBlocks.nagastonePillar,
                0,
                false);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 4, 6, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.fence, 0, 5, 4, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 4, 1, box);
        placeBlockAtCurrentPosition(world, Blocks.torch, 5, 5, 5, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.torch, 5, 4, 5, 1, box);
    }
}
