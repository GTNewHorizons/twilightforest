package twilightforest.structures.icetower;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFIceTowerStairs extends ComponentTFTowerWing {

    public ComponentTFIceTowerStairs() {}

    public ComponentTFIceTowerStairs(int index, int x, int y, int z, int size, int height, int direction) {
        super(index, x, y, z, size, height, direction);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        for (int x = 1; x < this.size; x++) {

            this.placeStairs(world, sbb, x, 1 - x, 5);

            for (int z = 0; z <= x; z++) {

                if (z > 0 && z <= this.size / 2) {
                    this.placeStairs(world, sbb, x, 1 - x, 5 - z);
                    this.placeStairs(world, sbb, x, 1 - x, 5 + z);
                }

                if (x <= this.size / 2) {
                    this.placeStairs(world, sbb, z, 1 - x, 5 - x);
                    this.placeStairs(world, sbb, z, 1 - x, 5 + x);
                }

            }

        }

        this.func_151554_b(world, deco.blockID, deco.blockMeta, 0, 0, 5, sbb);

        return true;
    }

    private void placeStairs(World world, StructureBoundingBox sbb, int x, int y, int z) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        if (!sbb.isVecInside(worldX, worldY, worldZ)
                || !world.getBlock(worldX, worldY, worldZ).isReplaceable(world, worldX, worldY, worldZ)) {
            return;
        }

        // keep the existing solid-block steps, place a foundation only for successful steps
        placeBlockAtCurrentPosition(world, deco.blockID, deco.blockMeta, x, y, z, sbb);
        func_151554_b(world, deco.blockID, deco.blockMeta, x, y - 1, z, sbb);
    }
}
