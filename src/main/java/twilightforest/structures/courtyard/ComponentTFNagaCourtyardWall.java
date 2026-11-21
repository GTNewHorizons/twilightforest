package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWall extends ComponentTFNagaCourtyardRotatedAbstract {

    private static final int WALL_LENGTH = 11;
    private static final int WALL_WIDTH = 3;
    private static final int WALL_HEIGHT = 9;

    public ComponentTFNagaCourtyardWall() {
        super();
    }

    public ComponentTFNagaCourtyardWall(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);

        // structure component coords transformation uses max x for rotation 1 and min x for rotation 3
        // preserve those anchors when rotating the box
        if (rotation == 1) {
            boundingBox = new StructureBoundingBox(
                    x + WALL_LENGTH - WALL_WIDTH,
                    y,
                    z,
                    x + WALL_LENGTH - 1,
                    y + WALL_HEIGHT - 1,
                    z + WALL_LENGTH - 1);
        } else if (rotation == 3) {
            boundingBox = new StructureBoundingBox(
                    x,
                    y,
                    z,
                    x + WALL_WIDTH - 1,
                    y + WALL_HEIGHT - 1,
                    z + WALL_LENGTH - 1);
        } else {
            boundingBox = new StructureBoundingBox(
                    x,
                    y,
                    z,
                    x + WALL_LENGTH - 1,
                    y + WALL_HEIGHT - 1,
                    z + WALL_WIDTH - 1);
        }
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox chunkBox) {
        buildBrickwork(world, chunkBox);
        buildPillars(world, chunkBox);
        buildSpiralBricks(world, chunkBox);
        buildChiseledBricks(world, chunkBox);
        buildStairs(world, chunkBox);
        buildEtchedNagastone(world, chunkBox);
        buildNagastoneOrnaments(world, chunkBox);
        return true;
    }

    private void buildBrickwork(World world, StructureBoundingBox box) {
        fillWithBlocks(world, box, 0, 0, 0, 10, 0, 2, Blocks.stonebrick, Blocks.air, false);
        fillWithBlocks(world, box, 0, 1, 1, 10, 7, 1, Blocks.stonebrick, Blocks.air, false);

        for (int z = 0; z <= 2; z += 2) {
            fillWithBlocks(world, box, 0, 8, z, 10, 8, z, Blocks.stone_slab, Blocks.air, false);
        }
    }

    private void buildPillars(World world, StructureBoundingBox box) {
        for (int x = 2; x <= 8; x += 3) {
            for (int z = 0; z <= 2; z += 2) {
                fillWithBlocks(world, box, x, 5, z, x, 7, z, TFBlocks.nagastonePillar, Blocks.air, false);
            }
        }

        for (int x = 0; x <= 9; x += 3) {
            fillWithMetadataBlocks(
                    world,
                    box,
                    x,
                    4,
                    1,
                    x + 1,
                    4,
                    1,
                    TFBlocks.nagastonePillar,
                    horizontalColumnsOrient8,
                    Blocks.air,
                    0,
                    false);
        }
    }

    private void buildSpiralBricks(World world, StructureBoundingBox box) {
        for (int x = 2; x <= 8; x += 3) {
            int y = x == 5 ? 2 : 3;
            placeSpiral(world, box, x == 5 ? rotatedStairs1 : rotatedStairs5, x, y, 0);
            placeSpiral(world, box, x == 5 ? rotatedStairs0 : rotatedStairs4, x, y, 2);
        }

        for (int x = 0; x <= 9; x += 3) {
            int y = x == 0 || x == 9 ? 6 : 5;
            for (int z = 0; z <= 2; z += 2) {
                placeSpiral(world, box, rotatedStairs3, x, y, z);
                placeSpiral(world, box, rotatedStairs2, x + 1, y, z);
                placeSpiral(world, box, rotatedStairs7, x, y + 1, z);
                placeSpiral(world, box, rotatedStairs6, x + 1, y + 1, z);
            }
        }
    }

    private void placeSpiral(World world, StructureBoundingBox box, int metadata, int x, int y, int z) {
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, metadata, x, y, z, box);
    }

    private void buildChiseledBricks(World world, StructureBoundingBox box) {
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 7, 0, box);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 7, 2, box);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 5, 3, 1, box);
    }

    private void buildStairs(World world, StructureBoundingBox box) {
        for (int z = 0; z <= 2; z += 2) {
            int outerStair = z == 0 ? rotatedStairs1 : rotatedStairs0;
            int centerStair = z == 0 ? rotatedStairs5 : rotatedStairs4;

            for (int x = 2; x <= 8; x += 3) {
                placeBlockAtCurrentPosition(
                        world,
                        Blocks.stone_brick_stairs,
                        x == 5 ? centerStair : outerStair,
                        x,
                        4,
                        z,
                        box);
            }
        }

        for (int z = 0; z <= 2; z += 2) {
            fillWithMetadataBlocks(
                    world,
                    box,
                    0,
                    1,
                    z,
                    10,
                    1,
                    z,
                    Blocks.stone_brick_stairs,
                    z == 0 ? rotatedStairs1 : rotatedStairs0,
                    Blocks.air,
                    0,
                    false);
        }

        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 0, 1, z, box);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs3, 6, 1, z, box);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 4, 1, z, box);
            placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 10, 1, z, box);
        }
    }

    private void buildEtchedNagastone(World world, StructureBoundingBox box) {
        int north = EtchedNagastoneNorth.ordinal();
        int south = EtchedNagastoneSouth.ordinal();

        for (int z = 0; z <= 2; z += 2) {
            for (int x = 3; x <= 4; x++) {
                placeEtched(world, box, north, x, 7, z);
            }
            for (int x = 6; x <= 7; x++) {
                placeEtched(world, box, south, x, 7, z);
            }
        }

        for (int z = 0; z <= 2; z += 2) {
            for (int x = 0; x <= 1; x++) {
                placeEtched(world, box, south, x, 5, z);
            }
            for (int x = 9; x <= 10; x++) {
                placeEtched(world, box, north, x, 5, z);
            }
        }

        for (int z = 0; z <= 2; z += 2) {
            placeEtched(world, box, ForgeDirection.UP.ordinal(), 5, 1, z);
        }
        placeEtched(world, box, north, 4, 2, 1);
        placeEtched(world, box, south, 6, 2, 1);
    }

    private void placeEtched(World world, StructureBoundingBox box, int metadata, int x, int y, int z) {
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, metadata, x, y, z, box);
    }

    private void buildNagastoneOrnaments(World world, StructureBoundingBox box) {
        int northSide = BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth);
        int southSide = BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneSouth);
        int northUp = BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth);
        int southUp = BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneSouth);

        for (int x = 0; x <= 3; x++) {
            placeOrnament(world, box, TFBlocks.nagastoneBody, northSide, x, 2);
        }
        placeOrnament(world, box, TFBlocks.nagastoneBody, southUp, 0, 3);
        placeOrnament(world, box, TFBlocks.nagastoneBody, southUp, 3, 3);
        placeOrnament(world, box, TFBlocks.nagastoneHead, southSide, 1, 3);
        placeOrnament(world, box, TFBlocks.nagastoneBody, northSide, 4, 3);

        for (int x = 7; x <= 10; x++) {
            placeOrnament(world, box, TFBlocks.nagastoneBody, southSide, x, 2);
        }
        placeOrnament(world, box, TFBlocks.nagastoneBody, northUp, 10, 3);
        placeOrnament(world, box, TFBlocks.nagastoneBody, northUp, 7, 3);
        placeOrnament(world, box, TFBlocks.nagastoneHead, northSide, 9, 3);
        placeOrnament(world, box, TFBlocks.nagastoneBody, southSide, 6, 3);
    }

    private void placeOrnament(World world, StructureBoundingBox box, Block block, int metadata, int x, int y) {
        placeBlockAtCurrentPosition(world, block, metadata, x, y, 1, box);
    }
}
