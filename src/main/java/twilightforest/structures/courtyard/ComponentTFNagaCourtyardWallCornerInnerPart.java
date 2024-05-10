package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWallCornerInnerPart extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardWallCornerInnerPart() {
        super();
    }

    public ComponentTFNagaCourtyardWallCornerInnerPart(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 2, y + 8, z + 2);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // Brick base
        this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 2, 0, 2, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 2, 7, 1, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        this.fillWithBlocks(world, structureBoundingBox, 0, 8, 0, 2, 8, 0, Blocks.stone_slab, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 0, 8, 2, 2, 8, 2, Blocks.stone_slab, Blocks.air, false);

        // Columns
        // Horizontal
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                5,
                0,
                2,
                5,
                0,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                5,
                2,
                2,
                5,
                2,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);

        // Spiral bricks
        // Individual
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs5,
                1,
                4,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs4,
                1,
                4,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs1,
                1,
                2,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs0,
                1,
                2,
                2,
                structureBoundingBox);
        // Patterns
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                0,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                1,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                0,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                1,
                7,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                0,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                1,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                0,
                7,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                1,
                7,
                2,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                2,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                2,
                4,
                1,
                structureBoundingBox);

        // Stone brick stairs
        // Top
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                2,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                2,
                7,
                2,
                structureBoundingBox);
        // Middle
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs7,
                0,
                4,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs7,
                0,
                4,
                2,
                structureBoundingBox);
        // Bottom rows
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                0,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs3,
                0,
                1,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                2,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                2,
                1,
                2,
                structureBoundingBox);

        // Etched nagastone
        // Top
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                2,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                2,
                6,
                2,
                structureBoundingBox);
        // Bottom
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                1,
                2,
                structureBoundingBox);

        // Nagastone ornament
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneNorth),
                0,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneNorth),
                1,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneNorth),
                2,
                2,
                1,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.UP, NagastoneSouth),
                0,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                1,
                3,
                1,
                structureBoundingBox);

        return true;
    }
}
