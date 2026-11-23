package twilightforest.structures;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.TFTreasure;
import twilightforest.block.TFBlocks;

public class ComponentTFHillMaze extends StructureTFComponent {

    private static final int FLOOR_LEVEL = 1;
    private static final int SMALL_MAZE_SIZE = 11;
    private static final int MEDIUM_MAZE_SIZE = 19;
    private static final int LARGE_MAZE_SIZE = 27;
    private static final int ROOM_SPACING = 3;
    private static final int ROOM_BLOCK_SPACING = 4;
    private static final int ROOM_BLOCK_OFFSET = 2;
    private static final int ROOM_DECORATION_DIAMETER = 8;
    private static final float TORCH_RARITY = 0.05F;

    private int hillSize;

    public ComponentTFHillMaze() {
        super();
    }

    public ComponentTFHillMaze(int i, int x, int y, int z, int hsize) {
        super(i);
        hillSize = hsize;
        setCoordBaseMode(0);

        int radius = getRadius();
        boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -radius, 0, -radius, radius * 2, 5, radius * 2, 0);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int diameter = getDiameter();

        // clear the area
        fillWithBlocks(world, sbb, 0, 1, 0, diameter, 3, diameter, Blocks.air, Blocks.air, false);
        fillWithBlocks(world, sbb, 0, 0, 0, diameter, 0, diameter, TFBlocks.mazestone, TFBlocks.mazestone, false);
        fillWithBlocks(world, sbb, 0, 4, 0, diameter, 4, diameter, TFBlocks.mazestone, TFBlocks.mazestone, true);

        // make maze object
        int mazeSize = getMazeSize();
        TFMaze maze = new TFMaze(mazeSize, mazeSize);
        maze.wallBlockID = TFBlocks.mazestone;
        maze.wallBlockMeta = 3;
        maze.torchRarity = TORCH_RARITY;

        // set the seed to a fixed value based on this maze's x and z
        maze.setSeed(world.getSeed() + boundingBox.minX * boundingBox.minZ);

        int roomCount = mazeSize / ROOM_SPACING;
        int[] roomCoordinates = new int[roomCount * 2];

        // add rooms, trying to keep them separate from existing rooms
        for (int i = 0; i < roomCount; i++) {
            int roomX;
            int roomZ;
            do {
                roomX = maze.rand.nextInt(mazeSize - 2) + 1;
                roomZ = maze.rand.nextInt(mazeSize - 2) + 1;
            } while (isNearRoom(roomX, roomZ, roomCoordinates));

            maze.carveRoom1(roomX, roomZ);

            roomCoordinates[i * 2] = roomX;
            roomCoordinates[i * 2 + 1] = roomZ;
        }

        // make actual maze
        maze.generateRecursiveBacktracker(0, 0);
        maze.add4Exits();
        maze.copyToStructure(world, 0, FLOOR_LEVEL, 0, this, sbb);

        decorate3x3Rooms(world, roomCoordinates, sbb);
        return true;
    }

    public int getMazeSize() {
        return switch (hillSize) {
            case 1 -> SMALL_MAZE_SIZE;
            case 2 -> MEDIUM_MAZE_SIZE;
            default -> LARGE_MAZE_SIZE;
        };
    }

    public int getRadius() {
        return getMazeSize() * 2;
    }

    public int getDiameter() {
        return getMazeSize() * 4;
    }

    /**
     * @return true if the specified dx and dz are within 3 of a room specified in rcoords
     */
    protected boolean isNearRoom(int dx, int dz, int[] rcoords) {
        return isNearMazeRoom(dx, dz, rcoords, ROOM_SPACING);
    }

    void decorate3x3Rooms(World world, int[] rcoords, StructureBoundingBox sbb) {
        for (int i = 0; i < rcoords.length; i += 2) {
            int dx = rcoords[i];
            int dz = rcoords[i + 1];

            // convert the maze coords into coords for the structure
            dx = dx * ROOM_BLOCK_SPACING + ROOM_BLOCK_OFFSET;
            dz = dz * ROOM_BLOCK_SPACING + ROOM_BLOCK_OFFSET;

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
        int rx = randomRoomCoordinate(rand, x, diameter);
        int rz = randomRoomCoordinate(rand, z, diameter);

        String mobID = switch (rand.nextInt(3)) {
            case 1 -> "Skeleton";
            case 2 -> "Zombie";
            default -> "Spider";
        };

        placeSpawnerAtCurrentPosition(world, rand, rx, FLOOR_LEVEL, rz, mobID, sbb);
    }

    /**
     * Place a treasure chest within diameter / 2 squares of the specified x and z coordinates
     */
    private void roomTreasure(World world, Random rand, int x, int z, int diameter, StructureBoundingBox sbb) {
        int rx = randomRoomCoordinate(rand, x, diameter);
        int rz = randomRoomCoordinate(rand, z, diameter);

        placeTreasureAtCurrentPosition(world, rand, rx, FLOOR_LEVEL, rz, TFTreasure.labyrinth_room, sbb);
    }

    private static int randomRoomCoordinate(Random rand, int center, int diameter) {
        return center + rand.nextInt(diameter) - diameter / 2;
    }
}
