package twilightforest.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.TFBlocks;

public class TFGenThorns extends TFGenerator {

    private static final int MAX_SPREAD = 7;
    private static final int CHANCE_OF_BRANCH = 3;
    private static final int CHANCE_OF_LEAF = 3;
    private static final int CHANCE_LEAF_IS_ROSE = 50;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // make a 2-5 long stack going up
        int nextLength = 2 + rand.nextInt(4);
        int maxLength = 2 + rand.nextInt(4) + rand.nextInt(4) + rand.nextInt(4);

        return placeThorns(world, rand, x, y, z, nextLength, ForgeDirection.UP, maxLength, x, y, z);
    }

    private boolean placeThorns(World world, Random rand, int x, int y, int z, int length, ForgeDirection dir,
            int maxLength, int ox, int oy, int oz) {
        boolean complete = false;
        boolean placedAny = false;

        for (int i = 0; i < length; i++) {
            int dx = x + dir.offsetX * i;
            int dy = y + dir.offsetY * i;
            int dz = z + dir.offsetZ * i;

            if (!isWithinSpread(dx, dz, ox, oz) || !canPlaceThorns(world, dx, dy, dz)) {
                break;
            }

            setBlockAndMetadata(world, dx, dy, dz, TFBlocks.thorns, getMetaFor(dir));
            placedAny = true;

            // did we make it to the end?
            if (i == length - 1) {
                complete = true;
                placeTipDecoration(world, rand, dx, dy, dz, dir);
            }
        }

        // add another off the end
        if (complete && maxLength > 1) {
            ForgeDirection nextDir = randomDirection(rand);

            int nextX = x + dir.offsetX * (length - 1) + nextDir.offsetX;
            int nextY = y + dir.offsetY * (length - 1) + nextDir.offsetY;
            int nextZ = z + dir.offsetZ * (length - 1) + nextDir.offsetZ;

            int nextLength = 1 + rand.nextInt(maxLength);
            placeThorns(world, rand, nextX, nextY, nextZ, nextLength, nextDir, maxLength - 1, ox, oy, oz);
        }

        // maybe another branch off the middle
        if (complete && length > 3 && rand.nextInt(CHANCE_OF_BRANCH) == 0) {
            int middle = rand.nextInt(length);
            ForgeDirection nextDir = randomDirection(rand);

            int nextX = x + dir.offsetX * middle + nextDir.offsetX;
            int nextY = y + dir.offsetY * middle + nextDir.offsetY;
            int nextZ = z + dir.offsetZ * middle + nextDir.offsetZ;

            int nextLength = 1 + rand.nextInt(maxLength);
            placeThorns(world, rand, nextX, nextY, nextZ, nextLength, nextDir, maxLength - 1, ox, oy, oz);
        }

        // maybe a leaf
        if (complete && length > 3 && rand.nextInt(CHANCE_OF_LEAF) == 0) {
            int middle = rand.nextInt(length);
            ForgeDirection nextDir = randomDirection(rand);

            int nextX = x + dir.offsetX * middle + nextDir.offsetX;
            int nextY = y + dir.offsetY * middle + nextDir.offsetY;
            int nextZ = z + dir.offsetZ * middle + nextDir.offsetZ;

            if (world.isAirBlock(nextX, nextY, nextZ)) {
                setBlockAndMetadata(world, nextX, nextY, nextZ, TFBlocks.leaves3, 0);
            }
        }

        return placedAny;
    }

    private void placeTipDecoration(World world, Random rand, int x, int y, int z, ForgeDirection dir) {
        // maybe a leaf? or a rose?
        int nextX = x + dir.offsetX;
        int nextY = y + dir.offsetY;
        int nextZ = z + dir.offsetZ;

        if (rand.nextInt(CHANCE_OF_LEAF) == 0 && world.isAirBlock(nextX, nextY, nextZ)) {
            if (rand.nextInt(CHANCE_LEAF_IS_ROSE) > 0) {
                // leaf
                setBlockAndMetadata(world, nextX, nextY, nextZ, TFBlocks.leaves3, 0);
            } else {
                // rose
                setBlock(world, nextX, nextY, nextZ, TFBlocks.thornRose);
            }
        }
    }

    private boolean isWithinSpread(int x, int z, int originX, int originZ) {
        return Math.abs(x - originX) < MAX_SPREAD && Math.abs(z - originZ) < MAX_SPREAD;
    }

    private boolean canPlaceThorns(World world, int x, int y, int z) {
        return world.isAirBlock(x, y, z) || world.getBlock(x, y, z).isLeaves(world, x, y, z);
    }

    private ForgeDirection randomDirection(Random rand) {
        return ForgeDirection.VALID_DIRECTIONS[rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)];
    }

    private int getMetaFor(ForgeDirection dir) {
        switch (dir) {
            case EAST:
            case WEST:
                return 4;
            case NORTH:
            case SOUTH:
                return 8;
            default:
                return 0;
        }
    }
}
