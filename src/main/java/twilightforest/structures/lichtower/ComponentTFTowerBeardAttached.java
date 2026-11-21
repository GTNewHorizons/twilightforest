
package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFTowerBeardAttached extends ComponentTFTowerBeard {

    public ComponentTFTowerBeardAttached() {
        super();
    }

    public ComponentTFTowerBeardAttached(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // just hang out at the very bottom of the tower
        StructureBoundingBox wingBox = wing.getBoundingBox();
        boundingBox = new StructureBoundingBox(
                wingBox.minX,
                wingBox.minY - height - 1,
                wingBox.minZ,
                wingBox.maxX,
                wingBox.minY - 1,
                wingBox.maxZ);
    }

    // makes a pyramid shaped beard
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int y = 0; y <= height; y++) {
            int min = y + 1;
            int max = size - y;
            int layerY = height - y;

            fillWithRandomizedBlocks(
                    world,
                    sbb,
                    0,
                    layerY,
                    min,
                    max,
                    layerY,
                    max,
                    false,
                    rand,
                    StructureTFComponent.getStrongholdStones());
        }

        return true;
    }
}
