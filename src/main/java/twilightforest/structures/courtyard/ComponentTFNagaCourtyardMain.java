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
    };

    public enum ConnectionDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    };

    int tableWidth = ROW_OF_CELLS * 2 - 1;
    int terraceLeft;
    CellType[][] courtyard = new CellType[tableWidth][tableWidth];

    static int ROW_OF_CELLS = 8;
    static int RADIUS = (int) ((((ROW_OF_CELLS - 2) / 2.0F) * 12.0F) + 8);
    static int DIAMETER = 2 * RADIUS + 1;

    static final float HEDGE_FLOOF = 0.5f;
    static final float TERRACE_RATE = 0.2f;

    public ComponentTFNagaCourtyardMain() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ComponentTFNagaCourtyardMain(World world, Random rand, int i, int x, int y, int z) {
        super(i);

        this.setCoordBaseMode(0);

        this.boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -RADIUS, -1, -RADIUS, RADIUS * 2 + 6, 10, RADIUS * 2 + 6, 0);

    }

    private void fillTable(int startX, int startZ, int finishX, int finishZ, CellType fillCell) {
        for (int i = startX; i <= finishX; i++) for (int j = startZ; j <= finishZ; j++) courtyard[i][j] = fillCell;
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        super.buildComponent(structurecomponent, list, random);

        // generate the table
        courtyard = new CellType[tableWidth][tableWidth];
        fillTable(0, 0, tableWidth - 1, tableWidth - 1, CellType.EMPTY);

        fillTable(0, 0, 1, 1, CellType.OUTSIDE);
        boolean outsideX = random.nextBoolean();
        boolean outsideZ = random.nextBoolean();
        if (outsideX) fillTable(2, 0, 3, 1, CellType.OUTSIDE);
        if (outsideZ) fillTable(0, 2, 1, 3, CellType.OUTSIDE);
        if (outsideX && outsideZ && random.nextBoolean()) fillTable(2, 2, 3, 3, CellType.OUTSIDE);

        fillTable(0, tableWidth - 2, 1, tableWidth - 1, CellType.OUTSIDE);
        outsideX = random.nextBoolean();
        outsideZ = random.nextBoolean();
        if (outsideX) fillTable(2, tableWidth - 2, 3, tableWidth - 1, CellType.OUTSIDE);
        if (outsideZ) fillTable(0, tableWidth - 4, 1, tableWidth - 3, CellType.OUTSIDE);
        if (outsideX && outsideZ && random.nextBoolean())
            fillTable(2, tableWidth - 4, 3, tableWidth - 3, CellType.OUTSIDE);

        fillTable(tableWidth - 2, 0, tableWidth - 1, 1, CellType.OUTSIDE);
        outsideX = random.nextBoolean();
        outsideZ = random.nextBoolean();
        if (outsideX) fillTable(tableWidth - 4, 0, tableWidth - 3, 1, CellType.OUTSIDE);
        if (outsideZ) fillTable(tableWidth - 2, 2, tableWidth - 1, 3, CellType.OUTSIDE);
        if (outsideX && outsideZ && random.nextBoolean())
            fillTable(tableWidth - 4, 2, tableWidth - 3, 3, CellType.OUTSIDE);

        fillTable(tableWidth - 2, tableWidth - 2, tableWidth - 1, tableWidth - 1, CellType.OUTSIDE);
        outsideX = random.nextBoolean();
        outsideZ = random.nextBoolean();
        if (outsideX) fillTable(tableWidth - 4, tableWidth - 2, tableWidth - 3, tableWidth - 1, CellType.OUTSIDE);
        if (outsideZ) fillTable(tableWidth - 2, tableWidth - 4, tableWidth - 1, tableWidth - 3, CellType.OUTSIDE);
        if (outsideX && outsideZ && random.nextBoolean())
            fillTable(tableWidth - 4, tableWidth - 4, tableWidth - 3, tableWidth - 3, CellType.OUTSIDE);

        int cellsCount = countCellsInArea(0, 0, tableWidth - 1, tableWidth - 1, CellType.EMPTY);

        // add terraces
        terraceLeft = (int) (cellsCount * TERRACE_RATE / 9);

        while (terraceLeft > 0) {
            int x = random.nextInt(tableWidth / 2) * 2;
            int z = random.nextInt(tableWidth / 2) * 2;

            boolean buildTerrace = countCellsInArea(x, z, x + 2, z + 2, CellType.EMPTY) == 9
                    && (((x - 3 < ROW_OF_CELLS) || (x > ROW_OF_CELLS))
                            && ((z - 3 < ROW_OF_CELLS) || (z > ROW_OF_CELLS)));
            if (buildTerrace) {
                if (x > 0)
                    buildTerrace = buildTerrace && (countCellsInArea(x - 1, z, x - 1, z + 2, CellType.EMPTY) == 3);
                if (x < tableWidth - 3)
                    buildTerrace = buildTerrace && (countCellsInArea(x + 3, z, x + 3, z + 2, CellType.EMPTY) == 3);
                if (z > 0)
                    buildTerrace = buildTerrace && (countCellsInArea(x, z - 1, x + 2, z - 1, CellType.EMPTY) == 3);
                if (z < tableWidth - 3)
                    buildTerrace = buildTerrace && (countCellsInArea(x, z + 3, x + 2, z + 3, CellType.EMPTY) == 3);
            }

            if (buildTerrace) {
                ComponentTFNagaCourtyardTerraceAbstract terrace;
                if (random.nextInt(150) == 0) terrace = new ComponentTFNagaCourtyardTerraceStatue(
                        1,
                        boundingBox.minX + 3 + x * 6,
                        boundingBox.minY,
                        boundingBox.minZ + 3 + z * 6,
                        random.nextInt(4));
                else if (random.nextBoolean()) terrace = new ComponentTFNagaCourtyardTerraceBrazier(
                        1,
                        boundingBox.minX + 3 + x * 6,
                        boundingBox.minY,
                        boundingBox.minZ + 3 + z * 6,
                        0);
                else terrace = new ComponentTFNagaCourtyardTerraceDuct(
                        1,
                        boundingBox.minX + 3 + x * 6,
                        boundingBox.minY,
                        boundingBox.minZ + 3 + z * 6,
                        random.nextInt(4));

                list.add(terrace);
                terrace.buildComponent(this, list, random);

                fillTable(x, z, x + 2, z + 2, CellType.TERRACE);
                terraceLeft--;
            }
        }

        // add hedges
        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (x % 2 == 1 && z % 2 == 1 && courtyard[x][z] == CellType.EMPTY && !(x == z && x == tableWidth / 2)) {
                    courtyard[x][z] = CellType.HEDGE;

                    ComponentTFNagaCourtyardHedge hedge = new ComponentTFNagaCourtyardHedge(
                            1,
                            boundingBox.minX + 3 + x * 6,
                            boundingBox.minY,
                            boundingBox.minZ + 3 + z * 6,
                            0);
                    list.add(hedge);
                    hedge.buildComponent(this, list, random);
                }
            }
        }

        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (x % 2 == 1 && z % 2 == 1 && courtyard[x][z] == CellType.HEDGE) {
                    ArrayList<ConnectionDirection> possibleConnections = getPossibleConnections(x, z);
                    if (possibleConnections.size() > 0) {
                        ConnectionDirection direction = possibleConnections
                                .get(random.nextInt(possibleConnections.size()));
                        int x1 = x;
                        int z1 = z;
                        switch (direction) {
                            default:
                            case NORTH:
                                x1++;
                                break;
                            case SOUTH:
                                x1--;
                                break;
                            case EAST:
                                z1++;
                                break;
                            case WEST:
                                z1--;
                                break;
                        }
                        courtyard[x1][z1] = CellType.HEDGE;

                        ComponentTFNagaCourtyardHedge hedge = new ComponentTFNagaCourtyardHedge(
                                1,
                                boundingBox.minX + 3 + x1 * 6,
                                boundingBox.minY,
                                boundingBox.minZ + 3 + z1 * 6,
                                0);
                        list.add(hedge);
                        hedge.buildComponent(this, list, random);
                    }
                }
            }
        }

        for (int x = 0; x < tableWidth; x++) {
            for (int z = 0; z < tableWidth; z++) {
                if (courtyard[x][z] == CellType.HEDGE) {
                    if (countNeighbours(x, z) == 1 && x != 0 && x != tableWidth - 1 && z != 0 && z != tableWidth - 1) {
                        if (random.nextInt(5) < 2) {
                            ComponentTFNagaCourtyardHedgePillar pillar = new ComponentTFNagaCourtyardHedgePillar(
                                    1,
                                    boundingBox.minX + 3 + x * 6,
                                    boundingBox.minY,
                                    boundingBox.minZ + 3 + z * 6,
                                    0);
                            list.add(pillar);
                            pillar.buildComponent(this, list, random);
                        }
                    }

                    boolean generatePadderNorth = false;
                    boolean generatePadderSouth = false;
                    boolean generatePadderEast = false;
                    boolean generatePadderWest = false;
                    if (x == 0) {
                        generatePadderSouth = true;
                    } else {
                        if (courtyard[x - 1][z] == CellType.OUTSIDE) generatePadderSouth = true;
                    }
                    if (z == 0) {
                        generatePadderWest = true;
                    } else {
                        if (courtyard[x][z - 1] == CellType.OUTSIDE) generatePadderWest = true;
                    }
                    if (hasNeighbour(x, z, ConnectionDirection.NORTH) || x == tableWidth - 1)
                        generatePadderNorth = true;
                    if (hasNeighbour(x, z, ConnectionDirection.EAST) || z == tableWidth - 1) generatePadderEast = true;

                    if (generatePadderNorth) {
                        /*
                         * ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(1,
                         * boundingBox.minX - 1 + (x + 1) * 6, boundingBox.minY, boundingBox.minZ + 4 + z * 6, 1,
                         * HEDGE_FLOOF); list.add(padder); padder.buildComponent(this, list, random);
                         */
                        ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(
                                1,
                                boundingBox.minX + 1 + (x + 1) * 6,
                                boundingBox.minY,
                                boundingBox.minZ + 6 + z * 6,
                                3);
                        list.add(padder);
                        padder.buildComponent(this, list, random);
                    }
                    if (generatePadderSouth) {
                        /*
                         * ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(1,
                         * boundingBox.minX - 1 + x * 6, boundingBox.minY, boundingBox.minZ + 4 + z * 6, 1,
                         * HEDGE_FLOOF); list.add(padder); padder.buildComponent(this, list, random);
                         */
                        ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(
                                1,
                                boundingBox.minX + 1 + x * 6,
                                boundingBox.minY,
                                boundingBox.minZ + 6 + z * 6,
                                3);
                        list.add(padder);
                        padder.buildComponent(this, list, random);
                    }
                    if (generatePadderEast) {
                        ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(
                                1,
                                boundingBox.minX + 3 + x * 6,
                                boundingBox.minY,
                                boundingBox.minZ + 2 + (z + 1) * 6,
                                0);
                        list.add(padder);
                        padder.buildComponent(this, list, random);
                    }
                    if (generatePadderWest) {
                        ComponentTFNagaCourtyardHedgePadder padder = new ComponentTFNagaCourtyardHedgePadder(
                                1,
                                boundingBox.minX + 3 + x * 6,
                                boundingBox.minY,
                                boundingBox.minZ + 2 + z * 6,
                                0);
                        list.add(padder);
                        padder.buildComponent(this, list, random);
                    }
                }
            }
        }

        /*
         * ComponentTFNagaCourtyardHedgePadder hedge = new ComponentTFNagaCourtyardHedgePadder(1, boundingBox.minX,
         * boundingBox.minY, boundingBox.minZ, 0, HEDGE_FLOOF); list.add(hedge); hedge.buildComponent(this, list,
         * random);
         */

        // add walls
        // z=0
        boolean generatingWall = false;
        int i = 0;
        int j = 0;
        int lastOutsideX = 0;
        int firstOutsideX = 0;
        while (!generatingWall) {
            if (courtyard[i][0] == CellType.OUTSIDE && courtyard[i + 1][0] != CellType.OUTSIDE) {
                generatingWall = true;
                lastOutsideX = i;

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX + ((i + 1) * 6),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(corner);
                corner.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + ((i + 1) * 6 + 5),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        while (generatingWall) {
            if (courtyard[i][0] != CellType.OUTSIDE && courtyard[i + 2][0] == CellType.OUTSIDE) {
                generatingWall = false;
                firstOutsideX = i + 2;

                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + (i * 6),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + (i * 6 + 11),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX + (i * 6 + 12),
                        boundingBox.minY,
                        boundingBox.minZ,
                        1);
                list.add(corner);
                corner.buildComponent(this, list, random);
            } else {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + (i * 6),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + (i * 6 + 11),
                        boundingBox.minY,
                        boundingBox.minZ,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        // inner corners
        // x = 0, z = 0
        i = lastOutsideX;
        j = 1;
        do {
            ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.minX + ((i + 1) * 6 + 2),
                    boundingBox.minY,
                    boundingBox.minZ + (j * 6 - 1),
                    1);
            list.add(padder);
            padder.buildComponent(this, list, random);

            if (courtyard[i][j + 1] == CellType.OUTSIDE) {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + ((i + 1) * 6 - 8),
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);
                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + ((i + 1) * 6),
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + ((i + 1) * 6 + 2),
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6 + 11),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                j = j + 2;
            }

            ComponentTFNagaCourtyardWallCornerInner cornerInner = new ComponentTFNagaCourtyardWallCornerInner(
                    1,
                    boundingBox.minX + (i * 6),
                    boundingBox.minY,
                    boundingBox.minZ + (j * 6),
                    3);
            list.add(cornerInner);
            cornerInner.buildComponent(this, list, random);

            padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.minX + (i * 6 - 1),
                    boundingBox.minY,
                    boundingBox.minZ + ((j + 1) * 6),
                    0);
            list.add(padder);
            padder.buildComponent(this, list, random);

            j++;
            i--;

            if (i != 0) {
                if (courtyard[i - 1][j] != CellType.OUTSIDE) {
                    ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                            1,
                            boundingBox.minX + (i * 6 - 6),
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            0);
                    list.add(wall);
                    wall.buildComponent(this, list, random);

                    padder = new ComponentTFNagaCourtyardWallPadder(
                            1,
                            boundingBox.minX + (i * 6 - 7),
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            0);
                    list.add(padder);
                    padder.buildComponent(this, list, random);

                    i = i - 2;
                } else {
                    ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                            1,
                            boundingBox.minX + (i * 6),
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            0);
                    list.add(corner);
                    corner.buildComponent(this, list, random);

                    j++;
                    i--;
                }
            }
        } while (i > 0);

        // x = max, z = 0
        i = firstOutsideX;
        j = 1;
        do {
            ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.maxX - (tableWidth - 2 - i) * 6 - 12,
                    boundingBox.minY,
                    boundingBox.minZ + (j * 6 - 1),
                    1);
            list.add(padder);
            padder.buildComponent(this, list, random);

            if (courtyard[i][j + 1] == CellType.OUTSIDE) {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 22,
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);
                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 14,
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 12,
                        boundingBox.minY,
                        boundingBox.minZ + (j * 6 + 11),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                j = j + 2;
            }

            ComponentTFNagaCourtyardWallCornerInner cornerInner = new ComponentTFNagaCourtyardWallCornerInner(
                    1,
                    boundingBox.maxX - (tableWidth - 1 - i) * 6 - 8,
                    boundingBox.minY,
                    boundingBox.minZ + (j * 6),
                    0);
            list.add(cornerInner);
            cornerInner.buildComponent(this, list, random);

            padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.maxX - (tableWidth - 1 - i) * 6 + 1,
                    boundingBox.minY,
                    boundingBox.minZ + ((j + 1) * 6),
                    0);
            list.add(padder);
            padder.buildComponent(this, list, random);

            j++;
            i++;

            if (i != tableWidth - 1) {
                if (courtyard[i + 1][j] != CellType.OUTSIDE) {
                    ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 - 4,
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            0);
                    list.add(wall);
                    wall.buildComponent(this, list, random);

                    padder = new ComponentTFNagaCourtyardWallPadder(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 + 7,
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            0);
                    list.add(padder);
                    padder.buildComponent(this, list, random);

                    i = i + 2;
                } else {
                    ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 - 4,
                            boundingBox.minY,
                            boundingBox.minZ + (j * 6),
                            1);
                    list.add(corner);
                    corner.buildComponent(this, list, random);

                    j++;
                    i++;
                }
            }
        } while (i < tableWidth - 1);

        // z = tableWidth - 1
        generatingWall = false;
        i = 0;
        while (!generatingWall) {
            if (courtyard[i][tableWidth - 1] == CellType.OUTSIDE
                    && courtyard[i + 1][tableWidth - 1] != CellType.OUTSIDE) {
                generatingWall = true;
                lastOutsideX = i;

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX + ((i + 1) * 6),
                        boundingBox.minY,
                        boundingBox.maxZ - 4,
                        3);
                list.add(corner);
                corner.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + ((i + 1) * 6 + 5),
                        boundingBox.minY,
                        boundingBox.maxZ - 2,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        while (generatingWall) {
            if (courtyard[i][tableWidth - 1] != CellType.OUTSIDE
                    && courtyard[i + 2][tableWidth - 1] == CellType.OUTSIDE) {
                generatingWall = false;
                firstOutsideX = i + 2;

                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + (i * 6),
                        boundingBox.minY,
                        boundingBox.maxZ - 2,
                        0);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + (i * 6 + 11),
                        boundingBox.minY,
                        boundingBox.maxZ - 2,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX + (i * 6 + 12),
                        boundingBox.minY,
                        boundingBox.maxZ - 4,
                        2);
                list.add(corner);
                corner.buildComponent(this, list, random);
            } else {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + (i * 6),
                        boundingBox.minY,
                        boundingBox.maxZ - 2,
                        0);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + (i * 6 + 11),
                        boundingBox.minY,
                        boundingBox.maxZ - 2,
                        0);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        // inner corners
        // x = 0, z = max
        i = lastOutsideX;
        j = tableWidth - 2;
        do {
            ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.minX + ((i + 1) * 6 + 2),
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6 - 1),
                    1);
            list.add(padder);
            padder.buildComponent(this, list, random);

            if (courtyard[i][j - 1] == CellType.OUTSIDE) {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + ((i + 1) * 6 - 8),
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6) - 10,
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);
                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX + ((i + 1) * 6),
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + ((i + 1) * 6 + 2),
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 11),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                j = j - 2;
            }

            ComponentTFNagaCourtyardWallCornerInner cornerInner = new ComponentTFNagaCourtyardWallCornerInner(
                    1,
                    boundingBox.minX + (i * 6),
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6) - 8,
                    2);
            list.add(cornerInner);
            cornerInner.buildComponent(this, list, random);

            padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.minX + (i * 6 - 1),
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6) - 8,
                    0);
            list.add(padder);
            padder.buildComponent(this, list, random);

            j--;
            i--;

            if (i != 0) {
                if (courtyard[i - 1][j] != CellType.OUTSIDE) {
                    ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                            1,
                            boundingBox.minX + (i * 6 - 6),
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                            0);
                    list.add(wall);
                    wall.buildComponent(this, list, random);

                    padder = new ComponentTFNagaCourtyardWallPadder(
                            1,
                            boundingBox.minX + (i * 6 - 7),
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                            0);
                    list.add(padder);
                    padder.buildComponent(this, list, random);

                    i = i - 2;
                } else {
                    ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                            1,
                            boundingBox.minX + (i * 6),
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 4),
                            3);
                    list.add(corner);
                    corner.buildComponent(this, list, random);

                    j--;
                    i--;
                }
            }
        } while (i > 0);

        // x = max, z = max
        i = firstOutsideX;
        j = tableWidth - 2;
        do {
            ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.maxX - (tableWidth - 2 - i) * 6 - 12,
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6 - 1),
                    1);
            list.add(padder);
            padder.buildComponent(this, list, random);

            if (courtyard[i][j - 1] == CellType.OUTSIDE) {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 22,
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6) - 10,
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);
                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 14,
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.maxX - (tableWidth - 2 - i) * 6 - 12,
                        boundingBox.minY,
                        boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 11),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                j = j - 2;
            }

            ComponentTFNagaCourtyardWallCornerInner cornerInner = new ComponentTFNagaCourtyardWallCornerInner(
                    1,
                    boundingBox.maxX - (tableWidth - 1 - i) * 6 - 8,
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 8),
                    1);
            list.add(cornerInner);
            cornerInner.buildComponent(this, list, random);

            padder = new ComponentTFNagaCourtyardWallPadder(
                    1,
                    boundingBox.maxX - (tableWidth - 1 - i) * 6 + 1,
                    boundingBox.minY,
                    boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 8),
                    0);
            list.add(padder);
            padder.buildComponent(this, list, random);

            j--;
            i++;

            if (i != tableWidth - 1) {
                if (courtyard[i + 1][j] != CellType.OUTSIDE) {
                    ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 - 4,
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                            0);
                    list.add(wall);
                    wall.buildComponent(this, list, random);

                    padder = new ComponentTFNagaCourtyardWallPadder(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 + 7,
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6 + 2),
                            0);
                    list.add(padder);
                    padder.buildComponent(this, list, random);

                    i = i + 2;
                } else {
                    ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                            1,
                            boundingBox.maxX - (tableWidth - 1 - i) * 6 - 4,
                            boundingBox.minY,
                            boundingBox.maxZ - ((tableWidth - 1 - j) * 6) - 4,
                            2);
                    list.add(corner);
                    corner.buildComponent(this, list, random);

                    j--;
                    i++;
                }
            }
        } while (i < tableWidth - 1);

        // x=0
        generatingWall = false;
        i = 0;
        while (!generatingWall) {
            if (courtyard[0][i] == CellType.OUTSIDE && courtyard[0][i + 1] != CellType.OUTSIDE) {
                generatingWall = true;

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6),
                        0);
                list.add(corner);
                corner.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + 2,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        while (generatingWall) {
            if (courtyard[0][i] != CellType.OUTSIDE && courtyard[0][i + 2] == CellType.OUTSIDE) {
                generatingWall = false;

                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX - 8,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + 2,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.minX,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 6),
                        3);
                list.add(corner);
                corner.buildComponent(this, list, random);
            } else {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX - 8,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.minX,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.minX + 2,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        // x=tableWidth - 1
        generatingWall = false;
        i = 0;
        while (!generatingWall) {
            if (courtyard[tableWidth - 1][i] == CellType.OUTSIDE
                    && courtyard[tableWidth - 1][i + 1] != CellType.OUTSIDE) {
                generatingWall = true;

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.maxX - 4,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6),
                        1);
                list.add(corner);
                corner.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.maxX,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }
        while (generatingWall) {
            if (courtyard[tableWidth - 1][i] != CellType.OUTSIDE
                    && courtyard[tableWidth - 1][i + 2] == CellType.OUTSIDE) {
                generatingWall = false;

                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - 10,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - 2,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.maxX,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallCornerOuter corner = new ComponentTFNagaCourtyardWallCornerOuter(
                        1,
                        boundingBox.maxX - 4,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 6),
                        2);
                list.add(corner);
                corner.buildComponent(this, list, random);
            } else {
                ComponentTFNagaCourtyardWall wall = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - 10,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6),
                        1);
                list.add(wall);
                wall.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWall wall1 = new ComponentTFNagaCourtyardWall(
                        1,
                        boundingBox.maxX - 2,
                        boundingBox.minY,
                        boundingBox.minZ + (i * 6 + 8),
                        3);
                list.add(wall1);
                wall1.buildComponent(this, list, random);

                ComponentTFNagaCourtyardWallPadder padder = new ComponentTFNagaCourtyardWallPadder(
                        1,
                        boundingBox.maxX,
                        boundingBox.minY,
                        boundingBox.minZ + ((i + 1) * 6 + 5),
                        1);
                list.add(padder);
                padder.buildComponent(this, list, random);

                i++;
            }
            i++;
        }

        // Decorator
        ComponentTFNagaCourtyardDecorator decorator = new ComponentTFNagaCourtyardDecorator(
                1,
                boundingBox.minX,
                boundingBox.minY,
                boundingBox.minZ,
                boundingBox.getXSize(),
                boundingBox.getYSize(),
                boundingBox.getZSize());
        list.add(decorator);
        decorator.buildComponent(this, list, random);

    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);

        // We don't really need to storeanything other than null vs CellType.EMPTY
        // This data is used by addComponentParts to generate paths in empty areas
        int numBits = tableWidth * tableWidth;
        // Use a byte array to store the tableWidth and a packed bit array
        byte[] emptySpots = new byte[1 + numBits / 8 + (numBits % 8 > 0 ? 1 : 0)];
        emptySpots[0] = (byte) tableWidth;
        for (int x = 0; x < this.tableWidth; x++) {
            for (int z = 0; z < this.tableWidth; z++) {
                if (this.courtyard[x][z] == CellType.EMPTY) {
                    int bitIndex = this.tableWidth * x + z;
                    emptySpots[1 + (bitIndex / 8)] |= 1 << (bitIndex % 8);
                }
            }
        }
        tag.setByteArray("CY", emptySpots);
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);

        // We don't really care about anything other than null vs CellType.EMPTY
        // This data is used by addComponentParts to generate paths in empty areas
        byte[] emptySpots = tag.getByteArray("CY");
        if (emptySpots.length > 1) {
            this.tableWidth = emptySpots[0];
            this.courtyard = new CellType[tableWidth][tableWidth];

            // Load boolean values from a packed bit array starting at index 1
            // 0 => Leave the CellType as null
            // 1 => Use CellType.EMPTY (it's the only one that gets used durring block generation)
            int index = 1;
            int shift = 0;
            read_bits_array: for (int x = 0; x < this.tableWidth; x++) {
                for (int z = 0; z < this.tableWidth; z++) {
                    if ((((emptySpots[index] & 0xFF) >> shift++) & 1) == 1) {
                        this.courtyard[x][z] = CellType.EMPTY;
                    }

                    // Incronmnet our bit index and break if we hit the end, that's it.
                    if (shift >= 8) {
                        shift = 0;
                        index++;
                        // Sanity check just in case
                        if (index >= emptySpots.length) {
                            break read_bits_array;
                        }
                    }
                }
            }
        }
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // test blocks
        /*
         * placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 0, 0, 0, sbb);
         * placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 0, 0, 2 * RADIUS + 6, sbb);
         * placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 2 * RADIUS + 6, 0, 0, sbb);
         * placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 2 * RADIUS + 6, 0, 2 * RADIUS + 6, sbb);
         */

        // paths
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableWidth; j++) {
                if (courtyard[i][j] == CellType.EMPTY) {
                    int startX = 3 + i * 6;
                    int startZ = 3 + j * 6;

                    for (int k = 0; k < 5; k++) {
                        for (int l = 0; l < 5; l++) {
                            if (k != 2 && l != 2) {
                                if (this.getBlockAtCurrentPosition(world, startX + k, 0, startZ + l, sbb) != Blocks.air)
                                    this.placeBlockAtCurrentPosition(
                                            world,
                                            TFBlocks.nagastoneEtched,
                                            0,
                                            startX + k,
                                            0,
                                            startZ + l,
                                            sbb);
                                else if (this.getBlockAtCurrentPosition(world, startX + k, -1, startZ + l, sbb)
                                        != Blocks.air)
                                    this.placeBlockAtCurrentPosition(
                                            world,
                                            TFBlocks.nagastoneEtched,
                                            0,
                                            startX + k,
                                            -1,
                                            startZ + l,
                                            sbb);
                            }
                        }
                    }

                    this.fillWithBlocks(
                            world,
                            sbb,
                            startX,
                            0,
                            startZ,
                            startX + 1,
                            0,
                            startZ + 1,
                            TFBlocks.nagastoneEtched,
                            TFBlocks.nagastoneEtched,
                            true);
                    this.fillWithBlocks(
                            world,
                            sbb,
                            startX + 3,
                            0,
                            startZ,
                            startX + 4,
                            0,
                            startZ + 1,
                            TFBlocks.nagastoneEtched,
                            TFBlocks.nagastoneEtched,
                            true);
                    this.fillWithBlocks(
                            world,
                            sbb,
                            startX,
                            0,
                            startZ + 3,
                            startX + 1,
                            0,
                            startZ + 4,
                            TFBlocks.nagastoneEtched,
                            TFBlocks.nagastoneEtched,
                            true);
                    this.fillWithBlocks(
                            world,
                            sbb,
                            startX + 3,
                            0,
                            startZ + 3,
                            startX + 4,
                            0,
                            startZ + 4,
                            TFBlocks.nagastoneEtched,
                            TFBlocks.nagastoneEtched,
                            true);
                }
            }
        }

        // naga spawner seems important
        placeBlockAtCurrentPosition(world, TFBlocks.bossSpawner, 0, RADIUS + 3, 2, RADIUS + 3, sbb);

        return true;
    }

    private int countCellsInArea(int minX, int minZ, int maxX, int maxZ, CellType countType) {
        int cellsCount = 0;

        for (int i = minX; i <= maxX; i++) {
            for (int j = minZ; j <= maxZ; j++) {
                if (courtyard[i][j] == countType) cellsCount++;
            }
        }

        return cellsCount;
    }

    private int countNeighbours(int x, int z, CellType neighbour) {
        int neighbours = 0;
        if (x > 0) if (courtyard[x - 1][z] == neighbour) neighbours++;
        if (x < tableWidth - 1) if (courtyard[x + 1][z] == neighbour) neighbours++;
        if (z > 0) if (courtyard[x][z - 1] == neighbour) neighbours++;
        if (z < tableWidth - 1) if (courtyard[x][z + 1] == neighbour) neighbours++;
        return neighbours;
    }

    private int countNeighbours(int x, int z) {
        return countNeighbours(x, z, CellType.HEDGE);
    }

    private ArrayList<ConnectionDirection> getPossibleConnections(int x, int z) {
        ArrayList<ConnectionDirection> possibleConnections = new ArrayList<ConnectionDirection>();

        if (x == 5 && z == 11) {
            int test = 0;
        }

        if (countNeighbours(x, z) < 2) {
            for (int i = 0; i < 4; i++) {
                ConnectionDirection direction = directionFromInt(i);
                if (canConnectTo(x, z, direction)) possibleConnections.add(direction);
            }
        }

        return possibleConnections;
    }

    private boolean hasNeighbour(int x, int z, ConnectionDirection direction) {
        switch (direction) {
            default:
            case NORTH:
                if (x < tableWidth - 1) return courtyard[x + 1][z] == CellType.HEDGE;
                break;
            case SOUTH:
                if (x > 0) return courtyard[x - 1][z] == CellType.HEDGE;
                break;
            case EAST:
                if (z < tableWidth - 1) return courtyard[x][z + 1] == CellType.HEDGE;
                break;
            case WEST:
                if (z > 0) return courtyard[x][z - 1] == CellType.HEDGE;
                break;
        }
        return false;
    }

    private ConnectionDirection directionFromInt(int x) {
        switch (x) {
            default:
            case 0:
                return ConnectionDirection.NORTH;
            case 1:
                return ConnectionDirection.SOUTH;
            case 2:
                return ConnectionDirection.EAST;
            case 3:
                return ConnectionDirection.WEST;
        }
    }

    private ConnectionDirection getOppositeDirection(ConnectionDirection direction) {
        switch (direction) {
            default:
            case NORTH:
                return ConnectionDirection.SOUTH;
            case SOUTH:
                return ConnectionDirection.NORTH;
            case EAST:
                return ConnectionDirection.WEST;
            case WEST:
                return ConnectionDirection.EAST;
        }
    }

    private boolean canConnectTo(int x, int z, ConnectionDirection direction) {
        if (hasNeighbour(x, z, direction) || hasNeighbour(x, z, getOppositeDirection(direction))) return false;

        int connectX = x;
        int connectZ = z;

        switch (direction) {
            default:
            case NORTH:
            case SOUTH:
                if (z > 1 && hasNeighbour(x, z, ConnectionDirection.WEST)) {
                    connectZ--;
                }
                if (z < tableWidth - 2 && hasNeighbour(x, z, ConnectionDirection.EAST)) {
                    connectZ++;
                }
                break;
            case EAST:
            case WEST:
                if (x > 1 && hasNeighbour(x, z, ConnectionDirection.SOUTH)) {
                    connectX--;
                }
                if (x < tableWidth - 2 && hasNeighbour(x, z, ConnectionDirection.NORTH)) {
                    connectX++;
                }
                break;
        }
        if ((connectX != x || connectZ != z) && countNeighbours(connectX, connectZ) == 2
                && !hasNeighbour(connectX, connectZ, direction))
            return false;

        connectX = x;
        connectZ = z;

        switch (direction) {
            default:
            case NORTH:
                if (x == tableWidth - 2) return true;
                connectX += 2;
                break;
            case SOUTH:
                if (x == 1) return true;
                connectX -= 2;
                break;
            case EAST:
                if (z == tableWidth - 2) return true;
                connectZ += 2;
                break;
            case WEST:
                if (z == 1) return true;
                connectZ -= 2;
                break;
        }

        if (courtyard[connectX][connectZ] != CellType.HEDGE) return false;
        else {
            if (courtyard[connectX][connectZ] == CellType.OUTSIDE) return true;
            switch (countNeighbours(connectX, connectZ)) {
                case 0:
                    return true;
                case 1:
                    if (hasNeighbour(connectX, connectZ, direction)) return false;
                    else {
                        int connectX1 = connectX;
                        int connectZ1 = connectZ;
                        switch (direction) {
                            default:
                            case NORTH:
                            case SOUTH:
                                if (hasNeighbour(connectX, connectZ, ConnectionDirection.EAST)) {
                                    if (connectZ == tableWidth - 2) return true;
                                    else connectZ1 += 2;
                                } else {
                                    if (connectZ == 1) return true;
                                    else connectZ1 -= 2;
                                }
                                break;
                            case EAST:
                            case WEST:
                                if (hasNeighbour(connectX, connectZ, ConnectionDirection.NORTH)) {
                                    if (connectX == tableWidth - 2) return true;
                                    else connectX1 += 2;
                                } else {
                                    if (connectX == 1) return true;
                                    else connectX1 -= 2;
                                }
                                break;
                        }
                        return countNeighbours(connectX1, connectZ1) == 1
                                || hasNeighbour(connectX1, connectZ1, getOppositeDirection(direction));
                    }
                default:
                    return false;
            }
        }

    }

}
