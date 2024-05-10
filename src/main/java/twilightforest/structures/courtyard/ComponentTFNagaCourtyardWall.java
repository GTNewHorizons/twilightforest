package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWall extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardWall() {
        super();
    }

    public ComponentTFNagaCourtyardWall(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 10, y + 8, z + 2);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // Brick base
        this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 10, 0, 2, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 10, 7, 1, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        this.fillWithBlocks(world, structureBoundingBox, 0, 8, 0, 10, 8, 0, Blocks.stone_slab, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 0, 8, 2, 10, 8, 2, Blocks.stone_slab, Blocks.air, false);

        // Columns
        // Vertical
        this.fillWithBlocks(world, structureBoundingBox, 2, 5, 0, 2, 7, 0, TFBlocks.nagastonePillar, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 2, 5, 2, 2, 7, 2, TFBlocks.nagastonePillar, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 5, 5, 0, 5, 7, 0, TFBlocks.nagastonePillar, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 5, 5, 2, 5, 7, 2, TFBlocks.nagastonePillar, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 8, 5, 0, 8, 7, 0, TFBlocks.nagastonePillar, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 8, 5, 2, 8, 7, 2, TFBlocks.nagastonePillar, Blocks.air, false);

        // Horizontal
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                4,
                1,
                1,
                4,
                1,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                3,
                4,
                1,
                4,
                4,
                1,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                6,
                4,
                1,
                7,
                4,
                1,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                9,
                4,
                1,
                10,
                4,
                1,
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
                2,
                3,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs4,
                2,
                3,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs1,
                5,
                2,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs0,
                5,
                2,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs5,
                8,
                3,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs4,
                8,
                3,
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
                3,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                4,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                3,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                4,
                6,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                3,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                4,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                3,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                4,
                6,
                2,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                6,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                7,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                6,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                7,
                6,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                6,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                7,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                6,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                7,
                6,
                2,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                9,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                10,
                6,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                9,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                10,
                7,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs3,
                9,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                10,
                6,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs7,
                9,
                7,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs6,
                10,
                7,
                2,
                structureBoundingBox);

        // Chiseled bricks
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 7, 0, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 7, 2, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 3, 1, structureBoundingBox);

        // Stone brick stairs
        // Middle rows
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                2,
                4,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs5,
                5,
                4,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                8,
                4,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                2,
                4,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs4,
                5,
                4,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                8,
                4,
                2,
                structureBoundingBox);
        // Bottom rows
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                1,
                0,
                10,
                1,
                0,
                Blocks.stone_brick_stairs,
                rotatedStairs1,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                1,
                2,
                10,
                1,
                2,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                Blocks.air,
                0,
                false);

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
                6,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                4,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                10,
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
                rotatedStairs3,
                6,
                1,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                4,
                1,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                10,
                1,
                2,
                structureBoundingBox);

        // Etched nagastone
        // Top rows
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                3,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                4,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                6,
                7,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                7,
                7,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                3,
                7,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                4,
                7,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                6,
                7,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                7,
                7,
                2,
                structureBoundingBox);
        // Middle rows
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                0,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                1,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                9,
                5,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                10,
                5,
                0,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                0,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                1,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                9,
                5,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                10,
                5,
                2,
                structureBoundingBox);
        // Bottom decor
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                5,
                1,
                0,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                5,
                1,
                2,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneNorth.ordinal(),
                4,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                EtchedNagastoneSouth.ordinal(),
                6,
                2,
                1,
                structureBoundingBox);

        // Nagastone ornament
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                0,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                1,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                2,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                3,
                2,
                1,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneSouth),
                0,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneSouth),
                3,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                1,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                4,
                3,
                1,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                7,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                8,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                9,
                2,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                10,
                2,
                1,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                10,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                7,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                9,
                3,
                1,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth),
                6,
                3,
                1,
                structureBoundingBox);

        return true;
    }
}
