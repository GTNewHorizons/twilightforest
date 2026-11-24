package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.TFTreasure;
import twilightforest.block.TFBlocks;

public class ComponentTFMazeDeadEndChest extends ComponentTFMazeDeadEnd {

    public ComponentTFMazeDeadEndChest() {
        // the treasure alcove must not have monster spawns
        this.spawnListIndex = Integer.MAX_VALUE;
    }

    public ComponentTFMazeDeadEndChest(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);
        this.spawnListIndex = Integer.MAX_VALUE;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        // Raised platform and step.
        placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 1, 4, bounds);
        placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 1, 4, bounds);
        int stairMeta = getStairMeta(1);
        placeBlockAtCurrentPosition(world, Blocks.oak_stairs, stairMeta, 2, 1, 3, bounds);
        placeBlockAtCurrentPosition(world, Blocks.oak_stairs, stairMeta, 3, 1, 3, bounds);

        // an empty chest in the generated loot chest into a double chest
        placeBlockAtCurrentPosition(world, Blocks.chest, 0, 2, 2, 4, bounds);
        placeTreasureAtCurrentPosition(world, random, 3, 2, 4, TFTreasure.labyrinth_deadend, bounds);

        fillWithMetadataBlocks(world, bounds, 1, 1, 0, 4, 3, 1, TFBlocks.mazestone, 2, Blocks.air, 0, false);
        fillWithMetadataBlocks(world, bounds, 1, 4, 0, 4, 4, 1, TFBlocks.mazestone, 3, Blocks.air, 0, false);
        fillWithBlocks(world, bounds, 2, 1, 0, 3, 3, 1, Blocks.iron_bars, Blocks.air, false);
        return true;
    }
}
