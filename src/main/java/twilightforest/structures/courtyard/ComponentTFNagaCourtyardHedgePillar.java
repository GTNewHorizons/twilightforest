package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardHedgePillar extends ComponentTFNagaCourtyardHedge {

    @SuppressWarnings("unused")
    public ComponentTFNagaCourtyardHedgePillar() {
        super();
    }

    public ComponentTFNagaCourtyardHedgePillar(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y + 1, z, x + 4, y + 11, z + 4);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // super.addComponentParts(world, random, structureBoundingBox);

        Block andesite = Blocks.stonebrick;
        int andesiteMeta = 3;

        this.fillWithBlocks(
                world,
                structureBoundingBox,
                2,
                0,
                2,
                2,
                9,
                2,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                false);
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                1,
                10,
                1,
                3,
                10,
                3,
                Blocks.stone_slab,
                Blocks.stone_slab,
                false);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 10, 2, structureBoundingBox);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                2,
                0,
                2,
                2,
                1,
                2,
                andesite,
                andesiteMeta,
                andesite,
                andesiteMeta,
                false);
        this.placeBlockAtCurrentPosition(world, andesite, andesiteMeta, 2, 4, 2, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, andesite, andesiteMeta, 2, 7, 2, structureBoundingBox);

        return true;
    }
}
