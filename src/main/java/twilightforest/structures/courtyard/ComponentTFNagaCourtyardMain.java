package twilightforest.structures.courtyard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFNagaCourtyardMain extends StructureTFComponent {

    public enum CellType {
        OUTSIDE,
        EMPTY,
        HEDGE,
        TERRACE
    }

    // follow the courtyard grid axis over minecrafts
    public enum ConnectionDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    static final int ROW_OF_CELLS = 8;
    static final int RADIUS = (int) ((((ROW_OF_CELLS - 2) / 2.0F) * 12.0F) + 8);
    static final int DIAMETER = 2 * RADIUS + 1;

    static final float HEDGE_FLOOF = 0.5f;

    private static final int DEFAULT_TABLE_WIDTH = ROW_OF_CELLS * 2 - 1;
    private static final int CELL_SPACING = 6;
    private static final int TERRACE_SIZE = 3;
    private static final float TERRACE_RATE = 0.2F;
    private static final int MAX_TERRACE_ATTEMPTS = 4096;
    private static final ConnectionDirection[] DIRECTIONS = ConnectionDirection.values();

    private int tableWidth = DEFAULT_TABLE_WIDTH;
    private CellType[][] courtyard = new CellType[DEFAULT_TABLE_WIDTH][DEFAULT_TABLE_WIDTH];

    public ComponentTFNagaCourtyardMain() {
        super();
    }

    public ComponentTFNagaCourtyardMain(World world, Random random, int componentId, int x, int y, int z) {
        super(componentId);
        setCoordBaseMode(0);
        boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -RADIUS, -1, -RADIUS, RADIUS * 2 + 6, 10, RADIUS * 2 + 6, 0);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void buildComponent(StructureComponent parent, List pieces, Random random) {
        super.buildComponent(parent, pieces, random);
        List<StructureComponent> components = pieces;

        generateLayout(random);
        addTerraces(components, random);
        addHedges(components, random);
        connectHedges(components, random);
        addHedgeDecorations(components, random);
        addWalls(components, random);

        addPiece(
                new ComponentTFNagaCourtyardDecorator(
                        1,
                        boundingBox.minX,
                        boundingBox.minY,
                        boundingBox.minZ,
                        boundingBox.getXSize(),
                        boundingBox.getYSize(),
                        boundingBox.getZSize()),
                components,
                random);
    }

    private void generateLayout(Random random) {
        courtyard = new CellType[tableWidth][tableWidth];
        fillCells(0, 0, tableWidth - 1, tableWidth - 1, CellType.EMPTY);

        int far = tableWidth - 2;
        markOutsideCorner(0, 0, 2, 2, random);
        markOutsideCorner(0, far, 2, -2, random);
        markOutsideCorner(far, 0, -2, 2, random);
        markOutsideCorner(far, far, -2, -2, random);
    }

    // these calls preserve the original generation
    private void markOutsideCorner(int x, int z, int dx, int dz, Random random) {
        markOutsideSquare(x, z);
        boolean extendX = random.nextBoolean();
        boolean extendZ = random.nextBoolean();
        if (extendX) markOutsideSquare(x + dx, z);
        if (extendZ) markOutsideSquare(x, z + dz);
        if (extendX && extendZ && random.nextBoolean()) markOutsideSquare(x + dx, z + dz);
    }

    private void markOutsideSquare(int x, int z) {
        fillCells(x, z, x + 1, z + 1, CellType.OUTSIDE);
    }

    private void fillCells(int minX, int minZ, int maxX, int maxZ, CellType type) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                courtyard[x][z] = type;
            }
        }
    }

    private void addTerraces(List<StructureComponent> pieces, Random random) {
        int remaining = (int) (countCellsInArea(0, 0, tableWidth - 1, tableWidth - 1, CellType.EMPTY) * TERRACE_RATE
                / (TERRACE_SIZE * TERRACE_SIZE));
        int attempts = 0;

        while (remaining > 0 && attempts++ < MAX_TERRACE_ATTEMPTS) {
            int x = random.nextInt(tableWidth / 2) * 2;
            int z = random.nextInt(tableWidth / 2) * 2;
            if (!canPlaceTerrace(x, z)) continue;

            int blockX = boundingBox.minX + 3 + x * CELL_SPACING;
            int blockZ = boundingBox.minZ + 3 + z * CELL_SPACING;
            ComponentTFNagaCourtyardTerraceAbstract terrace;
            if (random.nextInt(150) == 0) {
                terrace = new ComponentTFNagaCourtyardTerraceStatue(
                        1,
                        blockX,
                        boundingBox.minY,
                        blockZ,
                        random.nextInt(4));
            } else if (random.nextBoolean()) {
                terrace = new ComponentTFNagaCourtyardTerraceBrazier(1, blockX, boundingBox.minY, blockZ, 0);
            } else {
                terrace = new ComponentTFNagaCourtyardTerraceDuct(
                        1,
                        blockX,
                        boundingBox.minY,
                        blockZ,
                        random.nextInt(4));
            }

            addPiece(terrace, pieces, random);
            fillCells(x, z, x + 2, z + 2, CellType.TERRACE);
            remaining--;
        }
    }

    private boolean canPlaceTerrace(int x, int z) {
        int center = tableWidth / 2;
        // no terrace is allowed to cover the center cell, as it contains the naga spawner
        if (x <= center && center <= x + 2 && z <= center && center <= z + 2) return false;
        if (countCellsInArea(x, z, x + 2, z + 2, CellType.EMPTY) != 9) return false;
        if (x > 0 && countCellsInArea(x - 1, z, x - 1, z + 2, CellType.EMPTY) != 3) return false;
        if (x < tableWidth - 3 && countCellsInArea(x + 3, z, x + 3, z + 2, CellType.EMPTY) != 3) return false;
        if (z > 0 && countCellsInArea(x, z - 1, x + 2, z - 1, CellType.EMPTY) != 3) return false;
        return z >= tableWidth - 3 || countCellsInArea(x, z + 3, x + 2, z + 3, CellType.EMPTY) == 3;
    }

    private void addHedges(List<StructureComponent> pieces, Random random) {
        int center = tableWidth / 2;
        for (int x = 1; x < tableWidth; x += 2) {
            for (int z = 1; z < tableWidth; z += 2) {
                if (courtyard[x][z] == CellType.EMPTY && (x != center || z != center)) {
                    courtyard[x][z] = CellType.HEDGE;
                    addHedge(pieces, random, x, z);
                }
            }
        }
    }

    private void connectHedges(List<StructureComponent> pieces, Random random) {
        for (int x = 1; x < tableWidth; x += 2) {
            for (int z = 1; z < tableWidth; z += 2) {
                if (courtyard[x][z] != CellType.HEDGE) continue;
                List<ConnectionDirection> possible = getPossibleConnections(x, z);
                if (possible.isEmpty()) continue;

                ConnectionDirection direction = possible.get(random.nextInt(possible.size()));
                int connectedX = x + stepX(direction);
                int connectedZ = z + stepZ(direction);
                courtyard[connectedX][connectedZ] = CellType.HEDGE;
                addHedge(pieces, random, connectedX, connectedZ);
            }
        }
    }

    private void addHedge(List<StructureComponent> pieces, Random random, int cellX, int cellZ) {
        addPiece(
                new ComponentTFNagaCourtyardHedge(
                        1,
                        boundingBox.minX + 3 + cellX * CELL_SPACING,
                        boundingBox.minY,
                        boundingBox.minZ + 3 + cellZ * CELL_SPACING,
                        0),
                pieces,
                random);
    }

    private void addHedgeDecorations(List<StructureComponent> pieces, Random random) {
        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (courtyard[x][z] != CellType.HEDGE) continue;

                if (x > 0 && x < tableWidth - 1
                        && z > 0
                        && z < tableWidth - 1
                        && countNeighbours(x, z) == 1
                        && random.nextInt(5) < 2) {
                    addPiece(
                            new ComponentTFNagaCourtyardHedgePillar(
                                    1,
                                    boundingBox.minX + 3 + x * CELL_SPACING,
                                    boundingBox.minY,
                                    boundingBox.minZ + 3 + z * CELL_SPACING,
                                    0),
                            pieces,
                            random);
                }

                boolean north = x == tableWidth - 1 || hasNeighbour(x, z, ConnectionDirection.NORTH);
                boolean south = x == 0 || courtyard[x - 1][z] == CellType.OUTSIDE;
                boolean east = z == tableWidth - 1 || hasNeighbour(x, z, ConnectionDirection.EAST);
                boolean west = z == 0 || courtyard[x][z - 1] == CellType.OUTSIDE;

                if (north) addHedgePadder(
                        pieces,
                        random,
                        boundingBox.minX + 1 + (x + 1) * CELL_SPACING,
                        boundingBox.minZ + 6 + z * CELL_SPACING,
                        3);
                if (south) addHedgePadder(
                        pieces,
                        random,
                        boundingBox.minX + 1 + x * CELL_SPACING,
                        boundingBox.minZ + 6 + z * CELL_SPACING,
                        3);
                if (east) addHedgePadder(
                        pieces,
                        random,
                        boundingBox.minX + 3 + x * CELL_SPACING,
                        boundingBox.minZ + 2 + (z + 1) * CELL_SPACING,
                        0);
                if (west) addHedgePadder(
                        pieces,
                        random,
                        boundingBox.minX + 3 + x * CELL_SPACING,
                        boundingBox.minZ + 2 + z * CELL_SPACING,
                        0);
            }
        }
    }

    private void addPiece(StructureComponent piece, List<StructureComponent> pieces, Random random) {
        pieces.add(piece);
        piece.buildComponent(this, pieces, random);
    }

    private void addHedgePadder(List<StructureComponent> pieces, Random random, int x, int z, int rotation) {
        addPiece(new ComponentTFNagaCourtyardHedgePadder(1, x, boundingBox.minY, z, rotation), pieces, random);
    }

    private void addWall(List<StructureComponent> pieces, Random random, int x, int z, int rotation) {
        addPiece(new ComponentTFNagaCourtyardWall(1, x, boundingBox.minY, z, rotation), pieces, random);
    }

    private void addWallPadder(List<StructureComponent> pieces, Random random, int x, int z, int rotation) {
        addPiece(new ComponentTFNagaCourtyardWallPadder(1, x, boundingBox.minY, z, rotation), pieces, random);
    }

    private void addOuterCorner(List<StructureComponent> pieces, Random random, int x, int z, int rotation) {
        addPiece(new ComponentTFNagaCourtyardWallCornerOuter(1, x, boundingBox.minY, z, rotation), pieces, random);
    }

    private void addInnerCorner(List<StructureComponent> pieces, Random random, int x, int z, int rotation) {
        addPiece(new ComponentTFNagaCourtyardWallCornerInner(1, x, boundingBox.minY, z, rotation), pieces, random);
    }

    private static final class EdgeOpening {

        final int left;
        final int right;

        EdgeOpening(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    private void addWalls(List<StructureComponent> pieces, Random random) {
        EdgeOpening south = addHorizontalWall(0, boundingBox.minZ, boundingBox.minZ, 0, 1, pieces, random);
        if (south != null) {
            buildSouthWestCorner(south.left, pieces, random);
            buildSouthEastCorner(south.right, pieces, random);
        }

        EdgeOpening north = addHorizontalWall(
                tableWidth - 1,
                boundingBox.maxZ - 4,
                boundingBox.maxZ - 2,
                3,
                2,
                pieces,
                random);
        if (north != null) {
            buildNorthWestCorner(north.left, pieces, random);
            buildNorthEastCorner(north.right, pieces, random);
        }

        addVerticalWall(
                0,
                boundingBox.minX,
                boundingBox.minX - 8,
                boundingBox.minX,
                boundingBox.minX + 2,
                0,
                3,
                pieces,
                random);
        addVerticalWall(
                tableWidth - 1,
                boundingBox.maxX - 4,
                boundingBox.maxX - 10,
                boundingBox.maxX - 2,
                boundingBox.maxX,
                1,
                2,
                pieces,
                random);
    }

    private EdgeOpening addHorizontalWall(int edgeZ, int cornerZ, int wallZ, int startRotation, int endRotation,
            List<StructureComponent> pieces, Random random) {
        int start = -1;
        for (int i = 0; i < tableWidth - 1; i++) {
            if (courtyard[i][edgeZ] == CellType.OUTSIDE && courtyard[i + 1][edgeZ] != CellType.OUTSIDE) {
                start = i;
                break;
            }
        }
        if (start < 0) return null;

        addOuterCorner(pieces, random, boundingBox.minX + (start + 1) * CELL_SPACING, cornerZ, startRotation);
        addWallPadder(pieces, random, boundingBox.minX + (start + 1) * CELL_SPACING + 5, wallZ, 0);

        for (int i = start + 2; i + 2 < tableWidth; i += 2) {
            addWall(pieces, random, boundingBox.minX + i * CELL_SPACING, wallZ, 0);
            addWallPadder(pieces, random, boundingBox.minX + i * CELL_SPACING + 11, wallZ, 0);
            if (courtyard[i][edgeZ] != CellType.OUTSIDE && courtyard[i + 2][edgeZ] == CellType.OUTSIDE) {
                addOuterCorner(pieces, random, boundingBox.minX + i * CELL_SPACING + 12, cornerZ, endRotation);
                return new EdgeOpening(start, i + 2);
            }
        }
        return null;
    }

    private void addVerticalWall(int edgeX, int cornerX, int firstWallX, int secondWallX, int padderX,
            int startRotation, int endRotation, List<StructureComponent> pieces, Random random) {
        int start = -1;
        for (int i = 0; i < tableWidth - 1; i++) {
            if (courtyard[edgeX][i] == CellType.OUTSIDE && courtyard[edgeX][i + 1] != CellType.OUTSIDE) {
                start = i;
                break;
            }
        }
        if (start < 0) return;

        addOuterCorner(pieces, random, cornerX, boundingBox.minZ + (start + 1) * CELL_SPACING, startRotation);
        addWallPadder(pieces, random, padderX, boundingBox.minZ + (start + 1) * CELL_SPACING + 5, 1);

        for (int i = start + 2; i + 2 < tableWidth; i += 2) {
            addWall(pieces, random, firstWallX, boundingBox.minZ + i * CELL_SPACING, 1);
            addWall(pieces, random, secondWallX, boundingBox.minZ + i * CELL_SPACING + 8, 3);
            addWallPadder(pieces, random, padderX, boundingBox.minZ + (i + 1) * CELL_SPACING + 5, 1);
            if (courtyard[edgeX][i] != CellType.OUTSIDE && courtyard[edgeX][i + 2] == CellType.OUTSIDE) {
                addOuterCorner(pieces, random, cornerX, boundingBox.minZ + (i + 1) * CELL_SPACING + 6, endRotation);
                return;
            }
        }
    }

    private void buildSouthWestCorner(int cellX, List<StructureComponent> pieces, Random random) {
        buildCorner(cellX, false, false, pieces, random);
    }

    private void buildSouthEastCorner(int cellX, List<StructureComponent> pieces, Random random) {
        buildCorner(cellX, true, false, pieces, random);
    }

    private void buildNorthWestCorner(int cellX, List<StructureComponent> pieces, Random random) {
        buildCorner(cellX, false, true, pieces, random);
    }

    private void buildNorthEastCorner(int cellX, List<StructureComponent> pieces, Random random) {
        buildCorner(cellX, true, true, pieces, random);
    }

    private void buildCorner(int x, boolean east, boolean north, List<StructureComponent> pieces, Random random) {
        int z = north ? tableWidth - 2 : 1;
        int xStep = east ? 1 : -1;
        int zStep = north ? -1 : 1;
        int xEdge = east ? tableWidth - 1 : 0;
        int innerRotation = north ? (east ? 1 : 2) : (east ? 0 : 3);
        int outerRotation = north ? (east ? 2 : 3) : (east ? 1 : 0);

        do {
            int baseX = cornerX(x, east);
            int baseZ = cornerZ(z, north);
            int verticalX = baseX + (east ? -6 : 8);

            addWallPadder(pieces, random, verticalX, baseZ + (north ? 1 : -1), 1);
            if (courtyard[x][z + zStep] == CellType.OUTSIDE) {
                addWall(pieces, random, baseX + (east ? -16 : -2), baseZ + (north ? -10 : 0), 1);
                addWall(pieces, random, baseX + (east ? -8 : 6), baseZ + (north ? -2 : 8), 3);
                addWallPadder(pieces, random, verticalX, baseZ + (north ? -11 : 11), 1);
                z += zStep * 2;
            }

            baseZ = cornerZ(z, north);
            addInnerCorner(pieces, random, baseX + (east ? -8 : 0), baseZ + (north ? -8 : 0), innerRotation);
            addWallPadder(pieces, random, baseX + (east ? 1 : -1), baseZ + (north ? -8 : 6), 0);
            z += zStep;
            x += xStep;

            if (x != xEdge) {
                baseX = cornerX(x, east);
                baseZ = cornerZ(z, north);
                if (courtyard[x + xStep][z] != CellType.OUTSIDE) {
                    addWall(pieces, random, baseX + (east ? -4 : -6), baseZ + (north ? -2 : 0), 0);
                    addWallPadder(pieces, random, baseX + (east ? 7 : -7), baseZ + (north ? -2 : 0), 0);
                    x += xStep * 2;
                } else {
                    addOuterCorner(pieces, random, baseX + (east ? -4 : 0), baseZ + (north ? -4 : 0), outerRotation);
                    z += zStep;
                    x += xStep;
                }
            }
        } while (east ? x < xEdge : x > xEdge);
    }

    private int cornerX(int x, boolean east) {
        return east ? boundingBox.maxX - (tableWidth - 1 - x) * CELL_SPACING : boundingBox.minX + x * CELL_SPACING;
    }

    private int cornerZ(int z, boolean north) {
        return north ? boundingBox.maxZ - (tableWidth - 1 - z) * CELL_SPACING : boundingBox.minZ + z * CELL_SPACING;
    }

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        int cells = tableWidth * tableWidth;
        byte[] data = new byte[1 + (cells + 7) / 8];
        data[0] = (byte) tableWidth;

        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (courtyard[x][z] == CellType.EMPTY) {
                    int bit = x * tableWidth + z;
                    data[1 + bit / 8] |= 1 << (bit % 8);
                }
            }
        }
        tag.setByteArray("CY", data);
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        byte[] data = tag.getByteArray("CY");
        if (data.length < 2) return;

        int width = data[0] & 0xFF;
        int cells = width * width;
        // preserve legacy dimensions but reject negative and overflow widths and truncated data
        if (width == 0 || width > Byte.MAX_VALUE || data.length < 1 + (cells + 7) / 8) return;

        tableWidth = width;
        courtyard = new CellType[width][width];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
                int bit = x * width + z;
                if (((data[1 + bit / 8] & 0xFF) & (1 << (bit % 8))) != 0) {
                    courtyard[x][z] = CellType.EMPTY;
                }
            }
        }
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox chunkBox) {
        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (courtyard[x][z] == CellType.EMPTY) {
                    placePathCell(world, chunkBox, 3 + x * CELL_SPACING, 3 + z * CELL_SPACING);
                }
            }
        }

        placeBlockAtCurrentPosition(world, TFBlocks.bossSpawner, 0, RADIUS + 3, 2, RADIUS + 3, chunkBox);
        return true;
    }

    private void placePathCell(World world, StructureBoundingBox chunkBox, int startX, int startZ) {
        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                if (dx == 2 || dz == 2) continue;

                int x = startX + dx;
                int z = startZ + dz;
                if (getBlockAtCurrentPosition(world, x, 0, z, chunkBox) != Blocks.air) {
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, x, 0, z, chunkBox);
                } else if (getBlockAtCurrentPosition(world, x, -1, z, chunkBox) != Blocks.air) {
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, x, -1, z, chunkBox);
                }
            }
        }

        fillPathQuadrant(world, chunkBox, startX, startZ);
        fillPathQuadrant(world, chunkBox, startX + 3, startZ);
        fillPathQuadrant(world, chunkBox, startX, startZ + 3);
        fillPathQuadrant(world, chunkBox, startX + 3, startZ + 3);
    }

    private void fillPathQuadrant(World world, StructureBoundingBox chunkBox, int x, int z) {
        fillWithBlocks(
                world,
                chunkBox,
                x,
                0,
                z,
                x + 1,
                0,
                z + 1,
                TFBlocks.nagastoneEtched,
                TFBlocks.nagastoneEtched,
                true);
    }

    private int countCellsInArea(int minX, int minZ, int maxX, int maxZ, CellType type) {
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (courtyard[x][z] == type) count++;
            }
        }
        return count;
    }

    private int countNeighbours(int x, int z, CellType type) {
        int count = 0;
        if (x > 0 && courtyard[x - 1][z] == type) count++;
        if (x < tableWidth - 1 && courtyard[x + 1][z] == type) count++;
        if (z > 0 && courtyard[x][z - 1] == type) count++;
        if (z < tableWidth - 1 && courtyard[x][z + 1] == type) count++;
        return count;
    }

    private int countNeighbours(int x, int z) {
        return countNeighbours(x, z, CellType.HEDGE);
    }

    private List<ConnectionDirection> getPossibleConnections(int x, int z) {
        List<ConnectionDirection> possible = new ArrayList<>(4);
        if (countNeighbours(x, z) >= 2) return possible;
        for (ConnectionDirection direction : DIRECTIONS) {
            if (canConnectTo(x, z, direction)) possible.add(direction);
        }
        return possible;
    }

    private boolean hasNeighbour(int x, int z, ConnectionDirection direction) {
        int otherX = x + stepX(direction);
        int otherZ = z + stepZ(direction);
        return otherX >= 0 && otherX < tableWidth
                && otherZ >= 0
                && otherZ < tableWidth
                && courtyard[otherX][otherZ] == CellType.HEDGE;
    }

    private static int stepX(ConnectionDirection direction) {
        switch (direction) {
            case NORTH:
                return 1;
            case SOUTH:
                return -1;
            default:
                return 0;
        }
    }

    private static int stepZ(ConnectionDirection direction) {
        switch (direction) {
            case EAST:
                return 1;
            case WEST:
                return -1;
            default:
                return 0;
        }
    }

    private static ConnectionDirection opposite(ConnectionDirection direction) {
        switch (direction) {
            case NORTH:
                return ConnectionDirection.SOUTH;
            case SOUTH:
                return ConnectionDirection.NORTH;
            case EAST:
                return ConnectionDirection.WEST;
            default:
                return ConnectionDirection.EAST;
        }
    }

    private boolean canConnectTo(int x, int z, ConnectionDirection direction) {
        if (hasNeighbour(x, z, direction) || hasNeighbour(x, z, opposite(direction))) return false;

        int nearbyX = x;
        int nearbyZ = z;
        if (direction == ConnectionDirection.NORTH || direction == ConnectionDirection.SOUTH) {
            if (z > 1 && hasNeighbour(x, z, ConnectionDirection.WEST)) nearbyZ--;
            if (z < tableWidth - 2 && hasNeighbour(x, z, ConnectionDirection.EAST)) nearbyZ++;
        } else {
            if (x > 1 && hasNeighbour(x, z, ConnectionDirection.SOUTH)) nearbyX--;
            if (x < tableWidth - 2 && hasNeighbour(x, z, ConnectionDirection.NORTH)) nearbyX++;
        }

        if ((nearbyX != x || nearbyZ != z) && countNeighbours(nearbyX, nearbyZ) == 2
                && !hasNeighbour(nearbyX, nearbyZ, direction))
            return false;

        if ((direction == ConnectionDirection.NORTH && x == tableWidth - 2)
                || (direction == ConnectionDirection.SOUTH && x == 1)
                || (direction == ConnectionDirection.EAST && z == tableWidth - 2)
                || (direction == ConnectionDirection.WEST && z == 1))
            return true;

        int targetX = x + stepX(direction) * 2;
        int targetZ = z + stepZ(direction) * 2;
        if (courtyard[targetX][targetZ] != CellType.HEDGE) return false;

        int neighbours = countNeighbours(targetX, targetZ);
        if (neighbours == 0) return true;
        if (neighbours != 1 || hasNeighbour(targetX, targetZ, direction)) return false;

        int nextX = targetX;
        int nextZ = targetZ;

        if (direction == ConnectionDirection.NORTH || direction == ConnectionDirection.SOUTH) {
            if (hasNeighbour(targetX, targetZ, ConnectionDirection.EAST)) {
                if (targetZ == tableWidth - 2) return true;
                nextZ += 2;
            } else {
                if (targetZ == 1) return true;
                nextZ -= 2;
            }
        } else if (hasNeighbour(targetX, targetZ, ConnectionDirection.NORTH)) {
            if (targetX == tableWidth - 2) return true;
            nextX += 2;
        } else {
            if (targetX == 1) return true;
            nextX -= 2;
        }

        return countNeighbours(nextX, nextZ) == 1 || hasNeighbour(nextX, nextZ, opposite(direction));
    }
}
