package twilightforest.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.compat.Mods;

public class ItemTF extends Item {

    private boolean isRare = false;

    protected ItemTF() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Return an item rarity from EnumRarity
     * 
     * This is automatically uncommon
     */
    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return isRare ? EnumRarity.rare : EnumRarity.uncommon;
    }

    /**
     * Set rarity during creation
     */
    public ItemTF makeRare() {
        this.isRare = true;
        return this;
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
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        if (Mods.ticon.isLoaded() && (this == TFItems.fieryBlood || this == TFItems.fieryTears)) {
            String tooltip = "";
            if (this == TFItems.fieryBlood) {
                tooltip = StatCollector.translateToLocal("item.fieryBlood.tooltip");
            } else if (this == TFItems.fieryTears) {
                tooltip = StatCollector.translateToLocal("item.fieryTears.tooltip");
            }
            list.add(EnumChatFormatting.GRAY + tooltip);
        }
    }
}
