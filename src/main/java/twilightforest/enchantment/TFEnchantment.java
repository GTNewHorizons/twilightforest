package twilightforest.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import twilightforest.item.ItemTFFieryArmor;
import twilightforest.item.ItemTFYetiArmor;

public class TFEnchantment extends Enchantment {

    private static final int FIERY_AURA_PER_PIECE = 5;

    protected TFEnchantment(int id, int weight, EnumEnchantmentType type) {
        super(id, weight, type);
    }

    /**
     * Add up the number of fiery armor pieces the player is wearing, multiplied by 5
     */
    public static int getFieryAuraLevel(InventoryPlayer inventory, DamageSource damageSource) {
        return countArmorPieces(inventory, ItemTFFieryArmor.class) * FIERY_AURA_PER_PIECE;
    }

    /**
     * Add up the number of yeti armor pieces the player is wearing, 0-4
     */
    public static int getChillAuraLevel(InventoryPlayer inventory, DamageSource damageSource) {
        return countArmorPieces(inventory, ItemTFYetiArmor.class);
    }

    private static int countArmorPieces(InventoryPlayer inventory, Class<? extends Item> armorType) {
        int count = 0;

        for (ItemStack armor : inventory.armorInventory) {
            if (armor != null && armorType.isInstance(armor.getItem())) {
                count++;
            }
        }

        return count;
    }
}
