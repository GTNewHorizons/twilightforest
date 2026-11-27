package twilightforest.structures.minotaurmaze;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFMazeMound extends StructureTFComponent {

    public ComponentTFMazeMound() {
        super();
    }

    public static final int DIAMETER = 35;
    private static final int[] HEIGHT_PROFILE = createHeightProfile();

    private static int[] createHeightProfile() {
        int[] heights = new int[DIAMETER];
        for (int distance = 0; distance < heights.length; distance++) {
            heights[distance] = (int) (Math.cos((double) distance / DIAMETER * Math.PI) * (DIAMETER / 3));
        }
        return heights;
    }

    private int averageGroundLevel = -1;

    private ComponentTFMazeUpperEntrance mazeAbove;

    public ComponentTFMazeMound(int i, Random rand, int x, int y, int z) {
        super(i);
        this.coordBaseMode = rand.nextInt(4);

        this.boundingBox = new StructureBoundingBox(x, y, z, x + DIAMETER - 1, y + DIAMETER / 3, z + DIAMETER - 1);
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        // add aboveground maze entrance building
        mazeAbove = new ComponentTFMazeUpperEntrance(
                3,
                random,
                boundingBox.minX + 10,
                boundingBox.minY + 0,
                boundingBox.minZ + 10);
        list.add(mazeAbove);
        mazeAbove.buildComponent(this, list, random);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {

        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            // keep the base at surface - 1 even though the box includes the taller mound peak
            int offset = this.averageGroundLevel - this.boundingBox.minY - 1;

            this.boundingBox.offset(0, offset, 0);

            if (this.mazeAbove != null) {
                mazeAbove.getBoundingBox().offset(0, offset, 0);
            }
        }

        for (int x = 0; x < DIAMETER; x++) {
            for (int z = 0; z < DIAMETER; z++) {
                int cx = x - DIAMETER / 2;
                int cz = z - DIAMETER / 2;

                int dist = (int) Math.sqrt(cx * cx + cz * cz);
                int hheight = HEIGHT_PROFILE[dist];

                // leave a hole in the middle
                if (!(cx <= 2 && cx >= -1 && cz <= 2 && cz >= -1)
                        && ((!(cx <= 2 && cx >= -1) && !(cz <= 2 && cz >= -1)) || hheight > 6)) {
                    this.placeBlockAtCurrentPosition(world, Blocks.grass, 0, x, hheight, z, sbb);

                    // only fill to the bottom when we're not in the entrances
                    if (!(cx <= 2 && cx >= -1) && !(cz <= 2 && cz >= -1)) {
                        this.func_151554_b(world, Blocks.dirt, 0, x, hheight - 1, z, sbb);
                    } else if (hheight > 6) {
                        this.fillWithBlocks(world, sbb, x, 6, z, x, hheight - 1, z, Blocks.dirt, Blocks.air, false);
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger("tfMoundGround", averageGroundLevel);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        averageGroundLevel = nbt.hasKey("tfMoundGround") ? nbt.getInteger("tfMoundGround") : -1;
    }

    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the
     * levels in the BB's horizontal rectangle).
     */
    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox) {
        int totalHeight = 0;
        int totalMeasures = 0;

        for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5) {
            for (int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6) {
                if (par2StructureBoundingBox.isVecInside(var6, 64, var5)) {
                    totalHeight += Math.max(
                            par1World.getTopSolidOrLiquidBlock(var6, var5),
                            par1World.provider.getAverageGroundLevel());
                    ++totalMeasures;
                }
            }
        }

        if (totalMeasures == 0) {
            return -1;
        } else {
            return totalHeight / totalMeasures;
        }
    }

}
