package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFMazeCorridor extends StructureTFComponent {

    public ComponentTFMazeCorridor() {}

    public ComponentTFMazeCorridor(int componentType, int x, int y, int z, int rotation) {
        super(componentType);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 5, y + 5, z + 5);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        // arch with a 2 block wide opening
        fillWithMetadataBlocks(world, bounds, 1, 1, 2, 4, 4, 3, Blocks.fence, 0, Blocks.air, 0, false);
        fillWithAir(world, bounds, 2, 1, 2, 3, 3, 3);
        return true;
    }
}
