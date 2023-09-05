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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class BlockTFMagicLog extends BlockLog {

    public static final int META_TIME = 0;
    public static final int META_TRANS = 1;
    public static final int META_MINE = 2;
    public static final int META_SORT = 3;

    public static IIcon SPR_TIMESIDE;
    public static IIcon SPR_TIMETOP;
    public static IIcon SPR_TIMECLOCK;
    public static IIcon SPR_TIMECLOCKOFF;
    public static IIcon SPR_TRANSSIDE;
    public static IIcon SPR_TRANSTOP;
    public static IIcon SPR_TRANSHEART;
    public static IIcon SPR_TRANSHEARTOFF;
    public static IIcon SPR_MINESIDE;
    public static IIcon SPR_MINETOP;
    public static IIcon SPR_MINEGEM;
    public static IIcon SPR_MINEGEMOFF;
    public static IIcon SPR_SORTSIDE;
    public static IIcon SPR_SORTTOP;
    public static IIcon SPR_SORTEYE;
    public static IIcon SPR_SORTEYEOFF;

    protected BlockTFMagicLog() {
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
            default -> orient == 0 && (side == 1 || side == 0) ? SPR_TIMETOP
                    : (orient == 4 && (side == 5 || side == 4) ? SPR_TIMETOP
                            : (orient == 8 && (side == 2 || side == 3) ? SPR_TIMETOP : SPR_TIMESIDE));
            case META_TRANS -> orient == 0 && (side == 1 || side == 0) ? SPR_TRANSTOP
                    : (orient == 4 && (side == 5 || side == 4) ? SPR_TRANSTOP
                            : (orient == 8 && (side == 2 || side == 3) ? SPR_TRANSTOP : SPR_TRANSSIDE));
            case META_MINE -> orient == 0 && (side == 1 || side == 0) ? SPR_MINETOP
                    : (orient == 4 && (side == 5 || side == 4) ? SPR_MINETOP
                            : (orient == 8 && (side == 2 || side == 3) ? SPR_MINETOP : SPR_MINESIDE));
            case META_SORT -> orient == 0 && (side == 1 || side == 0) ? SPR_SORTTOP
                    : (orient == 4 && (side == 5 || side == 4) ? SPR_SORTTOP
                            : (orient == 8 && (side == 2 || side == 3) ? SPR_SORTTOP : SPR_SORTSIDE));
        };

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        BlockTFMagicLog.SPR_TIMESIDE = iconRegister.registerIcon(TwilightForestMod.ID + ":time_side");
        BlockTFMagicLog.SPR_TIMETOP = iconRegister.registerIcon(TwilightForestMod.ID + ":time_section");
        BlockTFMagicLog.SPR_TIMECLOCK = iconRegister.registerIcon(TwilightForestMod.ID + ":time_clock");
        BlockTFMagicLog.SPR_TIMECLOCKOFF = iconRegister.registerIcon(TwilightForestMod.ID + ":time_clock_off");
        BlockTFMagicLog.SPR_TRANSSIDE = iconRegister.registerIcon(TwilightForestMod.ID + ":trans_side");
        BlockTFMagicLog.SPR_TRANSTOP = iconRegister.registerIcon(TwilightForestMod.ID + ":trans_section");
        BlockTFMagicLog.SPR_TRANSHEART = iconRegister.registerIcon(TwilightForestMod.ID + ":trans_heart");
        BlockTFMagicLog.SPR_TRANSHEARTOFF = iconRegister.registerIcon(TwilightForestMod.ID + ":trans_heart_off");
        BlockTFMagicLog.SPR_MINESIDE = iconRegister.registerIcon(TwilightForestMod.ID + ":mine_side");
        BlockTFMagicLog.SPR_MINETOP = iconRegister.registerIcon(TwilightForestMod.ID + ":mine_section");
        BlockTFMagicLog.SPR_MINEGEM = iconRegister.registerIcon(TwilightForestMod.ID + ":mine_gem");
        BlockTFMagicLog.SPR_MINEGEMOFF = iconRegister.registerIcon(TwilightForestMod.ID + ":mine_gem_off");
        BlockTFMagicLog.SPR_SORTSIDE = iconRegister.registerIcon(TwilightForestMod.ID + ":sort_side");
        BlockTFMagicLog.SPR_SORTTOP = iconRegister.registerIcon(TwilightForestMod.ID + ":sort_section");
        BlockTFMagicLog.SPR_SORTEYE = iconRegister.registerIcon(TwilightForestMod.ID + ":sort_eye");
        BlockTFMagicLog.SPR_SORTEYEOFF = iconRegister.registerIcon(TwilightForestMod.ID + ":sort_eye_off");
    }

    @Override
    public Item getItemDropped(int par1, Random random, int par3) {
        return Item.getItemFromBlock(this); // hey that's my block ID!
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));

    }

}
