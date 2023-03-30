package twilightforest.structures.lichtower;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public abstract class ComponentTFTowerRoof extends StructureTFComponent {

    protected int size;
    protected int height;

    public ComponentTFTowerRoof() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ComponentTFTowerRoof(int i, ComponentTFTowerWing wing) {
        super(i);

        this.spawnListIndex = -1;

        // inheritors need to add a bounding box or die~!
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("roofSize", this.size);
        par1NBTTagCompound.setInteger("roofHeight", this.height);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.size = par1NBTTagCompound.getInteger("roofSize");
        this.height = par1NBTTagCompound.getInteger("roofHeight");
    }

    /**
     * Makes a bounding box that hangs forwards off of the tower wing we are on. This is for attached roofs.
     * 
     * @param wing
     */
    protected void makeAttachedOverhangBB(ComponentTFTowerWing wing) {
        // just hang out at the very top of the tower
        switch (getCoordBaseMode()) {
            case 0 -> this.boundingBox = new StructureBoundingBox(
                    wing.getBoundingBox().minX,
                    wing.getBoundingBox().maxY,
                    wing.getBoundingBox().minZ - 1,
                    wing.getBoundingBox().maxX + 1,
                    wing.getBoundingBox().maxY + this.height - 1,
                    wing.getBoundingBox().maxZ + 1);
            case 1 -> this.boundingBox = new StructureBoundingBox(
                    wing.getBoundingBox().minX - 1,
                    wing.getBoundingBox().maxY,
                    wing.getBoundingBox().minZ,
                    wing.getBoundingBox().maxX + 1,
                    wing.getBoundingBox().maxY + this.height - 1,
                    wing.getBoundingBox().maxZ + 1);
            case 2 -> this.boundingBox = new StructureBoundingBox(
                    wing.getBoundingBox().minX - 1,
                    wing.getBoundingBox().maxY,
                    wing.getBoundingBox().minZ - 1,
                    wing.getBoundingBox().maxX,
                    wing.getBoundingBox().maxY + this.height - 1,
                    wing.getBoundingBox().maxZ + 1);
            case 3 -> this.boundingBox = new StructureBoundingBox(
                    wing.getBoundingBox().minX - 1,
                    wing.getBoundingBox().maxY,
                    wing.getBoundingBox().minZ - 1,
                    wing.getBoundingBox().maxX + 1,
                    wing.getBoundingBox().maxY + this.height - 1,
                    wing.getBoundingBox().maxZ);
        }
    }

    /**
     * Makes a bounding box that sits at the top of the tower. Works for attached or freestanding roofs.
     * 
     * @param wing
     */
    protected void makeCapBB(ComponentTFTowerWing wing) {
        this.boundingBox = new StructureBoundingBox(
                wing.getBoundingBox().minX,
                wing.getBoundingBox().maxY,
                wing.getBoundingBox().minZ,
                wing.getBoundingBox().maxX,
                wing.getBoundingBox().maxY + this.height,
                wing.getBoundingBox().maxZ);
    }

    /**
     * Make a bounding box that hangs over the sides of the tower 1 block. Freestanding towers only.
     * 
     * @param wing
     */
    protected void makeOverhangBB(ComponentTFTowerWing wing) {
        this.boundingBox = new StructureBoundingBox(
                wing.getBoundingBox().minX - 1,
                wing.getBoundingBox().maxY,
                wing.getBoundingBox().minZ - 1,
                wing.getBoundingBox().maxX + 1,
                wing.getBoundingBox().maxY + this.height - 1,
                wing.getBoundingBox().maxZ + 1);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureboundingbox) {
        return false;
    }

    /**
     * Does this roof intersect anything except the parent tower?
     */
    public boolean fits(ComponentTFTowerWing parent, List<StructureComponent> list, Random rand) {
        return StructureComponent.findIntersecting(list, this.boundingBox) == parent;
    }

}
