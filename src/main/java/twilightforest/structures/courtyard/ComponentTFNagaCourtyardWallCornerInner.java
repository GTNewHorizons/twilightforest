package twilightforest.structures.courtyard;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWallCornerInner extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings({ "unused" })
    public ComponentTFNagaCourtyardWallCornerInner() {
        super();
    }

    public ComponentTFNagaCourtyardWallCornerInner(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.coordBaseMode = rotation;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        int dXwall1 = 0;
        int dXwall2 = 0;
        int dXwall3 = 0;
        int dXwall4 = 0;
        int dXpadder1 = 0;
        int dXpadder2 = 0;
        int dZwall1 = 0;
        int dZwall2 = 0;
        int dZwall3 = 0;
        int dZwall4 = 0;
        int dZpadder1 = 0;
        int dZpadder2 = 0;

        switch (coordBaseMode) {
            case 0:
                dXwall1 = 0;
                dZwall1 = 0;

                dXwall2 = 0;
                dZwall2 = 3;

                dXwall3 = 3;
                dZwall3 = 6;

                dXwall4 = 6;
                dZwall4 = 6;

                dXpadder1 = 0;
                dZpadder1 = 4;

                dXpadder2 = 2;
                dZpadder2 = 6;
                break;
            default:
            case 1:
                dXwall1 = 0;
                dZwall1 = 3;

                dXwall2 = 0;
                dZwall2 = 6;

                dXwall3 = 3;
                dZwall3 = 0;

                dXwall4 = 6;
                dZwall4 = 0;

                dXpadder1 = 2;
                dZpadder1 = 2;

                dXpadder2 = 2;
                dZpadder2 = 0;
                break;
            case 2:
                dXwall1 = 6;
                dZwall1 = 3;

                dXwall2 = 6;
                dZwall2 = 6;

                dXwall3 = 0;
                dZwall3 = 0;

                dXwall4 = 3;
                dZwall4 = 0;

                dXpadder1 = 8;
                dZpadder1 = 2;

                dXpadder2 = 6;
                dZpadder2 = 0;
                break;
            case 3:
                dXwall1 = 6;
                dZwall1 = 0;

                dXwall2 = 6;
                dZwall2 = 3;

                dXwall3 = 0;
                dZwall3 = 6;

                dXwall4 = 3;
                dZwall4 = 6;

                dXpadder1 = 8;
                dZpadder1 = 6;

                dXpadder2 = 6;
                dZpadder2 = 6;
                break;
        }

        // add walls
        ComponentTFNagaCourtyardWallCornerInnerPart cornerWall = new ComponentTFNagaCourtyardWallCornerInnerPart(
                1,
                boundingBox.minX + dXwall1,
                boundingBox.minY,
                boundingBox.minZ + dZwall1,
                1);
        list.add(cornerWall);
        cornerWall.buildComponent(this, list, random);

        ComponentTFNagaCourtyardWallCornerInnerPart cornerWall1 = new ComponentTFNagaCourtyardWallCornerInnerPart(
                1,
                boundingBox.minX + dXwall2,
                boundingBox.minY,
                boundingBox.minZ + dZwall2,
                3);
        list.add(cornerWall1);
        cornerWall1.buildComponent(this, list, random);

        ComponentTFNagaCourtyardWallCornerInnerPart cornerWall2 = new ComponentTFNagaCourtyardWallCornerInnerPart(
                1,
                boundingBox.minX + dXwall3,
                boundingBox.minY,
                boundingBox.minZ + dZwall3,
                0);
        list.add(cornerWall2);
        cornerWall2.buildComponent(this, list, random);

        ComponentTFNagaCourtyardWallCornerInnerPart cornerWall3 = new ComponentTFNagaCourtyardWallCornerInnerPart(
                1,
                boundingBox.minX + dXwall4,
                boundingBox.minY,
                boundingBox.minZ + dZwall4,
                2);
        list.add(cornerWall3);
        cornerWall3.buildComponent(this, list, random);

    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // Brick base
        this.fillWithBlocks(world, structureBoundingBox, 0, 0, 6, 2, 0, 8, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        this.fillWithBlocks(world, structureBoundingBox, 1, 5, 7, 1, 7, 7, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 8, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 7, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 8, 8, structureBoundingBox);

        // Columns
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                5,
                8,
                0,
                7,
                8,
                TFBlocks.nagastonePillar,
                0,
                Blocks.air,
                0,
                false);

        // Etched nagastone
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                0,
                1,
                8,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                2,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                3,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                4,
                7,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                0,
                5,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                0,
                6,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                0,
                7,
                7,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                5,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                6,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.nagastoneEtched,
                ForgeDirection.UP.ordinal(),
                1,
                7,
                8,
                structureBoundingBox);

        // Stone brick stairs + slabs
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs5,
                0,
                1,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs2,
                0,
                2,
                7,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                0,
                4,
                7,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs7,
                1,
                1,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs0,
                1,
                2,
                8,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs4,
                1,
                4,
                8,
                structureBoundingBox);

        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 0, 2, 8, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 13, 0, 4, 8, structureBoundingBox);

        // Padders
        // Brick base
        this.fillWithBlocks(world, structureBoundingBox, 0, 0, 8, 2, 0, 6, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        this.fillWithBlocks(world, structureBoundingBox, 1, 1, 6, 2, 7, 6, Blocks.stonebrick, Blocks.air, false);
        this.fillWithBlocks(world, structureBoundingBox, 2, 1, 7, 2, 7, 7, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 6, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 2, 8, 6, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 2, 8, 8, structureBoundingBox);

        // Columns
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                0,
                2,
                6,
                0,
                7,
                6,
                TFBlocks.nagastonePillar,
                0,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                2,
                2,
                8,
                2,
                7,
                8,
                TFBlocks.nagastonePillar,
                0,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                2,
                2,
                6,
                2,
                3,
                6,
                TFBlocks.nagastonePillar,
                0,
                Blocks.air,
                0,
                false);
        this.fillWithMetadataBlocks(
                world,
                structureBoundingBox,
                2,
                6,
                6,
                2,
                7,
                6,
                TFBlocks.nagastonePillar,
                0,
                Blocks.air,
                0,
                false);

        // Spiral bricks
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs2,
                0,
                4,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                TFBlocks.spiralStoneBricks,
                rotatedStairs0,
                2,
                4,
                8,
                structureBoundingBox);

        // Chiseled stone bricks
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 0, 5, 6, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 2, 5, 8, structureBoundingBox);
        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 2, 5, 6, structureBoundingBox);

        // Stone brick stairs
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs6,
                0,
                1,
                6,
                structureBoundingBox);
        this.placeBlockAtCurrentPosition(
                world,
                Blocks.stone_brick_stairs,
                rotatedStairs4,
                2,
                1,
                8,
                structureBoundingBox);

        return true;
    }
}
