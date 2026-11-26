package twilightforest.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import twilightforest.TwilightForestMod;

public class InventoryTFGoblinUncrafting implements IInventory {

    private final ItemStack[] contents = new ItemStack[9];
    public int numberOfInputItems;
    public int uncraftingCost;
    public int recraftingCost;

    public InventoryTFGoblinUncrafting(ContainerTFUncrafting container) {}

    @Override
    public int getSizeInventory() {
        return contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < contents.length ? contents[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null || amount <= 0) {
            return null;
        }
        ItemStack removed;
        if (stack.stackSize <= amount) {
            removed = stack;
            contents[slot] = null;
        } else {
            removed = stack.splitStack(amount);
            if (stack.stackSize <= 0) {
                contents[slot] = null;
            }
        }
        markDirty();
        return removed;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            contents[slot] = null;
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot < 0 || slot >= contents.length) {
            return;
        }
        contents[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "twilightforest.goblincrafting";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return !TwilightForestMod.disableUncrafting;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
}
