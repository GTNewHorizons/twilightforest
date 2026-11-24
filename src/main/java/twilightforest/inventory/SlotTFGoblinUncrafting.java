package twilightforest.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import twilightforest.TwilightForestMod;

public class SlotTFGoblinUncrafting extends Slot {

    protected final EntityPlayer thePlayer;
    protected final IInventory inputSlot;
    protected final InventoryTFGoblinUncrafting uncraftingMatrix;
    protected final IInventory assemblyMatrix;

    public SlotTFGoblinUncrafting(EntityPlayer player, IInventory inputSlot,
            InventoryTFGoblinUncrafting uncraftingMatrix, IInventory assemblyMatrix, int slotNum, int x, int y) {
        super(uncraftingMatrix, slotNum, x, y);
        this.thePlayer = player;
        this.inputSlot = inputSlot;
        this.uncraftingMatrix = uncraftingMatrix;
        this.assemblyMatrix = assemblyMatrix;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        ItemStack available = getStack();
        ItemStack input = inputSlot.getStackInSlot(0);
        if (TwilightForestMod.disableUncrafting || available == null
                || available.stackSize <= 0
                || input == null
                || uncraftingMatrix.numberOfInputItems <= 0
                || input.stackSize < uncraftingMatrix.numberOfInputItems) {
            return false;
        }
        for (int i = 0; i < assemblyMatrix.getSizeInventory(); i++) {
            if (assemblyMatrix.getStackInSlot(i) != null) {
                return false;
            }
        }
        return player.capabilities.isCreativeMode || player.experienceLevel >= uncraftingMatrix.uncraftingCost;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        super.onPickupFromSlot(player, stack);
        if (!player.worldObj.isRemote && !player.capabilities.isCreativeMode && uncraftingMatrix.uncraftingCost > 0) {
            player.addExperienceLevel(-uncraftingMatrix.uncraftingCost);
        }

        // copy the remaining recovered components before changing the input (which rebuilds the preview)
        for (int i = 0; i < uncraftingMatrix.getSizeInventory(); i++) {
            ItemStack remaining = uncraftingMatrix.getStackInSlot(i);
            if (remaining != null && remaining.stackSize > 0) {
                assemblyMatrix.setInventorySlotContents(i, remaining.copy());
            }
        }
        inputSlot.decrStackSize(0, uncraftingMatrix.numberOfInputItems);
    }
}
