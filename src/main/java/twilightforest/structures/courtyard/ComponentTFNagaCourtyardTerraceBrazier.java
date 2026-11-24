package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.BlockTFNagastone2.Yaw;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceBrazier extends ComponentTFNagaCourtyardTerraceAbstract {

    public ComponentTFNagaCourtyardTerraceBrazier() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceBrazier(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        super.addComponentParts(world, random, box);

        buildGroundLayer(world, box);
        buildFirstLayer(world, box);
        buildSecondLayer(world, box);
        buildSideColumns(world, box);
        buildMiddlePillar(world, box);
        buildBrazier(world, box);
        return true;
    }

    private void buildGroundLayer(World world, StructureBoundingBox box) {
        // ground layer
        fillWithBlocks(world, box, 6, 0, 6, 10, 0, 10, Blocks.stonebrick, Blocks.stonebrick, true);
        for (int x = 6; x <= 10; x += 4) {
            for (int z = 6; z <= 10; z += 4) {
                placeBlockAtCurrentPosition(world, Blocks.grass, 0, x, 0, z, box);
            }
        }

        fillWithBlocks(world, box, 8, 0, 7, 8, 0, 9, TFBlocks.nagastoneEtched, TFBlocks.nagastoneEtched, true);
        fillWithBlocks(world, box, 7, 0, 8, 9, 0, 8, TFBlocks.nagastoneEtched, TFBlocks.nagastoneEtched, true);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 6, 0, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 10, 0, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 8, 0, 6, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 8, 0, 10, box);
    }

    private void buildFirstLayer(World world, StructureBoundingBox box) {
        // ground + 1 layer
        fillWithBlocks(world, box, 7, 1, 7, 9, 1, 9, TFBlocks.nagastonePillar, TFBlocks.nagastonePillar, false);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 7, 1, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs7, 9, 1, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs5, 8, 1, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs4, 8, 1, 9, box);

        fillWithMetadataBlocks(
                world,
                box,
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
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsLeft, rotatedStairs2, 6, 1, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 1, 8, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs2, 6, 1, 9, box);

        fillWithMetadataBlocks(
                world,
                box,
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
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs3, 10, 1, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, 1, 8, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsLeft, rotatedStairs3, 10, 1, 9, box);

        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs1, 7, 1, 6, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsLeft, rotatedStairs1, 9, 1, 6, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs0, 7, 1, 10, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsLeft, rotatedStairs0, 9, 1, 10, box);
    }

    private void buildSecondLayer(World world, StructureBoundingBox box) {
        // ground + 2 layer
        fillWithMetadataBlocks(world, box, 7, 2, 7, 9, 2, 9, Blocks.stone_slab, 5, Blocks.stone_slab, 5, false);
    }

    private void buildSideColumns(World world, StructureBoundingBox box) {
        // side columns
        placeSideColumn(world, box, 7, 8, NagastoneSouth);
        placeSideColumn(world, box, 8, 7, NagastoneWest);
        placeSideColumn(world, box, 8, 9, NagastoneEast);
        placeSideColumn(world, box, 9, 8, NagastoneNorth);
    }

    private void placeSideColumn(World world, StructureBoundingBox box, int x, int z, Yaw yaw) {
        for (int y = 2; y <= 3; y++) {
            placeBlockAtCurrentPosition(
                    world,
                    TFBlocks.nagastoneBody,
                    BlockTFNagastone2.GetMetadata(Direction.UP, yaw),
                    x,
                    y,
                    z,
                    box);
        }
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, x, 4, z, box);
    }

    private void buildMiddlePillar(World world, StructureBoundingBox box) {
        // middle pillar
        fillWithBlocks(world, box, 8, 1, 8, 8, 4, 8, TFBlocks.nagastonePillar, TFBlocks.nagastonePillar, false);
    }

    private void buildBrazier(World world, StructureBoundingBox box) {
        // brazier
        placeBlockAtCurrentPosition(world, Blocks.netherrack, 0, 8, 5, 8, box);
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs5, 8, 5, 7, box);
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneStairsRight, rotatedStairs4, 8, 5, 9, box);

        fillWithBlocks(world, box, 7, 6, 7, 9, 6, 9, Blocks.stone_slab, Blocks.stone_slab, false);
        fillWithBlocks(world, box, 8, 6, 7, 8, 6, 9, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, box, 7, 6, 8, 9, 6, 8, Blocks.air, Blocks.air, false);
        placeBlockAtCurrentPosition(world, Blocks.fire, 0, 8, 6, 8, box);
    }
}
