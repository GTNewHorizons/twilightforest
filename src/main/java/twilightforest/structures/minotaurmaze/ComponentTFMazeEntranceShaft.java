package twilightforest.structures.minotaurmaze;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFMazeEntranceShaft extends StructureTFComponent {

    public ComponentTFMazeEntranceShaft() {
        super();
    }

    private int averageGroundLevel = -1;

    public ComponentTFMazeEntranceShaft(int i, Random rand, int x, int y, int z) {
        super(i);
        this.coordBaseMode = rand.nextInt(4);

        this.boundingBox = new StructureBoundingBox(x, y, z, x + 6 - 1, y + 14, z + 6 - 1);
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {}

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 15 - 1, 0);
        }

        this.fillWithMetadataBlocks(world, sbb, 0, 0 - 10, 0, 5, 0 + 30, 5, TFBlocks.mazestone, 1, Blocks.air, 0, true);
        this.fillWithAir(world, sbb, 1, 0 - 10, 1, 4, 0 + 30, 4);

        return true;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger("tfShaftGround", averageGroundLevel);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        averageGroundLevel = nbt.hasKey("tfShaftGround") ? nbt.getInteger("tfShaftGround") : -1;
    }

    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the
     * levels in the BB's horizontal rectangle).
     */
    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox) {
        int var3 = 0;
        int var4 = 0;

        for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5) {
            for (int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6) {
                if (par2StructureBoundingBox.isVecInside(var6, 64, var5)) {
                    var3 += Math.max(
                            par1World.getTopSolidOrLiquidBlock(var6, var5),
                            par1World.provider.getAverageGroundLevel());
                    ++var4;
                }
            }
        }

        if (var4 == 0) {
            return -1;
        } else {
            return var3 / var4;
        }
    }
}
