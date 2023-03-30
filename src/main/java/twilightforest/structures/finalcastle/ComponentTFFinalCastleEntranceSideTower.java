package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleEntranceSideTower extends ComponentTFFinalCastleMazeTower13 {

    public ComponentTFFinalCastleEntranceSideTower() {}

    public ComponentTFFinalCastleEntranceSideTower(Random rand, int i, int x, int y, int z, int floors,
            int entranceFloor, int direction) {
        super(rand, i, x, y, z, floors, entranceFloor, 0, direction);

        addOpening(0, 1, size / 2, 2);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        // add foundation
        ComponentTFFinalCastleFoundation13 foundation = new ComponentTFFinalCastleFoundation13(rand, 4, this);
        list.add(foundation);
        foundation.buildComponent(this, list, rand);

        // add roof
        StructureTFComponent roof = new ComponentTFFinalCastleRoof13Peaked(rand, 4, this);
        list.add(roof);
        roof.buildComponent(this, list, rand);
    }
}
