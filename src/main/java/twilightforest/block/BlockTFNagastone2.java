package twilightforest.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFNagastone;

public class BlockTFNagastone2 extends Block {

    // 0-3 = Down
    // 4-7 = Up
    // 8-11 = Side
    public enum Direction {
        DOWN,
        UP,
        SIDE
    };

    // 0,4,8 = East
    // 1,5,9 = West
    // 2,6,10 = North
    // 3,7,11 = South
    public enum Yaw {
        EAST,
        WEST,
        NORTH,
        SOUTH
    };

    public static IIcon FACE_LEFT;
    public static IIcon FACE_RIGHT;
    public static IIcon FACE_FRONT;
    public static IIcon CROSS_SECTION;
    public static IIcon TOP_TIP;
    public static IIcon TOP_LONG;
    public static IIcon TOP_CROSS;
    public static IIcon TOP_ANGLE_UP;
    public static IIcon TOP_ANGLE_DOWN;
    public static IIcon TOP_TRIPLE_UP;
    public static IIcon TOP_TRIPLE_DOWN;
    public static IIcon BOTTOM_TIP;
    public static IIcon BOTTOM_LONG;
    public static IIcon BOTTOM_ANGLE_UP;
    public static IIcon BOTTOM_ANGLE_DOWN;
    public static IIcon BOTTOM_TRIPLE_UP;
    public static IIcon BOTTOM_TRIPLE_DOWN;
    public static IIcon BOTTOM_CROSS;
    public static IIcon LEFT_DOWN_CLEAR;
    public static IIcon LEFT_DOWN_ORNATED;
    public static IIcon LEFT_UP_CLEAR;
    public static IIcon LEFT_UP_ORNATED;
    public static IIcon RIGHT_DOWN_CLEAR;
    public static IIcon RIGHT_DOWN_ORNATED;
    public static IIcon RIGHT_UP_CLEAR;
    public static IIcon RIGHT_UP_ORNATED;
    public static IIcon TIP_LEFT;
    public static IIcon TIP_RIGHT;
    public static IIcon RIGHT_SIDE;
    public static IIcon LEFT_SIDE;
    public static IIcon TRIPLE_RIGHT_UP_LB;
    public static IIcon TRIPLE_RIGHT_DOWN_LB;
    public static IIcon TRIPLE_RIGHT_UP_RB;
    public static IIcon TRIPLE_RIGHT_DOWN_RB;
    public static IIcon TRIPLE_LEFT_UP_LB;
    public static IIcon TRIPLE_LEFT_DOWN_LB;
    public static IIcon TRIPLE_LEFT_UP_RB;
    public static IIcon TRIPLE_LEFT_DOWN_RB;
    public static IIcon RIGHT_CROSS;
    public static IIcon LEFT_CROSS;

    public boolean isHead;

    public BlockTFNagastone2() {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setCreativeTab(TFItems.creativeTab);
        this.isHead = false;
    }

    public BlockTFNagastone2(boolean isHead) {
        this();
        this.isHead = isHead;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getNewNagastoneBlockRenderID();
    }

    @Override
    public boolean hasTileEntity(final int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTFNagastone();
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLivingBase par5EntityLiving,
            ItemStack par6ItemStack) {
        Vec3 start = Vec3.createVectorHelper(
                par5EntityLiving.posX,
                par5EntityLiving.posY + par5EntityLiving.getEyeHeight(),
                par5EntityLiving.posZ);
        Vec3 look = par5EntityLiving.getLookVec();
        Vec3 end = start.addVector(look.xCoord * 200, look.yCoord * 200, look.zCoord * 200);
        MovingObjectPosition mop = par1World.func_147447_a(start, end, false, true, false);
        Vec3 vec3 = mop.hitVec;
        double posX = vec3.xCoord;
        double posY = vec3.yCoord;
        double posZ = vec3.zCoord;
        vec3 = par5EntityLiving.getLookVec().normalize();
        double moveX = vec3.xCoord / 100;
        double moveY = vec3.yCoord / 100;
        double moveZ = vec3.zCoord / 100;
        int borderX = 0;
        int borderY = 0;
        int borderZ = 0;
        if (posX == Math.round(posX)) {
            if (moveX > 0) borderX = (int) posX + 1;
            else borderX = (int) posX;
            borderY = posY >= 0 ? (int) posY + 1 : (int) posY;
            borderZ = posZ >= 0 ? (int) posZ + 1 : (int) posZ;
        } else if (posY == Math.round(posY)) {
            if (moveY > 0) borderY = (int) posY + 1;
            else borderY = (int) posY;
            borderX = posX >= 0 ? (int) posX + 1 : (int) posX;
            borderZ = posZ >= 0 ? (int) posZ + 1 : (int) posZ;
        } else if (posZ == Math.round(posZ)) {
            if (moveZ > 0) borderZ = (int) posZ + 1;
            else borderZ = (int) posZ;
            borderX = posX >= 0 ? (int) posX + 1 : (int) posX;
            borderY = posY >= 0 ? (int) posY + 1 : (int) posY;
        }
        do {
            posX += moveX;
            posY += moveY;
            posZ += moveZ;
        } while (posX >= (borderX - 1) && posX <= borderX
                && posY >= (borderY - 1)
                && posY <= borderY
                && posZ >= (borderZ - 1)
                && posZ <= borderZ);
        int side = 0;
        if (posY > borderY) side = 0;
        else if (posY < (borderY - 1)) side = 1;
        else if (posX < (borderX - 1)) side = 2;
        else if (posX > borderX) side = 3;
        else if (posZ > borderZ) side = 4;
        else if (posZ < (borderZ - 1)) side = 5;
        par1World.setBlockMetadataWithNotify(
                x,
                y,
                z,
                GetMetadata(
                        side,
                        x,
                        z,
                        (float) par5EntityLiving.posX,
                        (float) par5EntityLiving.posZ,
                        par5EntityLiving.isSneaking()),
                2);
    }

    /**
     * Old metadata texture info. Left here just in case
     *
     * 0 - 3 head directions 4 - 7 bend down 8 - 11 bend up 12 straight n/s 13 straight e/w 14 straight u/d 15 weird
     * middle piece
     */

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborID) {
        if (!this.isHead) {
            Check4Neighbours(world, x, y, z);
        }
    }

    public void Check4Neighbours(World world, int x, int y, int z) {
        TileEntityTFNagastone te = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
        te.NoNeighbours();
        te.neighbourDown = CanConnectTo(world, x, y, z, 0);
        te.neighbourUp = CanConnectTo(world, x, y, z, 1);
        te.neighbourEast = CanConnectTo(world, x, y, z, 2);
        te.neighbourWest = CanConnectTo(world, x, y, z, 3);
        te.neighbourNorth = CanConnectTo(world, x, y, z, 4);
        te.neighbourSouth = CanConnectTo(world, x, y, z, 5);
        te.UpdateNeighbourCount();
    }

    public boolean CanConnectTo(World world, int x, int y, int z, int direction) {
        TileEntityTFNagastone currentTE = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
        int currentMeta = world.getBlockMetadata(x, y, z);

        Direction currentDirection = DirectionFromMeta(currentMeta);
        Yaw currentFacing = FacingFromMeta(currentMeta);

        if (world.getBlock(x, y, z) == TFBlocks.nagastoneBody && isNagaStoneInDirection(world, x, y, z, direction)) {
            int dx = 0;
            int dy = 0;
            int dz = 0;
            switch (direction) {
                case 0:
                    dy = -1;
                    break;
                case 1:
                    dy = 1;
                    break;
                case 2:
                    dx = 1;
                    break;
                case 3:
                    dx = -1;
                    break;
                case 4:
                    dz = -1;
                    break;
                case 5:
                    dz = 1;
                    break;
                default:
                    return false;
            }
            TileEntityTFNagastone neighbourTE = (TileEntityTFNagastone) world.getTileEntity(x + dx, y + dy, z + dz);
            int neighbourMeta = world.getBlockMetadata(x + dx, y + dy, z + dz);

            Direction neighbourDirection = DirectionFromMeta(neighbourMeta);
            Yaw neighbourFacing = FacingFromMeta(neighbourMeta);

            if (world.getBlock(x + dx, y + dy, z + dz) == TFBlocks.nagastoneHead) {
                switch (direction) {
                    case 0:
                        if (neighbourDirection != Direction.DOWN) return false;
                        else {
                            if (currentDirection == Direction.SIDE) switch (currentFacing) {
                                case EAST:
                                case WEST:
                                    return neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST;
                                case NORTH:
                                case SOUTH:
                                    return neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH;
                            }
                            else {
                                return neighbourFacing == currentFacing;
                            }
                        }
                        break;
                    case 1:
                        if (neighbourDirection != Direction.UP) return false;
                        else {
                            if (currentDirection == Direction.SIDE) switch (currentFacing) {
                                case EAST:
                                case WEST:
                                    return neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST;
                                case NORTH:
                                case SOUTH:
                                    return neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH;
                            }
                            else {
                                return neighbourFacing == currentFacing;
                            }
                        }
                        break;
                    case 2:
                        return neighbourDirection == Direction.SIDE && neighbourFacing == Yaw.EAST;
                    case 3:
                        return neighbourDirection == Direction.SIDE && neighbourFacing == Yaw.WEST;
                    case 4:
                        return neighbourDirection == Direction.SIDE && neighbourFacing == Yaw.NORTH;
                    case 5:
                        return neighbourDirection == Direction.SIDE && neighbourFacing == Yaw.SOUTH;
                }
            } else {
                switch (direction) {
                    case 0:
                    case 1:
                        if ((currentDirection == Direction.SIDE && neighbourDirection == Direction.SIDE)
                                || (currentDirection != Direction.SIDE && neighbourDirection != Direction.SIDE
                                        && neighbourFacing != currentFacing)) {
                            return false;
                        } else switch (currentFacing) {
                            case EAST:
                            case WEST:
                                return neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST;
                            case NORTH:
                            case SOUTH:
                                return neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH;
                        }
                        break;
                    case 2:
                    case 3:
                        if (currentDirection == Direction.SIDE) {
                            switch (currentFacing) {
                                case EAST:
                                case WEST:
                                    if (neighbourDirection == Direction.SIDE) return true;
                                    else return (neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST);
                                case NORTH:
                                case SOUTH:
                                    if (neighbourDirection != Direction.SIDE) return false;
                                    else return (neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST);
                            }
                        } else {
                            if (neighbourDirection != Direction.SIDE) return false;
                            else {
                                switch (currentFacing) {
                                    case EAST:
                                    case WEST:
                                        return neighbourFacing == Yaw.EAST || neighbourFacing == Yaw.WEST;
                                    case NORTH:
                                    case SOUTH:
                                        return false;
                                }
                            }
                        }
                        break;
                    case 4:
                    case 5:
                        if (currentDirection == Direction.SIDE) {
                            switch (currentFacing) {
                                case EAST:
                                case WEST:
                                    if (neighbourDirection != Direction.SIDE) return false;
                                    else return (neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH);
                                case NORTH:
                                case SOUTH:
                                    if (neighbourDirection == Direction.SIDE) return true;
                                    else return (neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH);
                            }
                        } else {
                            if (neighbourDirection != Direction.SIDE) return false;
                            else {
                                switch (currentFacing) {
                                    case EAST:
                                    case WEST:
                                        return false;
                                    case NORTH:
                                    case SOUTH:
                                        return neighbourFacing == Yaw.NORTH || neighbourFacing == Yaw.SOUTH;
                                }
                            }
                        }
                        break;
                }
            }
        } else {
            switch (direction) {
                case 0:
                    if (currentDirection != Direction.SIDE && !world.isAirBlock(x, y + 1, z)) {
                        if (!isNagaStoneInDirection(world, x, y, z, 1)) {
                            return true;
                        }
                    }
                    break;
                case 1:
                    if (currentDirection != Direction.SIDE && !world.isAirBlock(x, y - 1, z)) {
                        if (!isNagaStoneInDirection(world, x, y, z, 0)) {
                            return true;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return false;
    }

    /**
     * Is there nagastone in the following direction? 0 = d, 1 = u, 2 = e, 3 = w, 4 = n, 5 = s;
     */
    public boolean isNagaStoneInDirection(World par1World, int x, int y, int z, int direction) {
        switch (direction) {
            case 0:
                return par1World.getBlock(x, y - 1, z) instanceof BlockTFNagastone2;
            case 1:
                return par1World.getBlock(x, y + 1, z) instanceof BlockTFNagastone2;
            case 2:
                return par1World.getBlock(x + 1, y, z) instanceof BlockTFNagastone2;
            case 3:
                return par1World.getBlock(x - 1, y, z) instanceof BlockTFNagastone2;
            case 4:
                return par1World.getBlock(x, y, z - 1) instanceof BlockTFNagastone2;
            case 5:
                return par1World.getBlock(x, y, z + 1) instanceof BlockTFNagastone2;
        }
        // welp,
        return false;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     * 
     * 0 - 3 head directions 4 - 7 bend down 8 - 11 bend up 12 straight n/s 13 straight e/w 14 straight u/d 15 weird
     * middle piece
     * 
     * TODO: Surely there is a way to do this that's not just specifying all 6 * 16 values.
     */
    @Override
    public IIcon getIcon(int side, int meta) {

        if (this.isHead) {
            // Head
            switch (side) {
                case 0:
                    return BOTTOM_TIP;
                case 1:
                    return TOP_TIP;
                case 2:
                    return FACE_FRONT;
                case 3:
                    return CROSS_SECTION;
                case 4:
                    return FACE_LEFT;
                case 5:
                    return FACE_RIGHT;
            }
        } else {
            // Tail
            switch (side) {
                case 0:
                    return BOTTOM_LONG;
                case 1:
                    return TOP_LONG;
                case 2:
                    return CROSS_SECTION;
                case 3:
                    return CROSS_SECTION;
                case 4:
                    return LEFT_SIDE;
                case 5:
                    return RIGHT_SIDE;
            }
        }
        return null;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockTFNagastone2.FACE_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_left");
        BlockTFNagastone2.FACE_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_right");
        BlockTFNagastone2.FACE_FRONT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_front");
        BlockTFNagastone2.CROSS_SECTION = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_cross_section");
        BlockTFNagastone2.TOP_TIP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_tip");
        BlockTFNagastone2.TOP_LONG = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_long");
        BlockTFNagastone2.TOP_CROSS = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_turn");
        BlockTFNagastone2.TOP_ANGLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_angle_up");
        BlockTFNagastone2.TOP_ANGLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_angle_down");
        BlockTFNagastone2.TOP_TRIPLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_triple_up");
        BlockTFNagastone2.TOP_TRIPLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_triple_down");
        BlockTFNagastone2.BOTTOM_TIP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_tip");
        BlockTFNagastone2.BOTTOM_LONG = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_long");
        BlockTFNagastone2.LEFT_DOWN_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_down_clear");
        BlockTFNagastone2.LEFT_DOWN_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_down_ornated");
        BlockTFNagastone2.LEFT_UP_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_up_clear");
        BlockTFNagastone2.LEFT_UP_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_up_ornated");
        BlockTFNagastone2.RIGHT_DOWN_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_down_clear");
        BlockTFNagastone2.RIGHT_DOWN_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_down_ornated");
        BlockTFNagastone2.RIGHT_UP_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_up_clear");
        BlockTFNagastone2.RIGHT_UP_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_up_ornated");
        BlockTFNagastone2.TIP_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_tip_left");
        BlockTFNagastone2.TIP_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_tip_right");
        BlockTFNagastone2.RIGHT_SIDE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_side_right");
        BlockTFNagastone2.LEFT_SIDE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_side_left");
        BlockTFNagastone2.TRIPLE_RIGHT_UP_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_right_up_lb");
        BlockTFNagastone2.TRIPLE_RIGHT_DOWN_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_right_down_lb");
        BlockTFNagastone2.TRIPLE_RIGHT_UP_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_right_up_rb");
        BlockTFNagastone2.TRIPLE_RIGHT_DOWN_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_right_down_rb");
        BlockTFNagastone2.TRIPLE_LEFT_UP_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_left_up_lb");
        BlockTFNagastone2.TRIPLE_LEFT_DOWN_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_left_down_lb");
        BlockTFNagastone2.TRIPLE_LEFT_UP_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_left_up_rb");
        BlockTFNagastone2.TRIPLE_LEFT_DOWN_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_triple_left_down_rb");
        BlockTFNagastone2.RIGHT_CROSS = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_cross_right");
        BlockTFNagastone2.LEFT_CROSS = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_cross_left");
        BlockTFNagastone2.BOTTOM_ANGLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_angle_up");
        BlockTFNagastone2.BOTTOM_ANGLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_angle_down");
        BlockTFNagastone2.BOTTOM_TRIPLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_triple_up");
        BlockTFNagastone2.BOTTOM_TRIPLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_triple_down");
        BlockTFNagastone2.BOTTOM_CROSS = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_cross");
    }

    public int GetMetadata(int side, float hitX, float hitZ) {
        Direction direction = GetDirection(side);
        Yaw facing;
        if (direction == Direction.SIDE) {
            facing = GetFacing(side - 2);
        } else {
            double vecX = hitX - 0.5f;
            double vecZ = hitZ - 0.5f;
            double angle = Math.atan2(vecX, vecZ);
            if ((angle >= Math.PI / 4) && (angle < Math.PI * 3 / 4)) {
                facing = Yaw.EAST;
            } else if ((angle >= Math.PI * 3 / 4) || (angle < -Math.PI * 3 / 4)) {
                facing = Yaw.NORTH;
            } else if ((angle >= -Math.PI * 3 / 4) && (angle < -Math.PI / 4)) {
                facing = Yaw.WEST;
            } else facing = Yaw.SOUTH;
        }
        return GetMetadata(direction, facing);
    }

    public int GetMetadata(int side, int x, int z, float hitX, float hitZ, boolean invertFacing) {
        Direction direction = GetDirection(side);
        Yaw facing;
        if (direction == Direction.SIDE) {
            facing = GetFacing(side - 2);
        } else {
            double vecX = hitX - (x + 0.5f);
            double vecZ = hitZ - (z + 0.5f);
            double angle = Math.atan2(vecX, vecZ);
            if ((angle >= Math.PI / 4) && (angle < Math.PI * 3 / 4)) {
                facing = Yaw.EAST;
            } else if ((angle >= Math.PI * 3 / 4) || (angle < -Math.PI * 3 / 4)) {
                facing = Yaw.NORTH;
            } else if ((angle >= -Math.PI * 3 / 4) && (angle < -Math.PI / 4)) {
                facing = Yaw.WEST;
            } else facing = Yaw.SOUTH;
        }
        if (invertFacing) {
            switch (facing) {
                default:
                case EAST:
                    facing = Yaw.WEST;
                    break;
                case WEST:
                    facing = Yaw.EAST;
                    break;
                case SOUTH:
                    facing = Yaw.NORTH;
                    break;
                case NORTH:
                    facing = Yaw.SOUTH;
                    break;
            }
        }
        return GetMetadata(direction, facing);
    }

    public int GetMetadata(int side) {
        Direction direction = GetDirection(side);
        Yaw facing;
        if (direction == Direction.SIDE) {
            facing = GetFacing(side - 2);
        } else {
            facing = Yaw.EAST;
        }
        return GetMetadata(direction, facing);
    }

    public static int GetMetadata(Direction direction, Yaw facing) {
        return direction.ordinal() * 4 + facing.ordinal();
    }

    private Direction GetDirection(int d) {
        switch (d) {
            case 0:
                return Direction.DOWN;
            case 1:
                return Direction.UP;
            default:
            case 2:
                return Direction.SIDE;
        }
    }

    public Direction DirectionFromMeta(int meta) {
        return GetDirection(meta / 4);
    }

    private Yaw GetFacing(int f) {
        switch (f) {
            default:
            case 0:
                return Yaw.EAST;
            case 1:
                return Yaw.WEST;
            case 2:
                return Yaw.NORTH;
            case 3:
                return Yaw.SOUTH;
        }
    }

    public Yaw FacingFromMeta(int meta) {
        return GetFacing(meta % 4);
    }

}
