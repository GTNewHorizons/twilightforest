package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFMazeDeadEnd extends StructureTFComponent {

    public ComponentTFMazeDeadEnd() {}

    public ComponentTFMazeDeadEnd(int componentType, int x, int y, int z, int rotation) {
        super(componentType);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 5, y + 5, z + 5);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        fillWithMetadataBlocks(world, bounds, 1, 1, 0, 4, 4, 0, Blocks.fence, 0, Blocks.air, 0, false);
        fillWithAir(world, bounds, 2, 1, 0, 3, 3, 0);
        return true;
    }
}
