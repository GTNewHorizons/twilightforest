package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardHedge extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings("unused")
    public ComponentTFNagaCourtyardHedge() {
        super();
    }

    public ComponentTFNagaCourtyardHedge(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y + 1, z, x + 4, y + 6, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        int groundAir = 0;
        int undergroundAir = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.getBlockAtCurrentPosition(world, i, -1, j, structureBoundingBox) == Blocks.air) groundAir++;
                if (this.getBlockAtCurrentPosition(world, i, -2, j, structureBoundingBox) == Blocks.air)
                    undergroundAir++;
            }
        }
        if (groundAir > undergroundAir) this.boundingBox.minY--;

        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                0,
                0,
                0,
                0,
                3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                0,
                0,
                4,
                0,
                0,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs1,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                4,
                0,
                1,
                4,
                0,
                4,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs3,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                0,
                4,
                3,
                0,
                4,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs0,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                0,
                1,
                3,
                0,
                3,
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
                1,
                3,
                4,
                3,
                TFBlocks.leaves,
                4,
                TFBlocks.leaves,
                4,
                false);
        this.fillWithAir(world, structureBoundingBox, 2, 1, 2, 2, 3, 2);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
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

        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                1,
                1,
                0,
                4,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                1,
                0,
                3,
                4,
                0,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                4,
                1,
                1,
                4,
                4,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                1,
                4,
                3,
                4,
                4,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);
        this.partiallyFillWithMetadataBlocks(
                world,
                structureBoundingBox,
                1,
                5,
                1,
                3,
                5,
                3,
                TFBlocks.leaves,
                4,
                random,
                ComponentTFNagaCourtyardMain.HEDGE_FLOOF);

        return true;
    }
}
