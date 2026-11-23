package twilightforest.structures.darktower;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFDarkTowerMainBridge extends ComponentTFDarkTowerBridge {

    public ComponentTFDarkTowerMainBridge() {
        super();
    }

    protected ComponentTFDarkTowerMainBridge(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, 15, pHeight, direction);
    }

    @Override
    public StructureBoundingBox getWingBB() {
        int[] position = offsetTowerCoords(4, 1, 2, 19, getCoordBaseMode());
        // the next main tower is 19 wide and up to 86 tall, regardless of the bridges target size
        return StructureTFComponent.getComponentToAddBoundingBox(
                position[0],
                position[1],
                position[2],
                0,
                0,
                0,
                18,
                85,
                18,
                getCoordBaseMode());
    }

    @Override
    public boolean makeTowerWing(List<StructureComponent> list, Random rand, int index, int x, int y, int z,
            int wingSize, int wingHeight, int rotation) {
        // make another size 15 main tower
        int direction = (getCoordBaseMode() + rotation) % 4;
        int[] dx = offsetTowerCoords(x, y, z, 19, direction);

        ComponentTFDarkTowerMain wing = new ComponentTFDarkTowerMain(null, rand, index, dx[0], dx[1], dx[2], direction);

        list.add(wing);
        wing.buildComponent(this, list, rand);
        addOpening(x, y, z, rotation);
        return true;
    }
}
