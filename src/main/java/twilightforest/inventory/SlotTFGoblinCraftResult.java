package twilightforest.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import twilightforest.TwilightForestMod;

public class SlotTFGoblinCraftResult extends SlotCrafting {

    protected final EntityPlayer thePlayer;
    protected final IInventory inputSlot;
    protected final InventoryTFGoblinUncrafting uncraftingMatrix;
    protected final InventoryCrafting assemblyMatrix;
    protected final IInventory resultSlot;

    public SlotTFGoblinCraftResult(EntityPlayer player, IInventory input, IInventory uncraftingMatrix,
            IInventory assemblyMatrix, IInventory result, int slotIndex, int x, int y) {
        super(player, assemblyMatrix, result, slotIndex, x, y);
        this.thePlayer = player;
        this.inputSlot = input;
        this.assemblyMatrix = (InventoryCrafting) assemblyMatrix;
        this.resultSlot = result;
        this.uncraftingMatrix = (InventoryTFGoblinUncrafting) uncraftingMatrix;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        if (!super.canTakeStack(player) || !getHasStack()) {
            return false;
        }
        ItemStack input = inputSlot.getStackInSlot(0);
        if (input == null) {
            return true;
        }
        return !TwilightForestMod.disableUncrafting && uncraftingMatrix.numberOfInputItems > 0
                && input.stackSize >= uncraftingMatrix.numberOfInputItems
                && (player.capabilities.isCreativeMode || player.experienceLevel >= uncraftingMatrix.recraftingCost);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack output) {
        ItemStack input = inputSlot.getStackInSlot(0);
        if (input != null) {
            // 1 combined craft consumes one batch, not one batch for each of the 9 ghost slots
            int required = uncraftingMatrix.numberOfInputItems;
            int cost = uncraftingMatrix.recraftingCost;
            if (!player.worldObj.isRemote && !player.capabilities.isCreativeMode && cost > 0) {
                player.addExperienceLevel(-cost);
            }
            for (int i = 0; i < uncraftingMatrix.getSizeInventory(); i++) {
                uncraftingMatrix.setInventorySlotContents(i, null);
            }
            if (required > 0) {
                inputSlot.decrStackSize(0, required);
            }
        }
        super.onPickupFromSlot(player, output);
    }
}
