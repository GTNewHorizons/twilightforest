package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class ItemTFMazebreakerPick extends ItemPickaxe {

    protected ItemTFMazebreakerPick(Item.ToolMaterial par2EnumToolMaterial) {
        super(par2EnumToolMaterial);
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        ItemStack istack = new ItemStack(par1, 1, 0);
        istack.addEnchantment(Enchantment.efficiency, 4);
        istack.addEnchantment(Enchantment.unbreaking, 3);
        istack.addEnchantment(Enchantment.fortune, 2);
        par3List.add(istack);
    }

    @Override
    public float func_150893_a(ItemStack par1ItemStack, Block par2Block) {
        float strVsBlock = super.func_150893_a(par1ItemStack, par2Block);
        // 16x strength vs mazestone
        return par2Block == TFBlocks.mazestone ? strVsBlock * 16F : strVsBlock;
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }
}
