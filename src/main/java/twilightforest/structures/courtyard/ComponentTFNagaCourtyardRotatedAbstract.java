package twilightforest.structures.courtyard;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import twilightforest.block.BlockTFNagastone2.Yaw;
import twilightforest.block.TFBlocks;
import twilightforest.structures.StructureTFComponent;

public class ComponentTFNagaCourtyardRotatedAbstract extends StructureTFComponent {

    private static final String GROUND_HEIGHT_CHECKED_NBT_KEY = "CYGroundChecked";

    private boolean groundHeightChecked;

    protected int horizontalColumnsOrient4;
    protected int horizontalColumnsOrient8;

    protected int rotatedStairs0;
    protected int rotatedStairs1;
    protected int rotatedStairs2;
    protected int rotatedStairs3;
    protected int rotatedStairs4;
    protected int rotatedStairs5;
    protected int rotatedStairs6;
    protected int rotatedStairs7;

    protected ForgeDirection EtchedNagastoneNorth = ForgeDirection.NORTH;
    protected ForgeDirection EtchedNagastoneSouth = ForgeDirection.SOUTH;
    protected ForgeDirection EtchedNagastoneWest = ForgeDirection.WEST;
    protected ForgeDirection EtchedNagastoneEast = ForgeDirection.EAST;

    protected Yaw NagastoneNorth = Yaw.NORTH;
    protected Yaw NagastoneSouth = Yaw.SOUTH;
    protected Yaw NagastoneWest = Yaw.WEST;
    protected Yaw NagastoneEast = Yaw.EAST;

    public ComponentTFNagaCourtyardRotatedAbstract() {
        super();
    }

    public ComponentTFNagaCourtyardRotatedAbstract(int i, int x, int y, int z, int rotation) {
        super(i);
        this.coordBaseMode = Math.floorMod(rotation, 4);
        initRotation();
    }

    /**
     * Initialize all of our fancy rotation object thingys based on coordBaseMode.
     */
    private void initRotation() {
        switch (coordBaseMode) {
            default:
            case 0:
                horizontalColumnsOrient4 = 8;
                horizontalColumnsOrient8 = 4;
                setStairs(3, 2, 0, 1, 7, 6, 4, 5);
                setEtchedDirections(
                        ForgeDirection.WEST,
                        ForgeDirection.EAST,
                        ForgeDirection.SOUTH,
                        ForgeDirection.NORTH);
                setNagastoneYaws(Yaw.WEST, Yaw.EAST, Yaw.SOUTH, Yaw.NORTH);
                break;
            case 1:
                horizontalColumnsOrient4 = 4;
                horizontalColumnsOrient8 = 8;
                setStairs(0, 1, 2, 3, 4, 5, 6, 7);
                setEtchedDirections(
                        ForgeDirection.SOUTH,
                        ForgeDirection.NORTH,
                        ForgeDirection.EAST,
                        ForgeDirection.WEST);
                setNagastoneYaws(Yaw.NORTH, Yaw.SOUTH, Yaw.WEST, Yaw.EAST);
                break;
            case 2:
                horizontalColumnsOrient4 = 8;
                horizontalColumnsOrient8 = 4;
                setStairs(2, 3, 1, 0, 6, 7, 5, 4);
                setEtchedDirections(
                        ForgeDirection.EAST,
                        ForgeDirection.WEST,
                        ForgeDirection.NORTH,
                        ForgeDirection.SOUTH);
                setNagastoneYaws(Yaw.EAST, Yaw.WEST, Yaw.NORTH, Yaw.SOUTH);
                break;
            case 3:
                horizontalColumnsOrient4 = 4;
                horizontalColumnsOrient8 = 8;
                setStairs(1, 0, 3, 2, 5, 4, 7, 6);
                setEtchedDirections(
                        ForgeDirection.NORTH,
                        ForgeDirection.SOUTH,
                        ForgeDirection.WEST,
                        ForgeDirection.EAST);
                setNagastoneYaws(Yaw.SOUTH, Yaw.NORTH, Yaw.EAST, Yaw.WEST);
                break;
        }
    }

    private void setStairs(int stair0, int stair1, int stair2, int stair3, int stair4, int stair5, int stair6,
            int stair7) {
        rotatedStairs0 = stair0;
        rotatedStairs1 = stair1;
        rotatedStairs2 = stair2;
        rotatedStairs3 = stair3;
        rotatedStairs4 = stair4;
        rotatedStairs5 = stair5;
        rotatedStairs6 = stair6;
        rotatedStairs7 = stair7;
    }

    private void setEtchedDirections(ForgeDirection north, ForgeDirection south, ForgeDirection west,
            ForgeDirection east) {
        EtchedNagastoneNorth = north;
        EtchedNagastoneSouth = south;
        EtchedNagastoneWest = west;
        EtchedNagastoneEast = east;
    }

    private void setNagastoneYaws(Yaw north, Yaw south, Yaw west, Yaw east) {
        NagastoneNorth = north;
        NagastoneSouth = south;
        NagastoneWest = west;
        NagastoneEast = east;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        // TODO Auto-generated method stub
        return false;
    }

    // a component is populated once per intersecting chunk, dont shift its origin on every call
    // preserve the rotation anchor while sizing the world bounding box to the local footprint
    protected final void fitRotatedFootprint(int localWidth, int localDepth) {
        if (coordBaseMode == 1) {
            boundingBox.minX = boundingBox.maxX - localDepth + 1;
            boundingBox.maxZ = boundingBox.minZ + localWidth - 1;
        } else if (coordBaseMode == 3) {
            boundingBox.maxX = boundingBox.minX + localDepth - 1;
            boundingBox.minZ = boundingBox.maxZ - localWidth + 1;
        }
    }

    protected void normalizeRotatedFootprint() {}

    protected final void adjustGroundHeightOnce(World world, int width, int depth, int sampleZ) {
        if (groundHeightChecked) {
            return;
        }

        int groundY = getYWithOffset(-1);
        int undergroundY = getYWithOffset(-2);
        int groundAir = 0;
        int undergroundAir = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                int localZ = z + sampleZ;
                int worldX = getXWithOffset(x, localZ);
                int worldZ = getZWithOffset(x, localZ);
                if (!world.blockExists(worldX, groundY, worldZ) || !world.blockExists(worldX, undergroundY, worldZ)) {
                    groundHeightChecked = true;
                    return;
                }
                if (world.getBlock(worldX, groundY, worldZ) == Blocks.air) {
                    groundAir++;
                }
                if (world.getBlock(worldX, undergroundY, worldZ) == Blocks.air) {
                    undergroundAir++;
                }
            }
        }

        if (groundAir > undergroundAir) {
            boundingBox.minY--;
        }
        groundHeightChecked = true;
    }

    protected final void replaceGroundAround(World world, StructureBoundingBox chunkBox, int width, int depth) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                Block west = getBlockAtCurrentPosition(world, x - 1, 0, z, chunkBox);
                Block east = getBlockAtCurrentPosition(world, x + 1, 0, z, chunkBox);
                Block north = getBlockAtCurrentPosition(world, x, 0, z - 1, chunkBox);
                Block south = getBlockAtCurrentPosition(world, x, 0, z + 1, chunkBox);
                if (isGroundBlock(west) || isGroundBlock(east) || isGroundBlock(north) || isGroundBlock(south)) {
                    placeBlockAtCurrentPosition(world, TFBlocks.nagastoneEtched, 0, x, 0, z, chunkBox);
                }
            }
        }
    }

    private static boolean isGroundBlock(Block block) {
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.stonebrick;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setBoolean(GROUND_HEIGHT_CHECKED_NBT_KEY, groundHeightChecked);
    }

    // load from nbt
    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        // legacy pieces may already have had their bounding boxes lowered before being saved
        groundHeightChecked = !nbt.hasKey(GROUND_HEIGHT_CHECKED_NBT_KEY)
                || nbt.getBoolean(GROUND_HEIGHT_CHECKED_NBT_KEY);
        initRotation();
        normalizeRotatedFootprint();
    }
}
