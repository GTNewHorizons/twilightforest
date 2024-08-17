package twilightforest.integration;

import java.io.File;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import twilightforest.TFEventListener;
import twilightforest.TwilightForestMod;

public class TFBaublesIntegration {

    public static HashMap<String, InventoryBaubles> playerBaublesMap = new HashMap<>();

    public static void clearPlayerMap(EntityPlayer player) {
        if (TwilightForestMod.areBaublesLoaded) {
            playerBaublesMap.remove(player.getCommandSenderName());
        }
    }

    public static InventoryBaubles getPlayerKeepBaubles(EntityPlayer player) {
        if (!playerBaublesMap.containsKey(player.getCommandSenderName())) {
            InventoryBaubles inventory = new InventoryBaubles(player);
            playerBaublesMap.put(player.getCommandSenderName(), inventory);
        }
        return playerBaublesMap.get(player.getCommandSenderName());
    }

    public static void setPlayerKeepBaubles(EntityPlayer player, InventoryBaubles inventory) {
        playerBaublesMap.put(player.getCommandSenderName(), inventory);
    }

    public static void loadPlayerKeepBaubles(EntityPlayer player, NBTTagCompound data) {
        if (TwilightForestMod.areBaublesLoaded) {
            InventoryBaubles keepBaubles = new InventoryBaubles(player);
            keepBaubles.readNBT(data);
            playerBaublesMap.put(player.getCommandSenderName(), keepBaubles);
        }
    }

    public static void savePlayerKeepBaubles(EntityPlayer player, NBTTagCompound data) {
        if (TwilightForestMod.areBaublesLoaded) {
            InventoryBaubles inventory = getPlayerKeepBaubles(player);
            inventory.saveNBT(data);
        }
    }

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

    public static void restoreBaubles(EntityPlayer player, ItemStack[] baublesInventory) {
        if (TwilightForestMod.areBaublesLoaded && baublesInventory != null) {
            InventoryBaubles inventoryBaubles = PlayerHandler.getPlayerBaubles(player);
            for (int i = 0; i < inventoryBaubles.getSizeInventory(); i++) {
                if (baublesInventory[i] != null) inventoryBaubles.setInventorySlotContents(i, baublesInventory[i]);
            }
        }
    }

    public static void playerSaveDo(EntityPlayer player, File directory) {
        if (TwilightForestMod.areBaublesLoaded) PlayerHandler.savePlayerBaubles(
                player,
                TFEventListener.getPlayerFile(TFEventListener.fileName, directory, player.getCommandSenderName()),
                TFEventListener
                        .getPlayerFile(TFEventListener.fileNameBackup, directory, player.getCommandSenderName()));
    }

}
