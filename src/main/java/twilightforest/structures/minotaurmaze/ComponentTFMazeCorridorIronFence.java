package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFMazeCorridorIronFence extends ComponentTFMazeCorridor {

    public ComponentTFMazeCorridorIronFence() {}

    public ComponentTFMazeCorridorIronFence(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        fillWithMetadataBlocks(world, bounds, 1, 4, 2, 4, 4, 3, TFBlocks.mazestone, 3, Blocks.air, 0, false);
        fillWithMetadataBlocks(world, bounds, 1, 1, 2, 4, 3, 3, TFBlocks.mazestone, 2, Blocks.air, 0, false);
        fillWithBlocks(world, bounds, 2, 1, 2, 3, 3, 3, Blocks.iron_bars, Blocks.air, false);
        return true;
    }
}
