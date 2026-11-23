package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.BlockTFPlant;
import twilightforest.block.TFBlocks;

public class ComponentTFMazeCorridorRoots extends ComponentTFMazeCorridor {

    public ComponentTFMazeCorridorRoots() {}

    public ComponentTFMazeCorridorRoots(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);
        // dirt supporting the hanging roots is placed at Y-6
        this.boundingBox.maxY = y + 6;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        for (int x = 1; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                if (random.nextInt(x + 2) == 0) {
                    continue;
                }

                int length = random.nextInt(6);
                placeBlockAtCurrentPosition(world, Blocks.dirt, 0, x, 6, z, bounds);

                // roots hang from the ceiling
                for (int y = 6 - length; y < 6; y++) {
                    placeBlockAtCurrentPosition(world, TFBlocks.plant, BlockTFPlant.META_ROOT_STRAND, x, y, z, bounds);
                }

                // keep solid gravel out of the central 2 block wide path
                if ((x == 1 || x == 4) && random.nextInt(x + 1) > 1) {
                    placeBlockAtCurrentPosition(world, Blocks.gravel, 0, x, 1, z, bounds);
                    if (random.nextInt(x + 1) > 1) {
                        placeBlockAtCurrentPosition(world, Blocks.gravel, 0, x, 2, z, bounds);
                    }
                }
            }
        }
        return true;
    }
}
