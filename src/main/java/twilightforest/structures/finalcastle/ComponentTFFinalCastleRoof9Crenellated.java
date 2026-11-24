package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleRoof9Crenellated extends StructureTFComponent {

    private static final int ROOF_HEIGHT = 5;
    private static final int ROTATIONS = 4;
    private static final int BOTTOM_OVERHANG = 2;

    public ComponentTFFinalCastleRoof9Crenellated() {}

    public ComponentTFFinalCastleRoof9Crenellated(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        this.setCoordBaseMode(sideTower.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().maxY - 1 - BOTTOM_OVERHANG,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().maxY + ROOF_HEIGHT - 1,
                sideTower.getBoundingBox().maxZ + 2);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent != null && parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int yOffset = boundingBox.getYSize() > ROOF_HEIGHT + 1 ? BOTTOM_OVERHANG : 0;
        for (int rotation = 0; rotation < ROTATIONS; rotation++) {
            this.fillBlocksRotated(
                    world,
                    sbb,
                    0,
                    yOffset - 1,
                    0,
                    2,
                    yOffset + 3,
                    2,
                    deco.blockID,
                    deco.blockMeta,
                    rotation);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, yOffset - 2, 2, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, yOffset - 2, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 2, yOffset - 2, 1, rotation, sbb);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 3, yOffset, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 3, yOffset + 1, 1, rotation, sbb);

            this.fillBlocksRotated(
                    world,
                    sbb,
                    4,
                    yOffset,
                    0,
                    5,
                    yOffset + 3,
                    2,
                    deco.blockID,
                    deco.blockMeta,
                    rotation);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 6, yOffset, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 6, yOffset + 1, 1, rotation, sbb);

            this.fillBlocksRotated(
                    world,
                    sbb,
                    7,
                    yOffset,
                    0,
                    8,
                    yOffset + 3,
                    2,
                    deco.blockID,
                    deco.blockMeta,
                    rotation);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 9, yOffset, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 9, yOffset + 1, 1, rotation, sbb);
        }
        return true;
    }
}
