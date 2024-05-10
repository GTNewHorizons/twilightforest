package twilightforest.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class BlockTFNagastoneEtched extends Block {

    public IIcon TEX_TILES;
    public IIcon TEX_BARE;
    public IIcon TEX_DOWN;
    public IIcon TEX_UP;
    public IIcon TEX_LEFT;
    public IIcon TEX_RIGHT;

    public NagastoneType type = NagastoneType.NORMAL;
    public boolean stairsTop = false;

    /**
     * Note that the texture called for here will only be used when the meta value is not a good block to mimic
     * 
     * @param id
     * @param texture
     */
    public BlockTFNagastoneEtched(NagastoneType type) {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setCreativeTab(TFItems.creativeTab);
        this.type = type;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
            int metadata) {
        int newMeta = 0;

        switch (side) {
            default:
            case 0:
                newMeta = ForgeDirection.UP.ordinal();
                break;
            case 1:
                newMeta = ForgeDirection.DOWN.ordinal();
                break;
            case 2:
                newMeta = ForgeDirection.NORTH.ordinal();
                break;
            case 3:
                newMeta = ForgeDirection.SOUTH.ordinal();
                break;
            case 4:
                newMeta = ForgeDirection.WEST.ordinal();
                break;
            case 5:
                newMeta = ForgeDirection.EAST.ordinal();
                break;
        }

        return newMeta;
    }

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
        ForgeDirection direction = ForgeDirection.getOrientation(meta);
        switch (direction) {
            default:
            case DOWN:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                        return this.TEX_TILES;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return this.TEX_UP;
                }
            case UP:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                        return this.TEX_TILES;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return this.TEX_DOWN;
                }
            case SOUTH:
                switch (side) {
                    default:
                    case 2:
                    case 3:
                        return this.TEX_TILES;
                    case 0:
                    case 1:
                        return this.TEX_DOWN;
                    case 4:
                        return this.TEX_RIGHT;
                    case 5:
                        return this.TEX_LEFT;
                }
            case NORTH:
                switch (side) {
                    default:
                    case 2:
                    case 3:
                        return this.TEX_TILES;
                    case 0:
                    case 1:
                        return this.TEX_UP;
                    case 4:
                        return this.TEX_LEFT;
                    case 5:
                        return this.TEX_RIGHT;
                }
            case WEST:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                    case 3:
                        return this.TEX_LEFT;
                    case 2:
                        return this.TEX_RIGHT;
                    case 4:
                    case 5:
                        return this.TEX_TILES;
                }
            case EAST:
                switch (side) {
                    default:
                    case 0:
                    case 1:
                    case 3:
                        return this.TEX_RIGHT;
                    case 2:
                        return this.TEX_LEFT;
                    case 4:
                    case 5:
                        return this.TEX_TILES;
                }
        }
    }

    public IIcon getIconStairs(int side, boolean stairsRight) {
        IIcon topIcon;
        IIcon bottomIcon;
        IIcon sideIcon;
        if (this.stairsTop) {
            topIcon = this.TEX_TILES;
            bottomIcon = this.TEX_BARE;
        } else {
            topIcon = this.TEX_BARE;
            bottomIcon = this.TEX_TILES;
        }
        if (stairsRight) sideIcon = this.TEX_RIGHT;
        else sideIcon = this.TEX_LEFT;
        switch (side) {
            default:
            case 0:
                return bottomIcon;
            case 1:
                return topIcon;
            case 2:
            case 3:
            case 4:
            case 5:
                return sideIcon;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        String postfix;
        switch (this.type) {
            default:
            case NORMAL:
                postfix = "";
                break;
            case MOSSY:
                postfix = "_mossy";
                break;
            case WEATHERED:
                postfix = "_weathered";
                break;
        }

        this.TEX_TILES = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/stone_tiles" + postfix);
        this.TEX_BARE = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_bare" + postfix);
        this.TEX_DOWN = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/etched_nagastone_down" + postfix);
        this.TEX_UP = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/etched_nagastone_up" + postfix);
        this.TEX_LEFT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/etched_nagastone_left" + postfix);
        this.TEX_RIGHT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/etched_nagastone_right" + postfix);
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

}
