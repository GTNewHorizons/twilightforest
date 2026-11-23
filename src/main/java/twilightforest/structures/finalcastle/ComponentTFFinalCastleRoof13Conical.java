package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

// Pointy cone roof with variable height
public class ComponentTFFinalCastleRoof13Conical extends StructureTFComponent {

    private static final int BOTTOM_OVERHANG = 2;

    public int slope;

    public ComponentTFFinalCastleRoof13Conical() {}

    public ComponentTFFinalCastleRoof13Conical(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        this.slope = 2 + rand.nextInt(3) + rand.nextInt(3);

        // include the full spire, the point reaches local Y (7 * slope) + 2
        int height = (slope * 7) + 2;

        this.setCoordBaseMode(sideTower.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().maxY - 1 - BOTTOM_OVERHANG,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().maxY + height - 1,
                sideTower.getBoundingBox().maxZ + 2);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("slope", this.slope);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.slope = par1NBTTagCompound.getInteger("slope");
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int yOffset = this.boundingBox.getYSize() == (slope * 7) + 5 ? BOTTOM_OVERHANG : 0;
        int base = 2 - slope + yOffset;

        for (int rotation = 0; rotation < 4; rotation++) {
            this.fillBlocksRotated(
                    world,
                    sbb,
                    0,
                    yOffset - 1,
                    0,
                    3,
                    yOffset + 2,
                    3,
                    deco.blockID,
                    deco.blockMeta,
                    rotation);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, yOffset - 2, 2, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, yOffset - 2, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 2, yOffset - 2, 1, rotation, sbb);

            this.fillBlocksRotated(
                    world,
                    sbb,
                    4,
                    yOffset,
                    1,
                    12,
                    yOffset + 1,
                    1,
                    deco.blockID,
                    deco.blockMeta,
                    rotation);

            // more teeny crenellations
            for (int i = 3; i < 13; i += 2) {
                this.fillBlocksRotated(
                        world,
                        sbb,
                        i,
                        yOffset - 1,
                        1,
                        i,
                        yOffset + 2,
                        1,
                        deco.blockID,
                        deco.blockMeta,
                        rotation);
            }

            // cone roof
            for (int i = 2; i < 9; i++) {
                if (i < 7) {
                    this.fillBlocksRotated(
                            world,
                            sbb,
                            i - 1,
                            ((i - 1) * slope) + base,
                            i - 1,
                            i,
                            (i * slope) + base - 1,
                            i,
                            deco.blockID,
                            deco.blockMeta,
                            rotation);
                } else {
                    this.fillBlocksRotated(
                            world,
                            sbb,
                            16 - i,
                            ((i - 1) * slope) + base,
                            i,
                            16 - i,
                            (i * slope) + base - 1,
                            i,
                            deco.roofID,
                            deco.roofMeta,
                            rotation);
                }
                this.fillBlocksRotated(
                        world,
                        sbb,
                        i + 1,
                        ((i - 1) * slope) + base,
                        i,
                        15 - i,
                        (i * slope) + base - 1,
                        i,
                        deco.roofID,
                        deco.roofMeta,
                        rotation);
            }
            // point!
            this.fillBlocksRotated(
                    world,
                    sbb,
                    8,
                    (slope * 6) + 2 + yOffset,
                    8,
                    8,
                    (slope * 7) + 2 + yOffset,
                    8,
                    deco.roofID,
                    deco.roofMeta,
                    rotation);

        }
        return true;
    }
}
