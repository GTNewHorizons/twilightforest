package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceAbstract extends ComponentTFNagaCourtyardRotatedAbstract {

    public ComponentTFNagaCourtyardTerraceAbstract() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceAbstract(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y - 1, z, x + 16, y + 5, z + 16);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        /*
         * int groundAir = 0; int undergroundAir = 0; for(int i = 0; i < 17; i++) { for(int j = 0; j < 17; j++) {
         * if(this.getBlockAtCurrentPosition(world, i, 1, j, structureBoundingBox) == Blocks.air) groundAir++;
         * if(this.getBlockAtCurrentPosition(world, i, 0, j, structureBoundingBox) == Blocks.air) undergroundAir++; } }
         * if(groundAir > undergroundAir) this.boundingBox.minY--;
         */

        buildTerraceBase(world, structureBoundingBox);
        buildEtchedPaving(world, structureBoundingBox);
        return true;
    }

    private void buildTerraceBase(World world, StructureBoundingBox box) {
        fillWithBlocks(world, box, 1, 2, 1, 15, 3, 15, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, box, 0, 1, 0, 16, 1, 16, Blocks.stonebrick, Blocks.stonebrick, true);
        fillWithMetadataBlocks(world, box, 1, 1, 1, 15, 1, 15, Blocks.stone_slab, 5, Blocks.stone_slab, 5, true);
        fillWithBlocks(world, box, 2, 1, 2, 14, 1, 14, Blocks.air, Blocks.air, true);
        fillWithBlocks(world, box, 2, 0, 2, 14, 0, 14, Blocks.grass, Blocks.grass, true);
    }

    private void buildEtchedPaving(World world, StructureBoundingBox box) {
        for (int gridX = 0; gridX < 6; gridX++) {
            for (int gridZ = 0; gridZ < 6; gridZ++) {
                if ((gridX == 2 || gridX == 3) && (gridZ == 2 || gridZ == 3)) {
                    continue;
                }

                int x = gridX * 3;
                int z = gridZ * 3;
                fillWithBlocks(
                        world,
                        box,
                        x,
                        0,
                        z,
                        x + 1,
                        0,
                        z + 1,
                        TFBlocks.nagastoneEtched,
                        TFBlocks.nagastoneEtched,
                        true);
            }
        }
    }
}
