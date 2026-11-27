package twilightforest.structures.finalcastle;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleDungeonForgeRoom extends StructureTFComponent {

    public ComponentTFFinalCastleDungeonForgeRoom() {}

    public ComponentTFFinalCastleDungeonForgeRoom(Random rand, int i, int x, int y, int z, int direction) {
        super(i);
        this.spawnListIndex = 3; // forge monsters
        this.setCoordBaseMode(direction);
        this.boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox2(x, y, z, -15, 0, -15, 50, 30, 50, direction);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        this.fillWithAir(world, sbb, 0, 0, 0, 50, 30, 50);

        // keep the original placeholder sign, no boss :(
        placeSignAtCurrentPosition(world, 25, 0, 25, "Mini-boss 2", "Gives talisman", sbb);

        return true;
    }
}
