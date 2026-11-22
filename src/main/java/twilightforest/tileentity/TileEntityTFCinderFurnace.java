package twilightforest.tileentity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.block.BlockTFCinderFurnace;
import twilightforest.block.TFBlocks;

public class TileEntityTFCinderFurnace extends TileEntity implements ISidedInventory {

    private static final int SMELT_LOG_FACTOR = 10;
    private static final int COOK_TIME = 200;
    private static final int SPEED_LOG_FACTOR = 2;
    private static final int CINDERIZE_INTERVAL = 5;
    private static final int SIDE_BOTTOM = 0;
    private static final int INVENTORY_SIZE = 3;
    private static final String NBT_ITEMS = "Items";
    private static final String NBT_BURN_TIME = "BurnTime";
    private static final String NBT_COOK_TIME = "CookTime";
    private static final String NBT_CUSTOM_NAME = "CustomName";
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_FUEL = 1;
    private static final int SLOT_OUTPUT = 2;

    private static final int[] slotsTop = new int[] { SLOT_INPUT };
    private static final int[] slotsBottom = new int[] { SLOT_OUTPUT, SLOT_FUEL };
    private static final int[] slotsSides = new int[] { SLOT_FUEL };

    /** The ItemStacks that hold the items currently being used in the furnace */
    private ItemStack[] furnaceItemStacks = new ItemStack[INVENTORY_SIZE];
    /** The number of ticks that the furnace will keep burning */
    public int furnaceBurnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    public int currentItemBurnTime;
    /** The number of ticks that the current item has been cooking for */
    public int furnaceCookTime;
    private String customName;

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.furnaceItemStacks[slot];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = this.furnaceItemStacks[slot];
        if (stack == null) {
            return null;
        }

        ItemStack removed;
        if (stack.stackSize <= amount) {
            removed = stack;
            this.furnaceItemStacks[slot] = null;
        } else {
            removed = stack.splitStack(amount);
            if (stack.stackSize == 0) {
                this.furnaceItemStacks[slot] = null;
            }
        }

        markDirty();
        return removed;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = this.furnaceItemStacks[slot];
        if (stack != null) {
            this.furnaceItemStacks[slot] = null;
            markDirty();
        }
        return stack;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        this.furnaceItemStacks[slot] = itemStack;
        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.customName : "twilightforest.container.furnace";
    }

    /**
     * Returns if the inventory is named
     */
    @Override
    public boolean hasCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTags) {
        super.readFromNBT(nbtTags);
        NBTTagList nbttaglist = nbtTags.getTagList(NBT_ITEMS, 10);
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte slot = nbttagcompound1.getByte("Slot");

            if (slot >= 0 && slot < this.furnaceItemStacks.length) {
                this.furnaceItemStacks[slot] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.furnaceBurnTime = nbtTags.getShort(NBT_BURN_TIME);
        this.furnaceCookTime = nbtTags.getShort(NBT_COOK_TIME);
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);

        this.customName = nbtTags.hasKey(NBT_CUSTOM_NAME, 8) ? nbtTags.getString(NBT_CUSTOM_NAME) : null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTags) {
        super.writeToNBT(nbtTags);
        nbtTags.setShort(NBT_BURN_TIME, (short) this.furnaceBurnTime);
        nbtTags.setShort(NBT_COOK_TIME, (short) this.furnaceCookTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; ++i) {
            if (this.furnaceItemStacks[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbtTags.setTag(NBT_ITEMS, nbttaglist);

        if (this.hasCustomInventoryName()) {
            nbtTags.setString(NBT_CUSTOM_NAME, this.customName);
        }
    }

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int p_145953_1_) {
        return this.furnaceCookTime * p_145953_1_ / COOK_TIME;
    }

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int p_145955_1_) {
        if (this.currentItemBurnTime == 0) {
            this.currentItemBurnTime = COOK_TIME;
        }

        return this.furnaceBurnTime * p_145955_1_ / this.currentItemBurnTime;
    }

    /**
     * Furnace isBurning
     */
    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    @Override
    public void updateEntity() {
        boolean wasBurning = isBurning();
        boolean inventoryChanged = false;

        if (furnaceBurnTime > 0) {
            --furnaceBurnTime;
        }

        if (!worldObj.isRemote) {
            if (furnaceBurnTime != 0 || furnaceItemStacks[SLOT_FUEL] != null && furnaceItemStacks[SLOT_INPUT] != null) {
                if (furnaceBurnTime == 0 && canSmelt()) {
                    currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks[SLOT_FUEL]);
                    if (furnaceBurnTime > 0) {
                        inventoryChanged = true;
                        ItemStack fuel = furnaceItemStacks[SLOT_FUEL];
                        if (fuel != null && --fuel.stackSize == 0) {
                            furnaceItemStacks[SLOT_FUEL] = fuel.getItem().getContainerItem(fuel);
                        }
                    }
                }

                if (isBurning() && canSmelt()) {
                    int speedMultiplier = getCurrentSpeedMultiplier();
                    // System.out.println("cooking with cinder furnace, speed multiplier = " + speedMultiplier);
                    furnaceCookTime += speedMultiplier;
                    if (furnaceCookTime >= COOK_TIME) {
                        furnaceCookTime = 0;
                        smeltItem();
                        inventoryChanged = true;
                    }
                } else {
                    furnaceCookTime = 0;
                }
            }

            // update block if needed
            if (wasBurning != isBurning()) {
                inventoryChanged = true;
                BlockTFCinderFurnace.updateFurnaceBlockState(isBurning(), worldObj, xCoord, yCoord, zCoord);
            }

            // occasionally cinderize nearby logs
            if (isBurning() && furnaceBurnTime % CINDERIZE_INTERVAL == 0) {
                cinderizeNearbyLog();
            }
        }

        if (inventoryChanged) {
            markDirty();
        }
    }

    /**
     * Turn a nearby log into a cinder log
     */
    private void cinderizeNearbyLog() {
        Random rand = this.getWorldObj().rand;

        int dx = rand.nextInt(2) - rand.nextInt(2);
        int dy = rand.nextInt(2) - rand.nextInt(2);
        int dz = rand.nextInt(2) - rand.nextInt(2);

        if (this.worldObj.blockExists(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz)) {
            Block nearbyBlock = this.getWorldObj().getBlock(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz);

            if (nearbyBlock != TFBlocks.cinderLog && this.isLog(nearbyBlock)) {
                this.getWorldObj().setBlock(
                        this.xCoord + dx,
                        this.yCoord + dy,
                        this.zCoord + dz,
                        TFBlocks.cinderLog,
                        getCinderMetaFor(dx, dy, dz),
                        2);
                // special effect?
                this.getWorldObj().playAuxSFX(2004, this.xCoord + dx, this.yCoord + dy, this.zCoord + dz, 0);
                this.getWorldObj().playAuxSFX(2004, this.xCoord + dx, this.yCoord + dy, this.zCoord + dz, 0);
                this.getWorldObj().playSoundEffect(
                        this.xCoord + dx + 0.5F,
                        this.yCoord + dy + 0.5F,
                        this.zCoord + dz + 0.5F,
                        "fire.fire",
                        1.0F,
                        1.0F);
            }
        }
    }

    /**
     * What meta should we set the log block with the specified offset to?
     */
    private int getCinderMetaFor(int dx, int dy, int dz) {
        if (dz == 0 && dx != 0) {
            return dy == 0 ? 4 : 8;
        } else if (dx == 0 && dz != 0) {
            return dy == 0 ? 8 : 4;
        } else if (dx == 0 && dz == 0) {
            return 0;
        } else {
            return dy == 0 ? 0 : 12;
        }

    }

    /**
     * Check the ore dictionary to see if a nearby block is a wood log block
     */
    private boolean isLog(Block nearbyBlock) {
        int woodLogId = OreDictionary.getOreID("logWood");
        int[] oreIDs = OreDictionary.getOreIDs(new ItemStack(nearbyBlock));
        for (int id : oreIDs) {
            if (id == woodLogId) {
                return true;
            }
        }

        return false;
    }

    /**
     * What is the current speed multiplier, as an int.
     */
    private int getCurrentSpeedMultiplier() {
        return getCurrentMultiplier(SPEED_LOG_FACTOR);
    }

    /**
     * Returns a number that is based on the number of nearby logs divided by the factor given.
     */
    private int getCurrentMultiplier(int factor) {
        int logs = this.countNearbyLogs();

        if (logs < factor) {
            return 1;
        } else {
            return (logs / factor) + (this.worldObj.rand.nextInt(factor) >= (logs % factor) ? 0 : 1);
        }
    }

    /**
     * Search around the block and return how many (out of a possible 26) of the blocks nearby are cinder log blocks
     */
    private int countNearbyLogs() {
        int count = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (this.worldObj.blockExists(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz)
                            && this.getWorldObj().getBlock(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz)
                                    == TFBlocks.cinderLog) {
                        count++;
                    }
                }
            }
        }

        // System.out.println("cooking with cinder furnace, log factor = " + count);

        return count;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt() {
        if (this.furnaceItemStacks[SLOT_INPUT] == null) {
            return false;
        } else {
            ItemStack outputStack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[SLOT_INPUT]);
            if (outputStack == null) {
                return false;
            }
            if (this.furnaceItemStacks[SLOT_OUTPUT] == null) {
                // a modded recipe may return more than 1 stack worth of items
                int resultCount = getMaxOutputStacks(furnaceItemStacks[SLOT_INPUT], outputStack);
                return resultCount <= getInventoryStackLimit() && resultCount <= outputStack.getMaxStackSize();
            }
            if (!this.furnaceItemStacks[SLOT_OUTPUT].isItemEqual(outputStack)) {
                return false;
            }

            int resultStackSize = furnaceItemStacks[SLOT_OUTPUT].stackSize
                    + this.getMaxOutputStacks(this.furnaceItemStacks[SLOT_INPUT], outputStack);

            return resultStackSize <= getInventoryStackLimit()
                    && resultStackSize <= this.furnaceItemStacks[SLOT_OUTPUT].getMaxStackSize();
        }
    }

    /**
     * Return the max number of items in the output stack, given our current multiplier
     */
    public int getMaxOutputStacks(ItemStack input, ItemStack output) {
        if (this.canMultiply(input, output)) {
            return output.stackSize * this.getCurrentMaxSmeltMultiplier();
        } else {
            return output.stackSize;
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack outputStack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[SLOT_INPUT]);

            if (this.canMultiply(this.furnaceItemStacks[SLOT_INPUT], outputStack)) {

                // multiply number of results by our current smelt factor
                int smeltMultiplier = this.getCurrentSmeltMultiplier();

                if (smeltMultiplier > 1) {
                    // copy output stack
                    outputStack = outputStack.copy();

                    // we shouldn't run into max stack size problems, since we've already checked in canSmelt()
                    outputStack.stackSize *= smeltMultiplier;
                }

            }

            if (this.furnaceItemStacks[SLOT_OUTPUT] == null) {
                this.furnaceItemStacks[SLOT_OUTPUT] = outputStack.copy();
            } else if (this.furnaceItemStacks[SLOT_OUTPUT].getItem() == outputStack.getItem()) {
                this.furnaceItemStacks[SLOT_OUTPUT].stackSize += outputStack.stackSize; // Forge BugFix: Results may
                // have multiple items
            }

            --this.furnaceItemStacks[SLOT_INPUT].stackSize;

            if (this.furnaceItemStacks[SLOT_INPUT].stackSize <= 0) {
                this.furnaceItemStacks[SLOT_INPUT] = null;
            }
        }
    }

    /**
     * Can we multiply the output? We currently multiply logs->charcoal, ore->ingots, and food
     */
    public boolean canMultiply(ItemStack input, ItemStack output) {

        // check the input ore ID for ores
        int[] oreIDs = OreDictionary.getOreIDs(input);
        for (int id : oreIDs) {
            // does the input have an oredictionary result that starts with "ore"?
            if (OreDictionary.getOreName(id).startsWith("ore")) {
                // System.out.println("cinder furnace cook result, ore found, name is " +
                // OreDictionary.getOreName(id));

                return true;
            } else if (id == OreDictionary.getOreID("logWood")) {
                // System.out.println("cinder furnace cook result, log found");
                return true;
            }
        }

        // does the in

        return false;
    }

    /**
     * What is the current speed multiplier, as an int.
     */
    private int getCurrentSmeltMultiplier() {
        return getCurrentMultiplier(SMELT_LOG_FACTOR);
    }

    /**
     * What is the current speed multiplier, as an int.
     */
    private int getCurrentMaxSmeltMultiplier() {
        // the non random multiplier never drops below 1, even with no nearby logs
        return Math.max(1, (countNearbyLogs() + SMELT_LOG_FACTOR - 1) / SMELT_LOG_FACTOR);
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't
     * fuel
     */
    public static int getItemBurnTime(ItemStack p_145952_0_) {
        if (p_145952_0_ == null) {
            return 0;
        } else {
            Item item = p_145952_0_.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab) {
                    return 150;
                }

                if (block.getMaterial() == Material.wood) {
                    return 300;
                }

                if (block == Blocks.coal_block) {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(p_145952_0_);
        }
    }

    public static boolean isItemFuel(ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;

    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return slot != SLOT_OUTPUT && (slot != SLOT_FUEL || TileEntityFurnace.isItemFuel(itemStack));
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return p_94128_1_ == 0 ? slotsBottom : (p_94128_1_ == 1 ? slotsTop : slotsSides);
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return this.isItemValidForSlot(slot, itemStack);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return side != SIDE_BOTTOM || slot != SLOT_FUEL || itemStack != null && itemStack.getItem() == Items.bucket;
    }

}
