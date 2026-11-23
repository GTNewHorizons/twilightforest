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

    private static final int[][][] WALL_OFFSETS = { { { 0, 0 }, { 0, 3 }, { 3, 6 }, { 6, 6 } },
            { { 0, 3 }, { 0, 6 }, { 3, 0 }, { 6, 0 } }, { { 6, 3 }, { 6, 6 }, { 0, 0 }, { 3, 0 } },
            { { 6, 0 }, { 6, 3 }, { 0, 6 }, { 3, 6 } } };
    private static final int[] WALL_ROTATIONS = { 1, 3, 0, 2 };

    public ComponentTFNagaCourtyardWallCornerInner() {
        super();
    }

    public ComponentTFNagaCourtyardWallCornerInner(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);
        List<StructureComponent> components = list;

        int rotation = coordBaseMode >= 0 && coordBaseMode < WALL_OFFSETS.length ? coordBaseMode : 1;
        int[][] offsets = WALL_OFFSETS[rotation];

        // add walls
        for (int i = 0; i < offsets.length; i++) {
            ComponentTFNagaCourtyardWallCornerInnerPart cornerWall = new ComponentTFNagaCourtyardWallCornerInnerPart(
                    1,
                    boundingBox.minX + offsets[i][0],
                    boundingBox.minY,
                    boundingBox.minZ + offsets[i][1],
                    WALL_ROTATIONS[i]);
            components.add(cornerWall);
            cornerWall.buildComponent(this, components, random);
        }
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        buildCorner(world, structureBoundingBox);
        buildPadders(world, structureBoundingBox);
        return true;
    }

    private void buildCorner(World world, StructureBoundingBox box) {
        // Brick base
        fillWithBlocks(world, box, 0, 0, 6, 2, 0, 8, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        fillWithBlocks(world, box, 1, 5, 7, 1, 7, 7, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 8, 8, box);

        // Columns
        fillWithMetadataBlocks(world, box, 0, 5, 8, 0, 7, 8, TFBlocks.nagastonePillar, 0, Blocks.air, 0, false);

        // Etched nagastone
        int etchedUp = ForgeDirection.UP.ordinal();
        placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, etchedUp, 0, 1, 8, box);
        for (int y = 1; y <= 4; y++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, etchedUp, 1, y, 7, box);
        }
        for (int y = 5; y <= 7; y++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, etchedUp, 0, y, 7, box);
        }
        for (int y = 5; y <= 7; y++) {
            placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, etchedUp, 1, y, 8, box);
        }

        // Stone brick stairs + slabs
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs5, 0, 1, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs2, 0, 2, 7, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 0, 4, 7, box);

        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs7, 1, 1, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs0, 1, 2, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs4, 1, 4, 8, box);

        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 0, 2, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 13, 0, 4, 8, box);
    }

    private void buildPadders(World world, StructureBoundingBox box) {
        // Padders
        // Brick base
        // the original Z bounds were reversed, so this part of the brick footing never generated
        fillWithBlocks(world, box, 0, 0, 6, 2, 0, 8, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        fillWithBlocks(world, box, 1, 1, 6, 2, 7, 6, Blocks.stonebrick, Blocks.air, false);
        fillWithBlocks(world, box, 2, 1, 7, 2, 7, 7, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, 6, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 2, 8, 6, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 2, 8, 8, box);

        // Columns
        fillWithMetadataBlocks(world, box, 0, 2, 6, 0, 7, 6, TFBlocks.nagastonePillar, 0, Blocks.air, 0, false);
        fillWithMetadataBlocks(world, box, 2, 2, 8, 2, 7, 8, TFBlocks.nagastonePillar, 0, Blocks.air, 0, false);
        fillWithMetadataBlocks(world, box, 2, 2, 6, 2, 3, 6, TFBlocks.nagastonePillar, 0, Blocks.air, 0, false);
        fillWithMetadataBlocks(world, box, 2, 6, 6, 2, 7, 6, TFBlocks.nagastonePillar, 0, Blocks.air, 0, false);

        // Spiral bricks
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs2, 0, 4, 6, box);
        placeBlockAtCurrentPosition(world, TFBlocks.spiralStoneBricks, rotatedStairs0, 2, 4, 8, box);

        // Chiseled stone bricks
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 0, 5, 6, box);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 2, 5, 8, box);
        placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 2, 5, 6, box);

        // Stone brick stairs
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs6, 0, 1, 6, box);
        placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, rotatedStairs4, 2, 1, 8, box);
    }
}
