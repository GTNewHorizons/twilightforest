package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFFinalCastleStairTower extends ComponentTFTowerWing {

    public ComponentTFFinalCastleStairTower() {}

    public ComponentTFFinalCastleStairTower(Random rand, int i, int x, int y, int z, int rotation) {
        super(i);

        this.setCoordBaseMode(rotation);
        this.size = 9;
        this.height = 51;
        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(x, y, z, -4, 0, -4, 8, 50, 8, 0);
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }
        // add crown
        ComponentTFFinalCastleRoof9Crenellated roof = new ComponentTFFinalCastleRoof9Crenellated(rand, 4, this);
        list.add(roof);
        roof.buildComponent(this, list, rand);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        Random decoRNG = new Random(
                world.getSeed() + (this.boundingBox.minX * 321534781) ^ (this.boundingBox.minZ * 756839));

        fillWithRandomizedBlocks(world, sbb, 0, 0, 0, 8, 49, 8, false, rand, deco.randomBlocks);

        // add branching runes
        int numBranches = 6 + decoRNG.nextInt(4);
        for (int i = 0; i < numBranches; i++) {
            makeGlyphBranches(world, decoRNG, this.getGlyphMeta(), sbb);
        }

        // beard
        for (int i = 1; i < 4; i++) {
            fillWithRandomizedBlocks(
                    world,
                    sbb,
                    i,
                    0 - (i * 2),
                    i,
                    8 - i,
                    1 - (i * 2),
                    8 - i,
                    false,
                    rand,
                    deco.randomBlocks);
        }
        this.placeBlockAtCurrentPosition(world, deco.blockID, deco.blockMeta, 4, -7, 4, sbb);

        // door, first floor
        this.fillWithMetadataBlocks(
                world,
                sbb,
                0,
                1,
                1,
                0,
                3,
                2,
                TFBlocks.castleDoor,
                this.getGlyphMeta(),
                Blocks.air,
                0,
                false);

        // stairs
        buildStairFlights(world, sbb, 5, 1, 2);

        // door, second floor
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                18,
                0,
                2,
                20,
                0,
                TFBlocks.castleDoor,
                this.getGlyphMeta(),
                Blocks.air,
                0,
                false);

        // second floor landing
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                17,
                1,
                3,
                17,
                3,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                17,
                4,
                2,
                17,
                4,
                deco.stairID,
                getStairMeta(3),
                deco.stairID,
                getStairMeta(3),
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                16,
                4,
                2,
                16,
                4,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                16,
                5,
                2,
                16,
                5,
                deco.stairID,
                getStairMeta(3),
                deco.stairID,
                getStairMeta(3),
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                15,
                5,
                2,
                15,
                5,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);

        // door, roof
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                39,
                0,
                2,
                41,
                0,
                TFBlocks.castleDoor,
                this.getGlyphMeta(),
                Blocks.air,
                0,
                false);

        // stairs
        buildStairFlights(world, sbb, 7, 18, 0);

        // roof access landing
        this.fillWithMetadataBlocks(
                world,
                sbb,
                1,
                38,
                1,
                3,
                38,
                5,
                deco.blockID,
                deco.blockMeta,
                deco.blockID,
                deco.blockMeta,
                false);
        this.fillWithMetadataBlocks(
                world,
                sbb,
                3,
                39,
                1,
                3,
                39,
                5,
                deco.fenceID,
                deco.fenceMeta,
                deco.fenceID,
                deco.fenceMeta,
                false);

        return true;
    }

    private void buildStairFlights(World world, StructureBoundingBox sbb, int count, int firstY, int rotationOffset) {
        for (int floor = 0; floor < count; floor++) {
            int rotation = (floor + rotationOffset) % 4;
            int y = firstY + (floor * 3);
            int stairMeta = getStairMeta(rotation);

            for (int i = 0; i < 3; i++) {
                int x = 3 + i;
                int stairY = y + i;
                this.placeBlockRotated(world, deco.stairID, stairMeta, x, stairY, 1, rotation, sbb);
                this.placeBlockRotated(world, deco.blockID, deco.blockMeta, x, stairY - 1, 1, rotation, sbb);
                this.placeBlockRotated(world, deco.stairID, stairMeta, x, stairY, 2, rotation, sbb);
                this.placeBlockRotated(world, deco.blockID, deco.blockMeta, x, stairY - 1, 2, rotation, sbb);
            }
            // landing
            this.fillBlocksRotated(world, sbb, 6, y + 2, 1, 7, y + 2, 2, deco.blockID, deco.blockMeta, rotation);
        }
    }

    public int getGlyphMeta() {
        return 1;
    }
}
