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
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y - 1, z - 1, x + 5, y + 5, z + 7);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {

        // ground stairs
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
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
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
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
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
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

        // borders
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
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
        for (int i = 1; i <= 7; i++) this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                0,
                1,
                i,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                1,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                1,
                1,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                1,
                1,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                1,
                1,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                1,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                1,
                1,
                8,
                structureBoundingBox);

        // columns
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, 4, 0, 0, structureBoundingBox);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
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
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 4, 6, 0, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 5, 4, 0, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 4, 1, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 5, 5, 0, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 4, 5, 1, structureBoundingBox);

        return true;
    }
}
