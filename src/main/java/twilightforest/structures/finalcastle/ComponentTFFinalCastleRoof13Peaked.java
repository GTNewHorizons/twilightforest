package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleRoof13Peaked extends StructureTFComponent {

    private static final int BOTTOM_OVERHANG = 2;

    public ComponentTFFinalCastleRoof13Peaked() {}

    public ComponentTFFinalCastleRoof13Peaked(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        // the decorative ridge reaches local Y 19, not 18
        int height = 19;

        this.setCoordBaseMode(sideTower.getCoordBaseMode());
        this.boundingBox = new StructureBoundingBox(
                sideTower.getBoundingBox().minX - 2,
                sideTower.getBoundingBox().maxY - 1 - BOTTOM_OVERHANG,
                sideTower.getBoundingBox().minZ - 2,
                sideTower.getBoundingBox().maxX + 2,
                sideTower.getBoundingBox().maxY + height - 1,
                sideTower.getBoundingBox().maxZ + 2);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int yOffset = getRoofYOffset();

        // peaky roof, loop unrolled as it was getting dumb
        for (int i = 0; i < 3; i++) {
            fillRoof(world, sbb, 1, i, i, 15, i, i);
            fillRoof(world, sbb, 1, i, 16 - i, 15, i, 16 - i);
        }

        for (int i = 0; i < 3; i++) {
            int lowerY = 5 + ((i - 1) * 2);
            int upperY = 4 + (i * 2);
            fillRoofLayer(world, sbb, 3 + i, lowerY, upperY, true);
            fillRoofLayer(world, sbb, 13 - i, lowerY, upperY, false);
        }

        for (int i = 0; i < 3; i++) {
            int lowerY = 12 + ((i - 1) * 3);
            int upperY = 11 + (i * 3);
            fillRoofLayer(world, sbb, 6 + i, lowerY, upperY, true);
            fillRoofLayer(world, sbb, 10 - i, lowerY, upperY, false);
        }

        // top roof bobbles
        fillRoof(world, sbb, 1, 18, 8, 5, 18, 8);
        fillRoof(world, sbb, 11, 18, 8, 14, 18, 8);
        fillRoof(world, sbb, 0, 17, 8, 1, 19, 8);
        fillRoof(world, sbb, 15, 17, 8, 16, 19, 8);

        for (int rotation = 1; rotation < 4; rotation += 2) {
            // this might be one of my more confusing instances of code recycling
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
        }

        // corners
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
        }
        return true;
    }

    private int getRoofYOffset() {
        return this.boundingBox.getYSize() == 22 ? BOTTOM_OVERHANG : 0;
    }

    private void fillRoofLayer(World world, StructureBoundingBox sbb, int z, int lowerY, int upperY, boolean nearSide) {
        fillRoof(world, sbb, 2, lowerY, z, 14, upperY, z);
        for (int x = 1; x <= 15; x += 14) {
            fillRoof(world, sbb, x, 1, z, x, lowerY, z);
            fillWall(world, sbb, x, lowerY, nearSide ? z - 1 : z, x, upperY, nearSide ? z : z + 1);
        }
    }

    private void fillRoof(World world, StructureBoundingBox sbb, int x1, int y1, int z1, int x2, int y2, int z2) {
        int yOffset = getRoofYOffset();
        this.fillWithMetadataBlocks(
                world,
                sbb,
                x1,
                y1 + yOffset,
                z1,
                x2,
                y2 + yOffset,
                z2,
                deco.roofID,
                deco.roofMeta,
                deco.roofID,
                deco.roofMeta,
                false);
    }

    private void fillWall(World world, StructureBoundingBox sbb, int x1, int y1, int z1, int x2, int y2, int z2) {
        int yOffset = getRoofYOffset();
        this.fillWithMetadataBlocks(
                world,
                sbb,
                x1,
                y1 + yOffset,
                z1,
                x2,
                y2 + yOffset,
                z2,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);
    }
}
