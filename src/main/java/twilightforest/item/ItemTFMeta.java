package twilightforest.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import twilightforest.TwilightForestMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTFMeta extends Item {

    public String[] textureNames = { "adherentFragment", "harbingerFragment" };// make something called emissary?
    public IIcon[] icons;

    public ItemTFMeta() {
        super();
        this.setHasSubtypes(true);
        this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            case 0 -> "item.adherentFragment";
            case 1 -> "item.harbingerFragment";
            default -> "unusedMeta";
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            case 0, 1 -> EnumRarity.uncommon;

            // return EnumRarity.rare;
            default -> EnumRarity.common;
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
        switch (stack.getItemDamage()) {
            case 0 -> list.add(StatCollector.translateToLocal("tooltip.tf.adherent"));
            case 1 -> list.add(StatCollector.translateToLocal("tooltip.tf.harbinger"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
            this.icons[i] = iconRegister.registerIcon(TwilightForestMod.ID + ":" + textureNames[i]);
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < textureNames.length; i++) list.add(new ItemStack(id, 1, i));
    }

}
