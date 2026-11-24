package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFMazeDeadEndShrooms extends ComponentTFMazeDeadEndRoots {

    public ComponentTFMazeDeadEndShrooms() {
        super();
    }

    public ComponentTFMazeDeadEndShrooms(int i, int x, int y, int z, int rotation) {
        super(i, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // no door

        // mycelium & mushrooms going back
        for (int x = 1; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if (rand.nextInt(z + 2) > 0) {
                    this.placeBlockAtCurrentPosition(world, Blocks.mycelium, 0, x, 0, z, sbb);
                }
                if (rand.nextInt(z + 2) > 0) {
                    this.placeBlockAtCurrentPosition(
                            world,
                            rand.nextBoolean() ? Blocks.red_mushroom : Blocks.brown_mushroom,
                            0,
                            x,
                            1,
                            z,
                            sbb);
                }
            }
        }

        // grow wall brackets above head height to keep the route through the room open
        Block first = rand.nextBoolean() ? Blocks.red_mushroom_block : Blocks.brown_mushroom_block;
        placeWallBracket(world, sbb, first, 1, rand.nextInt(2) + 3, rand.nextInt(3) + 1);
        Block second = first == Blocks.red_mushroom_block ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
        placeWallBracket(world, sbb, second, 4, rand.nextInt(2) + 3, rand.nextInt(3) + 1);
        placeBackBracket(
                world,
                sbb,
                rand.nextBoolean() ? Blocks.red_mushroom_block : Blocks.brown_mushroom_block,
                rand.nextInt(3) + 2,
                rand.nextInt(2) + 3);

        return true;
    }

    private void placeWallBracket(World world, StructureBoundingBox sbb, Block mushroom, int x, int height, int z) {
        this.fillWithMetadataBlocks(world, sbb, x, 1, z, x, height, z, mushroom, 10, Blocks.air, 0, false);
        int innerX = x == 1 ? 2 : 3;
        this.placeBlockAtCurrentPosition(world, mushroom, 14, innerX, height, z, sbb);
        this.placeBlockAtCurrentPosition(world, mushroom, 14, x, height, z - 1, sbb);
        this.placeBlockAtCurrentPosition(world, mushroom, 14, x, height, z + 1, sbb);
        if (x == 1) {
            this.placeBlockAtCurrentPosition(world, mushroom, 15, x, height - 1, z, sbb);
        }
    }

    private void placeBackBracket(World world, StructureBoundingBox sbb, Block mushroom, int x, int height) {
        this.fillWithMetadataBlocks(world, sbb, x, 1, 4, x, height, 4, mushroom, 10, Blocks.air, 0, false);
        this.placeBlockAtCurrentPosition(world, mushroom, 14, x - 1, height, 4, sbb);
        this.placeBlockAtCurrentPosition(world, mushroom, 14, x + 1, height, 4, sbb);
        this.placeBlockAtCurrentPosition(world, mushroom, 14, x, height, 3, sbb);
    }
}
