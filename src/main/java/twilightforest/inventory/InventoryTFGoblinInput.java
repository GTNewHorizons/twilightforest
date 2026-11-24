package twilightforest.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryTFGoblinInput implements IInventory {

    private ItemStack input;
    private final ContainerTFUncrafting container;

    public InventoryTFGoblinInput(ContainerTFUncrafting container) {
        this.container = container;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? input : null;
    }

    @Override
    public String getInventoryName() {
        return "Input";
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot != 0 || input == null || amount <= 0) {
            return null;
        }
        ItemStack removed;
        if (input.stackSize <= amount) {
            removed = input;
            input = null;
        } else {
            removed = input.splitStack(amount);
            if (input.stackSize <= 0) {
                input = null;
            }
        }
        markDirty();
        return removed;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot != 0) {
            return null;
        }
        ItemStack removed = input;
        input = null;
        return removed;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 0) {
            input = stack;
            if (input != null && input.stackSize > getInventoryStackLimit()) {
                input.stackSize = getInventoryStackLimit();
            }
            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        container.onCraftMatrixChanged(this);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
}
