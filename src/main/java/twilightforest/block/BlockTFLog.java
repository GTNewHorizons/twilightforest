package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTFLog extends BlockLog {

    public static IIcon sprOakSide;
    public static IIcon sprOakTop;
    public static IIcon sprCanopySide;
    public static IIcon sprCanopyTop;
    public static IIcon sprMangroveSide;
    public static IIcon sprMangroveTop;
    public static IIcon sprDarkwoodSide;
    public static IIcon sprDarkwoodTop;

    // public static int sprRottenSide = 15;

    protected BlockTFLog() {
        super();
        this.setHardness(2.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int orient = meta & 12;
        int woodType = meta & 3;

        return switch (woodType) {
            default -> orient == 0 && (side == 1 || side == 0) ? sprOakTop
                    : (orient == 4 && (side == 5 || side == 4) ? sprOakTop
                            : (orient == 8 && (side == 2 || side == 3) ? sprOakTop : sprOakSide));
            case 1 -> orient == 0 && (side == 1 || side == 0) ? sprCanopyTop
                    : (orient == 4 && (side == 5 || side == 4) ? sprCanopyTop
                            : (orient == 8 && (side == 2 || side == 3) ? sprCanopyTop : sprCanopySide));
            case 2 -> orient == 0 && (side == 1 || side == 0) ? sprMangroveTop
                    : (orient == 4 && (side == 5 || side == 4) ? sprMangroveTop
                            : (orient == 8 && (side == 2 || side == 3) ? sprMangroveTop : sprMangroveSide));
            case 3 -> orient == 0 && (side == 1 || side == 0) ? sprDarkwoodTop
                    : (orient == 4 && (side == 5 || side == 4) ? sprDarkwoodTop
                            : (orient == 8 && (side == 2 || side == 3) ? sprDarkwoodTop : sprDarkwoodSide));
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockTFLog.sprOakSide = par1IconRegister.registerIcon(TwilightForestMod.ID + ":oak_side");
        BlockTFLog.sprOakTop = par1IconRegister.registerIcon(TwilightForestMod.ID + ":oak_top");
        BlockTFLog.sprCanopySide = par1IconRegister.registerIcon(TwilightForestMod.ID + ":canopy_side");
        BlockTFLog.sprCanopyTop = par1IconRegister.registerIcon(TwilightForestMod.ID + ":canopy_top");
        BlockTFLog.sprMangroveSide = par1IconRegister.registerIcon(TwilightForestMod.ID + ":mangrove_side");
        BlockTFLog.sprMangroveTop = par1IconRegister.registerIcon(TwilightForestMod.ID + ":mangrove_top");
        BlockTFLog.sprDarkwoodSide = par1IconRegister.registerIcon(TwilightForestMod.ID + ":darkwood_side");
        BlockTFLog.sprDarkwoodTop = par1IconRegister.registerIcon(TwilightForestMod.ID + ":darkwood_top");
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return Item.getItemFromBlock(TFBlocks.log); // hey that's my block ID!
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));
    }

}
