package twilightforest.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class BlockTFNagastonePillar extends BlockRotatedPillar {

    private static IIcon PILLAR_END;
    private static IIcon PILLAR_END_MOSSY;
    private static IIcon PILLAR_END_WEATHERED;
    private static IIcon PILLAR_SIDE;
    private static IIcon PILLAR_SIDE_MOSSY;
    private static IIcon PILLAR_SIDE_WEATHERED;
    private static IIcon PILLAR_SIDE_ALT;
    private static IIcon PILLAR_SIDE_ALT_MOSSY;
    private static IIcon PILLAR_SIDE_ALT_WEATHERED;

    public NagastoneType blockType;

    protected BlockTFNagastonePillar(NagastoneType type) {
        super(Material.rock);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setCreativeTab(TFItems.creativeTab);
        blockType = type;
    }

    @Override
    protected IIcon getSideIcon(int p_150163_1_) {
        switch (blockType) {
            default:
            case NORMAL:
                return PILLAR_SIDE;
            case MOSSY:
                return PILLAR_SIDE_MOSSY;
            case WEATHERED:
                return PILLAR_SIDE_WEATHERED;
        }
    }

    @Override
    protected IIcon getTopIcon(int p_150163_1_) {
        switch (blockType) {
            default:
            case NORMAL:
                return PILLAR_END;
            case MOSSY:
                return PILLAR_END_MOSSY;
            case WEATHERED:
                return PILLAR_END_WEATHERED;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.PILLAR_END = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_end");
        this.PILLAR_END_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_end_mossy");
        this.PILLAR_END_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_end_weathered");
        this.PILLAR_SIDE = par1IconRegister.registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side");
        this.PILLAR_SIDE_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side_mossy");
        this.PILLAR_SIDE_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side_weathered");
        this.PILLAR_SIDE_ALT = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side_alt");
        this.PILLAR_SIDE_ALT_MOSSY = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side_alt_mossy");
        this.PILLAR_SIDE_ALT_WEATHERED = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":nagastone/nagastone_pillar_side_alt_weathered");
    }

}
