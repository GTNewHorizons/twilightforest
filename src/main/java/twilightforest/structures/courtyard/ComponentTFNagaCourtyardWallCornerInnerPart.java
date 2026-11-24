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
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        buildBrickwork(world, box);
        buildSpiralBricks(world, box);
        buildStoneBrickStairs(world, box);
        buildEtchedNagastone(world, box);
        buildNagastoneOrnament(world, box);
        return true;
    }

    private void buildBrickwork(World world, StructureBoundingBox box) {
        // Brick base
        fillWithBlocks(world, box, 0, 0, 0, 2, 0, 2, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        fillWithBlocks(world, box, 0, 1, 1, 2, 7, 1, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        fillWithBlocks(world, box, 0, 8, 0, 2, 8, 0, Blocks.stone_slab, Blocks.air, false);
        fillWithBlocks(world, box, 0, 8, 2, 2, 8, 2, Blocks.stone_slab, Blocks.air, false);

        // Columns
        // Horizontal
        for (int z = 0; z <= 2; z += 2) {
            fillWithMetadataBlocks(
                    world,
                    box,
                    0,
                    5,
                    z,
                    2,
                    5,
                    z,
                    TFBlocks.nagastonePillar,
                    horizontalColumnsOrient8,
                    Blocks.air,
                    0,
                    false);
        }
    }

    private void buildSpiralBricks(World world, StructureBoundingBox box) {
        // Spiral bricks
        // Individual
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs5, 1, 4, 0, box);
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs4, 1, 4, 2, box);
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs1, 1, 2, 0, box);
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs0, 1, 2, 2, box);

        // Patterns
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs3, 0, 6, z, box);
            placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs2, 1, 6, z, box);
            placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs7, 0, 7, z, box);
            placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs6, 1, 7, z, box);
        }

        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs3, 2, 3, 1, box);
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs7, 2, 4, 1, box);
    }

    private void buildStoneBrickStairs(World world, StructureBoundingBox box) {
        // Stone brick stairs
        // Top
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 2, 7, z, box);
        }

        // Middle
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs7, 0, 4, z, box);
        }

        // Bottom rows
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 0, 1, z, box);
        }
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs1, 2, 1, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 2, 1, 2, box);
    }

    private void buildEtchedNagastone(World world, StructureBoundingBox box) {
        // Etched nagastone
        // Top
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, ForgeDirection.UP.ordinal(), 2, 6, z, box);
        }

        // Bottom
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, ForgeDirection.UP.ordinal(), 1, 1, z, box);
        }
    }

    private void buildNagastoneOrnament(World world, StructureBoundingBox box) {
        // Nagastone ornament
        int bodyMeta = BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneNorth);
        for (int x = 0; x <= 2; x++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneBody, bodyMeta, x, 2, 1, box);
        }

        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.UP, NagastoneSouth),
                0,
                3,
                1,
                box);
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(BlockTFNagastone2.Direction.SIDE, NagastoneSouth),
                1,
                3,
                1,
                box);
    }
}
