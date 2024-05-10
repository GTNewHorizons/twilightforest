package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class BlockTFNagastone extends Block {

    private static IIcon FACE_LEFT;
    private static IIcon FACE_RIGHT;
    private static IIcon CROSS_SECTION;
    private static IIcon FACE_FRONT;
    private static IIcon TOP_TIP;
    private static IIcon TOP_LONG;
    private static IIcon BOTTOM_TIP;
    private static IIcon BOTTOM_LONG;
    private static IIcon LEFT_DOWN;
    private static IIcon RIGHT_DOWN;
    private static IIcon LEFT_UP;
    private static IIcon RIGHT_UP;
    private static IIcon TIP_LEFT;
    private static IIcon LONG_SIDE;
    private static IIcon TIP_RIGHT;
    private static IIcon TURN_TOP;

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

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     * 
     * 0 - 3 head directions 4 - 7 bend down 8 - 11 bend up 12 straight n/s 13 straight e/w 14 straight u/d 15 weird
     * middle piece
     */
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
            int metadata) {
        int type = metadata & 12;
        int orient = 0;
        if (type == 0) {
            // head!
            orient = switch (side) {
                default ->
                    // don't know
                    1;
                case 2 -> 0;
                case 3 -> 1;
                case 4 -> 2;
                case 5 -> 3;
            };
        }
        if (type == 12) {
            // straight
            // n/s
            orient = switch (side) {
                case 0, 1 -> 2; // u/d
                case 2, 3 -> 1; // e/w
                case 4, 5 -> 0;
                default -> orient;
            };
        }

        return type | orient;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World par1World, int x, int y, int z, Block neighborID) {
        int type = par1World.getBlockMetadata(x, y, z) & 12;
        if (type == 0) {
            placePerfectFitHead(par1World, x, y, z);
        } else {
            placePerfectFitBody(par1World, x, y, z);
        }
    }

    /**
     * Check if there is a perfect fit naga body block at the specified position, and if so, place it.
     * 
     * A perfect fit is when there are two nagastone blocks in exactly two directions.
     * 
     * TODO: and possibly the two other blocks should be "facing" this one?
     */
    public boolean placePerfectFitBody(World world, int x, int y, int z) {
        int firstConnection = getPosibleConnection(world, x, y, z, -1, -1);
        int secondConnection = getPosibleConnection(world, x, y, z, firstConnection, -1);

        // if we aren't connecting twice, fail
        if (firstConnection == -1 || secondConnection == -1) {
            // System.out.println("Perfect fit failed because we did not find two connections");
            return false;
        }

        // third connection == fail
        if (getPosibleConnection(world, x, y, z, firstConnection, secondConnection) != -1) {
            // System.out.println("Perfect fit failed because of 3 connections");
            return false;
        }

        // place the perfect block
        if ((firstConnection == 2 && secondConnection == 3) || (firstConnection == 3 && secondConnection == 2)) {
            // e/w straight piece
            world.setBlockMetadataWithNotify(x, y, z, 12, 3);
            return true;
        } else if (firstConnection + secondConnection == 1) {
            // n/s straight piece
            world.setBlockMetadataWithNotify(x, y, z, 13, 3);
            return true;
        } else if (firstConnection + secondConnection == 9) {
            // up/down straight piece
            world.setBlockMetadataWithNotify(x, y, z, 14, 3);
            return true;
        } else if (firstConnection == 4) {
            // curve up towards second connection
            world.setBlockMetadataWithNotify(x, y, z, 4 | secondConnection, 3);
            return true;
        } else if (firstConnection == 5) {
            // curve down towards second connection
            world.setBlockMetadataWithNotify(x, y, z, 8 | secondConnection, 3);
            return true;
        } else if (secondConnection == 4) {
            // curve up towards first connection
            world.setBlockMetadataWithNotify(x, y, z, 4 | firstConnection, 3);
            return true;
        } else if (secondConnection == 5) {
            // curve down towards first connection
            world.setBlockMetadataWithNotify(x, y, z, 8 | firstConnection, 3);
            return true;
        } else {
            // block 15!
            world.setBlockMetadataWithNotify(x, y, z, 15, 3);
            return true;
        }
    }

    /**
     * If we can place the perfect head bit, do it, otherwise do nothing.
     */
    public boolean placePerfectFitHead(World world, int x, int y, int z) {
        int connect = getOnlyNSEWConnection(world, x, y, z);

        if (connect != -1) {
            // if the first connection is n/s/e/w, and the second connection is not, we win!
            // System.out.println("Got a perfect head fit : " + connect);
            switch (connect) {
                case 0 -> {
                    world.setBlock(x, y, z, this, 1, 3);
                    return true;
                }
                case 1 -> {
                    world.setBlock(x, y, z, this, 0, 3);
                    return true;
                }
                case 2 -> {
                    world.setBlock(x, y, z, this, 3, 3);
                    return true;
                }
                case 3 -> {
                    world.setBlock(x, y, z, this, 2, 3);
                    return true;
                }
            }
            return false; // this is impossible
        } else {
            return false;
        }
    }

    /**
     * Return a direction only if it is the only possible connection, otherwise return -1. This only returns n/s/e/w
     */
    public int getOnlyNSEWConnection(World world, int x, int y, int z) {
        int firstConnection = getPosibleConnection(world, x, y, z, -1, -1);
        int secondConnection = getPosibleConnection(world, x, y, z, firstConnection, -1);

        if (firstConnection > 0 && firstConnection < 4 && (secondConnection < 0 || secondConnection > 3)) {
            return firstConnection;
        } else {
            return -1;
        }

    }

    /**
     * Return a direction only if it is the only possible connection, otherwise return -1.
     */
    public int getOnlyConnection(World world, int x, int y, int z) {
        int firstConnection = getPosibleConnection(world, x, y, z, -1, -1);
        int secondConnection = getPosibleConnection(world, x, y, z, firstConnection, -1);

        if (firstConnection > 0 && firstConnection < 6 && secondConnection == -1) {
            return firstConnection;
        } else {
            return -1;
        }

    }

    /**
     * Return a direction we have found nagastone in, excluding the two exclude directions.
     */
    public int getPosibleConnection(World world, int x, int y, int z, int exclude1, int exclude2) {
        for (int i = 0; i < 6; i++) {
            if (i != exclude1 && i != exclude2 && isNagaStoneInDirection(world, x, y, z, i)) {
                return i;
            }
        }
        // none found
        return -1;
    }

    /**
     * Is there nagastone in the following direction? 0 = n, 1 = s, 2 = w, 3 = e, 4 = u, 5 = d;
     */
    public boolean isNagaStoneInDirection(World par1World, int x, int y, int z, int direction) {
        return switch (direction) {
            case 0 -> par1World.getBlock(x, y, z - 1) == this;
            case 1 -> par1World.getBlock(x, y, z + 1) == this;
            case 2 -> par1World.getBlock(x - 1, y, z) == this;
            case 3 -> par1World.getBlock(x + 1, y, z) == this;
            case 4 -> par1World.getBlock(x, y - 1, z) == this;
            case 5 -> par1World.getBlock(x, y + 1, z) == this;
            default ->
                // welp,
                false;
        };
    }

    /**
     * Returns only 0-4. 0 = n, 1 = s, 2 = w, 3 = e.
     */
    public static int determineOrientation(World par0World, int par1, int par2, int par3,
            EntityPlayer par4EntityPlayer) {
        int rot = MathHelper.floor_double(par4EntityPlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return rot == 0 ? 0 : (rot == 1 ? 3 : (rot == 2 ? 1 : (rot == 3 ? 2 : 0)));
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
        int type = meta & 12;
        int orient = meta & 3;

        if (type == 0) {
            if (orient == 0) {
                // north
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return FACE_FRONT;
                    }
                    case 3 -> {
                        return CROSS_SECTION;
                    }
                    case 4 -> {
                        return FACE_LEFT;
                    }
                    case 5 -> {
                        return FACE_RIGHT;
                    }
                }
            } else if (orient == 1) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return CROSS_SECTION;
                    }
                    case 3 -> {
                        return FACE_FRONT;
                    }
                    case 4 -> {
                        return FACE_RIGHT;
                    }
                    case 5 -> {
                        return FACE_LEFT;
                    }
                }
            } else if (orient == 2) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return FACE_RIGHT;
                    }
                    case 3 -> {
                        return FACE_LEFT;
                    }
                    case 4 -> {
                        return FACE_FRONT;
                    }
                    case 5 -> {
                        return CROSS_SECTION;
                    }
                }
            } else if (orient == 3) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return FACE_LEFT;
                    }
                    case 3 -> {
                        return FACE_RIGHT;
                    }
                    case 4 -> {
                        return CROSS_SECTION;
                    }
                    case 5 -> {
                        return FACE_FRONT;
                    }
                }
            }
        }

        // TURN DOWN
        if (type == 4) {
            if (orient == 0) {
                // north
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return CROSS_SECTION;
                    }
                    case 3 -> {
                        return TIP_RIGHT;
                    }
                    case 4 -> {
                        return LEFT_DOWN;
                    }
                    case 5 -> {
                        return RIGHT_DOWN;
                    }
                }
            } else if (orient == 1) {
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return TIP_LEFT;
                    }
                    case 3 -> {
                        return CROSS_SECTION;
                    }
                    case 4 -> {
                        return RIGHT_DOWN;
                    }
                    case 5 -> {
                        return LEFT_DOWN;
                    }
                }
            } else if (orient == 2) {
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return RIGHT_DOWN;
                    }
                    case 3 -> {
                        return LEFT_DOWN;
                    }
                    case 4 -> {
                        return CROSS_SECTION;
                    }
                    case 5 -> {
                        return TIP_LEFT;
                    }
                }
            } else if (orient == 3) {
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return TOP_TIP;
                    }
                    case 2 -> {
                        return LEFT_DOWN;
                    }
                    case 3 -> {
                        return RIGHT_DOWN;
                    }
                    case 4 -> {
                        return TIP_RIGHT;
                    }
                    case 5 -> {
                        return CROSS_SECTION;
                    }
                }
            }

        }

        if (type == 8) {
            // TURN UP
            if (orient == 0) {
                // north
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return CROSS_SECTION;
                    }
                    case 2 -> {
                        return CROSS_SECTION;
                    }
                    case 3 -> {
                        return TIP_LEFT;
                    }
                    case 4 -> {
                        return LEFT_UP;
                    }
                    case 5 -> {
                        return RIGHT_UP;
                    }
                }
            } else if (orient == 1) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return CROSS_SECTION;
                    }
                    case 2 -> {
                        return TIP_RIGHT;
                    }
                    case 3 -> {
                        return CROSS_SECTION;
                    }
                    case 4 -> {
                        return RIGHT_UP;
                    }
                    case 5 -> {
                        return LEFT_UP;
                    }
                }
            } else if (orient == 2) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return CROSS_SECTION;
                    }
                    case 2 -> {
                        return RIGHT_UP;
                    }
                    case 3 -> {
                        return LEFT_UP;
                    }
                    case 4 -> {
                        return CROSS_SECTION;
                    }
                    case 5 -> {
                        return TIP_RIGHT;
                    }
                }
            } else if (orient == 3) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_TIP;
                    }
                    case 1 -> {
                        return CROSS_SECTION;
                    }
                    case 2 -> {
                        return LEFT_UP;
                    }
                    case 3 -> {
                        return RIGHT_UP;
                    }
                    case 4 -> {
                        return TIP_LEFT;
                    }
                    case 5 -> {
                        return CROSS_SECTION;
                    }
                }
            }

        }

        if (type == 12) {
            if (orient == 0) {
                // north
                switch (side) {
                    case 0 -> {
                        return BOTTOM_LONG;
                    }
                    case 1 -> {
                        return TOP_LONG;
                    }
                    case 2 -> {
                        return LONG_SIDE;
                    }
                    case 3 -> {
                        return LONG_SIDE;
                    }
                    case 4 -> {
                        return CROSS_SECTION;
                    }
                    case 5 -> {
                        return CROSS_SECTION;
                    }
                }
            } else if (orient == 1) {
                switch (side) {
                    case 0 -> {
                        return BOTTOM_LONG;
                    }
                    case 1 -> {
                        return TOP_LONG;
                    }
                    case 2 -> {
                        return CROSS_SECTION;
                    }
                    case 3 -> {
                        return CROSS_SECTION;
                    }
                    case 4 -> {
                        return LONG_SIDE;
                    }
                    case 5 -> {
                        return LONG_SIDE;
                    }
                }
            } else if (orient == 2) {
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return CROSS_SECTION;
                    }
                    case 2 -> {
                        return LONG_SIDE;
                    }
                    case 3 -> {
                        return LONG_SIDE;
                    }
                    case 4 -> {
                        return LONG_SIDE;
                    }
                    case 5 -> {
                        return LONG_SIDE;
                    }
                }
            } else if (orient == 3) {
                switch (side) {
                    case 0 -> {
                        return CROSS_SECTION;
                    }
                    case 1 -> {
                        return TURN_TOP;
                    }
                    case 2 -> {
                        return LONG_SIDE;
                    }
                    case 3 -> {
                        return LONG_SIDE;
                    }
                    case 4 -> {
                        return LONG_SIDE;
                    }
                    case 5 -> {
                        return LONG_SIDE;
                    }
                }
            }

        }

        return null;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        // No, we don't
    }

    public Item getItemDropped(int meta, Random random, int fortune) {
        if (meta < 4) return Item.getItemFromBlock(TFBlocks.nagastoneHead);
        else return Item.getItemFromBlock(TFBlocks.nagastoneBody);
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
        BlockTFNagastone.FACE_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_left");
        BlockTFNagastone.FACE_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_right");
        BlockTFNagastone.CROSS_SECTION = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_cross_section");
        BlockTFNagastone.FACE_FRONT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_face_front");
        BlockTFNagastone.TOP_TIP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_tip");
        BlockTFNagastone.TOP_LONG = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_top_long");
        BlockTFNagastone.BOTTOM_TIP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_tip");
        BlockTFNagastone.BOTTOM_LONG = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bottom_long");
        BlockTFNagastone.LEFT_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_down");
        BlockTFNagastone.RIGHT_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_down");
        BlockTFNagastone.LEFT_UP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_left_up");
        BlockTFNagastone.RIGHT_UP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_right_up");
        BlockTFNagastone.TIP_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_tip_left");
        BlockTFNagastone.LONG_SIDE = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_long_side");
        BlockTFNagastone.TIP_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_tip_right");
        BlockTFNagastone.TURN_TOP = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_turn_top");
    }

}
