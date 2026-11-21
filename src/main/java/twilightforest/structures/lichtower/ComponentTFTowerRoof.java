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

        spawnListIndex = -1;

        // inheritors need to add a bounding box or die~!
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        tag.setInteger("roofSize", size);
        tag.setInteger("roofHeight", height);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        size = tag.getInteger("roofSize");
        height = tag.getInteger("roofHeight");
    }

    /**
     * Makes a bounding box that hangs forwards off of the tower wing we are on. This is for attached roofs.
     *
     * @param wing
     */
    protected void makeAttachedOverhangBB(ComponentTFTowerWing wing) {
        // just hang out at the very top of the tower
        int direction = getCoordBaseMode();
        if (direction < 0 || direction > 3) {
            return;
        }

        // keep the edge facing the parent tower and extend the other three sides
        setRoofBoundingBox(
                wing,
                direction == 0 ? 0 : -1,
                direction == 1 ? 0 : -1,
                direction == 2 ? 0 : 1,
                direction == 3 ? 0 : 1,
                height - 1);
    }

    /**
     * Makes a bounding box that sits at the top of the tower. Works for attached or freestanding roofs.
     *
     * @param wing
     */
    protected void makeCapBB(ComponentTFTowerWing wing) {
        setRoofBoundingBox(wing, 0, 0, 0, 0, height);
    }

    /**
     * Make a bounding box that hangs over the sides of the tower 1 block. Freestanding towers only.
     *
     * @param wing
     */
    protected void makeOverhangBB(ComponentTFTowerWing wing) {
        setRoofBoundingBox(wing, -1, -1, 1, 1, height - 1);
    }

    private void setRoofBoundingBox(ComponentTFTowerWing wing, int minXOffset, int minZOffset, int maxXOffset,
            int maxZOffset, int topOffset) {
        StructureBoundingBox wingBox = wing.getBoundingBox();
        boundingBox = new StructureBoundingBox(
                wingBox.minX + minXOffset,
                wingBox.maxY,
                wingBox.minZ + minZOffset,
                wingBox.maxX + maxXOffset,
                wingBox.maxY + topOffset,
                wingBox.maxZ + maxZOffset);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureboundingbox) {
        return false;
    }

    /**
     * Does this roof intersect anything except the parent tower?
     */
    public boolean fits(ComponentTFTowerWing parent, List<StructureComponent> list, Random rand) {
        return StructureComponent.findIntersecting(list, boundingBox) == parent;
    }
}
