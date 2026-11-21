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
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 3, y + 2, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        buildFirstLayer(world, box);
        buildSecondLayer(world, box);
        buildThirdLayer(world, box);
        buildSnakes(world, box);
        return true;
    }

    private void buildFirstLayer(World world, StructureBoundingBox box) {
        // 1 layer
        for (int z = 0; z <= 4; z += 4) {
            fillWithMetadataBlocks(
                    world,
                    box,
                    1,
                    0,
                    z,
                    3,
                    0,
                    z,
                    TFBlocks.nagastonePillar,
                    horizontalColumnsOrient8,
                    TFBlocks.nagastonePillar,
                    horizontalColumnsOrient8,
                    false);
        }
        fillWithMetadataBlocks(
                world,
                box,
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
    }

    private void buildSecondLayer(World world, StructureBoundingBox box) {
        // 2 layer
        fillWithBlocks(world, box, 1, 1, 0, 3, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
        fillWithBlocks(world, box, 0, 1, 1, 0, 1, 3, Blocks.iron_bars, Blocks.iron_bars, false);
        fillWithMetadataBlocks(world, box, 1, 1, 1, 1, 1, 3, Blocks.stone_slab, 13, Blocks.stone_slab, 13, false);
    }

    private void buildThirdLayer(World world, StructureBoundingBox box) {
        // 3 layer
        fillWithMetadataBlocks(world, box, 0, 2, 0, 2, 2, 4, Blocks.stone_slab, 5, Blocks.stone_slab, 5, false);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 1, 2, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 1, 2, 1, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 1, 2, 3, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 1, 2, 4, box);

        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 1, box);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 3, box);

        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 3, 2, 1, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 3, 2, 2, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 3, 2, 3, box);
    }

    private void buildSnakes(World world, StructureBoundingBox box) {
        // sneks
        int westBody = BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest);
        for (int y = 0; y <= 2; y++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, westBody, 0, y, 0, box);
        }
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                0,
                2,
                1,
                box);

        int eastBody = BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast);
        for (int y = 0; y <= 2; y++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, eastBody, 0, y, 4, box);
        }
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                0,
                2,
                3,
                box);
    }
}
