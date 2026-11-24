package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFMazeDeadEndFountain extends ComponentTFMazeDeadEnd {

    public ComponentTFMazeDeadEndFountain() {}

    public ComponentTFMazeDeadEndFountain(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        super.addComponentParts(world, random, bounds);

        // fountain wall and its two water outlets
        fillWithMetadataBlocks(world, bounds, 1, 1, 4, 4, 4, 4, TFBlocks.mazestone, 1, Blocks.air, 0, false);
        placeBlockAtCurrentPosition(world, Blocks.flowing_water, 0, 2, 3, 4, bounds);
        placeBlockAtCurrentPosition(world, Blocks.flowing_water, 0, 3, 3, 4, bounds);

        // open the floor of the receptacle for the descending water
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 0, 3, bounds);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, 0, 3, bounds);
        return true;
    }
}
