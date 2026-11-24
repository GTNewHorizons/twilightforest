package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFMazeDeadEndTorches extends ComponentTFMazeDeadEnd {

    public ComponentTFMazeDeadEndTorches() {
        super();
    }

    public ComponentTFMazeDeadEndTorches(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // normal doorway
        super.addComponentParts(world, rand, sbb);

        // floor mounted torches need floor, floating torch walls break on update
        for (int z = 1; z <= 4; z++) {
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 1, 1, z, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 4, 1, z, sbb);
        }
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 2, 1, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.torch, 5, 3, 1, 4, sbb);

        return true;
    }
}
