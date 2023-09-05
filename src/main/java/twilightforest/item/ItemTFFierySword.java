package twilightforest.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;

public class ItemTFFierySword extends ItemSword {

    public ItemTFFierySword(Item.ToolMaterial toolMaterial) {
        super(toolMaterial);
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
        ItemStack istack = new ItemStack(item, 1, 0);
        // istack.addEnchantment(Enchantment.fireAspect, 1);
        itemList.add(istack);
    }

    /**
     * Return an item rarity from EnumRarity
     * 
     * This is automatically rare
     */
    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.rare;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        // repair with fiery ingots
        return par2ItemStack.getItem() == TFItems.fieryIngot ? true
                : super.getIsRepairable(par1ItemStack, par2ItemStack);
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

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase target, EntityLivingBase par3EntityLiving) {
        boolean result = super.hitEntity(par1ItemStack, target, par3EntityLiving);

        if (result && !target.isImmuneToFire()) {
            if (target.worldObj.isRemote) {
                // fire animation!
                for (int var1 = 0; var1 < 20; ++var1) {
                    double var2 = itemRand.nextGaussian() * 0.02D;
                    double var4 = itemRand.nextGaussian() * 0.02D;
                    double var6 = itemRand.nextGaussian() * 0.02D;
                    double var8 = 10.0D;
                    target.worldObj.spawnParticle(
                            "flame",
                            target.posX + itemRand.nextFloat() * target.width * 2.0F - target.width - var2 * var8,
                            target.posY + itemRand.nextFloat() * target.height - var4 * var8,
                            target.posZ + itemRand.nextFloat() * target.width * 2.0F - target.width - var6 * var8,
                            var2,
                            var4,
                            var6);
                }
            } else {
                target.setFire(15);
            }
        }

        return result;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List,
            boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        par3List.add(StatCollector.translateToLocal(getUnlocalizedName() + ".tooltip"));
    }
}
