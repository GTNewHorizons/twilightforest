package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class ComponentTFMazeRoomCollapse extends ComponentTFMazeRoom {

    public ComponentTFMazeRoomCollapse() {
        super();
    }

    public ComponentTFMazeRoomCollapse(int i, Random rand, int x, int y, int z) {
        super(i, rand, x, y, z);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        super.addComponentParts(world, rand, sbb);

        //
        for (int x = 1; x < 14; x++) {
            for (int z = 1; z < 14; z++) {
                // calculate distance from middle
                int dist = ComponentTFMazeMushRoom.mushroomDensity(x, z);
                int gravel = rand.nextInt(dist);
                int root = rand.nextInt(dist);

                if (gravel > 0 && (x < 6 || x > 9 || z < 6 || z > 9)) {
                    gravel = Math.min(gravel + 1, 4); // keep rubble inside the room and away from the centre
                    this.fillWithBlocks(world, sbb, x, 1, z, x, gravel, z, Blocks.gravel, Blocks.air, false);
                    this.fillWithAir(world, sbb, x, gravel, z, x, 5, z);
                } else if (root > 0) {
                    this.fillWithBlocks(world, sbb, x, 5, z, x, 5 + root, z, Blocks.dirt, Blocks.air, true);
                    this.fillWithMetadataBlocks(
                            world,
                            sbb,
                            x,
                            5 - rand.nextInt(5),
                            z,
                            x,
                            5,
                            z,
                            TFBlocks.plant,
                            BlockTFPlant.META_ROOT_STRAND,
                            Blocks.air,
                            0,
                            false);
                } else if (rand.nextInt(dist + 1) > 0) {
                    // remove ceiling
                    this.fillWithAir(world, sbb, x, 5, z, x, 5, z);
                }
            }
        }

        return true;
    }
}
