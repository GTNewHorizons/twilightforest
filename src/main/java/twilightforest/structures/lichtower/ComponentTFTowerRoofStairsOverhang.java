
package twilightforest.structures.lichtower;

import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFTowerRoofStairsOverhang extends ComponentTFTowerRoofStairs {

    public ComponentTFTowerRoofStairsOverhang() {
        super();
    }

    // makes a pyramid shaped roof out of stairs
    public ComponentTFTowerRoofStairsOverhang(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        // always facing = 0. This roof cant rotate, due to stair facing issues

        size = wing.size + 2; // assuming only square towers and roofs right now
        height = size / 2;

        // just hang out at the very top of the tower
        StructureBoundingBox wingBox = wing.getBoundingBox();
        boundingBox = new StructureBoundingBox(
                wingBox.minX - 1,
                wingBox.maxY,
                wingBox.minZ - 1,
                wingBox.maxX + 1,
                wingBox.maxY + height - 1,
                wingBox.maxZ + 1);
    }
}
