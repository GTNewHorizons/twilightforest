package twilightforest.structures;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;

public class ComponentTFHydraLair extends ComponentTFHollowHill {

    private static final int HILL_SIZE = 2;
    private static final int HILL_Y_OFFSET = 2;
    private static final int STALACTITE_COUNT = 64;
    private static final int STALAGMITE_COUNT = 8;
    private static final int STALACTITE_Y = 1;
    private static final int BOSS_SPAWNER_POSITION = 27;
    private static final int BOSS_SPAWNER_Y = 3;
    private static final int BOSS_SPAWNER_META = 2;

    public ComponentTFHydraLair() {}

    public ComponentTFHydraLair(World world, Random rand, int i, int x, int y, int z) {
        super(world, rand, i, HILL_SIZE, x, y + HILL_Y_OFFSET, z);
    }

    /**
     * Add on any other components we need. Override with noop since we don't need a maze
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random rand) {}

    /**
     * Add in all the blocks we're adding.
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // fill in features
        // ore or glowing stalactites! (smaller, less plentiful)
        for (int i = 0; i < STALACTITE_COUNT; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateOreStalactite(world, dest[0], STALACTITE_Y, dest[1], sbb);
        }

        // stone stalactites!
        for (int i = 0; i < STALACTITE_COUNT; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateBlockStalactite(world, Blocks.stone, 1.0F, true, dest[0], STALACTITE_Y, dest[1], sbb);
        }

        // stone stalagmites!
        for (int i = 0; i < STALAGMITE_COUNT; i++) {
            int[] dest = getCoordsInHill2D(rand);
            generateBlockStalactite(world, Blocks.stone, 0.9F, false, dest[0], STALACTITE_Y, dest[1], sbb);
        }

        // boss spawner seems important
        placeBlockAtCurrentPosition(
                world,
                TFBlocks.bossSpawner,
                BOSS_SPAWNER_META,
                BOSS_SPAWNER_POSITION,
                BOSS_SPAWNER_Y,
                BOSS_SPAWNER_POSITION,
                sbb);

        return true;
    }
}
