package twilightforest.integration;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import twilightforest.TwilightForestMod;

public class TFBaublesIntegration {

    public static Boolean consumeBaublesItem(EntityPlayer player, Item item) {
        if (TwilightForestMod.areBaublesLoaded) {
            InventoryBaubles inventoryBaubles = PlayerHandler.getPlayerBaubles(player);
            for (int a = 0; a < inventoryBaubles.getSizeInventory(); a++) {
                ItemStack stackInSlot = inventoryBaubles.getStackInSlot(a);
                if (stackInSlot != null && stackInSlot.getItem() == item) {
                    if (--stackInSlot.stackSize <= 0) {
                        inventoryBaubles.setInventorySlotContents(a, null);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Move the full baubles inventory to the keep pile
     */
    public static ItemStack[] keepAllBaubles(EntityPlayer player) {
        ItemStack[] baublesInventory = null;
        if (TwilightForestMod.areBaublesLoaded) {
            InventoryBaubles inventoryBaubles = PlayerHandler.getPlayerBaubles(player);
            baublesInventory = new ItemStack[inventoryBaubles.getSizeInventory()];
            for (int i = 0; i < inventoryBaubles.getSizeInventory(); i++) {
                baublesInventory[i] = ItemStack.copyItemStack(inventoryBaubles.getStackInSlot(i));
                inventoryBaubles.setInventorySlotContents(i, null);
            }
        }
        return baublesInventory;
    }

}
