package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceDuctPart extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardTerraceDuctPart() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceDuctPart(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 3, y + 2, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // 1 layer
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                0,
                0,
                3,
                0,
                0,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                0,
                4,
                3,
                0,
                4,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                3,
                0,
                1,
                3,
                0,
                3,
                Blocks.stained_hardened_clay,
                15,
                Blocks.stained_hardened_clay,
                15,
                false);

        // 2 layer
        this.fillWithBlocks(world, structureBoundingBox, 1, 1, 0, 3, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
        this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 1, 3, Blocks.iron_bars, Blocks.iron_bars, false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                1,
                1,
                1,
                1,
                3,
                Blocks.stone_slab,
                13,
                Blocks.stone_slab,
                13,
                false);

        // 3 layer
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                2,
                0,
                2,
                2,
                4,
                Blocks.stone_slab,
                5,
                Blocks.stone_slab,
                5,
                false);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                1,
                2,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                1,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                1,
                2,
                3,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                1,
                2,
                4,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 1, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 3, structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                3,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                3,
                2,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                3,
                2,
                3,
                structureBoundingBox);

        // sneks
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                0,
                0,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                0,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                0,
                2,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                0,
                2,
                1,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                0,
                0,
                4,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                0,
                1,
                4,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                0,
                2,
                4,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                0,
                2,
                3,
                structureBoundingBox);

        return true;
    }
}
