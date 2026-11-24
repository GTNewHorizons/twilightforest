package twilightforest.structures.icetower;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFIceTowerBeard extends StructureTFComponent {

    protected int size;
    protected int height;

    public ComponentTFIceTowerBeard() {}

    public ComponentTFIceTowerBeard(int i, ComponentTFTowerWing wing) {
        super(i);

        setCoordBaseMode(wing.getCoordBaseMode());
        size = wing.size;
        height = Math.round(size * 1.414F);

        deco = wing.deco;

        // just hang out at the very bottom of the tower
        this.boundingBox = new StructureBoundingBox(
                wing.getBoundingBox().minX,
                wing.getBoundingBox().minY - this.height,
                wing.getBoundingBox().minZ,
                wing.getBoundingBox().maxX,
                wing.getBoundingBox().minY,
                wing.getBoundingBox().maxZ);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("beardSize", this.size);
        par1NBTTagCompound.setInteger("beardHeight", this.height);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.size = par1NBTTagCompound.getInteger("beardSize");
        this.height = par1NBTTagCompound.getInteger("beardHeight");
    }

    /**
     * Makes a dark tower type beard
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                int columnHeight = Math.round((float) Math.sqrt(x * x + z * z));
                for (int y = 0; y < columnHeight; y++) {
                    placeBlockAtCurrentPosition(world, deco.blockID, deco.blockMeta, x, height - y, z, sbb);
                }
            }
        }
        return true;
    }

}
