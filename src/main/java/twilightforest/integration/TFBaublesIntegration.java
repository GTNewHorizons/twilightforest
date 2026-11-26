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

    // keep the public map and username keys compatible with existing event handlers
    public static HashMap<String, InventoryBaubles> playerBaublesMap = new HashMap<>();

    private TFBaublesIntegration() {}

    public static void clearPlayerMap(EntityPlayer player) {
        if (TwilightForestMod.areBaublesLoaded) {
            playerBaublesMap.remove(player.getCommandSenderName());
        }
    }

    public static InventoryBaubles getPlayerKeepBaubles(EntityPlayer player) {
        return playerBaublesMap.computeIfAbsent(player.getCommandSenderName(), name -> new InventoryBaubles(player));
    }

    public static void setPlayerKeepBaubles(EntityPlayer player, InventoryBaubles inventory) {
        playerBaublesMap.put(player.getCommandSenderName(), inventory);
    }

    public static void loadPlayerKeepBaubles(EntityPlayer player, NBTTagCompound data) {
        if (!TwilightForestMod.areBaublesLoaded) {
            return;
        }

        InventoryBaubles keepBaubles = new InventoryBaubles(player);
        keepBaubles.readNBT(data);
        setPlayerKeepBaubles(player, keepBaubles);
    }

    public static void savePlayerKeepBaubles(EntityPlayer player, NBTTagCompound data) {
        if (TwilightForestMod.areBaublesLoaded) {
            getPlayerKeepBaubles(player).saveNBT(data);
        }
    }

    public static Boolean consumeBaublesItem(EntityPlayer player, Item item) {
        if (!TwilightForestMod.areBaublesLoaded || item == null) {
            return false;
        }

        InventoryBaubles inventory = PlayerHandler.getPlayerBaubles(player);
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0 || stack.getItem() != item) {
                continue;
            }

            if (--stack.stackSize == 0) {
                inventory.setInventorySlotContents(slot, null);
            } else {
                inventory.markDirty();
            }
            return true;
        }
        return false;
    }

    /**
     * Move the full baubles inventory to the keep pile
     */
    public static ItemStack[] keepAllBaubles(EntityPlayer player) {
        if (!TwilightForestMod.areBaublesLoaded) {
            return null;
        }

        InventoryBaubles inventory = PlayerHandler.getPlayerBaubles(player);
        ItemStack[] retained = new ItemStack[inventory.getSizeInventory()];
        for (int slot = 0; slot < retained.length; slot++) {
            retained[slot] = ItemStack.copyItemStack(inventory.getStackInSlot(slot));
            inventory.setInventorySlotContents(slot, null);
        }
        return retained;
    }

    public static void restoreBaubles(EntityPlayer player, ItemStack[] baublesInventory) {
        if (!TwilightForestMod.areBaublesLoaded || baublesInventory == null || player.worldObj.isRemote) {
            return;
        }

        InventoryBaubles inventory = PlayerHandler.getPlayerBaubles(player);
        for (int slot = 0; slot < baublesInventory.length; slot++) {
            ItemStack saved = baublesInventory[slot];
            if (saved == null || saved.stackSize <= 0) {
                continue;
            }

            if (slot < inventory.getSizeInventory() && inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, saved.copy());
                baublesInventory[slot] = null;
            } else {
                // never overwrite an equipped bauble or discard an item
                int emptySlot = player.inventory.getFirstEmptyStack();
                if (emptySlot < 0) {
                    continue;
                }

                int amount = Math.min(
                        saved.stackSize,
                        Math.min(saved.getMaxStackSize(), player.inventory.getInventoryStackLimit()));
                ItemStack returned = saved.copy();
                returned.stackSize = amount;
                player.inventory.setInventorySlotContents(emptySlot, returned);
                player.inventory.markDirty();
                saved.stackSize -= amount;
                if (saved.stackSize == 0) {
                    baublesInventory[slot] = null;
                }
            }
        }
    }

    public static void playerSaveDo(EntityPlayer player, File directory) {
        if (!TwilightForestMod.areBaublesLoaded) {
            return;
        }

        String playerName = player.getCommandSenderName();
        PlayerHandler.savePlayerBaubles(
                player,
                TFEventListener.getPlayerFile(TFEventListener.fileName, directory, playerName),
                TFEventListener.getPlayerFile(TFEventListener.fileNameBackup, directory, playerName));
    }
}
