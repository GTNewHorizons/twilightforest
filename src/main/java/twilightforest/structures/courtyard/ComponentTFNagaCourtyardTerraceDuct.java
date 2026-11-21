package twilightforest.structures.courtyard;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.BlockTFNagastone2.Direction;
import twilightforest.block.BlockTFNagastone2.Yaw;
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
            addDuctPart(list, random, boundingBox.minX + 13, boundingBox.minZ + 6, 0);
        }
        if (coordBaseMode == 3 || coordBaseMode == 2) {
            addDuctPart(list, random, boundingBox.minX + 6, boundingBox.minZ - 1, 3);
        }
        if (coordBaseMode == 2 || coordBaseMode == 1) {
            addDuctPart(list, random, boundingBox.minX, boundingBox.minZ + 6, 2);
        }
        if (coordBaseMode == 1 || coordBaseMode == 0) {
            addDuctPart(list, random, boundingBox.minX + 7, boundingBox.minZ + 13, 1);
        }
    }

    private void addDuctPart(List list, Random random, int x, int z, int rotation) {
        ComponentTFNagaCourtyardTerraceDuctPart ductPart = new ComponentTFNagaCourtyardTerraceDuctPart(
                1,
                x,
                boundingBox.minY,
                z,
                rotation);
        list.add(ductPart);
        ductPart.buildComponent(this, list, random);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        super.addComponentParts(world, random, structureBoundingBox);
        buildDuctBottom(world, structureBoundingBox);
        buildDuct(world, structureBoundingBox);
        return true;
    }

    private void buildDuctBottom(World world, StructureBoundingBox box) {
        // duct bottom
        fillWithBlocks(world, box, 6, -1, 6, 16, -1, 10, TFBlocks.nagastoneEtched, TFBlocks.nagastoneEtched, true);
        fillWithBlocks(world, box, 6, -1, 6, 10, -1, 16, TFBlocks.nagastoneEtched, TFBlocks.nagastoneEtched, true);
        /*
         * this.fillWithMetadataBlocks( world, structureBoundingBox, 7, -1, 7, 16, -1, 7, TFBlocks.newNagastoneBody, 0,
         * TFBlocks.newNagastoneBody, 0, true); this.fillWithMetadataBlocks( world, structureBoundingBox, 9, -1, 9, 16,
         * -1, 9, TFBlocks.newNagastoneBody, 0, TFBlocks.newNagastoneBody, 0, true); this.fillWithMetadataBlocks( world,
         * structureBoundingBox, 7, -1, 7, 7, -1, 16, TFBlocks.newNagastoneBody, 0, TFBlocks.newNagastoneBody, 0, true);
         * this.fillWithMetadataBlocks( world, structureBoundingBox, 9, -1, 9, 9, -1, 16, TFBlocks.newNagastoneBody, 0,
         * TFBlocks.newNagastoneBody, 0, true);
         */
        for (int i = 16; i > 6; i--) {
            placeDuctBodyIfNotAir(world, box, i, 7, NagastoneNorth);
            placeDuctBodyIfNotAir(world, box, 7, i, NagastoneWest);
            if (i > 8) {
                placeDuctBodyIfNotAir(world, box, i, 9, NagastoneNorth);
                placeDuctBodyIfNotAir(world, box, 9, i, NagastoneWest);
            }
        }
    }

    private void placeDuctBodyIfNotAir(World world, StructureBoundingBox box, int x, int z, Yaw yaw) {
        if (getBlockAtCurrentPosition(world, x, -1, z, box) != Blocks.air) {
            placeBlockAtCurrentPosition(
                    world,
                    TFBlocks.nagastoneBody,
                    BlockTFNagastone2.GetMetadata(Direction.SIDE, yaw),
                    x,
                    -1,
                    z,
                    box);
        }
    }

    private void buildDuct(World world, StructureBoundingBox box) {
        // duct
        fillWithBlocks(world, box, 6, 0, 6, 10, 0, 10, TFBlocks.nagastoneEtched, TFBlocks.nagastoneEtched, true);

        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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
        fillWithMetadataBlocks(
                world,
                box,
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

        fillWithBlocksAndUpdate(world, box, 7, 0, 7, 16, 0, 9, Blocks.water, Blocks.water, false);
        fillWithBlocksAndUpdate(world, box, 7, 0, 7, 9, 0, 16, Blocks.water, Blocks.water, false);
    }

    /**
     * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
     * maxZ, int placeBlock, int replaceBlock, boolean alwaysreplace)
     */
    protected void fillWithBlocksAndUpdate(World world, StructureBoundingBox sbb, int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ, Block placeBlock, Block replaceBlock, boolean alwaysReplace) {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (alwaysReplace && getBlockAtCurrentPosition(world, x, y, z, sbb).getMaterial() == Material.air) {
                        continue;
                    }
                    boolean inside = y != minY && y != maxY && x != minX && x != maxX && z != minZ && z != maxZ;
                    placeBlockAtCurrentPositionWithUpdate(world, inside ? replaceBlock : placeBlock, 0, x, y, z, sbb);
                }
            }
        }
    }

    /**
     * current Position depends on currently set Coordinates mode, is computed here
     */
    protected void placeBlockAtCurrentPositionWithUpdate(World world, Block block, int meta, int x, int y, int z,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);

        if (sbb.isVecInside(worldX, worldY, worldZ)) {
            world.setBlock(worldX, worldY, worldZ, block, meta, 1);
        }
    }
}
