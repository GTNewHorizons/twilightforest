package tconstruct.tools.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import mantle.items.abstracts.CraftingItem;
import tconstruct.library.TConstructRegistry;

public class TFMaterialItem extends CraftingItem {

    public TFMaterialItem() {
        super(materialNames, getTextures(), "materials/", "tinker", TConstructRegistry.materialTab);
    }

    private static String[] getTextures() {
        String[] names = new String[craftingTextures.length];
        for (int i = 0; i < craftingTextures.length; i++) {
            if (craftingTextures[i].equals("")) names[i] = "";
            else names[i] = "material_" + craftingTextures[i];
        }
        return names;
    }

    static String[] materialNames = new String[] { "FieryNugget", "KnightlyNugget" };

    static String[] craftingTextures = new String[] { "nugget_fierymetal", "nugget_knightmetal" };

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return itemStack.getItemDamage() == 0 ? EnumRarity.rare : EnumRarity.uncommon;
    }
}
