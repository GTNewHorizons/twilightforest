package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWallCornerOuter extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardWallCornerOuter() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ComponentTFNagaCourtyardWallCornerOuter(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 4, y + 8, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        // Brick base
        this.fillWithBlocks(world, sbb, 0, 0, 0, 4, 0, 4, Blocks.stonebrick, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 3, 0, 3, 4, 0, 4, TFBlocks.nagastoneEtched, Blocks.air, false);

        // Middle brick layer
        this.fillWithBlocks(world, sbb, 1, 1, 1, 1, 7, 4, Blocks.stonebrick, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 1, 1, 1, 4, 7, 1, Blocks.stonebrick, Blocks.air, false);

        // Corner columns
        // Outer
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 1, 0, sbb);
        this.fillWithBlocks(world, sbb, 0, 2, 0, 0, 7, 0, TFBlocks.nagastonePillar, Blocks.air, false);
        // Inner
        this.fillWithBlocks(world, sbb, 2, 1, 2, 2, 4, 2, TFBlocks.nagastoneEtched, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 2, 5, 2, 2, 7, 2, TFBlocks.nagastonePillar, Blocks.air, false);

        // Upper slab rows
        this.fillWithBlocks(world, sbb, 0, 8, 0, 0, 8, 4, Blocks.stone_slab, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 0, 8, 0, 4, 8, 0, Blocks.stone_slab, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 2, 8, 2, 2, 8, 4, Blocks.stone_slab, Blocks.air, false);
        this.fillWithBlocks(world, sbb, 2, 8, 2, 4, 8, 2, Blocks.stone_slab, Blocks.air, false);

        // Horizontal rows
        // Outer
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneWest.ordinal(), 0, 5, 1, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneWest.ordinal(), 0, 5, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneWest.ordinal(), 0, 5, 3, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneWest.ordinal(), 0, 5, 4, sbb);

        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneNorth.ordinal(), 1, 5, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneNorth.ordinal(), 2, 5, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneNorth.ordinal(), 3, 5, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneNorth.ordinal(), 4, 5, 0, sbb);
        // Inner
        this.fillWithMetadataBlocks(
                world,
                sbb,
                3,
                5,
                2,
                4,
                5,
                2,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                2,
                5,
                3,
                2,
                5,
                4,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient4,
                Blocks.air,
                0,
                false);

        // Stone brick stairs
        // Upper ornament
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                6,
                0,
                4,
                7,
                0,
                Blocks.stone_brick_stairs,
                rotatedStairs5,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                6,
                1,
                0,
                7,
                4,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                Blocks.air,
                0,
                false);
        // Middle ornament
        // Outer
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                4,
                0,
                4,
                4,
                0,
                Blocks.stone_brick_stairs,
                rotatedStairs5,
                Blocks.air,
                0,
                false);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 4, 4, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs7, 1, 4, 0, sbb);

        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                4,
                1,
                0,
                4,
                4,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                Blocks.air,
                0,
                false);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs5, 0, 4, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs4, 0, 4, 1, sbb);
        // Inner
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs4, 3, 4, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 4, 4, 2, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs7, 2, 4, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs5, 2, 4, 4, sbb);

        // Lower ornament
        // Outer
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs5, 1, 1, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 2, 1, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 1, 2, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 3, 1, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 4, 1, 0, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 0, 1, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 0, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 0, 2, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 0, 1, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 0, 1, 4, sbb);
        // Inner
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 3, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 4, 1, 2, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 2, 1, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 2, 1, 4, sbb);

        // Spiral bricks
        // Outer
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs3, 2, 6, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs2, 3, 6, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs7, 2, 7, 0, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs6, 3, 7, 0, sbb);

        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs0, 0, 6, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs1, 0, 6, 3, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs4, 0, 7, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs5, 0, 7, 3, sbb);
        // Inner
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs3, 3, 6, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs2, 4, 6, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs7, 3, 7, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs6, 4, 7, 2, sbb);

        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs0, 2, 6, 3, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs1, 2, 6, 4, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs4, 2, 7, 3, sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs5, 2, 7, 4, sbb);

        // Snake ornament
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                1,
                2,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                2,
                2,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                3,
                2,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                4,
                2,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.UP, NagastoneNorth),
                4,
                3,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneNorth),
                3,
                3,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneNorth.ordinal(), 2, 3, 1, sbb);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneWest),
                1,
                2,
                1,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneWest),
                1,
                2,
                2,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneWest),
                1,
                2,
                3,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneWest),
                1,
                2,
                4,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.UP, NagastoneEast),
                1,
                3,
                4,
                sbb);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneEast),
                1,
                3,
                3,
                sbb);
        this.placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, EtchedNagastoneWest.ordinal(), 1, 3, 2, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 1, 3, 1, sbb);

        return true;
    }

}
