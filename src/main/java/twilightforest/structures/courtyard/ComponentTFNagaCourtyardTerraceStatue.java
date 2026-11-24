package twilightforest.structures.courtyard;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.BlockTFNagastone2.Yaw;
import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardTerraceStatue extends ComponentTFNagaCourtyardTerraceAbstract {

    public ComponentTFNagaCourtyardTerraceStatue() {
        super();
    }

    public ComponentTFNagaCourtyardTerraceStatue(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y - 1, z, x + 16, y + 6, z + 16);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);
        List<StructureComponent> components = list;

        addBorder(components, random, boundingBox.minX, boundingBox.minZ + 5, 0);
        addBorder(components, random, boundingBox.minX + 7, boundingBox.minZ + 1, 1);
        addBorder(components, random, boundingBox.minX + 11, boundingBox.minZ + 5, 2);
        addBorder(components, random, boundingBox.minX + 4, boundingBox.minZ + 9, 3);
    }

    private void addBorder(List<StructureComponent> list, Random random, int x, int z, int rotation) {
        ComponentTFNagaCourtyardTerraceStatueBorder border = new ComponentTFNagaCourtyardTerraceStatueBorder(
                1,
                x,
                boundingBox.minY + 1,
                z,
                rotation);
        list.add(border);
        border.buildComponent(this, list, random);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        super.addComponentParts(world, random, box);

        buildBase(world, box);
        buildFeet(world, box);
        buildLegs(world, box);
        buildTorso(world, box);
        buildLowerHeads(world, box);
        buildUpperHeads(world, box);
        return true;
    }

    private void buildBase(World world, StructureBoundingBox box) {
        // ground layer
        fillWithBlocks(world, box, 5, 0, 5, 11, 0, 11, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, box, 5, -1, 5, 11, -1, 11, Blocks.grass, Blocks.grass, true);

        // fire
        fillWithBlocks(world, box, 6, -1, 7, 6, -1, 9, Blocks.netherrack, Blocks.netherrack, true);
        fillWithBlocks(world, box, 6, 0, 7, 6, 0, 9, Blocks.fire, Blocks.fire, false);
    }

    private void buildFeet(World world, StructureBoundingBox box) {
        // statue
        // feet
        for (int z = 7; z <= 9; z += 2) {
            for (int x = 8; x <= 9; x++) {
                placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneNorth, x, 0, z);
            }
        }
    }

    private void buildLegs(World world, StructureBoundingBox box) {
        // legs
        for (int y = 1; y <= 2; y++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.UP, NagastoneWest, 9, y, 7);
        }
        for (int y = 1; y <= 2; y++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.UP, NagastoneEast, 9, y, 9);
        }
        placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneEast, 9, 2, 8);
    }

    private void buildTorso(World world, StructureBoundingBox box) {
        // torso
        for (int y = 3; y <= 7; y++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.UP, NagastoneNorth, 9, y, 8);
        }
        placeNagastone(world, box, TFBlocks.nagastoneHead, Direction.SIDE, NagastoneNorth, 8, 7, 8);
    }

    private void buildLowerHeads(World world, StructureBoundingBox box) {
        // heads (lower)
        for (int z = 7; z <= 8; z++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneEast, 9, 4, z);
        }
        placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneWest, 9, 4, 9);
        for (int z = 7; z <= 9; z += 2) {
            placeNagastone(world, box, TFBlocks.nagastoneHead, Direction.SIDE, NagastoneNorth, 8, 4, z);
        }
    }

    private void buildUpperHeads(World world, StructureBoundingBox box) {
        // heads (upper)
        for (int z = 6; z <= 7; z++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneEast, 9, 6, z);
        }
        for (int z = 9; z <= 10; z++) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneWest, 9, 6, z);
        }
        for (int z = 6; z <= 10; z += 4) {
            placeNagastone(world, box, TFBlocks.nagastoneBody, Direction.SIDE, NagastoneNorth, 8, 6, z);
            placeNagastone(world, box, TFBlocks.nagastoneHead, Direction.SIDE, NagastoneNorth, 7, 6, z);
        }
    }

    private void placeNagastone(World world, StructureBoundingBox box, Block block, Direction direction, Yaw yaw, int x,
            int y, int z) {
        placeBlockAtCurrentPosition(world, block, BlockTFNagastone2.GetMetadata(direction, yaw), x, y, z, box);
    }
}
