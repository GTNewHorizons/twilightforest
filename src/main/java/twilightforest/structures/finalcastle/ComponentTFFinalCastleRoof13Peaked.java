package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.structures.StructureTFComponent;

public class ComponentTFFinalCastleRoof13Peaked extends StructureTFComponent {

    public ComponentTFFinalCastleRoof13Peaked() {}

    public ComponentTFFinalCastleRoof13Peaked(Random rand, int i, StructureTFComponent sideTower) {
        super(i);

        int height = 18;

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

        // peaky roof, loop unrolled as it was getting dumb
        for (int i = 0; i < 3; i++) {
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    i,
                    i,
                    15,
                    i,
                    i,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    i,
                    16 - i,
                    15,
                    i,
                    16 - i,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
        }

        for (int i = 0; i < 3; i++) {
            int dz = 3 + i;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    2,
                    5 + ((i - 1) * 2),
                    dz,
                    14,
                    4 + (i * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    1,
                    dz,
                    1,
                    5 + ((i - 1) * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    5 + ((i - 1) * 2),
                    dz - 1,
                    1,
                    4 + (i * 2),
                    dz,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    1,
                    dz,
                    15,
                    5 + ((i - 1) * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    5 + ((i - 1) * 2),
                    dz - 1,
                    15,
                    4 + (i * 2),
                    dz,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);

            dz = 13 - i;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    2,
                    5 + ((i - 1) * 2),
                    dz,
                    14,
                    4 + (i * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    1,
                    dz,
                    1,
                    5 + ((i - 1) * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    5 + ((i - 1) * 2),
                    dz,
                    1,
                    4 + (i * 2),
                    dz + 1,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    1,
                    dz,
                    15,
                    5 + ((i - 1) * 2),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    5 + ((i - 1) * 2),
                    dz,
                    15,
                    4 + (i * 2),
                    dz + 1,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
        }

        for (int i = 0; i < 3; i++) {
            int dz = 6 + i;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    2,
                    12 + ((i - 1) * 3),
                    dz,
                    14,
                    11 + (i * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    1,
                    dz,
                    1,
                    12 + ((i - 1) * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    12 + ((i - 1) * 3),
                    dz - 1,
                    1,
                    11 + (i * 3),
                    dz,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    1,
                    dz,
                    15,
                    12 + ((i - 1) * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    12 + ((i - 1) * 3),
                    dz - 1,
                    15,
                    11 + (i * 3),
                    dz,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);

            dz = 10 - i;
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    2,
                    12 + ((i - 1) * 3),
                    dz,
                    14,
                    11 + (i * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    1,
                    dz,
                    1,
                    12 + ((i - 1) * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    1,
                    12 + ((i - 1) * 3),
                    dz,
                    1,
                    11 + (i * 3),
                    dz + 1,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    1,
                    dz,
                    15,
                    12 + ((i - 1) * 3),
                    dz,
                    deco.roofID,
                    deco.roofMeta,
                    deco.roofID,
                    deco.roofMeta,
                    false);
            this.fillWithMetadataBlocks(
                    world,
                    sbb,
                    15,
                    12 + ((i - 1) * 3),
                    dz,
                    15,
                    11 + (i * 3),
                    dz + 1,
                    deco.blockID,
                    deco.blockMeta,
                    deco.blockID,
                    deco.blockMeta,
                    false);
        }

        // top roof bobbles
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                18,
                8,
                5,
                18,
                8,
                deco.roofID,
                deco.roofMeta,
                deco.roofID,
                deco.roofMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                11,
                18,
                8,
                14,
                18,
                8,
                deco.roofID,
                deco.roofMeta,
                deco.roofID,
                deco.roofMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                17,
                8,
                1,
                19,
                8,
                deco.roofID,
                deco.roofMeta,
                deco.roofID,
                deco.roofMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                15,
                17,
                8,
                16,
                19,
                8,
                deco.roofID,
                deco.roofMeta,
                deco.roofID,
                deco.roofMeta,
                false);

        for (int rotation = 1; rotation < 4; rotation += 2) {
            // this might be one of my more confusing instances of code recycling
            this.fillBlocksRotated(world, sbb, 4, 0, 1, 12, 1, 1, deco.blockID, deco.blockMeta, rotation);
            // more teeny crenellations
            for (int i = 3; i < 13; i += 2) {
                this.fillBlocksRotated(world, sbb, i, -1, 1, i, 2, 1, deco.blockID, deco.blockMeta, rotation);
            }
        }

        // corners
        for (int rotation = 0; rotation < 4; rotation++) {
            this.fillBlocksRotated(world, sbb, 0, -1, 0, 3, 2, 3, deco.blockID, deco.blockMeta, rotation);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, -2, 2, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 1, -2, 1, rotation, sbb);
            this.placeBlockRotated(world, deco.blockID, deco.blockMeta, 2, -2, 1, rotation, sbb);
        }
        return true;
    }
}
