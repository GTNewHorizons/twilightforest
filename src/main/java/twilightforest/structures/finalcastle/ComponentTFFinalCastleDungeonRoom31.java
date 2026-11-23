package twilightforest.structures.finalcastle;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;
import twilightforest.structures.StructureTFDecoratorCastle;
import twilightforest.structures.lichtower.ComponentTFTowerWing;

public class ComponentTFFinalCastleDungeonRoom31 extends ComponentTFTowerWing {

    private static final String NBT_DUNGEON_LEVEL = "castleDungeonLevel";
    public int level;

    // a full biome scan is needed only once per piece, not every chunk pass
    private Boolean outsidePlateau;

    public ComponentTFFinalCastleDungeonRoom31() {}

    public ComponentTFFinalCastleDungeonRoom31(Random rand, int i, int x, int y, int z, int direction, int level) {
        super(i);
        this.setCoordBaseMode(direction);
        this.spawnListIndex = 2; // dungeon monsters
        this.size = 31;
        this.height = 7;
        this.level = level;
        this.boundingBox = StructureTFComponent
                .getComponentToAddBoundingBox(x, y, z, -15, 0, -15, this.size - 1, this.height - 1, this.size - 1, 0);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger(NBT_DUNGEON_LEVEL, level);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        level = nbt.getInteger(NBT_DUNGEON_LEVEL);
        outsidePlateau = null;
        // the dungeons custom magic block and force field palette is not
        // encoded by the decorator class name in the original save format
        restoreDungeonDecoration();
    }

    protected void restoreDungeonDecoration() {
        deco = new StructureTFDecoratorCastle();
        deco.blockID = TFBlocks.castleMagic;
        deco.blockMeta = 2;
        deco.fenceID = TFBlocks.forceField;
        deco.fenceMeta = 1;
    }

    @Override
    public void buildComponent(StructureComponent parent, List<StructureComponent> list, Random rand) {
        if (parent instanceof StructureTFComponent) {
            this.deco = ((StructureTFComponent) parent).deco;
        }

        int mySpread = this.getComponentType() - parent.getComponentType();
        int maxSpread = (this.level == 1) ? 2 : 3;

        // add exit if we're far enough away and don't have one
        if (mySpread == maxSpread && !isExitBuildForLevel(parent)) {
            int direction = rand.nextInt(4);
            for (int i = 0; i < 8 && !isExitBuildForLevel(parent); i++) {
                direction = (direction + i) % 4;
                if (this.addDungeonExit(parent, list, rand, direction)) {
                    this.setExitBuiltForLevel(parent, true);
                }
            }
        }

        // add other rooms
        if (mySpread < maxSpread) {
            int direction = rand.nextInt(4);
            for (int i = 0; i < 12; i++) {
                direction = (direction + i) % 4;
                this.addDungeonRoom(parent, list, rand, direction, this.level);
            }
        }
    }

    private boolean isExitBuildForLevel(StructureComponent parent) {
        if (parent instanceof ComponentTFFinalCastleDungeonEntrance) {
            return ((ComponentTFFinalCastleDungeonEntrance) parent).hasExit;
        } else {
            return false;
        }
    }

    private void setExitBuiltForLevel(StructureComponent parent, boolean exit) {
        if (parent instanceof ComponentTFFinalCastleDungeonEntrance) {
            ((ComponentTFFinalCastleDungeonEntrance) parent).hasExit = exit;
        }
    }

    protected boolean addDungeonRoom(StructureComponent parent, List<StructureComponent> list, Random rand,
            int rotation, int level) {
        rotation = (this.coordBaseMode + rotation) % 4;

        ChunkCoordinates rc = this.getNewRoomCoords(rand, rotation);

        ComponentTFFinalCastleDungeonRoom31 dRoom = new ComponentTFFinalCastleDungeonRoom31(
                rand,
                this.componentType + 1,
                rc.posX,
                rc.posY,
                rc.posZ,
                rotation,
                level);

        // use the existing bounding box
        StructureComponent intersect = StructureTFComponent
                .findIntersectingExcluding(list, dRoom.getBoundingBox(), this);
        if (intersect == null) {
            list.add(dRoom);
            dRoom.buildComponent(parent, list, rand);
            return true;
        } else {
            return false;
        }
    }

    protected boolean addDungeonExit(StructureComponent parent, List<StructureComponent> list, Random rand,
            int rotation) {

        // TODO: check if we are sufficiently near the castle center

        rotation = (this.coordBaseMode + rotation) % 4;
        ChunkCoordinates rc = this.getNewRoomCoords(rand, rotation);
        ComponentTFFinalCastleDungeonExit dRoom = new ComponentTFFinalCastleDungeonExit(
                rand,
                this.componentType + 1,
                rc.posX,
                rc.posY,
                rc.posZ,
                rotation,
                this.level);
        StructureComponent intersect = StructureTFComponent
                .findIntersectingExcluding(list, dRoom.getBoundingBox(), this);
        if (intersect == null) {
            list.add(dRoom);
            dRoom.buildComponent(this, list, rand);
            return true;
        } else {
            return false;
        }
    }

    private ChunkCoordinates getNewRoomCoords(Random rand, int rotation) {
        // make the rooms connect around the corners, not the centers
        int offset = rand.nextInt(15) - 9;
        if (rand.nextBoolean()) {
            offset += this.size;
        }

        return switch (rotation) {
            default -> new ChunkCoordinates(
                    this.boundingBox.maxX + 9,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + offset);
            case 1 -> new ChunkCoordinates(
                    this.boundingBox.minX + offset,
                    this.boundingBox.minY,
                    this.boundingBox.maxZ + 9);
            case 2 -> new ChunkCoordinates(
                    this.boundingBox.minX - 9,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + offset);
            case 3 -> new ChunkCoordinates(
                    this.boundingBox.minX + offset,
                    this.boundingBox.minY,
                    this.boundingBox.minZ - 9);
        };
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        if (isBoundingBoxOutOfPlateau(world)) {
            return false;
        }

        Random decoRNG = new Random(
                world.getSeed() + (this.boundingBox.minX * 321534781) ^ (this.boundingBox.minZ * 756839));

        this.fillWithAir(world, sbb, 0, 0, 0, this.size - 1, this.height - 1, this.size - 1);
        int forceFieldMeta = this.getForceFieldMeta(decoRNG);
        int runeMeta = getRuneMeta(forceFieldMeta);

        for (int rotation = 0; rotation < 4; rotation++) {
            int cs = 7;
            this.fillBlocksRotated(
                    world,
                    sbb,
                    cs,
                    0,
                    cs + 1,
                    cs,
                    this.height - 1,
                    this.size - 2 - cs,
                    TFBlocks.forceField,
                    forceFieldMeta,
                    rotation);
            // verticals
            for (int z = cs; z < ((this.size - 1) - cs); z += 4) {
                this.fillBlocksRotated(
                        world,
                        sbb,
                        cs,
                        0,
                        z,
                        cs,
                        this.height - 1,
                        z,
                        TFBlocks.castleMagic,
                        runeMeta,
                        rotation);
                // horizontals
                int y = ((z - cs) % 8 == 0) ? decoRNG.nextInt(3) + 0 : decoRNG.nextInt(3) + 4;
                this.fillBlocksRotated(
                        world,
                        sbb,
                        cs,
                        y,
                        z + 1,
                        cs,
                        y,
                        z + 3,
                        TFBlocks.castleMagic,
                        runeMeta,
                        rotation);
            }
        }
        return true;
    }

    private boolean isBoundingBoxOutOfPlateau(World world) {
        if (outsidePlateau != null) {
            return outsidePlateau;
        }
        int minX = this.boundingBox.minX - 1;
        int minZ = this.boundingBox.minZ - 1;
        int maxX = this.boundingBox.maxX + 1;
        int maxZ = this.boundingBox.maxZ + 1;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                var biome = world.getBiomeGenForCoords(x, z);
                if (biome != TFBiomeBase.highlandsCenter && biome != TFBiomeBase.thornlands) {
                    outsidePlateau = true;
                    return true;
                }
            }
        }
        outsidePlateau = false;
        return false;
    }

    protected int getRuneMeta(int forceFieldMeta) {
        return (forceFieldMeta == 4) ? 1 : 2;
    }

    protected int getForceFieldMeta(Random decoRNG) {
        return decoRNG.nextInt(2) + 3;
    }
}
