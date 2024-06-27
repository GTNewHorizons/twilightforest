package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceBrazier extends ComponentTFNagaCourtyardTerraceAbstract {

    public ComponentTFNagaCourtyardTerraceBrazier() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceBrazier(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        super.addComponentParts(world, random, structureBoundingBox);

        // ground layer
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                6,
                0,
                6,
                10,
                0,
                10,
                Blocks.stonebrick,
                Blocks.stonebrick,
                true);
        this.placeBlockAtCurrentPosition(world, Blocks.grass, 0, 6, 0, 6, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.grass, 0, 6, 0, 10, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.grass, 0, 10, 0, 6, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.grass, 0, 10, 0, 10, structureBoundingBox);
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                8,
                0,
                7,
                8,
                0,
                9,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                7,
                0,
                8,
                9,
                0,
                8,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                6,
                0,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                10,
                0,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                8,
                0,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                8,
                0,
                10,
                structureBoundingBox);

        // ground + 1 layer
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                7,
                1,
                7,
                9,
                1,
                9,
                TFBlocks.nagastonePillar,
                TFBlocks.nagastonePillar,
                false);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                7,
                1,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs7,
                9,
                1,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs5,
                8,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs4,
                8,
                1,
                9,
                structureBoundingBox);

        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                6,
                1,
                6,
                6,
                1,
                10,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                false);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs2,
                6,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 1, 8, structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                6,
                1,
                9,
                structureBoundingBox);

        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                10,
                1,
                6,
                10,
                1,
                10,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                false);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                10,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, 1, 8, structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs3,
                10,
                1,
                9,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs1,
                7,
                1,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs1,
                9,
                1,
                6,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs0,
                7,
                1,
                10,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsLeft,
                rotatedStairs0,
                9,
                1,
                10,
                structureBoundingBox);

        // ground + 2 layer
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                7,
                2,
                7,
                9,
                2,
                9,
                Blocks.stone_slab,
                5,
                Blocks.stone_slab,
                5,
                false);

        // side columns
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneSouth),
                7,
                2,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneSouth),
                7,
                3,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 7, 4, 8, structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                8,
                2,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                8,
                3,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 8, 4, 7, structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                8,
                2,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                8,
                3,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 8, 4, 9, structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                2,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                3,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 9, 4, 8, structureBoundingBox);

        // middle pillar
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                8,
                1,
                8,
                8,
                4,
                8,
                TFBlocks.nagastonePillar,
                TFBlocks.nagastonePillar,
                false);

        // brazier
        this.placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 8, 5, 8, structureBoundingBox);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                7,
                5,
                7,
                7,
                5,
                9,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs6,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs6,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                9,
                5,
                7,
                9,
                5,
                9,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs7,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs7,
                false);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs5,
                8,
                5,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs4,
                8,
                5,
                9,
                structureBoundingBox);

        this.fillWithBlocks(world, structureBoundingBox, 7, 6, 7, 9, 6, 9, Blocks.stone_slab, Blocks.stone_slab, false);
        this.fillWithBlocks(world, structureBoundingBox, 8, 6, 7, 8, 6, 9, Blocks.air, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 7, 6, 8, 9, 6, 8, Blocks.air, Blocks.air, false);
        this.placeBlockAtCurrentPosition(world, Blocks.fire, 0, 8, 6, 8, structureBoundingBox);

        return true;
    }
}
