package twilightforest.structures.hollowtree;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

/**
 * A blob of leaves used to make trees
 *
 * @author Ben
 *
 */
public class ComponentTFLeafSphere extends StructureTFComponent {

    private static final int LEAF_UPDATE_FLAGS = 2;

    int radius;

    public ComponentTFLeafSphere() {
        super();
    }

    /**
     * Make a blob of leaves
     *
     * @param index
     * @param x
     * @param y
     * @param z
     * @param radius
     */
    protected ComponentTFLeafSphere(int index, int x, int y, int z, int radius) {
        super(index);

        this.setCoordBaseMode(0);

        boundingBox = new StructureBoundingBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        this.radius = radius;
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("leafRadius", this.radius);

    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);

        this.radius = par1NBTTagCompound.hasKey("leafRadius") ? par1NBTTagCompound.getInteger("leafRadius")
                : (boundingBox.maxX - boundingBox.minX) / 2;
    }

    /**
     * Draw a giant blob of whatevs (okay, it's going to be leaves).
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        for (int[] offset : HollowTreeBlobGeometry.getOffsets(radius)) {
            placeBlockIfEmpty(
                    world,
                    TFBlocks.leaves,
                    0,
                    radius + offset[0],
                    radius + offset[1],
                    radius + offset[2],
                    sbb);
        }
        return true;
    }

    protected void placeBlockIfEmpty(World world, Block block, int meta, int x, int y, int z,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        if (sbb.isVecInside(worldX, worldY, worldZ) && world.getBlock(worldX, worldY, worldZ) == Blocks.air) {
            world.setBlock(worldX, worldY, worldZ, block, meta, LEAF_UPDATE_FLAGS);
        }
    }

}
