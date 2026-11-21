package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.TFTreasure;
import twilightforest.block.TFBlocks;
import twilightforest.entity.TFCreatures;

public class ComponentTFHedgeMaze extends StructureTFComponent {

    private static final int MSIZE = 16;
    private static final int RADIUS = (MSIZE / 2 * 3) + 1;
    private static final int DIAMETER = 2 * RADIUS;

    private static final int FLOOR_LEVEL = 3;
    private static final int ROOM_COUNT = MSIZE / 3;
    private static final int ROOM_SPACING = 3;
    private static final int ROOM_OFFSET = 3;
    private static final int ROOM_DECORATION_DIAMETER = 8;

    public ComponentTFHedgeMaze() {}

    public ComponentTFHedgeMaze(World world, Random rand, int i, int x, int y, int z) {
        super(i);

        setCoordBaseMode(0);

        // the maze is 50 x 50 for now
        boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -RADIUS, -3, -RADIUS, RADIUS * 2, 10, RADIUS * 2, 0);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        TFMaze maze = new TFMaze(MSIZE, MSIZE);

        maze.oddBias = 2;
        maze.torchBlockID = TFBlocks.firefly;
        maze.wallBlockID = TFBlocks.hedge;
        maze.wallBlockMeta = 0;
        maze.type = 4;
        maze.tall = 3;
        maze.roots = 3;

        // set the seed to a fixed value based on this maze's x and z
        maze.setSeed(world.getSeed() + boundingBox.minX * boundingBox.minZ);

        // just add grass below the maze for now
        // grass underneath
        for (int x = 0; x <= DIAMETER; x++) {
            for (int z = 0; z <= DIAMETER; z++) {
                placeBlockAtCurrentPosition(world, Blocks.grass, 0, x, FLOOR_LEVEL - 1, z, sbb);
            }
        }

        // plunk down some jack-o-lanterns outside for decoration
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 1, 0, FLOOR_LEVEL, 24, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 1, 0, FLOOR_LEVEL, 29, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 3, 50, FLOOR_LEVEL, 24, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 3, 50, FLOOR_LEVEL, 29, sbb);

        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 2, 24, FLOOR_LEVEL, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 2, 29, FLOOR_LEVEL, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 0, 24, FLOOR_LEVEL, 50, sbb);
        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, 0, 29, FLOOR_LEVEL, 50, sbb);

        int[] roomCoordinates = new int[ROOM_COUNT * 2];

        for (int i = 0; i < ROOM_COUNT; i++) {
            int roomX;
            int roomZ;

            do {
                roomX = maze.rand.nextInt(MSIZE - 2) + 1;
                roomZ = maze.rand.nextInt(MSIZE - 2) + 1;
            } while (isNearRoom(roomX, roomZ, roomCoordinates));

            maze.carveRoom1(roomX, roomZ);

            roomCoordinates[i * 2] = roomX;
            roomCoordinates[i * 2 + 1] = roomZ;
        }

        maze.generateRecursiveBacktracker(0, 0);
        maze.add4Exits();
        maze.copyToStructure(world, 1, FLOOR_LEVEL, 1, this, sbb);

        decorate3x3Rooms(world, roomCoordinates, sbb);

        return true;
    }

    /**
     * @return true if the specified dx and dz are within 3 of a room specified in rcoords
     */
    protected boolean isNearRoom(int dx, int dz, int[] rcoords) {
        // if proposed coordinates are covering the origin, return true to stop the room from causing the
        // maze to fail
        if (dx == 1 && dz == 1) {
            return true;
        }

        for (int i = 0; i < rcoords.length; i += 2) {
            int rx = rcoords[i];
            int rz = rcoords[i + 1];

            if (rx == 0 && rz == 0) {
                continue;
            }

            if (Math.abs(dx - rx) < ROOM_SPACING && Math.abs(dz - rz) < ROOM_SPACING) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param rand
     * @param world
     * @param rcoords
     * @param sbb
     */
    void decorate3x3Rooms(World world, int[] rcoords, StructureBoundingBox sbb) {
        for (int i = 0; i < rcoords.length; i += 2) {
            int dx = rcoords[i];
            int dz = rcoords[i + 1];

            // MAGIC NUMBERS!!! convert the maze coordinates into coordinates for our structure
            dx = dx * ROOM_SPACING + ROOM_OFFSET;
            dz = dz * ROOM_SPACING + ROOM_OFFSET;

            decorate3x3Room(world, dx, dz, sbb);
        }
    }

    /**
     * Decorates a room in the maze. Makes assumptions that the room is 3x3 cells and thus 11x11 blocks large.
     *
     * @param rand
     * @param world
     * @param sbb
     * @param dx
     * @param dy
     */
    void decorate3x3Room(World world, int x, int z, StructureBoundingBox sbb) {
        // make a new RNG for this room!
        Random roomRNG = new Random(world.getSeed() ^ (x + z));

        // a few jack-o-lanterns
        roomJackO(world, roomRNG, x, z, ROOM_DECORATION_DIAMETER, sbb);
        if (roomRNG.nextInt(4) == 0) {
            roomJackO(world, roomRNG, x, z, ROOM_DECORATION_DIAMETER, sbb);
        }

        // all rooms should have 1 spawner
        roomSpawner(world, roomRNG, x, z, ROOM_DECORATION_DIAMETER, sbb);

        // and 1-2 chests
        roomTreasure(world, roomRNG, x, z, ROOM_DECORATION_DIAMETER, sbb);
        if (roomRNG.nextInt(4) == 0) {
            roomTreasure(world, roomRNG, x, z, ROOM_DECORATION_DIAMETER, sbb);
        }
    }

    /**
     * Place a spawner within diameter / 2 squares of the specified x and z coordinates
     */
    private void roomSpawner(World world, Random rand, int x, int z, int diameter, StructureBoundingBox sbb) {
        int rx = x + rand.nextInt(diameter) - diameter / 2;
        int rz = z + rand.nextInt(diameter) - diameter / 2;

        String mobID = switch (rand.nextInt(3)) {
            case 1 -> TFCreatures.getSpawnerNameFor("Swarm Spider");
            case 2 -> TFCreatures.getSpawnerNameFor("Hostile Wolf");
            default -> TFCreatures.getSpawnerNameFor("Hedge Spider");
        };

        placeSpawnerAtCurrentPosition(world, rand, rx, FLOOR_LEVEL, rz, mobID, sbb);
    }

    /**
     * Place a treasure chest within diameter / 2 squares of the specified x and z coordinates
     */
    private void roomTreasure(World world, Random rand, int x, int z, int diameter, StructureBoundingBox sbb) {
        int rx = x + rand.nextInt(diameter) - diameter / 2;
        int rz = z + rand.nextInt(diameter) - diameter / 2;

        placeTreasureAtCurrentPosition(world, rand, rx, FLOOR_LEVEL, rz, TFTreasure.hedgemaze, sbb);
    }

    /**
     * Place a lit pumpkin lantern within diameter / 2 squares of the specified x and z coordinates
     */
    private void roomJackO(World world, Random rand, int x, int z, int diameter, StructureBoundingBox sbb) {
        int rx = x + rand.nextInt(diameter) - diameter / 2;
        int rz = z + rand.nextInt(diameter) - diameter / 2;

        placeBlockAtCurrentPosition(world, Blocks.lit_pumpkin, rand.nextInt(4), rx, FLOOR_LEVEL, rz, sbb);
    }
}
