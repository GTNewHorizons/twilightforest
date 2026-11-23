package twilightforest.structures.courtyard;

import java.util.Random;

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
        this.boundingBox = new StructureBoundingBox(x, y + 1, z, x + 4, y + 6, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        adjustGroundHeightOnce(world, structureBoundingBox, 5, 5, 0);
        buildHedge(world, structureBoundingBox);
        replaceGroundAround(world, structureBoundingBox, 5, 5);
        addFoliage(world, random, structureBoundingBox);
        return true;
    }

    private void buildHedge(World world, StructureBoundingBox box) {
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(world, box, 1, 0, 1, 3, 0, 3, TFBlocks.log, 12, TFBlocks.log, 12, false);
        fillWithMetadataBlocks(world, box, 1, 1, 1, 3, 4, 3, TFBlocks.leaves, 4, TFBlocks.leaves, 4, false);
        fillWithAir(world, box, 2, 1, 2, 2, 3, 2);
    }

    private void addFoliage(World world, Random random, StructureBoundingBox box) {
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
    }
}
