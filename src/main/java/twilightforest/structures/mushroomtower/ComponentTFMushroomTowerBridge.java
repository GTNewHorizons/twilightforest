package twilightforest.structures.mushroomtower;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFMushroomTowerBridge extends ComponentTFMushroomTowerWing {

    int dSize;
    int dHeight;

    public ComponentTFMushroomTowerBridge() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected ComponentTFMushroomTowerBridge(int i, int x, int y, int z, int pSize, int pHeight, int direction) {
        super(i, x, y, z, pSize, pHeight, direction);

        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, size - 1, height - 1, 3, direction);

        this.dSize = pSize;
        this.dHeight = pHeight;
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("destSize", this.dSize);
        par1NBTTagCompound.setInteger("destHeight", this.dHeight);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.dSize = par1NBTTagCompound.getInteger("destSize");
        this.dHeight = par1NBTTagCompound.getInteger("destHeight");
    }

    @Override
    public void buildComponent(StructureComponent parent, List list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        int[] dest = new int[] { dSize - 1, 1, 1 };

        boolean madeWing = makeTowerWing(list, rand, this.getComponentType(), dest[0], dest[1], dest[2], dSize, dHeight, 0);

        if (!madeWing) {
            int[] dx = offsetTowerCoords(dest[0], dest[1], dest[2], dSize, 0);

            FMLLog.fine("[TwilightForest] Making tower wing failed when bridge was already made.  Size = " + dSize + ", x = " + dx[0] + " z = " + dx[2]);
        }
    }

    public StructureBoundingBox getWingBB() {
        int[] dest = offsetTowerCoords(dSize - 1, 1, 1, dSize, this.getCoordBaseMode());
        return StructureTFComponent.getComponentToAddBoundingBox(dest[0], dest[1], dest[2], 0, 0, 0, dSize - 1, dHeight - 1, dSize - 1, this.getCoordBaseMode());

    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        // make walls
        for (int x = 0; x < dSize; x++) {
            placeBlockAtCurrentPosition(world, deco.fenceID, deco.fenceMeta, x, 1, 0, sbb);
            placeBlockAtCurrentPosition(world, deco.fenceID, deco.fenceMeta, x, 1, 2, sbb);

            placeBlockAtCurrentPosition(world, deco.floorID, this.isAscender ? 3 : deco.floorMeta, x, 0, 1, sbb);
        }

        // clear bridge walkway
        this.fillWithAir(world, sbb, 0, 1, 1, 2, 2, 1);

        return true;
    }

}
