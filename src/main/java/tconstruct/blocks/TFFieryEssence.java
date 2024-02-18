package tconstruct.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.integration.TFTinkerConstructIntegration;

public class TFFieryEssence extends BlockFluidClassic {

    IIcon stillIcon;
    IIcon flowIcon;

    public TFFieryEssence(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        stillIcon = iconRegister.registerIcon("tinker:fiery_essence");
        flowIcon = iconRegister.registerIcon("tinker:fiery_essence_flow");
        TFTinkerConstructIntegration.fieryEssenceFluid.setStillIcon(stillIcon);
        TFTinkerConstructIntegration.fieryEssenceFluid.setFlowingIcon(flowIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) return stillIcon;
        return flowIcon;
    }

    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {}
}
