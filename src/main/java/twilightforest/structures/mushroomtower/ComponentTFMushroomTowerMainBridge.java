package twilightforest.structures.mushroomtower;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFMushroomTowerMainBridge extends ComponentTFMushroomTowerBridge {

    public ComponentTFMushroomTowerMainBridge() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected ComponentTFMushroomTowerMainBridge(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        // bridge only 11 long
        super(i, x, y, z, 11, pHeight, direction);
    }

    @Override
    public StructureBoundingBox getWingBB() {
        int[] dest = offsetTowerCoords(dSize - 1, 1, 1, MAIN_SIZE, getCoordBaseMode());
        return StructureTFComponent.getComponentToAddBoundingBox(
                dest[0],
                dest[1],
                dest[2],
                0,
                0,
                0,
                MAIN_SIZE - 1,
                dHeight - 1,
                MAIN_SIZE - 1,
                getCoordBaseMode());
    }

    @Override
    public boolean makeTowerWing(List list, Random rand, int index, int x, int y, int z, int wingSize, int wingHeight,
            int rotation) {

        // make a new size 15 main tower
        int direction = (getCoordBaseMode() + rotation) % 4;
        int[] dx = offsetTowerCoords(x, y, z, 15, direction);

        // adjust coordinates to fit an existing tower
        dx = adjustCoordinates(dx[0], dx[1], dx[2], 15, direction, list);

        ComponentTFMushroomTowerMain wing = new ComponentTFMushroomTowerMain(
                index,
                dx[0],
                dx[1],
                dx[2],
                MAIN_SIZE,
                wingHeight,
                direction);

        // main bridges used to create a tower even when it overlapped an existing room
        for (Object candidate : list) {
            if (candidate instanceof StructureComponent component && component != this
                    && !(component instanceof ComponentTFTowerRoofMushroom)
                    && !(component instanceof ComponentTFMushroomTowerBridge)
                    && component.getBoundingBox() != null
                    && component.getBoundingBox().intersectsWith(wing.getBoundingBox())) {
                return false;
            }
        }

        if (list.isEmpty() || !(list.get(0) instanceof StructureComponent)) {
            return false;
        }
        list.add(wing);
        wing.buildComponent((StructureComponent) list.get(0), list, rand);
        addOpening(x, y, z, rotation);

        return true;
    }

}
