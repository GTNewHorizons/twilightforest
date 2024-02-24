package twilightforest.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;

public class StalactiteOreGen {

    private static final List<OrePlacer> orePlacers = new LinkedList<>(ImmutableList.of(new DefaultOrePlacer()));

    /**
     * Add an ore placing handler that will return true when it determines it wants to override the default ore placing
     * behavior.
     * <p>
     * The OrePlacer must NOT have any side effects if it returns false.
     *
     * @param orePlacer An OrePlacer
     */
    public static void addOrePlacer(OrePlacer orePlacer) {
        orePlacers.add(0, orePlacer);
    }

    static void placeOre(World world, int x, int y, int z, Block block) {
        Iterator<OrePlacer> iterator = orePlacers.iterator();
        boolean orePlaced = false;
        while (iterator.hasNext() && !orePlaced) {
            orePlaced = iterator.next().placeOre(world, x, y, z, block);
        }
    }

}
