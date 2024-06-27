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

public class ComponentTFNagaCourtyardTerraceStatue extends ComponentTFNagaCourtyardTerraceAbstract {

    public ComponentTFNagaCourtyardTerraceStatue() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceStatue(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y - 1, z, x + 16, y + 6, z + 16);
    }

    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        ComponentTFNagaCourtyardTerraceStatueBorder border = new ComponentTFNagaCourtyardTerraceStatueBorder(
                1,
                boundingBox.minX,
                boundingBox.minY + 1,
                boundingBox.minZ + 5,
                0);
        list.add(border);
        border.buildComponent(this, list, random);

        border = new ComponentTFNagaCourtyardTerraceStatueBorder(
                1,
                boundingBox.minX + 7,
                boundingBox.minY + 1,
                boundingBox.minZ + 1,
                1);
        list.add(border);
        border.buildComponent(this, list, random);

        border = new ComponentTFNagaCourtyardTerraceStatueBorder(
                1,
                boundingBox.minX + 11,
                boundingBox.minY + 1,
                boundingBox.minZ + 5,
                2);
        list.add(border);
        border.buildComponent(this, list, random);

        border = new ComponentTFNagaCourtyardTerraceStatueBorder(
                1,
                boundingBox.minX + 4,
                boundingBox.minY + 1,
                boundingBox.minZ + 9,
                3);
        list.add(border);
        border.buildComponent(this, list, random);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        super.addComponentParts(world, random, structureBoundingBox);

        // ground layer
        this.fillWithBlocks(world, structureBoundingBox, 5, 0, 5, 11, 0, 11, Blocks.air, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 5, -1, 5, 11, -1, 11, Blocks.grass, Blocks.grass, true);

        // fire
        this.fillWithBlocks(
                world,
                structureBoundingBox,
                6,
                -1,
                7,
                6,
                -1,
                9,
                Blocks.netherrack,
                Blocks.netherrack,
                true);
        this.fillWithBlocks(world, structureBoundingBox, 6, 0, 7, 6, 0, 9, Blocks.fire, Blocks.fire, false);

        // statue
        // feet
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                0,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                9,
                0,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                0,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                9,
                0,
                9,
                structureBoundingBox);

        // legs
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                9,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneWest),
                9,
                2,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                9,
                1,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneEast),
                9,
                2,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                9,
                2,
                8,
                structureBoundingBox);

        // torso
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                3,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                4,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                5,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                6,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.UP, NagastoneNorth),
                9,
                7,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                7,
                8,
                structureBoundingBox);

        // heads (lower)
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                9,
                4,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                9,
                4,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                9,
                4,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                4,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                4,
                9,
                structureBoundingBox);

        // heads (upper)
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                9,
                6,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneEast),
                9,
                6,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                9,
                6,
                9,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneWest),
                9,
                6,
                10,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                6,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                7,
                6,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneBody,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                8,
                6,
                10,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneHead,
                BlockTFNagastone2.GetMetadata(Direction.SIDE, NagastoneNorth),
                7,
                6,
                10,
                structureBoundingBox);

        return true;
    }
}
