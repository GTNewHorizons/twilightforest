package twilightforest.structures.courtyard;

import java.util.Random;

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
        this.boundingBox = new StructureBoundingBox(x, y + 1, z - 1, x + 4, y + 6, z + 1);
        normalizeRotatedFootprint();
    }

    @Override
    protected void normalizeRotatedFootprint() {
        fitRotatedFootprint(5, 3);
    }

    @Override
    public StructureBoundingBox getBoundingBox() {
        boolean rotated = coordBaseMode == 1 || coordBaseMode == 3;
        int expandX = rotated ? 1 : 0;
        int expandZ = rotated ? 0 : 1;
        return new StructureBoundingBox(
                boundingBox.minX - expandX,
                boundingBox.minY,
                boundingBox.minZ - expandZ,
                boundingBox.maxX + expandX,
                boundingBox.maxY,
                boundingBox.maxZ + expandZ);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        adjustGroundHeightOnce(world, 5, 1, 1);
        buildHedge(world, structureBoundingBox);
        addFoliage(world, random, structureBoundingBox);
        replaceGroundAround(world, structureBoundingBox, 5, 3);
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
                2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                TFBlocks.nagastoneStairsRight,
                rotatedStairs2,
                false);
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(world, box, 1, 0, 0, 3, 0, 2, TFBlocks.log, 12, TFBlocks.log, 12, false);
        fillWithMetadataBlocks(world, box, 1, 1, 0, 3, 4, 2, TFBlocks.leaves, 4, TFBlocks.leaves, 4, false);
        fillWithAir(world, box, 2, 1, -1, 2, 3, 3);
    }

    private void addFoliage(World world, Random random, StructureBoundingBox box) {
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
        partiallyFillWithMetadataBlocks(
                world,
                box,
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
    }

}
