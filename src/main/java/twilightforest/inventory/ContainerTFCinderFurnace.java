package twilightforest.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.tileentity.TileEntityTFCinderFurnace;

public class ContainerTFCinderFurnace extends Container {

    private static final int OUTPUT_SLOT = 2;
    private static final int INVENTORY_START = 3;
    private static final int HOTBAR_START = 30;
    private static final int SLOT_COUNT = 39;

    private final TileEntityTFCinderFurnace tileFurnace;
    private int lastCookTime;
    private int lastBurnTime;
    private int lastItemBurnTime;

    public ContainerTFCinderFurnace(InventoryPlayer inventory, World world, int x, int y, int z) {
        this(inventory, (TileEntityTFCinderFurnace) world.getTileEntity(x, y, z));
    }

    public ContainerTFCinderFurnace(InventoryPlayer inventory, TileEntityTFCinderFurnace furnace) {
        this.tileFurnace = furnace;
        addSlotToContainer(new Slot(furnace, 0, 56, 17));
        addSlotToContainer(new Slot(furnace, 1, 56, 53));
        addSlotToContainer(new SlotFurnace(inventory.player, furnace, OUTPUT_SLOT, 116, 35));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        super.addCraftingToCrafters(crafter);
        crafter.sendProgressBarUpdate(this, 0, tileFurnace.furnaceCookTime);
        crafter.sendProgressBarUpdate(this, 1, tileFurnace.furnaceBurnTime);
        crafter.sendProgressBarUpdate(this, 2, tileFurnace.currentItemBurnTime);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (ICrafting crafter : crafters) {
            if (lastCookTime != tileFurnace.furnaceCookTime) {
                crafter.sendProgressBarUpdate(this, 0, tileFurnace.furnaceCookTime);
            }
            if (lastBurnTime != tileFurnace.furnaceBurnTime) {
                crafter.sendProgressBarUpdate(this, 1, tileFurnace.furnaceBurnTime);
            }
            if (lastItemBurnTime != tileFurnace.currentItemBurnTime) {
                crafter.sendProgressBarUpdate(this, 2, tileFurnace.currentItemBurnTime);
            }
        }
        lastCookTime = tileFurnace.furnaceCookTime;
        lastBurnTime = tileFurnace.furnaceBurnTime;
        lastItemBurnTime = tileFurnace.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0 -> tileFurnace.furnaceCookTime = value;
            case 1 -> tileFurnace.furnaceBurnTime = value;
            case 2 -> tileFurnace.currentItemBurnTime = value;
            default -> {}
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileFurnace != null && tileFurnace.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= inventorySlots.size()) {
            return null;
        }
        Slot slot = inventorySlots.get(slotIndex);
        if (!slot.getHasStack() || !slot.canTakeStack(player)) {
            return null;
        }

        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        boolean moved;
        if (slotIndex == OUTPUT_SLOT) {
            moved = mergeItemStack(stack, INVENTORY_START, SLOT_COUNT, true);
        } else if (slotIndex >= INVENTORY_START) {
            if (FurnaceRecipes.smelting().getSmeltingResult(stack) != null) {
                moved = mergeItemStack(stack, 0, 1, false);
            } else if (TileEntityFurnace.isItemFuel(stack)) {
                moved = mergeItemStack(stack, 1, 2, false);
            } else if (slotIndex < HOTBAR_START) {
                moved = mergeItemStack(stack, HOTBAR_START, SLOT_COUNT, false);
            } else {
                moved = mergeItemStack(stack, INVENTORY_START, HOTBAR_START, false);
            }
        } else {
            moved = mergeItemStack(stack, INVENTORY_START, SLOT_COUNT, false);
        }
        if (!moved || stack.stackSize == original.stackSize) {
            return null;
        }
        if (slotIndex == OUTPUT_SLOT) {
            slot.onSlotChange(stack, original);
        }
        if (stack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
