package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleRoof9Crenellated extends StructureTFComponent {

    public ComponentTFFinalCastleRoof9Crenellated() {}

    public ComponentTFFinalCastleRoof9Crenellated(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        int height = 5;

        this.setCoordBaseMode(sideTower.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().maxY - 1,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().maxY + height - 1,
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
        for (int rotation = 0; rotation < 4; rotation++) {
            this.fillBlocksRotated(world, sbb, 0, -1, 0, 2, 3, 2, deco.blockID, deco.blockMeta, rotation);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, -2, 2, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, -2, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 2, -2, 1, rotation, sbb);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 3, 0, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 3, 1, 1, rotation, sbb);

            this.fillBlocksRotated(world, sbb, 4, 0, 0, 5, 3, 2, deco.blockID, deco.blockMeta, rotation);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 6, 0, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 6, 1, 1, rotation, sbb);

            this.fillBlocksRotated(world, sbb, 7, 0, 0, 8, 3, 2, deco.blockID, deco.blockMeta, rotation);

            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 9, 0, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 9, 1, 1, rotation, sbb);
        }
        return true;
    }
}
