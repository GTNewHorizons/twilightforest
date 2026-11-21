package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.block.TFBlocks;

public class ComponentTFNagaCourtyardWallPadder extends ComponentTFNagaCourtyardRotatedAbstract {

    @SuppressWarnings({ "unused" })
    public ComponentTFNagaCourtyardWallPadder() {
        super();
    }

    public ComponentTFNagaCourtyardWallPadder(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
        this.boundingBox = new StructureBoundingBox(x, y, z, x, y + 8, z + 2);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        // Brick base
        fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 0, 0, 2, Blocks.stonebrick, Blocks.air, false);

        // Middle brick layer
        fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 7, 1, Blocks.stonebrick, Blocks.air, false);

        // Upper slab rows
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 0, 8, z, structureBoundingBox);
        }

        // Columns
        for (int z = 0; z <= 2; z += 2) {
            fillWithMetadataBlocks(
                    world,
                    structureBoundingBox,
                    0,
                    2,
                    z,
                    0,
                    7,
                    z,
                    TFBlocks.nagastonePillar,
                    0,
                    Blocks.air,
                    0,
                    false);
        }

        // Spiral bricks
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(
                    world,
                    TFBlocks.spiralStoneBricks,
                    z == 0 ? rotatedStairs1 : rotatedStairs0,
                    0,
                    4,
                    z,
                    structureBoundingBox);
        }

        // Chiseled stone bricks
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 0, 5, z, structureBoundingBox);
        }

        // Stone brick stairs
        for (int z = 0; z <= 2; z += 2) {
            placeBlockAtCurrentPosition(
                    world,
                    Blocks.stone_brick_stairs,
                    z == 0 ? rotatedStairs5 : rotatedStairs4,
                    0,
                    1,
                    z,
                    structureBoundingBox);
        }
        return true;
    }
}
