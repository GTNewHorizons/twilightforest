package twilightforest.structures;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFTreasure;

public abstract class StructureTFComponent extends StructureComponent {

    private static final StructureTFStrongholdStones strongholdStones = new StructureTFStrongholdStones();

    public StructureTFDecorator deco = null;
    public int spawnListIndex = 0;

    public StructureTFComponent() {}

    public StructureTFComponent(int i) {
        super(i);
    }

    /**
     * Save to NBT
     */
    @Override
    protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("si", this.spawnListIndex);
        par1NBTTagCompound.setString("deco", StructureTFDecorator.getDecoString(this.deco));
    }

    /**
     * Load from NBT
     */
    @Override
    protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        this.spawnListIndex = par1NBTTagCompound.getInteger("si");
        this.deco = StructureTFDecorator.getDecoFor(par1NBTTagCompound.getString("deco"));
    }

    public static StructureBoundingBox getComponentToAddBoundingBox(int x, int y, int z, int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ, int dir) {
        switch (dir) {
            case 1: // '\001'
                return new StructureBoundingBox(
                        x - maxZ + minZ,
                        y + minY,
                        z + minX,
                        x + minZ,
                        y + maxY + minY,
                        z + maxX + minX);
            case 2: // '\002'
                return new StructureBoundingBox(
                        x - maxX - minX,
                        y + minY,
                        z - maxZ - minZ,
                        x - minX,
                        y + maxY + minY,
                        z - minZ);
            case 3: // '\003'
                return new StructureBoundingBox(
                        x + minZ,
                        y + minY,
                        z - maxX,
                        x + maxZ + minZ,
                        y + maxY + minY,
                        z + minX);
            case 0: // '\0'
            default:
                return new StructureBoundingBox(
                        x + minX,
                        y + minY,
                        z + minZ,
                        x + maxX + minX,
                        y + maxY + minY,
                        z + maxZ + minZ);
        }
    }

    public static StructureBoundingBox getComponentToAddBoundingBox2(int x, int y, int z, int minX, int minY, int minZ,
            int maxX, int maxY, int maxZ, int dir) {
        switch (dir) {
            case 1: // '\001'
                return new StructureBoundingBox(
                        x - maxZ - minZ,
                        y + minY,
                        z + minX,
                        x - minZ,
                        y + maxY + minY,
                        z + maxX + minX);
            case 3: // '\003'
                return new StructureBoundingBox(
                        x + minZ,
                        y + minY,
                        z - maxX,
                        x + maxZ + minZ,
                        y + maxY + minY,
                        z - minX);
            case 0: // '\0'
            case 2: // '\002'
            default:
                return getComponentToAddBoundingBox(x, y, z, minX, minY, minZ, maxX, maxY, maxZ, dir);
        }
    }

    /**
     * Place a monster spawner at the specified coordinates
     *
     * @param monsterID
     */
    protected TileEntityMobSpawner placeSpawnerAtCurrentPosition(World world, Random rand, int x, int y, int z,
            String monsterID, StructureBoundingBox sbb) {
        return placeSpawnerAtWorldPosition(
                world,
                getXWithOffset(x, z),
                getYWithOffset(y),
                getZWithOffset(x, z),
                monsterID,
                sbb);
    }

    /**
     * Place a monster spawner at the specified coordinates, as if rotated
     *
     * @param monsterID
     */
    protected TileEntityMobSpawner placeSpawnerRotated(World world, int x, int y, int z, int rotation, String monsterID,
            StructureBoundingBox sbb) {
        return placeSpawnerAtWorldPosition(
                world,
                getXWithOffsetAsIfRotated(x, z, rotation),
                getYWithOffset(y),
                getZWithOffsetAsIfRotated(x, z, rotation),
                monsterID,
                sbb);
    }

    private TileEntityMobSpawner placeSpawnerAtWorldPosition(World world, int x, int y, int z, String monsterID,
            StructureBoundingBox sbb) {
        if (!sbb.isVecInside(x, y, z) || world.getBlock(x, y, z) == Blocks.mob_spawner) return null;

        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileEntityMobSpawner)) return null;

        TileEntityMobSpawner spawner = (TileEntityMobSpawner) tileEntity;
        spawner.func_145881_a().setEntityName(monsterID);
        return spawner;
    }

    /**
     * Place a treasure chest at the specified coordinates
     *
     * @param treasureType
     */
    protected void placeTreasureAtCurrentPosition(World world, Random rand, int x, int y, int z,
            TFTreasure treasureType, StructureBoundingBox sbb) {
        this.placeTreasureAtCurrentPosition(world, rand, x, y, z, treasureType, false, sbb);
    }

    /**
     * Place a treasure chest at the specified coordinates
     *
     * @param treasureType
     */
    protected void placeTreasureAtCurrentPosition(World world, Random rand, int x, int y, int z,
            TFTreasure treasureType, boolean trapped, StructureBoundingBox sbb) {
        placeTreasureAtWorldPosition(
                world,
                rand,
                getXWithOffset(x, z),
                getYWithOffset(y),
                getZWithOffset(x, z),
                treasureType,
                trapped,
                sbb);
    }

    /**
     * Place a treasure chest at the specified coordinates
     *
     * @param treasureType
     */
    protected void placeTreasureRotated(World world, int x, int y, int z, int rotation, TFTreasure treasureType,
            StructureBoundingBox sbb) {
        this.placeTreasureRotated(world, x, y, z, rotation, treasureType, false, sbb);
    }

    /**
     * Place a treasure chest at the specified coordinates
     *
     * @param treasureType
     */
    protected void placeTreasureRotated(World world, int x, int y, int z, int rotation, TFTreasure treasureType,
            boolean trapped, StructureBoundingBox sbb) {
        placeTreasureRotated(world, null, x, y, z, rotation, treasureType, trapped, sbb);
    }

    // allows new call sites to supply deterministic loot rng for rotated treasure chests
    protected void placeTreasureRotated(World world, Random random, int x, int y, int z, int rotation,
            TFTreasure treasureType, boolean trapped, StructureBoundingBox sbb) {
        placeTreasureAtWorldPosition(
                world,
                random,
                getXWithOffsetAsIfRotated(x, z, rotation),
                getYWithOffset(y),
                getZWithOffsetAsIfRotated(x, z, rotation),
                treasureType,
                trapped,
                sbb);
    }

    private void placeTreasureAtWorldPosition(World world, Random random, int x, int y, int z, TFTreasure treasureType,
            boolean trapped, StructureBoundingBox sbb) {
        if (!sbb.isVecInside(x, y, z)) return;

        Block block = world.getBlock(x, y, z);
        if (block != Blocks.chest && block != Blocks.trapped_chest) {
            treasureType.generate(world, random, x, y, z, trapped ? Blocks.trapped_chest : Blocks.chest);
        }
    }

    protected void placeSignAtCurrentPosition(World world, int x, int y, int z, String string0, String string1,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        if (!sbb.isVecInside(worldX, worldY, worldZ)
                || world.getBlock(worldX, worldY, worldZ) == Blocks.standing_sign) {
            return;
        }

        world.setBlock(worldX, worldY, worldZ, Blocks.standing_sign, coordBaseMode * 2, 2);
        TileEntity tileEntity = world.getTileEntity(worldX, worldY, worldZ);
        if (tileEntity instanceof TileEntitySign) {
            TileEntitySign sign = (TileEntitySign) tileEntity;
            sign.signText[1] = string0;
            sign.signText[2] = string1;
        }
    }

    //
    // public boolean makeTowerWing2(List list, Random rand, int index, int x, int y, int z, int size, int height, int
    // direction) {
    //
    // ComponentTFTowerWing wing = new ComponentTFTowerWing(index, x, y, z, size, height, direction);
    // // check to see if it intersects something already there
    // StructureComponent intersect = StructureComponent.getIntersectingStructureComponent(list, wing.boundingBox);
    // if (intersect == null || intersect == this) {
    // list.add(wing);
    // wing.buildComponent(this, list, rand);
    // return true;
    // } else {
    // System.out.println("Planned wing intersects with " + intersect);
    // return false;
    // }
    // }

    /**
     * Provides coordinates to make a tower such that it will open into the parent tower at the provided coordinates.
     */
    protected int[] offsetTowerCoords(int x, int y, int z, int towerSize, int direction) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y) - 1;
        int worldZ = getZWithOffset(x, z);
        int halfSize = towerSize / 2;

        switch (direction) {
            case 0:
                return new int[] { worldX + 1, worldY, worldZ - halfSize };
            case 1:
                return new int[] { worldX + halfSize, worldY, worldZ + 1 };
            case 2:
                return new int[] { worldX - 1, worldY, worldZ + halfSize };
            case 3:
                return new int[] { worldX - halfSize, worldY, worldZ - 1 };
            default:
                // ugh?
                return new int[] { x, y, z };
        }
    }

    /**
     * Provides coordinates to make a tower such that it will open into the parent tower at the provided coordinates.
     */
    protected ChunkCoordinates offsetTowerCCoords(int x, int y, int z, int towerSize, int direction) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y) - 1;
        int worldZ = getZWithOffset(x, z);
        int halfSize = towerSize / 2;

        switch (direction) {
            case 0:
                return new ChunkCoordinates(worldX + 1, worldY, worldZ - halfSize);
            case 1:
                return new ChunkCoordinates(worldX + halfSize, worldY, worldZ + 1);
            case 2:
                return new ChunkCoordinates(worldX - 1, worldY, worldZ + halfSize);
            case 3:
                return new ChunkCoordinates(worldX - halfSize, worldY, worldZ - 1);
            default:
                // ugh?
                return new ChunkCoordinates(x, y, z);
        }
    }

    public int[] getOffsetAsIfRotated(int[] src, int rotation) {
        // the vanilla offset methods dispatch virtually, so restore the mode even if override throws
        int originalMode = getCoordBaseMode();
        try {
            setCoordBaseMode(rotation);
            return new int[] { getXWithOffset(src[0], src[2]), getYWithOffset(src[1]), getZWithOffset(src[0], src[2]) };
        } finally {
            setCoordBaseMode(originalMode);
        }
    }

    protected int getXWithOffset(int x, int z) {
        switch (getCoordBaseMode()) {
            case 0: // '\0'
                return boundingBox.minX + x;
            case 1: // '\001'
                return boundingBox.maxX - z;
            case 2: // '\002'
                return boundingBox.maxX - x;
            case 3: // '\003'
                return boundingBox.minX + z;
            default:
                return x;
        }
    }

    protected int getYWithOffset(int par1) {
        return super.getYWithOffset(par1);
    }

    protected int getZWithOffset(int x, int z) {
        switch (getCoordBaseMode()) {
            case 0: // '\0'
                return boundingBox.minZ + z;
            case 1: // '\001'
                return boundingBox.minZ + x;
            case 2: // '\002'
                return boundingBox.maxZ - z;
            case 3: // '\003'
                return boundingBox.maxZ - x;
            default:
                return z;
        }
    }

    /**
     * Pretend as though the structure is rotated beyond what it already is
     */
    protected int getXWithOffsetAsIfRotated(int x, int z, int rotation) {
        if (coordBaseMode < 0) return x;

        switch ((coordBaseMode + rotation) % 4) {
            case 0:
                return boundingBox.minX + x;
            case 1:
                return boundingBox.maxX - z;
            case 2:
                return boundingBox.maxX - x;
            case 3:
                return boundingBox.minX + z;
            default:
                return x;
        }
    }

    protected int getZWithOffsetAsIfRotated(int x, int z, int rotation) {
        if (coordBaseMode < 0) return z;

        switch ((coordBaseMode + rotation) % 4) {
            case 0:
                return boundingBox.minZ + z;
            case 1:
                return boundingBox.minZ + x;
            case 2:
                return boundingBox.maxZ - z;
            case 3:
                return boundingBox.maxZ - x;
            default:
                return z;
        }
    }

    public int getCoordBaseMode() {
        return coordBaseMode;
    }

    public void setCoordBaseMode(int coordBaseMode) {
        this.coordBaseMode = coordBaseMode;
    }

    protected Block getBlockAtCurrentPosition(World par1World, int par2, int par3, int par4,
            StructureBoundingBox par5StructureBoundingBox) {
        return super.getBlockAtCurrentPosition(par1World, par2, par3, par4, par5StructureBoundingBox);
    }

    protected int getMetadataAtCurrentPosition(World world, int x, int y, int z,
            StructureBoundingBox structureBoundingBox) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        return structureBoundingBox.isVecInside(worldX, worldY, worldZ) ? world.getBlockMetadata(worldX, worldY, worldZ)
                : 0;
    }

    /**
     * current Position depends on currently set Coordinates mode, is computed here
     */
    protected void setTileEntityAtCurrentPosition(World world, TileEntity te, int x, int y, int z,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffset(x, z);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffset(x, z);
        if (sbb.isVecInside(worldX, worldY, worldZ)) world.setTileEntity(worldX, worldY, worldZ, te);
    }

    /**
     * current Position depends on currently set Coordinates mode, is computed here
     */
    protected void placeBlockAtCurrentPosition(World par1World, Block par2, int par3, int par4, int par5, int par6,
            StructureBoundingBox par7StructureBoundingBox) {
        super.placeBlockAtCurrentPosition(par1World, par2, par3, par4, par5, par6, par7StructureBoundingBox);
    }

    protected void partiallyFillWithMetadataBlocks(World world, StructureBoundingBox structureBoundingBox, int minX,
            int minY, int minZ, int maxX, int maxY, int maxZ, Block placeBlock, int placeBlockMeta, Random random,
            float placeRate) {
        // preserve the original y x z iteration order and rng calls for seeded structures
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (random.nextFloat() <= placeRate) {
                        placeBlockAtCurrentPosition(world, placeBlock, placeBlockMeta, x, y, z, structureBoundingBox);
                    }
                }
            }
        }
    }

    /**
     * Place a block as though the entire structure were rotated the specified amount beyond what it already is
     */
    protected void placeBlockRotated(World world, Block blockID, int meta, int x, int y, int z, int rotation,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffsetAsIfRotated(x, z, rotation);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffsetAsIfRotated(x, z, rotation);
        if (sbb.isVecInside(worldX, worldY, worldZ)) {
            world.setBlock(worldX, worldY, worldZ, blockID, meta, 2);
        }
    }

    /**
     * Get a block ID as though the entire structure were rotated the specified amount beyond what it already is
     */
    protected Block getBlockIDRotated(World world, int x, int y, int z, int rotation, StructureBoundingBox sbb) {
        int worldX = getXWithOffsetAsIfRotated(x, z, rotation);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffsetAsIfRotated(x, z, rotation);
        return sbb.isVecInside(worldX, worldY, worldZ) ? world.getBlock(worldX, worldY, worldZ) : Blocks.air;
    }

    /**
     * Fill with blocks as though rotated
     */
    protected void fillBlocksRotated(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX,
            int maxY, int maxZ, Block blockID, int meta, int rotation) {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    placeBlockRotated(world, blockID, meta, x, y, z, rotation, sbb);
                }
            }
        }
    }

    /**
     * Fill with blocks as though rotated
     */
    protected void randomlyFillBlocksRotated(World world, StructureBoundingBox sbb, Random rand, float chance, int minX,
            int minY, int minZ, int maxX, int maxY, int maxZ, Block blockID, int meta, Block blockID2, int meta2,
            int rotation) {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (rand.nextFloat() < chance) {
                        placeBlockRotated(world, blockID, meta, x, y, z, rotation, sbb);
                    } else {
                        placeBlockRotated(world, blockID2, meta2, x, y, z, rotation, sbb);
                    }
                }
            }
        }
    }

    public void fillToGroundRotated(World world, Block stonebrick, int meta, int x, int y, int z, int rotation,
            StructureBoundingBox sbb) {
        int worldX = getXWithOffsetAsIfRotated(x, z, rotation);
        int worldY = getYWithOffset(y);
        int worldZ = getZWithOffsetAsIfRotated(x, z, rotation);
        if (!sbb.isVecInside(worldX, worldY, worldZ)) return;

        while (worldY > 1 && (world.isAirBlock(worldX, worldY, worldZ)
                || world.getBlock(worldX, worldY, worldZ).getMaterial().isLiquid())) {
            world.setBlock(worldX, worldY--, worldZ, stonebrick, meta, 2);
        }
    }

    /**
     * Fill with blocks as though rotated
     */
    protected void fillAirRotated(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX,
            int maxY, int maxZ, int rotation) {
        this.fillBlocksRotated(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, Blocks.air, 0, rotation);
    }

    // shared by the hedge and underground mazes, preserve the origin exclusion and
    // ignore unfilled (0,0) room slots during placement
    protected static boolean isNearMazeRoom(int x, int z, int[] rooms, int spacing) {
        // if proposed coordinates are covering the origin, return true to stop the room from causing the
        // maze to fail
        if (x == 1 && z == 1) return true;
        for (int i = 0; i < rooms.length; i += 2) {
            int roomX = rooms[i];
            int roomZ = rooms[i + 1];
            if (roomX == 0 && roomZ == 0) continue;
            if (Math.abs(x - roomX) < spacing && Math.abs(z - roomZ) < spacing) return true;
        }
        return false;
    }

    public static StructureComponent.BlockSelector getStrongholdStones() {
        return strongholdStones;
    }

    /**
     * Gets the metadata necessary to make stairs facing the proper direction.
     *
     * @param dir
     * @return
     */
    protected int getStairMeta(int dir) {
        switch ((getCoordBaseMode() + dir) % 4) {
            case 0:
                return 0;
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 3;
            default:
                return -1; // this is impossible
        }
    }

    /**
     * Gets the metadata necessary to stick the ladder on the specified wall.
     *
     * @param ladderDir
     * @return
     */
    protected int getLadderMeta(int ladderDir) {
        // ladder data values are... dumb.
        switch ((getCoordBaseMode() + ladderDir) % 4) {
            case 0:
                return 4;
            case 1:
                return 2;
            case 2:
                return 5;
            case 3:
                return 3;
            default:
                return -1; // this is impossible
        }
    }

    /**
     * Gets the metadata necessary to stick the ladder on the specified wall.
     */
    protected int getLadderMeta(int ladderDir, int rotation) {
        return getLadderMeta(ladderDir + rotation);
    }

    /**
     * Nullify all the sky light in this component bounding box
     */
    public void nullifySkyLightForBoundingBox(World world) {
        nullifySkyLight(
                world,
                boundingBox.minX - 1,
                boundingBox.minY - 1,
                boundingBox.minZ - 1,
                boundingBox.maxX + 1,
                boundingBox.maxY + 1,
                boundingBox.maxZ + 1);
    }

    /**
     * Nullify all the sky light at the specified positions, using local coordinates
     */
    public void nullifySkyLightAtCurrentPosition(World world, int sx, int sy, int sz, int dx, int dy, int dz) {
        // resolve all variables to their actual in-world positions
        int x1 = getXWithOffset(sx, sz);
        int y1 = getYWithOffset(sy);
        int z1 = getZWithOffset(sx, sz);
        int x2 = getXWithOffset(dx, dz);
        int y2 = getYWithOffset(dy);
        int z2 = getZWithOffset(dx, dz);
        // rotations can reverse the transformed corner order
        nullifySkyLight(
                world,
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2),
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2));
    }

    /**
     * Nullify all the sky light at the specified positions, using world coordinates
     */
    public void nullifySkyLight(World world, int sx, int sy, int sz, int dx, int dy, int dz) {
        for (int x = sx; x <= dx; x++) {
            for (int z = sz; z <= dz; z++) {
                for (int y = sy; y <= dy; y++) {
                    world.setLightValue(EnumSkyBlock.Sky, x, y, z, 0);
                }
            }
        }
    }

    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox. (A median of all the
     * levels in the BB's horizontal rectangle).
     *
     * This is basically copied from ComponentVillage
     */
    protected int getAverageGroundLevel(World world, StructureBoundingBox sbb) {
        int totalHeight = 0;
        int heightCount = 0;
        for (int z = boundingBox.minZ; z <= boundingBox.maxZ; z++) {
            for (int x = boundingBox.minX; x <= boundingBox.maxX; x++) {
                if (sbb.isVecInside(x, 64, z)) {
                    totalHeight += Math
                            .max(world.getTopSolidOrLiquidBlock(x, z), world.provider.getAverageGroundLevel());
                    heightCount++;
                }
            }
        }
        return heightCount == 0 ? -1 : totalHeight / heightCount;
    }

    /**
     * Find what y level the dirt/grass/stone is. Just check the center of the chunk we're given
     */
    protected int getSampledDirtLevel(World world, StructureBoundingBox sbb) {
        int centerX = sbb.getCenterX();
        int centerZ = sbb.getCenterZ();
        for (int y = 90; y > 0; y--) { // is 90 like a good place to start? :)
            Material material = world.getBlock(centerX, y, centerZ).getMaterial();
            if (material == Material.ground || material == Material.rock || material == Material.grass) return y;
        }
        return 256;
    }

    /**
     * Temporary replacement
     */
    protected void randomlyPlaceBlock(World world, StructureBoundingBox sbb, Random rand, float chance, int x, int y,
            int z, Block blockPlaced, int meta) {
        this.func_151552_a(world, sbb, rand, chance, x, y, z, blockPlaced, meta);
    }

    /**
     * Does this component fall under block protection when progression is turned on, normally true
     */
    public boolean isComponentProtected() {
        return true;
    }

    /**
     * Discover if bounding box can fit within the current bounding box object.
     */
    public static StructureComponent findIntersectingExcluding(List list, StructureBoundingBox toCheck,
            StructureComponent exclude) {
        for (Object candidate : list) {
            StructureComponent component = (StructureComponent) candidate;
            if (component != exclude && component.getBoundingBox() != null
                    && component.getBoundingBox().intersectsWith(toCheck))
                return component;
        }
        return null;
    }

}
