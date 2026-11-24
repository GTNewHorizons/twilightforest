package twilightforest.structures.minotaurmaze;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ComponentTFMazeCorridorShrooms extends ComponentTFMazeCorridor {

    public ComponentTFMazeCorridorShrooms() {}

    public ComponentTFMazeCorridorShrooms(int componentType, int x, int y, int z, int rotation) {
        super(componentType, x, y, z, rotation);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bounds) {
        // mycelium and small mushrooms are passable decorations
        for (int x = 1; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                if (random.nextBoolean()) {
                    placeBlockAtCurrentPosition(world, Blocks.mycelium, 0, x, 0, z, bounds);
                }
                if (random.nextBoolean()) {
                    Block mushroom = random.nextBoolean() ? Blocks.red_mushroom : Blocks.brown_mushroom;
                    placeBlockAtCurrentPosition(world, mushroom, 0, x, 1, z, bounds);
                }
            }
        }

        Block firstMushroom = random.nextBoolean() ? Blocks.red_mushroom_block : Blocks.brown_mushroom_block;
        placeBracket(world, random, bounds, firstMushroom, 1, 2);
        Block secondMushroom = firstMushroom == Blocks.brown_mushroom_block ? Blocks.red_mushroom_block
                : Blocks.brown_mushroom_block;
        placeBracket(world, random, bounds, secondMushroom, 4, 3);
        return true;
    }

    private void placeBracket(World world, Random random, StructureBoundingBox bounds, Block mushroom, int wallX,
            int innerX) {
        // low mushroom caps used to seal the whole corridor when both sides overlapped
        // keep the cap above player head height and the stem against its side wall, stops clog
        int capY = random.nextInt(2) + 3;
        int z = random.nextInt(4) + 1;
        fillWithMetadataBlocks(world, bounds, wallX, 1, z, wallX, capY, z, mushroom, 10, Blocks.air, 0, false);
        fillWithMetadataBlocks(
                world,
                bounds,
                Math.min(wallX, innerX),
                capY,
                z - 1,
                Math.max(wallX, innerX),
                capY,
                z + 1,
                mushroom,
                14,
                Blocks.air,
                0,
                false);
    }
}
