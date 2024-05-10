package twilightforest.structures.courtyard;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceDuct extends ComponentTFNagaCourtyardTerraceAbstract {

    public ComponentTFNagaCourtyardTerraceDuct() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceDuct(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        if (coordBaseMode == 0 || coordBaseMode == 3) {
            ComponentTFNagaCourtyardTerraceDuctPart ductPart = new ComponentTFNagaCourtyardTerraceDuctPart(
                    1,
                    boundingBox.minX + 13,
                    boundingBox.minY,
                    boundingBox.minZ + 6,
                    0);
            list.add(ductPart);
            ductPart.buildComponent(this, list, random);
        }

        if (coordBaseMode == 3 || coordBaseMode == 2) {
            ComponentTFNagaCourtyardTerraceDuctPart ductPart = new ComponentTFNagaCourtyardTerraceDuctPart(
                    1,
                    boundingBox.minX + 6,
                    boundingBox.minY,
                    boundingBox.minZ - 1,
                    3);
            list.add(ductPart);
            ductPart.buildComponent(this, list, random);
        }

        if (coordBaseMode == 2 || coordBaseMode == 1) {
            ComponentTFNagaCourtyardTerraceDuctPart ductPart = new ComponentTFNagaCourtyardTerraceDuctPart(
                    1,
                    boundingBox.minX,
                    boundingBox.minY,
                    boundingBox.minZ + 6,
                    2);
            list.add(ductPart);
            ductPart.buildComponent(this, list, random);
        }

        if (coordBaseMode == 1 || coordBaseMode == 0) {
            ComponentTFNagaCourtyardTerraceDuctPart ductPart = new ComponentTFNagaCourtyardTerraceDuctPart(
                    1,
                    boundingBox.minX + 7,
                    boundingBox.minY,
                    boundingBox.minZ + 13,
                    1);
            list.add(ductPart);
            ductPart.buildComponent(this, list, random);
        }

    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        super.addComponentParts(world, random, structureBoundingBox);

        // duct bottom
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                6,
                -1,
                6,
                16,
                -1,
                10,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                6,
                -1,
                6,
                10,
                -1,
                16,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);
        /*
         * this.fillWithMetadataBlocks( world, structureBoundingBox, 7, -1, 7, 16, -1, 7, TFBlocks.newNagastoneBody, 0,
         * TFBlocks.newNagastoneBody, 0, true); this.fillWithMetadataBlocks( world, structureBoundingBox, 9, -1, 9, 16,
         * -1, 9, TFBlocks.newNagastoneBody, 0, TFBlocks.newNagastoneBody, 0, true); this.fillWithMetadataBlocks( world,
         * structureBoundingBox, 7, -1, 7, 7, -1, 16, TFBlocks.newNagastoneBody, 0, TFBlocks.newNagastoneBody, 0, true);
         * this.fillWithMetadataBlocks( world, structureBoundingBox, 9, -1, 9, 9, -1, 16, TFBlocks.newNagastoneBody, 0,
         * TFBlocks.newNagastoneBody, 0, true);
         */
        for (int i = 16; i > 6; i--) {
            if (this.getBlockAtCurrentPosition(world, i, -1, 7, structureBoundingBox) != Blocks.air)
                this.placeBlockAtCurrentPosition(
                        world,
                        TFBlocks.nagastoneBody,
                        BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                        i,
                        -1,
                        7,
                        structureBoundingBox);
            if (this.getBlockAtCurrentPosition(world, 7, -1, i, structureBoundingBox) != Blocks.air)
                this.placeBlockAtCurrentPosition(
                        world,
                        TFBlocks.nagastoneBody,
                        BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                        7,
                        -1,
                        i,
                        structureBoundingBox);
            if (i > 8) {
                if (this.getBlockAtCurrentPosition(world, i, -1, 9, structureBoundingBox) != Blocks.air)
                    this.placeBlockAtCurrentPosition(
                            world,
                            TFBlocks.nagastoneBody,
                            BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                            i,
                            -1,
                            9,
                            structureBoundingBox);
                if (this.getBlockAtCurrentPosition(world, 9, -1, i, structureBoundingBox) != Blocks.air)
                    this.placeBlockAtCurrentPosition(
                            world,
                            TFBlocks.nagastoneBody,
                            BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                            9,
                            -1,
                            i,
                            structureBoundingBox);
            }
        }

        // duct
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                6,
                0,
                6,
                10,
                0,
                10,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);

        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                7,
                0,
                6,
                16,
                0,
                6,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                true);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                11,
                0,
                10,
                16,
                0,
                10,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient8,
                true);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                6,
                0,
                7,
                6,
                0,
                16,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient4,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient4,
                true);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                10,
                0,
                11,
                10,
                0,
                16,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient4,
                TFBlocks.nagastonePillar,
                horizontalColumnsOrient4,
                true);
        this.fillWithBlocks(world, structureBoundingBox, 7, 0, 7, 16, 0, 9, Blocks.water, Blocks.water, true);
        this.fillWithBlocks(world, structureBoundingBox, 7, 0, 7, 9, 0, 16, Blocks.water, Blocks.water, true);

        return true;
    }
}
