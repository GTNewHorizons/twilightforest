package twilightforest.structures.lichtower;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFTowerBeard extends StructureTFComponent {

    int size;
    int height;

    public ComponentTFTowerBeard() {
        super();
    }

    public ComponentTFTowerBeard(int i, ComponentTFTowerWing wing) {
        super(i);

        setCoordBaseMode(wing.getCoordBaseMode());
        size = wing.size - 2;
        height = size / 2;

        // just hang out at the very bottom of the tower
        StructureBoundingBox wingBox = wing.getBoundingBox();
        boundingBox = new StructureBoundingBox(
                wingBox.minX + 1,
                wingBox.minY - height - 1,
                wingBox.minZ + 1,
                wingBox.maxX - 1,
                wingBox.minY - 1,
                wingBox.maxZ - 1);
    }

    // save to nbt
    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);

        nbt.setInteger("beardSize", size);
        nbt.setInteger("beardHeight", height);
    }

    // load from nbt
    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);

        size = nbt.getInteger("beardSize");
        height = nbt.getInteger("beardHeight");
    }

    // makes a pyramid-shaped beard
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int y = 0; y <= height; y++) {
            int min = y;
            int max = size - y - 1;
            if (min > max) break;
            int layerY = height - y;

            fillWithRandomizedBlocks(
                    world,
                    sbb,
                    min,
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
