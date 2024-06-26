package twilightforest.structures.minotaurmaze;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.TFMaze;

public class ComponentTFMinotaurMaze extends StructureTFComponent {

    private static final int FLOOR_LEVEL = 1;
    TFMaze maze;
    int[] rcoords;
    private int level;

    public ComponentTFMinotaurMaze() {
        super();
    }

    public ComponentTFMinotaurMaze(int index, int x, int y, int z, int entranceX, int entranceZ, int level) {
        super(index);
        this.setCoordBaseMode(0);
        this.level = level;
        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(
                x,
                y,
                z,
                -getRadius(),
                0,
                -getRadius(),
                getRadius() * 2,
                5,
                getRadius() * 2,
                0);

        // make maze object
        maze = new TFMaze(getMazeSize(), getMazeSize());

        // set the seed to a fixed value based on this maze's x and z
        setFixedMazeSeed();

        // rooms
        int nrooms = 7;
        rcoords = new int[nrooms * 2];

        addRoomsToMaze(entranceX, entranceZ, nrooms);

        // make actual maze
        maze.generateRecursiveBacktracker(0, 0);
    }

    private void addRoomsToMaze(int entranceX, int entranceZ, int nrooms) {
        // make one entrance room always
        rcoords[0] = entranceX;
        rcoords[1] = entranceZ;
        maze.carveRoom1(entranceX, entranceZ);

        // add room coordinates, trying to keep them separate from existing rooms
        for (int i = 1; i < nrooms; i++) {
            int rx, rz;
            do {
                rx = maze.rand.nextInt(getMazeSize() - 2) + 1;
                rz = maze.rand.nextInt(getMazeSize() - 2) + 1;
            } while (isNearRoom(rx, rz, rcoords, i == 1 ? 7 : 4));

            maze.carveRoom1(rx, rz);

            // System.out.println("Initially carving room " + rx + ", " + rz);

            rcoords[i * 2] = rx;
            rcoords[i * 2 + 1] = rz;
        }
    }

    private void setFixedMazeSeed() {
        maze.setSeed(this.boundingBox.minX * 90342903 + this.boundingBox.minY * 90342903 ^ this.boundingBox.minZ);
    }

    public ComponentTFMinotaurMaze(int index, int x, int y, int z, int level) {
        this(index, x, y, z, 11, 11, level);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143012_a(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("mazeLevel", this.level);
        par1NBTTagCompound.setIntArray("roomCoords", this.rcoords);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.level = par1NBTTagCompound.getInteger("mazeLevel");
        this.rcoords = par1NBTTagCompound.getIntArray("roomCoords");

        // recreate maze object
        maze = new TFMaze(getMazeSize(), getMazeSize());
        setFixedMazeSeed();

        // blank out rcoords above 1 so that the room generation works properly
        // TODO: re-do this. :)
        for (int i = 2; i < rcoords.length; i++) {
            this.rcoords[i] = 0;
        }

        // recreate rooms
        this.addRoomsToMaze(this.rcoords[0], this.rcoords[1], (this.rcoords.length + 1) / 2);

        // regenerate maze
        maze.generateRecursiveBacktracker(0, 0);
    }

    protected ComponentTFMazeRoom makeRoom(Random random, int i, int dx, int dz) {
        ComponentTFMazeRoom room = null;

        int worldX = boundingBox.minX + dx * 5 - 4;
        int worldY = boundingBox.minY;
        int worldZ = boundingBox.minZ + dz * 5 - 4;

        if (i == 0) {
            // default room
            room = new ComponentTFMazeRoom(3 + i, random, worldX, worldY, worldZ);
        } else if (i == 1) {
            if (this.level == 1) {
                // exit room
                room = new ComponentTFMazeRoomExit(3 + i, random, worldX, worldY, worldZ);
            } else {
                // boss room
                room = new ComponentTFMazeRoomBoss(3 + i, random, worldX, worldY, worldZ);
            }
        } else if (i == 2 || i == 3) {
            if (this.level == 1) {
                // collapsed room
                room = new ComponentTFMazeRoomCollapse(3 + i, random, worldX, worldY, worldZ);
            } else {
                // mush-room
                room = new ComponentTFMazeMushRoom(3 + i, random, worldX, worldY, worldZ);
            }
        } else if (i == 4) {
            if (this.level == 1) {
                // fountain room
                room = new ComponentTFMazeRoomFountain(3 + i, random, worldX, worldY, worldZ);
            } else {
                // vault
                room = new ComponentTFMazeRoomVault(3 + i, random, worldX, worldY, worldZ);

            }
        } else {
            room = new ComponentTFMazeRoomSpawnerChests(3 + i, random, worldX, worldY, worldZ);
        }

        return room;
    }

    /**
     * Find dead ends and put something there
     * 
     * @param random
     * @param list
     */
    protected void decorateDeadEndsCorridors(Random random, List list) {
        for (int x = 0; x < maze.width; x++) {
            for (int z = 0; z < maze.depth; z++) {
                StructureTFComponent component = null;

                // dead ends
                if (!maze.isWall(x, z, x - 1, z) && maze.isWall(x, z, x + 1, z)
                        && maze.isWall(x, z, x, z - 1)
                        && maze.isWall(x, z, x, z + 1)) {
                    component = makeDeadEnd(random, x, z, 3);
                }
                if (maze.isWall(x, z, x - 1, z) && !maze.isWall(x, z, x + 1, z)
                        && maze.isWall(x, z, x, z - 1)
                        && maze.isWall(x, z, x, z + 1)) {
                    component = makeDeadEnd(random, x, z, 1);
                }
                if (maze.isWall(x, z, x - 1, z) && maze.isWall(x, z, x + 1, z)
                        && !maze.isWall(x, z, x, z - 1)
                        && maze.isWall(x, z, x, z + 1)) {
                    component = makeDeadEnd(random, x, z, 0);
                }
                if (maze.isWall(x, z, x - 1, z) && maze.isWall(x, z, x + 1, z)
                        && maze.isWall(x, z, x, z - 1)
                        && !maze.isWall(x, z, x, z + 1)) {
                    component = makeDeadEnd(random, x, z, 2);
                }

                // corridors
                if (!maze.isWall(x, z, x - 1, z) && !maze.isWall(x, z, x + 1, z)
                        && maze.isWall(x, z, x, z - 1)
                        && maze.isWall(x, z, x, z + 1)
                        && maze.isWall(x - 1, z, x - 1, z - 1)
                        && maze.isWall(x - 1, z, x - 1, z + 1)
                        && maze.isWall(x + 1, z, x + 1, z - 1)
                        && maze.isWall(x + 1, z, x + 1, z + 1)) {
                    component = makeCorridor(random, x, z, 1);
                }
                if (!maze.isWall(x, z, x, z - 1) && !maze.isWall(x, z, x, z + 1)
                        && maze.isWall(x, z, x - 1, z)
                        && maze.isWall(x, z, x + 1, z)
                        && maze.isWall(x, z - 1, x - 1, z - 1)
                        && maze.isWall(x, z - 1, x + 1, z - 1)
                        && maze.isWall(x, z + 1, x - 1, z + 1)
                        && maze.isWall(x, z + 1, x + 1, z + 1)) {
                    component = makeCorridor(random, x, z, 0);
                }

                if (component != null) {
                    list.add(component);
                    component.buildComponent(this, list, random);
                }
            }
        }
    }

    /**
     * Add a dead end structure at the specified coords
     */
    protected ComponentTFMazeDeadEnd makeDeadEnd(Random random, int dx, int dz, int rotation) {
        int worldX = boundingBox.minX + dx * 5 + 1;
        int worldY = boundingBox.minY;
        int worldZ = boundingBox.minZ + dz * 5 + 1;

        int decorationType = random.nextInt(8);

        return switch (decorationType) {
            default ->
                // blank with fence doorway
                new ComponentTFMazeDeadEnd(4, worldX, worldY, worldZ, rotation);
            case 1 -> new ComponentTFMazeDeadEndChest(4, worldX, worldY, worldZ, rotation);
            case 2 -> new ComponentTFMazeDeadEndTrappedChest(4, worldX, worldY, worldZ, rotation);
            case 3 -> new ComponentTFMazeDeadEndTorches(4, worldX, worldY, worldZ, rotation);
            case 4 -> new ComponentTFMazeDeadEndFountain(4, worldX, worldY, worldZ, rotation);
            case 5 -> new ComponentTFMazeDeadEndFountainLava(4, worldX, worldY, worldZ, rotation);
            case 6 -> new ComponentTFMazeDeadEndPainting(4, worldX, worldY, worldZ, rotation);
            case 7 -> this.level == 1 ? new ComponentTFMazeDeadEndRoots(4, worldX, worldY, worldZ, rotation)
                    : new ComponentTFMazeDeadEndShrooms(4, worldX, worldY, worldZ, rotation);
        };

    }

    protected ComponentTFMazeCorridor makeCorridor(Random random, int dx, int dz, int rotation) {
        int worldX = boundingBox.minX + dx * 5 + 1;
        int worldY = boundingBox.minY;
        int worldZ = boundingBox.minZ + dz * 5 + 1;

        int decorationType = random.nextInt(5);

        return switch (decorationType) {
            default -> null;
            case 1 -> new ComponentTFMazeCorridor(4, worldX, worldY, worldZ, rotation);
            case 2 -> new ComponentTFMazeCorridorIronFence(4, worldX, worldY, worldZ, rotation);
            case 3 -> null; // painting
            case 4 -> this.level == 1 ? new ComponentTFMazeCorridorRoots(4, worldX, worldY, worldZ, rotation)
                    : new ComponentTFMazeCorridorShrooms(4, worldX, worldY, worldZ, rotation);
        };

    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        // add a second story
        if (this.level == 1) {
            int centerX = boundingBox.minX + ((boundingBox.maxX - boundingBox.minX) / 2);
            int centerZ = boundingBox.minZ + ((boundingBox.maxZ - boundingBox.minZ) / 2);

            ComponentTFMinotaurMaze maze = new ComponentTFMinotaurMaze(
                    1,
                    centerX,
                    boundingBox.minY - 10,
                    centerZ,
                    rcoords[2],
                    rcoords[3],
                    2);
            list.add(maze);
            maze.buildComponent(this, list, random);
        }

        // add rooms where we have our coordinates
        for (int i = 0; i < rcoords.length / 2; i++) {
            int dx = rcoords[i * 2];
            int dz = rcoords[i * 2 + 1];

            // add the room as a component
            ComponentTFMazeRoom room = makeRoom(random, i, dx, dz);
            list.add(room);
            room.buildComponent(this, list, random);
        }

        // find dead ends and corridors and make components for them
        decorateDeadEndsCorridors(random, list);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // level 2 maze surrounded by bedrock
        if (level == 2) {
            fillWithBlocks(
                    world,
                    sbb,
                    0,
                    -1,
                    0,
                    getDiameter() + 2,
                    6,
                    getDiameter() + 2,
                    Blocks.bedrock,
                    Blocks.air,
                    false);
        }

        // clear the area
        fillWithAir(world, sbb, 1, 1, 1, getDiameter(), 4, getDiameter());
        // fillWithBlocks(world, sbb, 0, 0, 0, getDiameter(), 0, getDiameter(), TFBlocks.mazestone, Blocks.stone,
        // false);
        // fillWithBlocks(world, sbb, 0, 5, 0, getDiameter(), 5, getDiameter(), TFBlocks.mazestone, Blocks.stone, true);
        fillWithMetadataBlocks(
                world,
                sbb,
                1,
                5,
                1,
                getDiameter(),
                5,
                getDiameter(),
                TFBlocks.mazestone,
                0,
                Blocks.stone,
                0,
                this.level == 1);
        fillWithMetadataBlocks(
                world,
                sbb,
                1,
                0,
                1,
                getDiameter(),
                0,
                getDiameter(),
                TFBlocks.mazestone,
                6,
                Blocks.stone,
                0,
                false);

        //
        maze.headBlockID = TFBlocks.mazestone;
        maze.headBlockMeta = 3;
        maze.wallBlockID = TFBlocks.mazestone;
        maze.wallBlockMeta = 1;
        maze.rootBlockID = TFBlocks.mazestone;
        maze.rootBlockMeta = 3;
        maze.pillarBlockID = TFBlocks.mazestone;
        maze.pillarBlockMeta = 2;
        maze.wallVar0ID = TFBlocks.mazestone;
        maze.wallVar0Meta = 4;
        maze.wallVarRarity = 0.2F;
        maze.torchRarity = 0.05F;
        maze.tall = 2;
        maze.head = 1;
        maze.roots = 1;
        maze.oddBias = 4;

        maze.copyToStructure(world, 1, 2, 1, this, sbb);

        return true;

    }

    public int getMazeSize() {
        return 22;
    }

    public int getRadius() {
        return (int) (getMazeSize() * 2.5);
    }

    public int getDiameter() {
        return getMazeSize() * 5;
    }

    /**
     * @return true if the specified dx and dz are within 3 of a room specified in rcoords
     */
    protected boolean isNearRoom(int dx, int dz, int[] rcoords, int range) {
        // if proposed coordinates are covering the origin, return true to stop the room from causing the
        // maze to fail
        if (dx == 1 && dz == 1) {
            return true;
        }

        for (int i = 0; i < rcoords.length / 2; i++) {
            int rx = rcoords[i * 2];
            int rz = rcoords[i * 2 + 1];

            if (rx == 0 && rz == 0) {
                continue;
            }

            if (Math.abs(dx - rx) < range && Math.abs(dz - rz) < range) {
                return true;
            }
        }
        return false;
    }

}
