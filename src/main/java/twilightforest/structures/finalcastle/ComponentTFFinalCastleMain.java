package twilightforest.structures.finalcastle;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import twilightforest.TFFeature;
import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.StructureTFDecoratorCastle;

public class ComponentTFFinalCastleMain extends StructureTFComponent {
    public ComponentTFFinalCastleMain() {
    }

    public ComponentTFFinalCastleMain(World world, Random rand, int i, int x, int y, int z) {
        this.setCoordBaseMode(0);
        this.spawnListIndex = 1; // main monsters

        x = ((x + 127) >> 8) << 8;
        z = ((z + 127) >> 8) << 8;

        this.boundingBox = StructureTFComponent.getComponentToAddBoundingBox(x, y, z, -24, 120, -24, 48, 40, 48, 0);

        ChunkCoordinates cc = TFFeature.getNearestCenterXYZ(x >> 4, z >> 4, world);

        int cx = (x >> 8) << 8;
        int cz = (z >> 8) << 8;

        FMLLog.fine("[TwilightForest] Making castle at " + x + ", " + z + ". center is " + cc.posX + ", " + cc.posZ);
        FMLLog.fine("[TwilightForest] Natural center at " + cx + ", " + cz);

        // decorator
        if (this.deco == null) {
            this.deco = new StructureTFDecoratorCastle();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void buildComponent(StructureComponent parent, List list, Random rand) {
        // add foundation
        ComponentTFFinalCastleFoundation48 foundation = new ComponentTFFinalCastleFoundation48(rand, 4, this);
        list.add(foundation);
        foundation.buildComponent(this, list, rand);

        // add roof
        StructureTFComponent roof = new ComponentTFFinalCastleRoof48Crenellated(rand, 4, this);
        list.add(roof);
        roof.buildComponent(this, list, rand);

        // boss gazebo on roof
        StructureTFComponent gazebo = new ComponentTFFinalCastleBossGazebo(rand, 5, this);
        list.add(gazebo);
        gazebo.buildComponent(this, list, rand);

        // build 4 towers on sides
        ComponentTFFinalCastleStairTower tower0 = new ComponentTFFinalCastleStairTower(rand, 3, boundingBox.minX, boundingBox.minY + 3, boundingBox.minZ, 2);
        list.add(tower0);
        tower0.buildComponent(this, list, rand);

        ComponentTFFinalCastleLargeTower tower1 = new ComponentTFFinalCastleLargeTower(rand, 3, boundingBox.maxX, boundingBox.minY + 3, boundingBox.minZ, 3);
        list.add(tower1);
        tower1.buildComponent(this, list, rand);

        ComponentTFFinalCastleStairTower tower2 = new ComponentTFFinalCastleStairTower(rand, 3, boundingBox.minX, boundingBox.minY + 3, boundingBox.maxZ, 1);
        list.add(tower2);
        tower2.buildComponent(this, list, rand);

        ComponentTFFinalCastleStairTower tower3 = new ComponentTFFinalCastleStairTower(rand, 3, boundingBox.maxX, boundingBox.minY + 3, boundingBox.maxZ, 0);
        list.add(tower3);
        tower3.buildComponent(this, list, rand);

        // tower maze towards entrance
        ChunkCoordinates dest = new ChunkCoordinates(boundingBox.minX - 4, boundingBox.maxY, boundingBox.minZ - 24);
        buildTowerMaze(list, rand, 48, 0, 24, 60, 0, 0, dest);

        // another tower/bridge maze towards the clock tower
        dest = new ChunkCoordinates(boundingBox.maxX + 4, boundingBox.minY, boundingBox.maxZ + 24);
        buildTowerMaze(list, rand, 0, 30, 24, 60, 2, 1, dest);

        // initial stairs down towards dungeon
        ComponentTFFinalCastleDungeonSteps steps0 = new ComponentTFFinalCastleDungeonSteps(rand, 5, boundingBox.minX + 18, boundingBox.minY + 1, boundingBox.minZ + 18, 0);
        list.add(steps0);
        steps0.buildComponent(this, list, rand);

        // continued steps
        ComponentTFFinalCastleDungeonSteps steps1 = steps0.buildMoreStepsTowards(parent, list, rand, 3);
        ComponentTFFinalCastleDungeonSteps steps2 = steps1.buildMoreStepsTowards(parent, list, rand, 3);
        ComponentTFFinalCastleDungeonSteps steps3 = steps2.buildMoreStepsTowards(parent, list, rand, 3);

        // start dungeon
        ComponentTFFinalCastleDungeonEntrance dRoom = steps3.buildLevelUnder(parent, list, rand, 1);

        // ComponentTFFinalCastleMural on front
        ChunkCoordinates mc = this.offsetTowerCCoords(48, 23, 25, 1, 0);
        ComponentTFFinalCastleMural ComponentTFFinalCastleMural0 = new ComponentTFFinalCastleMural(rand, 7, mc.posX, mc.posY, mc.posZ, 35, 30, 0);
        list.add(ComponentTFFinalCastleMural0);
        ComponentTFFinalCastleMural0.buildComponent(this, list, rand);

        // ComponentTFFinalCastleMural inside
        ChunkCoordinates mc1 = this.offsetTowerCCoords(48, 33, 24, -1, 0);
        ComponentTFFinalCastleMural ComponentTFFinalCastleMural1 = new ComponentTFFinalCastleMural(rand, 7, mc1.posX, mc1.posY, mc.posZ, 19, 12, 2);
        list.add(ComponentTFFinalCastleMural1);
        ComponentTFFinalCastleMural1.buildComponent(this, list, rand);
    }

    /**
     * Build a side tower, then tell it to start building towards the destination
     */
    private void buildTowerMaze(List list, Random rand, int x, int y, int z, int howFar, int direction, int type, ChunkCoordinates dest) {
        // duplicate list
        LinkedList before = new LinkedList(list);

        // build
        ChunkCoordinates tc = this.offsetTowerCCoords(x, y, z, howFar, direction);
        ComponentTFFinalCastleMazeTower13 sTower = new ComponentTFFinalCastleMazeTower13(rand, 3, tc.posX, tc.posY, tc.posZ, type, direction);
        // add bridge
        ChunkCoordinates bc = this.offsetTowerCCoords(x, y, z, 1, direction);
        ComponentTFFinalCastleBridge bridge = new ComponentTFFinalCastleBridge(this.getComponentType() + 1, bc.posX, bc.posY, bc.posZ, howFar - 7, direction);
        list.add(bridge);
        bridge.buildComponent(this, list, rand);

        // don't check if the bounding box is clear, there's either nothing there or we've made a terrible
        // mistake
        list.add(sTower);
        sTower.buildTowards(this, list, rand, dest);

        // check if we've successfully built the end tower
        if (this.isMazeComplete(list, type)) {
            FMLLog.fine("[TwilightForest] Tower maze type " + type + " complete!");
        } else {
            // TODO: add limit on retrying, in case of infinite loop?
            FMLLog.fine("[TwilightForest] Tower maze type " + type + " INCOMPLETE, retrying!");
            list.clear();
            list.addAll(before);
            this.buildTowerMaze(list, rand, x, y, z, howFar, direction, type, dest);
        }
    }

    private boolean isMazeComplete(List list, int type) {
        Iterator iterator = list.iterator();
        StructureComponent structurecomponent;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurecomponent = (StructureComponent) iterator.next();
        } while (!((structurecomponent instanceof ComponentTFFinalCastleEntranceTower && type == 0) || (structurecomponent instanceof ComponentTFFinalCastleBellTower21 && type == 1)));
        return true;
    }

    /**
     * Provides coordinates to make a tower such that it will open into the parent tower at the provided
     * coordinates.
     */
    protected ChunkCoordinates offsetTowerCCoords(int x, int y, int z, int howFar, int direction) {

        int dx = getXWithOffset(x, z);
        int dy = getYWithOffset(y);
        int dz = getZWithOffset(x, z);

        switch (direction) {
        case 0:
            dx += howFar;
            break;
        case 1:
            dz += howFar;
            break;
        case 2:
            dx -= howFar;
            break;
        case 3:
            dz -= howFar;
            break;
        }

        // ugh?
        return new ChunkCoordinates(dx, dy, dz);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        // walls
        fillWithRandomizedBlocks(world, sbb, 0, 0, 0, 48, 40, 48, false, rand, deco.randomBlocks);

        // 2M
        fillWithRandomizedBlocks(world, sbb, 13, 30, 1, 47, 30, 12, false, rand, deco.randomBlocks);
        this.fillWithBlocks(world, sbb, 13, 31, 12, 36, 31, 12, deco.fenceID, deco.fenceID, false);
        fillWithRandomizedBlocks(world, sbb, 13, 30, 36, 47, 30, 47, false, rand, deco.randomBlocks);
        this.fillWithBlocks(world, sbb, 13, 31, 36, 36, 31, 36, deco.fenceID, deco.fenceID, false);
        fillWithRandomizedBlocks(world, sbb, 1, 30, 1, 12, 30, 47, false, rand, deco.randomBlocks);
        this.fillWithBlocks(world, sbb, 12, 31, 12, 12, 31, 36, deco.fenceID, deco.fenceID, false);

        // second floor stairs to mezzanine
        fillWithRandomizedBlocks(world, sbb, 38, 25, 13, 47, 25, 35, false, rand, deco.randomBlocks);

        for (int i = 0; i < 5; i++) {
            int y = 30 - i;

            makeMezzTopStairs(world, sbb, y, 10 + i, 3);
            makeMezzTopStairs(world, sbb, y, 38 - i, 1);

            y = 25 - i;
            int x = 37 - i;
            this.fillWithMetadataBlocks(world, sbb, x, y, 14, x, y, 22, deco.stairID, getStairMeta(0), deco.stairID, getStairMeta(0), false);
            this.fillWithMetadataBlocks(world, sbb, x, y - 1, 14, x, y - 1, 22, deco.blockID, deco.blockMeta, deco.blockID, deco.blockMeta, false);
            this.fillWithMetadataBlocks(world, sbb, x, y, 26, x, y, 34, deco.stairID, getStairMeta(0), deco.stairID, getStairMeta(0), false);
            this.fillWithMetadataBlocks(world, sbb, x, y - 1, 26, x, y - 1, 34, deco.blockID, deco.blockMeta, deco.blockID, deco.blockMeta, false);
        }

        // pillars
        for (int x = 11; x < 47; x += 12) {
            for (int z = 11; z < 47; z += 12) {
                this.fillWithMetadataBlocks(world, sbb, x, 1, z, x + 2, 40, z + 2, deco.pillarID, deco.pillarMeta, deco.blockID, deco.blockMeta, false);

                makePillarBase(world, sbb, x, z, 1, 0);
                makePillarBase(world, sbb, x, z, 19, 4);
                makePillarBase(world, sbb, x, z, 21, 0);
                makePillarBase(world, sbb, x, z, 39, 4);

            }
        }

        // side pillars
        for (int rotation = 0; rotation < 4; rotation++) {
            for (int z = 11; z < 47; z += 12) {

                // no middle pillars on walls with entrances
                if (z == 23 && (rotation == 0 || rotation == 2)) {
                    continue;
                }

                this.fillBlocksRotated(world, sbb, 1, 1, z, 1, 40, z + 2, deco.pillarID, deco.pillarMeta, rotation);
                makeHalfPillarBase(world, sbb, rotation, 1, z, 0);
                makeHalfPillarBase(world, sbb, rotation, 19, z, 4);
                makeHalfPillarBase(world, sbb, rotation, 21, z, 0);
                makeHalfPillarBase(world, sbb, rotation, 39, z, 4);
            }
        }

        // second floor
        fillWithRandomizedBlocks(world, sbb, 1, 20, 1, 47, 20, 47, false, rand, deco.randomBlocks);

        // force field around dungeon stairs
        Block fieldBlock = TFBlocks.forceField;
        int fieldMeta = 6;
        this.fillWithMetadataBlocks(world, sbb, 12, 1, 12, 24, 10, 12, fieldBlock, fieldMeta, fieldBlock, fieldMeta, false);
        this.fillWithMetadataBlocks(world, sbb, 12, 1, 12, 12, 10, 24, fieldBlock, fieldMeta, fieldBlock, fieldMeta, false);
        this.fillWithMetadataBlocks(world, sbb, 24, 1, 12, 24, 10, 24, fieldBlock, fieldMeta, fieldBlock, fieldMeta, false);
        this.fillWithMetadataBlocks(world, sbb, 12, 1, 24, 24, 10, 24, fieldBlock, fieldMeta, fieldBlock, fieldMeta, false);

        this.fillWithMetadataBlocks(world, sbb, 12, 10, 12, 24, 10, 24, fieldBlock, fieldMeta, fieldBlock, fieldMeta, false);

        // doors in dungeon force field
        this.fillWithMetadataBlocks(world, sbb, 17, 1, 12, 19, 4, 12, TFBlocks.castleDoor, 2, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(world, sbb, 17, 1, 24, 19, 4, 24, TFBlocks.castleDoor, 2, Blocks.air, 0, false);

        // stairs to stair towers
        makeSmallTowerStairs(world, sbb, 0);
        makeSmallTowerStairs(world, sbb, 1);
        makeSmallTowerStairs(world, sbb, 3);
        makeComponentTFFinalCastleLargeTowerStairs(world, sbb, 2);

        // door, first floor
        this.fillWithMetadataBlocks(world, sbb, 48, 1, 23, 48, 4, 25, TFBlocks.castleDoor, 0, Blocks.air, 0, false);

        // door, second floor
        this.fillWithMetadataBlocks(world, sbb, 0, 31, 23, 0, 34, 25, TFBlocks.castleDoor, 1, Blocks.air, 0, false);

        return true;
    }

    private void makeSmallTowerStairs(World world, StructureBoundingBox sbb, int rotation) {
        for (int y = 1; y < 4; y++) {
            int z = 40 + y;
            this.fillBlocksRotated(world, sbb, 1, 1, z, 4, y, z, deco.blockID, deco.blockMeta, rotation);
            this.fillBlocksRotated(world, sbb, 2, y, z, 3, y, z, deco.stairID, getStairMeta(1 + rotation), rotation);
        }
    }

    private void makeComponentTFFinalCastleLargeTowerStairs(World world, StructureBoundingBox sbb, int rotation) {
        for (int y = 1; y < 4; y++) {
            int z = 38 + y;
            this.fillBlocksRotated(world, sbb, 2, 1, z, 6, y, z, deco.blockID, deco.blockMeta, rotation);
            this.fillBlocksRotated(world, sbb, 3, y, z, 5, y, z, deco.stairID, getStairMeta(1 + rotation), rotation);
        }
    }

    private void makeMezzTopStairs(World world, StructureBoundingBox sbb, int y, int z, int stairMeta) {
        this.fillWithMetadataBlocks(world, sbb, 38, y, z, 46, y, z, deco.stairID, getStairMeta(stairMeta), deco.stairID, getStairMeta(stairMeta), false);
        this.fillWithMetadataBlocks(world, sbb, 38, y - 1, z, 46, y - 1, z, deco.blockID, deco.blockMeta, deco.blockID, deco.blockMeta, false);
        this.fillWithAir(world, sbb, 38, y + 1, z, 46, y + 3, z);
    }

    private void makeHalfPillarBase(World world, StructureBoundingBox sbb, int rotation, int y, int z, int metaBit) {
        this.fillBlocksRotated(world, sbb, 2, y, z - 1, 2, y, z + 3, deco.stairID, getStairMeta(2 + rotation) | metaBit, rotation);
        this.placeBlockRotated(world, deco.stairID, getStairMeta(1 + rotation) | metaBit, 1, y, z - 1, rotation, sbb);
        this.placeBlockRotated(world, deco.stairID, getStairMeta(3 + rotation) | metaBit, 1, y, z + 3, rotation, sbb);
    }

    private void makePillarBase(World world, StructureBoundingBox sbb, int x, int z, int y, int metaBit) {
        this.fillWithMetadataBlocks(world, sbb, x + 0, y, z + 3, x + 3, y, z + 3, deco.stairID, getStairMeta(3) | metaBit, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(world, sbb, x - 1, y, z - 1, x + 2, y, z - 1, deco.stairID, getStairMeta(1) | metaBit, Blocks.air, 0, false);

        this.fillWithMetadataBlocks(world, sbb, x + 3, y, z - 1, x + 3, y, z + 2, deco.stairID, getStairMeta(2) | metaBit, Blocks.air, 0, false);
        this.fillWithMetadataBlocks(world, sbb, x - 1, y, z + 0, x - 1, y, z + 3, deco.stairID, getStairMeta(0) | metaBit, Blocks.air, 0, false);
    }

}
