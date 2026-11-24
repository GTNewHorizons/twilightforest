package twilightforest.structures.hollowtree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFTreasure;
import twilightforest.block.TFBlocks;
import twilightforest.entity.TFCreatures;
import twilightforest.structures.StructureTFComponent;

/**
 * A blob of leaves used to make trees
 *
 * @author Ben
 *
 */
public class ComponentTFHollowTreeLeafDungeon extends StructureTFComponent {

    private static final String NBT_RADIUS = "leafRadius";
    private static final String SPAWNER_MOB = "Swarm Spider";

    private static final int ROTATION_COUNT = 4;
    private static final int WOOD_SHELL_INSET = 1;
    private static final int HOLLOW_INTERIOR_INSET = 2;
    private static final int CHEST_X_OFFSET = 2;
    private static final int CHEST_Y_OFFSET = -1;
    private static final int LOG_METADATA = 12;
    private static final int BLOCK_UPDATE_FLAGS = 2;

    int radius; // radius

    public ComponentTFHollowTreeLeafDungeon() {
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
    protected ComponentTFHollowTreeLeafDungeon(int index, int x, int y, int z, int radius) {
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

        par1NBTTagCompound.setInteger(NBT_RADIUS, this.radius);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);

        // recover the radius from the saved bounding box if older nbt lacks the explicit field
        this.radius = par1NBTTagCompound.hasKey(NBT_RADIUS) ? par1NBTTagCompound.getInteger(NBT_RADIUS)
                : (this.boundingBox.maxX - this.boundingBox.minX) / 2;
    }

    /**
     * Add other structure components to this one if needed
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List<StructureComponent> list, Random rand) {
        // the bounding box should be cubical, so we can rotate freely
        this.setCoordBaseMode(rand.nextInt(ROTATION_COUNT));
    }

    /**
     * Draw a giant blob of whatevs (okay, it's going to be leaves).
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // leaves on the outside
        drawBlockBlob(world, sbb, radius, radius, radius, radius, TFBlocks.leaves, 0, true);
        // then wood
        if (radius >= WOOD_SHELL_INSET) {
            drawBlockBlob(
                    world,
                    sbb,
                    radius,
                    radius,
                    radius,
                    radius - WOOD_SHELL_INSET,
                    TFBlocks.log,
                    LOG_METADATA,
                    false);
        }
        // then air
        if (radius >= HOLLOW_INTERIOR_INSET) {
            drawBlockBlob(world, sbb, radius, radius, radius, radius - HOLLOW_INTERIOR_INSET, Blocks.air, 0, false);
        }

        // then treasure chest
        // which direction is this chest in?
        this.placeTreasureAtCurrentPosition(
                world,
                rand,
                radius + CHEST_X_OFFSET,
                radius + CHEST_Y_OFFSET,
                radius,
                TFTreasure.tree_cache,
                sbb);

        // then spawner
        placeSpawnerAtCurrentPosition(
                world,
                rand,
                radius,
                radius,
                radius,
                TFCreatures.getSpawnerNameFor(SPAWNER_MOB),
                sbb);

        return true;
    }

    private void drawBlockBlob(World world, StructureBoundingBox sbb, int sx, int sy, int sz, int blobRadius,
            Block blockID, int metadata, boolean isLeaves) {
        for (int[] offset : HollowTreeBlobGeometry.getOffsets(blobRadius)) {
            int x = sx + offset[0];
            int y = sy + offset[1];
            int z = sz + offset[2];
            if (isLeaves) {
                placeLeafBlock(world, blockID, metadata, x, y, z, sbb);
            } else {
                placeBlockAtCurrentPosition(world, blockID, metadata, x, y, z, sbb);
            }
        }
    }

    /**
     * Puts a block only if leaves can go there.
     */
    protected void placeLeafBlock(World world, Block blockID, int meta, int x, int y, int z, StructureBoundingBox sbb) {
        int offX = this.getXWithOffset(x, z);
        int offY = this.getYWithOffset(y);
        int offZ = this.getZWithOffset(x, z);

        if (sbb.isVecInside(offX, offY, offZ)) {
            Block whatsThere = world.getBlock(offX, offY, offZ);

            if (whatsThere == null || whatsThere.canBeReplacedByLeaves(world, offX, offY, offZ)) {
                world.setBlock(offX, offY, offZ, blockID, meta, BLOCK_UPDATE_FLAGS);
            }
        }
    }
}
