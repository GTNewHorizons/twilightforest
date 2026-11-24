package twilightforest.structures.icetower;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.lichtower.ComponentTFTowerRoof;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFIceTowerRoof extends ComponentTFTowerRoof {

    public ComponentTFIceTowerRoof() {}

    public ComponentTFIceTowerRoof(int i, ComponentTFTowerWing wing) {
        super(i, wing);

        setCoordBaseMode(wing.getCoordBaseMode());
        size = wing.size;
        // the farthest corner is sqrt(2) * (size - 1) blocks high
        // the roof bounding box must include that corner for chunk generation to visit it
        int edge = size - 1;
        height = Math.round((float) Math.sqrt(2 * edge * edge));

        deco = wing.deco;

        // just hang out at the very top of the tower
        makeCapBB(wing);
    }

    /**
     * Swoopy ice roof
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                int columnHeight = Math.round((float) Math.sqrt(x * x + z * z));
                for (int y = 0; y < columnHeight; y++) {
                    placeBlockAtCurrentPosition(world, deco.blockID, deco.blockMeta, x, y, z, sbb);
                }
            }
        }

        return true;
    }

}
