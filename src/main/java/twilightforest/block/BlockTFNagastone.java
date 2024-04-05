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
import twilightforest.tileentity.TileEntityTFNagastone.Direction;
import twilightforest.tileentity.TileEntityTFNagastone.Facing;

public class BlockTFNagastone extends Block {

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

    public BlockTFNagastone() {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType() {
        return TwilightForestMod.proxy.getNagastoneBlockRenderID();
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
        TileEntityTFNagastone te = new TileEntityTFNagastone(
                side,
                x,
                z,
                (float) par5EntityLiving.posX,
                (float) par5EntityLiving.posZ,
                par5EntityLiving.isSneaking());
        te.blockMetadata = par1World.getBlockMetadata(x, y, z);
        par1World.setTileEntity(x, y, z, te);
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
        TileEntityTFNagastone te = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
        if (te == null) {
            te = new TileEntityTFNagastone();
            switch (world.getBlockMetadata(x, y, z)) {
                case 0:
                    te.blockMetadata = 0;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.NORTH;
                    break;
                case 1:
                    te.blockMetadata = 0;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.SOUTH;
                    break;
                case 2:
                    te.blockMetadata = 0;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.EAST;
                    break;
                case 3:
                    te.blockMetadata = 0;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.WEST;
                    break;
                case 4:
                    te.blockMetadata = 1;
                    te.direction = Direction.DOWN;
                    te.facing = Facing.NORTH;
                    break;
                case 5:
                    te.blockMetadata = 1;
                    te.direction = Direction.DOWN;
                    te.facing = Facing.SOUTH;
                    break;
                case 6:
                    te.blockMetadata = 1;
                    te.direction = Direction.DOWN;
                    te.facing = Facing.EAST;
                    break;
                case 7:
                    te.blockMetadata = 1;
                    te.direction = Direction.DOWN;
                    te.facing = Facing.WEST;
                    break;
                case 8:
                    te.blockMetadata = 1;
                    te.direction = Direction.UP;
                    te.facing = Facing.NORTH;
                    break;
                case 9:
                    te.blockMetadata = 1;
                    te.direction = Direction.UP;
                    te.facing = Facing.SOUTH;
                    break;
                case 10:
                    te.blockMetadata = 1;
                    te.direction = Direction.UP;
                    te.facing = Facing.EAST;
                    break;
                case 11:
                    te.blockMetadata = 1;
                    te.direction = Direction.UP;
                    te.facing = Facing.WEST;
                    break;
                default:
                case 12:
                case 15:
                    te.blockMetadata = 1;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.SOUTH;
                    break;
                case 13:
                    te.blockMetadata = 1;
                    te.direction = Direction.SIDE;
                    te.facing = Facing.EAST;
                    break;
                case 14:
                    te.blockMetadata = 1;
                    te.direction = Direction.UP;
                    te.facing = Facing.SOUTH;
                    break;
            }
            world.setTileEntity(x, y, z, te);
        }
        if (world.getBlockMetadata(x, y, z) == 1) {
            // Check4Neighbours(world, x, y, z);
            te = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
            te.NoNeighbours();
            te.neighbourDown = CanConnectTo(world, x, y, z, 0);
            te.neighbourUp = CanConnectTo(world, x, y, z, 1);
            te.neighbourEast = CanConnectTo(world, x, y, z, 2);
            te.neighbourWest = CanConnectTo(world, x, y, z, 3);
            te.neighbourNorth = CanConnectTo(world, x, y, z, 4);
            te.neighbourSouth = CanConnectTo(world, x, y, z, 5);
            te.UpdateNeighbourCount();
            world.setTileEntity(x, y, z, te);
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
        world.setTileEntity(x, y, z, te);
    }

    public boolean CanConnectTo(World world, int x, int y, int z, int direction) {
        TileEntityTFNagastone currentBlock = (TileEntityTFNagastone) world.getTileEntity(x, y, z);
        if (world.getBlockMetadata(x, y, z) == 1) if (isNagaStoneInDirection(world, x, y, z, direction)) {
            TileEntityTFNagastone neighbourBlock = null;
            switch (direction) {
                case 0:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x, y - 1, z);
                    break;
                case 1:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x, y + 1, z);
                    break;
                case 2:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x + 1, y, z);
                    break;
                case 3:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x - 1, y, z);
                    break;
                case 4:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x, y, z - 1);
                    break;
                case 5:
                    neighbourBlock = (TileEntityTFNagastone) world.getTileEntity(x, y, z + 1);
                    break;
                default:
                    return false;
            }
            if (neighbourBlock.getBlockMetadata() == 0) {
                switch (direction) {
                    case 0:
                        if (neighbourBlock.direction != Direction.DOWN) return false;
                        else {
                            if (currentBlock.direction == Direction.SIDE) switch (currentBlock.facing) {
                                case EAST:
                                case WEST:
                                    return neighbourBlock.facing == Facing.EAST || neighbourBlock.facing == Facing.WEST;
                                case NORTH:
                                case SOUTH:
                                    return neighbourBlock.facing == Facing.NORTH
                                            || neighbourBlock.facing == Facing.SOUTH;
                            }
                            else {
                                return neighbourBlock.facing == currentBlock.facing;
                            }
                        }
                        break;
                    case 1:
                        if (neighbourBlock.direction != Direction.UP) return false;
                        else {
                            if (currentBlock.direction == Direction.SIDE) switch (currentBlock.facing) {
                                case EAST:
                                case WEST:
                                    return neighbourBlock.facing == Facing.EAST || neighbourBlock.facing == Facing.WEST;
                                case NORTH:
                                case SOUTH:
                                    return neighbourBlock.facing == Facing.NORTH
                                            || neighbourBlock.facing == Facing.SOUTH;
                            }
                            else {
                                return neighbourBlock.facing == currentBlock.facing;
                            }
                        }
                        break;
                    case 2:
                        return neighbourBlock.direction == Direction.SIDE && neighbourBlock.facing == Facing.EAST;
                    case 3:
                        return neighbourBlock.direction == Direction.SIDE && neighbourBlock.facing == Facing.WEST;
                    case 4:
                        return neighbourBlock.direction == Direction.SIDE && neighbourBlock.facing == Facing.NORTH;
                    case 5:
                        return neighbourBlock.direction == Direction.SIDE && neighbourBlock.facing == Facing.SOUTH;
                }
            } else {
                switch (direction) {
                    case 0:
                    case 1:
                        if ((currentBlock.direction == Direction.SIDE && neighbourBlock.direction == Direction.SIDE)
                                || (currentBlock.direction != Direction.SIDE
                                        && neighbourBlock.direction != Direction.SIDE
                                        && neighbourBlock.facing != currentBlock.facing)) {
                            return false;
                        } else switch (currentBlock.facing) {
                            case EAST:
                            case WEST:
                                return neighbourBlock.facing == Facing.EAST || neighbourBlock.facing == Facing.WEST;
                            case NORTH:
                            case SOUTH:
                                return neighbourBlock.facing == Facing.NORTH || neighbourBlock.facing == Facing.SOUTH;
                        }
                        break;
                    case 2:
                    case 3:
                        if (currentBlock.direction == Direction.SIDE) {
                            switch (currentBlock.facing) {
                                case EAST:
                                case WEST:
                                    if (neighbourBlock.direction == Direction.SIDE) return true;
                                    else return (neighbourBlock.facing == Facing.EAST
                                            || neighbourBlock.facing == Facing.WEST);
                                case NORTH:
                                case SOUTH:
                                    if (neighbourBlock.direction != Direction.SIDE) return false;
                                    else return (neighbourBlock.facing == Facing.EAST
                                            || neighbourBlock.facing == Facing.WEST);
                            }
                        } else {
                            if (neighbourBlock.direction != Direction.SIDE) return false;
                            else {
                                switch (currentBlock.facing) {
                                    case EAST:
                                    case WEST:
                                        return neighbourBlock.facing == Facing.EAST
                                                || neighbourBlock.facing == Facing.WEST;
                                    case NORTH:
                                    case SOUTH:
                                        return false;
                                }
                            }
                        }
                        break;
                    case 4:
                    case 5:
                        if (currentBlock.direction == Direction.SIDE) {
                            switch (currentBlock.facing) {
                                case EAST:
                                case WEST:
                                    if (neighbourBlock.direction != Direction.SIDE) return false;
                                    else return (neighbourBlock.facing == Facing.NORTH
                                            || neighbourBlock.facing == Facing.SOUTH);
                                case NORTH:
                                case SOUTH:
                                    if (neighbourBlock.direction == Direction.SIDE) return true;
                                    else return (neighbourBlock.facing == Facing.NORTH
                                            || neighbourBlock.facing == Facing.SOUTH);
                            }
                        } else {
                            if (neighbourBlock.direction != Direction.SIDE) return false;
                            else {
                                switch (currentBlock.facing) {
                                    case EAST:
                                    case WEST:
                                        return false;
                                    case NORTH:
                                    case SOUTH:
                                        return neighbourBlock.facing == Facing.NORTH
                                                || neighbourBlock.facing == Facing.SOUTH;
                                }
                            }
                        }
                        break;
                }
            }
        } else {
            switch (direction) {
                case 0:
                    if (currentBlock.direction != Direction.SIDE && !world.isAirBlock(x, y + 1, z)) {
                        if (!isNagaStoneInDirection(world, x, y, z, 1)) {
                            return true;
                        }
                    }
                    break;
                case 1:
                    if (currentBlock.direction != Direction.SIDE && !world.isAirBlock(x, y - 1, z)) {
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
                return par1World.getBlock(x, y - 1, z) == this;
            case 1:
                return par1World.getBlock(x, y + 1, z) == this;
            case 2:
                return par1World.getBlock(x + 1, y, z) == this;
            case 3:
                return par1World.getBlock(x - 1, y, z) == this;
            case 4:
                return par1World.getBlock(x, y, z - 1) == this;
            case 5:
                return par1World.getBlock(x, y, z + 1) == this;
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

        if (meta == 0) {
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
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int meta) {
        if (meta > 3) {
            return 1;
        } else {
            if (meta > 1) return 0;
            else return meta;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockTFNagastone.FACE_LEFT = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_face_left");
        BlockTFNagastone.FACE_RIGHT = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_face_right");
        BlockTFNagastone.FACE_FRONT = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_face_front");
        BlockTFNagastone.CROSS_SECTION = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_cross_section");
        BlockTFNagastone.TOP_TIP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_top_tip");
        BlockTFNagastone.TOP_LONG = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_top_long");
        BlockTFNagastone.TOP_CROSS = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_top_turn");
        BlockTFNagastone.TOP_ANGLE_UP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_top_angle_up");
        BlockTFNagastone.TOP_ANGLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_top_angle_down");
        BlockTFNagastone.TOP_TRIPLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_top_triple_up");
        BlockTFNagastone.TOP_TRIPLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_top_triple_down");
        BlockTFNagastone.BOTTOM_TIP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_bottom_tip");
        BlockTFNagastone.BOTTOM_LONG = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_bottom_long");
        BlockTFNagastone.LEFT_DOWN_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_left_down_clear");
        BlockTFNagastone.LEFT_DOWN_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_left_down_ornated");
        BlockTFNagastone.LEFT_UP_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_left_up_clear");
        BlockTFNagastone.LEFT_UP_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_left_up_ornated");
        BlockTFNagastone.RIGHT_DOWN_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_right_down_clear");
        BlockTFNagastone.RIGHT_DOWN_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_right_down_ornated");
        BlockTFNagastone.RIGHT_UP_CLEAR = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_right_up_clear");
        BlockTFNagastone.RIGHT_UP_ORNATED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_right_up_ornated");
        BlockTFNagastone.TIP_LEFT = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_tip_left");
        BlockTFNagastone.TIP_RIGHT = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_tip_right");
        BlockTFNagastone.RIGHT_SIDE = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_side_right");
        BlockTFNagastone.LEFT_SIDE = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_side_left");
        BlockTFNagastone.TRIPLE_RIGHT_UP_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_right_up_lb");
        BlockTFNagastone.TRIPLE_RIGHT_DOWN_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_right_down_lb");
        BlockTFNagastone.TRIPLE_RIGHT_UP_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_right_up_rb");
        BlockTFNagastone.TRIPLE_RIGHT_DOWN_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_right_down_rb");
        BlockTFNagastone.TRIPLE_LEFT_UP_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_left_up_lb");
        BlockTFNagastone.TRIPLE_LEFT_DOWN_LB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_left_down_lb");
        BlockTFNagastone.TRIPLE_LEFT_UP_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_left_up_rb");
        BlockTFNagastone.TRIPLE_LEFT_DOWN_RB = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_triple_left_down_rb");
        BlockTFNagastone.RIGHT_CROSS = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_cross_right");
        BlockTFNagastone.LEFT_CROSS = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_cross_left");
        BlockTFNagastone.BOTTOM_ANGLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bottom_angle_up");
        BlockTFNagastone.BOTTOM_ANGLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bottom_angle_down");
        BlockTFNagastone.BOTTOM_TRIPLE_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bottom_triple_up");
        BlockTFNagastone.BOTTOM_TRIPLE_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bottom_triple_down");
        BlockTFNagastone.BOTTOM_CROSS = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_bottom_cross");
    }

}
