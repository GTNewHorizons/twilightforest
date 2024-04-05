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
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFNagastoneEtched;
import twilightforest.tileentity.TileEntityTFNagastoneEtched.Direction;

public class BlockTFNagastoneEtched extends Block {

    private static IIcon TEX_TILES;
    private static IIcon TEX_TILES_MOSSY;
    private static IIcon TEX_TILES_WEATHERED;
    private static IIcon TEX_BARE;
    private static IIcon TEX_BARE_MOSSY;
    private static IIcon TEX_BARE_WEATHERED;
    private static IIcon TEX_DOWN;
    private static IIcon TEX_DOWN_MOSSY;
    private static IIcon TEX_DOWN_WEATHERED;
    private static IIcon TEX_UP;
    private static IIcon TEX_UP_MOSSY;
    private static IIcon TEX_UP_WEATHERED;
    private static IIcon TEX_LEFT;
    private static IIcon TEX_LEFT_MOSSY;
    private static IIcon TEX_LEFT_WEATHERED;
    private static IIcon TEX_RIGHT;
    private static IIcon TEX_RIGHT_MOSSY;
    private static IIcon TEX_RIGHT_WEATHERED;

    public Direction direction = Direction.UP;

    /**
     * Note that the texture called for here will only be used when the meta value is not a good block to mimic
     * 
     * @param id
     * @param texture
     */
    public BlockTFNagastoneEtched() {
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
        return TwilightForestMod.proxy.getNagastoneEtchedBlockRenderID();
    }

    @Override
    public boolean hasTileEntity(final int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTFNagastoneEtched();
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
        par1World.setTileEntity(x, y, z, new TileEntityTFNagastoneEtched(side));
    }

    // @Override
    // public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int
    // metadata)
    // {
    // world.setTileEntity(x, y, z, new TileEntityTFNagastoneEtched(side));
    // return metadata;
    // }

    /**
     * gets the way these bricks should face for that entity that placed it.
     */
    public static int determineOrientation(World world, int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F
                && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + 1.82D - (double) entity.yOffset;

            if (d0 - (double) y > 2.0D) {
                return 1;
            }

            if ((double) y - d0 > 0.0D) {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int side, int meta) {
        IIcon[][] iconList = { { TEX_TILES, TEX_BARE, TEX_DOWN, TEX_UP, TEX_LEFT, TEX_RIGHT },
                { TEX_TILES_MOSSY, TEX_BARE_MOSSY, TEX_DOWN_MOSSY, TEX_UP_MOSSY, TEX_LEFT_MOSSY, TEX_RIGHT_MOSSY },
                { TEX_TILES_WEATHERED, TEX_BARE_WEATHERED, TEX_DOWN_WEATHERED, TEX_UP_WEATHERED, TEX_LEFT_WEATHERED,
                        TEX_RIGHT_WEATHERED } };
        meta = meta % 3;
        switch (direction) {
            default:
            case UP:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                        return iconList[meta][0];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][2];
                }
            case DOWN:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                        return iconList[meta][0];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][3];
                }
            case EAST:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                    case 3:
                        return iconList[meta][4];
                    case 2:
                        return iconList[meta][5];
                    case 4:
                    case 5:
                        return iconList[meta][0];
                }
            case WEST:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                    case 3:
                        return iconList[meta][5];
                    case 2:
                        return iconList[meta][4];
                    case 4:
                    case 5:
                        return iconList[meta][0];
                }
            case SOUTH:
                switch (side) {
                    default:
                    case 2:
                    case 3:
                        return iconList[meta][0];
                    case 0:
                    case 1:
                        return iconList[meta][2];
                    case 4:
                        return iconList[meta][5];
                    case 5:
                        return iconList[meta][4];
                }
            case NORTH:
                switch (side) {
                    default:
                    case 2:
                    case 3:
                        return iconList[meta][0];
                    case 0:
                    case 1:
                        return iconList[meta][3];
                    case 4:
                        return iconList[meta][4];
                    case 5:
                        return iconList[meta][5];
                }
            case StairsLeftTop:
                switch (side) {
                    default:
                    case 0:
                        return iconList[meta][1];
                    case 1:
                        return iconList[meta][0];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][4];
                }
            case StairsLeftBottom:
                switch (side) {
                    default:
                    case 0:
                        return iconList[meta][0];
                    case 1:
                        return iconList[meta][1];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][4];
                }
            case StairsRightTop:
                switch (side) {
                    default:
                    case 0:
                        return iconList[meta][1];
                    case 1:
                        return iconList[meta][0];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][5];
                }
            case StairsRightBottom:
                switch (side) {
                    default:
                    case 0:
                        return iconList[meta][0];
                    case 1:
                        return iconList[meta][1];
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return iconList[meta][5];
                }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockTFNagastoneEtched.TEX_TILES = par1IconRegister.registerIcon(TwilightForestMod.ID + ":stone_tiles");
        BlockTFNagastoneEtched.TEX_TILES_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":stone_tiles_mossy");
        BlockTFNagastoneEtched.TEX_TILES_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":stone_tiles_weathered");
        BlockTFNagastoneEtched.TEX_BARE = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone_bare");
        BlockTFNagastoneEtched.TEX_BARE_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bare_mossy");
        BlockTFNagastoneEtched.TEX_BARE_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone_bare_weathered");
        BlockTFNagastoneEtched.TEX_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_down");
        BlockTFNagastoneEtched.TEX_DOWN_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_down_mossy");
        BlockTFNagastoneEtched.TEX_DOWN_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_down_weathered");
        BlockTFNagastoneEtched.TEX_UP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":etched_nagastone_up");
        BlockTFNagastoneEtched.TEX_UP_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_up_mossy");
        BlockTFNagastoneEtched.TEX_UP_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_up_weathered");
        BlockTFNagastoneEtched.TEX_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_left");
        BlockTFNagastoneEtched.TEX_LEFT_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_left_mossy");
        BlockTFNagastoneEtched.TEX_LEFT_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_left_weathered");
        BlockTFNagastoneEtched.TEX_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_right");
        BlockTFNagastoneEtched.TEX_RIGHT_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_right_mossy");
        BlockTFNagastoneEtched.TEX_RIGHT_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":etched_nagastone_right_weathered");
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int meta) {
        return meta;
    }

}
