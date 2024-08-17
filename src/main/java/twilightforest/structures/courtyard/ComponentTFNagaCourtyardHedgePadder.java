package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardHedgePadder extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings("unused")
    public ComponentTFNagaCourtyardHedgePadder() {
        super();
    }

    public ComponentTFNagaCourtyardHedgePadder(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y + 1, z - 1, x + 4, y + 6, z + 1);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        int groundAir = 0;
        int undergroundAir = 0;
        for (int i = 0; i < 5; i++) {
            if (this.getBlockAtCurrentPosition(world, i, -1, 1, structureBoundingBox) == Blocks.air) groundAir++;
            if (this.getBlockAtCurrentPosition(world, i, -2, 1, structureBoundingBox) == Blocks.air) undergroundAir++;
        }
        if (groundAir > undergroundAir/*
                                       * || this.getBlockAtCurrentPosition(world, 0, -1, 1, structureBoundingBox) ==
                                       * TFBlocks.nagastoneStairsRight
                                       */) this.boundingBox.minY--;

        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                0,
                0,
                0,
                0,
                2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                4,
                0,
                0,
                4,
                0,
                2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                0,
                0,
                3,
                0,
                2,
                TFBlocks.log,
                12,
                TFBlocks.log,
                12,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                1,
                0,
                3,
                4,
                2,
                TFBlocks.leaves,
                4,
                TFBlocks.leaves,
                4,
                false);
        this.fillWithAir(world, structureBoundingBox, 2, 1, -1, 2, 3, 3);

        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                1,
                0,
                0,
                4,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                4,
                1,
                0,
                4,
                4,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                5,
                0,
                3,
                5,
                2,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Block[] neighbours = new Block[4];
                neighbours[0] = this.getBlockAtCurrentPosition(world, i - 1, 0, j, structureBoundingBox);
                neighbours[1] = this.getBlockAtCurrentPosition(world, i + 1, 0, j, structureBoundingBox);
                neighbours[2] = this.getBlockAtCurrentPosition(world, i, 0, j - 1, structureBoundingBox);
                neighbours[3] = this.getBlockAtCurrentPosition(world, i, 0, j + 1, structureBoundingBox);
                for (int k = 0; k < 4; k++) if (neighbours[k] == Blocks.grass || neighbours[k] == Blocks.dirt
                        || neighbours[k] == Blocks.stonebrick) {
                            this.placeBlockAtCurrentPosition(
                                    world,
                                    TFBlocks.nagastoneEtched,
                                    0,
                                    i,
                                    0,
                                    j,
                                    structureBoundingBox);
                            break;
                        }
            }
        }

        return true;
    }
}
