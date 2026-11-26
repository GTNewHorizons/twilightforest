package twilightforest.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import twilightforest.block.TFBlocks;

public class ContainerTFUncrafting extends Container {

    private static final int INPUT_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int UNCRAFTING_START = 2;
    private static final int ASSEMBLY_START = 11;
    private static final int PLAYER_INVENTORY_START = 20;
    private static final int HOTBAR_START = 47;
    private static final int SLOT_COUNT = 56;
    private static final int MATRIX_SIZE = 9;

    public final InventoryTFGoblinUncrafting uncraftingMatrix = new InventoryTFGoblinUncrafting(this);
    public final InventoryCrafting assemblyMatrix = new InventoryCrafting(this, 3, 3);
    public final InventoryCrafting combineMatrix = new InventoryCrafting(this, 3, 3);
    public final IInventory tinkerInput = new InventoryTFGoblinInput(this);
    public final IInventory tinkerResult = new InventoryCraftResult();

    private final World worldObj;
    private final int x;
    private final int y;
    private final int z;

    public ContainerTFUncrafting(InventoryPlayer inventory, World world, int x, int y, int z) {
        this.worldObj = world;
        this.x = x;
        this.y = y;
        this.z = z;

        addSlotToContainer(new Slot(tinkerInput, 0, 13, 35));
        addSlotToContainer(
                new SlotTFGoblinCraftResult(
                        inventory.player,
                        tinkerInput,
                        uncraftingMatrix,
                        assemblyMatrix,
                        tinkerResult,
                        0,
                        147,
                        35));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int index = column + row * 3;
                addSlotToContainer(
                        new SlotTFGoblinUncrafting(
                                inventory.player,
                                tinkerInput,
                                uncraftingMatrix,
                                assemblyMatrix,
                                index,
                                300000 + column * 18,
                                17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int index = column + row * 3;
                addSlotToContainer(
                        new SlotTFGoblinAssembly(
                                inventory.player,
                                tinkerInput,
                                assemblyMatrix,
                                uncraftingMatrix,
                                index,
                                62 + column * 18,
                                17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlotToContainer(new Slot(inventory, column, 8 + column * 18, 142));
        }

        onCraftMatrixChanged(assemblyMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        if (inventory == combineMatrix) {
            return;
        }
        if (inventory == tinkerInput) {
            rebuildUncraftingMatrix();
        }
        if (inventory == tinkerInput || inventory == assemblyMatrix) {
            rebuildCraftingResult();
        }
    }

    private void rebuildUncraftingMatrix() {
        ItemStack input = tinkerInput.getStackInSlot(0);
        IRecipe recipe = getRecipeFor(input);
        clearUncraftingMatrix();

        if (recipe == null) {
            return;
        }

        int width = getRecipeWidth(recipe);
        int height = getRecipeHeight(recipe);
        ItemStack[] ingredients = getRecipeItems(recipe);
        ItemStack output = recipe.getRecipeOutput();
        if (width <= 0 || width > 3
                || height <= 0
                || height > 3
                || ingredients == null
                || ingredients.length < width * height
                || output == null
                || output.stackSize <= 0) {
            return;
        }

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                ItemStack ingredient = ItemStack.copyItemStack(ingredients[column + row * width]);
                if (ingredient != null) {
                    ingredient.stackSize = 1;
                    if (ingredient.getItemDamageForDisplay() == -1
                            || ingredient.getItemDamageForDisplay() == Short.MAX_VALUE) {
                        ingredient.setItemDamage(0);
                    }
                }
                uncraftingMatrix.setInventorySlotContents(column + row * 3, ingredient);
            }
        }

        if (input.isItemDamaged()) {
            int damagedParts = countDamagedParts(input);
            for (int i = 0; i < MATRIX_SIZE && damagedParts > 0; i++) {
                ItemStack ingredient = uncraftingMatrix.getStackInSlot(i);
                if (isDamageableComponent(ingredient)) {
                    ingredient.stackSize = 0;
                    damagedParts--;
                }
            }
        }

        for (int i = 0; i < MATRIX_SIZE; i++) {
            ItemStack ingredient = uncraftingMatrix.getStackInSlot(i);
            if (isIngredientProblematic(ingredient)) {
                ingredient.stackSize = 0;
            }
        }

        uncraftingMatrix.numberOfInputItems = output.stackSize;
        uncraftingMatrix.uncraftingCost = calculateUncraftingCost();
    }

    private void clearUncraftingMatrix() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            uncraftingMatrix.setInventorySlotContents(i, null);
        }
        uncraftingMatrix.numberOfInputItems = 0;
        uncraftingMatrix.uncraftingCost = 0;
        uncraftingMatrix.recraftingCost = 0;
    }

    private void rebuildCraftingResult() {
        tinkerResult.setInventorySlotContents(0, null);
        uncraftingMatrix.recraftingCost = 0;
        uncraftingMatrix.recraftingResult = false;
        uncraftingMatrix.uncraftingCost = calculateUncraftingCost();

        ItemStack assemblyResult = CraftingManager.getInstance().findMatchingRecipe(assemblyMatrix, worldObj);
        if (assemblyResult != null) {
            tinkerResult.setInventorySlotContents(0, assemblyResult);
            return;
        }

        ItemStack input = tinkerInput.getStackInSlot(0);
        if (input == null || isMatrixEmpty(assemblyMatrix) || uncraftingMatrix.numberOfInputItems <= 0) {
            return;
        }

        for (int i = 0; i < MATRIX_SIZE; i++) {
            ItemStack supplied = assemblyMatrix.getStackInSlot(i);
            ItemStack recovered = uncraftingMatrix.getStackInSlot(i);
            combineMatrix.setInventorySlotContents(
                    i,
                    supplied != null ? supplied : recovered != null && recovered.stackSize > 0 ? recovered : null);
        }

        ItemStack result = CraftingManager.getInstance().findMatchingRecipe(combineMatrix, worldObj);
        if (result == null || !isValidMatchForInput(input, result)) {
            return;
        }

        mergeRecraftingEnchantments(input, result);
        tinkerResult.setInventorySlotContents(0, result);
        uncraftingMatrix.uncraftingCost = 0;
        uncraftingMatrix.recraftingResult = true;
        uncraftingMatrix.recraftingCost = calculateRecraftingCost();
        if (uncraftingMatrix.recraftingCost > 0 && !result.hasDisplayName()) {
            result.setRepairCost(input.getRepairCost() + 2);
        }
    }

    private void mergeRecraftingEnchantments(ItemStack input, ItemStack result) {
        Map<Integer, Integer> enchantments = new HashMap<>(EnchantmentHelper.getEnchantments(result));
        Map<Integer, Integer> original = EnchantmentHelper.getEnchantments(input);
        for (Map.Entry<Integer, Integer> entry : original.entrySet()) {
            int id = entry.getKey();
            if (id < 0 || id >= Enchantment.enchantmentsList.length) {
                continue;
            }
            Enchantment enchantment = Enchantment.enchantmentsList[id];
            if (enchantment != null && enchantment.canApply(result)) {
                enchantments.merge(id, entry.getValue(), Math::max);
            }
        }

        NBTTagCompound tags = input.getTagCompound();
        if (tags != null) {
            NBTTagCompound copy = (NBTTagCompound) tags.copy();
            copy.removeTag("ench");
            result.setTagCompound(copy);
        }
        if (!enchantments.isEmpty()) {
            EnchantmentHelper.setEnchantments(enchantments, result);
        }
    }

    protected boolean isIngredientProblematic(ItemStack ingredient) {
        return ingredient != null && (ingredient.getItem().hasContainerItem(ingredient)
                || ingredient.getUnlocalizedName().contains("itemMatter"));
    }

    public IRecipe getRecipeFor(ItemStack input) {
        if (input == null) {
            return null;
        }
        for (Object candidate : CraftingManager.getInstance().getRecipeList()) {
            if (!(candidate instanceof IRecipe recipe)
                    || !(recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe)) {
                continue;
            }
            ItemStack output = recipe.getRecipeOutput();
            if (output != null && output.stackSize > 0
                    && output.getItem() == input.getItem()
                    && input.stackSize >= output.stackSize
                    && (!output.getHasSubtypes() || output.getItemDamage() == input.getItemDamage())) {
                return recipe;
            }
        }
        return null;
    }

    public boolean isValidMatchForInput(ItemStack input, ItemStack result) {
        if (input == null || result == null) {
            return false;
        }
        if (input.getItem() instanceof ItemArmor inputArmor && result.getItem() instanceof ItemArmor resultArmor) {
            return inputArmor.armorType == resultArmor.armorType;
        }
        return (input.getItem() instanceof ItemPickaxe && result.getItem() instanceof ItemPickaxe)
                || (input.getItem() instanceof ItemAxe && result.getItem() instanceof ItemAxe)
                || (input.getItem() instanceof ItemSpade && result.getItem() instanceof ItemSpade)
                || (input.getItem() instanceof ItemHoe && result.getItem() instanceof ItemHoe)
                || (input.getItem() instanceof ItemSword && result.getItem() instanceof ItemSword)
                || (input.getItem() instanceof ItemBow && result.getItem() instanceof ItemBow);
    }

    public int getUncraftingCost() {
        return uncraftingMatrix.uncraftingCost;
    }

    public int getRecraftingCost() {
        return uncraftingMatrix.recraftingCost;
    }

    public int calculateUncraftingCost() {
        return isMatrixEmpty(assemblyMatrix) ? countDamageableParts(uncraftingMatrix) : 0;
    }

    public int calculateRecraftingCost() {
        ItemStack input = tinkerInput.getStackInSlot(0);
        ItemStack result = tinkerResult.getStackInSlot(0);
        if (!uncraftingMatrix.recraftingResult || input == null || !input.isItemEnchanted() || result == null) {
            return 0;
        }
        int cost = input.getRepairCost() + countTotalEnchantmentCost(input);
        cost += (1 + countDamagedParts(input)) * EnchantmentHelper.getEnchantments(result).size();
        cost += input.getItem().getItemEnchantability() - result.getItem().getItemEnchantability();
        return Math.max(1, cost);
    }

    public int countHighestEnchantmentCost(ItemStack itemStack) {
        int highest = 0;
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, itemStack);
                highest = Math.max(highest, getWeightModifier(enchantment) * level);
            }
        }
        return highest;
    }

    public int countTotalEnchantmentCost(ItemStack itemStack) {
        int count = 0;
        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, itemStack);
                if (level > 0) {
                    count += getWeightModifier(enchantment) * level + 1;
                }
            }
        }
        return count;
    }

    public int getWeightModifier(Enchantment enchantment) {
        return switch (enchantment.getWeight()) {
            case 1 -> 8;
            case 2 -> 4;
            case 3, 4, 5 -> 2;
            default -> 1;
        };
    }

    @Override
    public ItemStack slotClick(int slotNum, int mouseButton, int mode, EntityPlayer player) {
        if (slotNum < 0 && slotNum != -999 || slotNum >= inventorySlots.size()) {
            return null;
        }
        if (slotNum >= ASSEMBLY_START && slotNum < PLAYER_INVENTORY_START
                && player.inventory.getItemStack() == null
                && !inventorySlots.get(slotNum).getHasStack()
                && isMatrixEmpty(assemblyMatrix)) {
            slotNum -= MATRIX_SIZE;
        }
        if (slotNum >= 0) {
            Slot slot = inventorySlots.get(slotNum);
            if ((slotNum == RESULT_SLOT || slotNum >= UNCRAFTING_START && slotNum < ASSEMBLY_START)
                    && !slot.canTakeStack(player)) {
                return null;
            }
        }
        return super.slotClick(slotNum, mouseButton, mode, player);
    }

    @Override
    protected void retrySlotClick(int slotNum, int mouseButton, boolean modifier, EntityPlayer player) {
        if (slotNum < 0 || slotNum >= inventorySlots.size()) {
            return;
        }
        if (slotNum >= UNCRAFTING_START && slotNum < ASSEMBLY_START) {
            slotNum += MATRIX_SIZE;
        }
        slotClick(slotNum, mouseButton, 1, player);
    }

    private boolean isMatrixEmpty(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isDamageableComponent(ItemStack stack) {
        return stack != null && stack.stackSize > 0 && stack.getItem() != Items.stick;
    }

    public int countDamageableParts(IInventory inventory) {
        int count = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (isDamageableComponent(inventory.getStackInSlot(i))) {
                count++;
            }
        }
        return count;
    }

    public int countDamagedParts(ItemStack input) {
        if (input == null || input.getMaxDamage() <= 0) {
            return 0;
        }
        int total = Math.max(4, countDamageableParts(uncraftingMatrix));
        double ratio = (double) input.getItemDamage() / input.getMaxDamage();
        return (int) Math.ceil(total * Math.max(0.0, Math.min(1.0, ratio)));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum) {
        if (slotNum < 0 || slotNum >= inventorySlots.size()) {
            return null;
        }
        Slot slot = inventorySlots.get(slotNum);
        if (!slot.getHasStack() || !slot.canTakeStack(player)) {
            return null;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        boolean moved;

        if (slotNum == INPUT_SLOT || slotNum == RESULT_SLOT) {
            moved = mergeItemStack(stack, PLAYER_INVENTORY_START, SLOT_COUNT, true);
        } else if (slotNum >= PLAYER_INVENTORY_START && slotNum < HOTBAR_START) {
            moved = mergeItemStack(stack, HOTBAR_START, SLOT_COUNT, false);
        } else if (slotNum >= HOTBAR_START && slotNum < SLOT_COUNT) {
            moved = mergeItemStack(stack, PLAYER_INVENTORY_START, HOTBAR_START, false);
        } else {
            moved = mergeItemStack(stack, PLAYER_INVENTORY_START, SLOT_COUNT, false);
        }
        if (!moved || stack.stackSize == original.stackSize) {
            return null;
        }
        if (slotNum == RESULT_SLOT) {
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

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!worldObj.isRemote) {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                ItemStack stack = assemblyMatrix.getStackInSlotOnClosing(i);
                if (stack != null) {
                    player.dropPlayerItemWithRandomChoice(stack, false);
                }
            }
            ItemStack input = tinkerInput.getStackInSlotOnClosing(0);
            if (input != null) {
                player.dropPlayerItemWithRandomChoice(input, false);
            }
        }
    }

    public ItemStack[] getRecipeItems(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes shaped) {
            return getRecipeItemsShaped(shaped);
        }
        if (recipe instanceof ShapedOreRecipe shaped) {
            return getRecipeItemsOre(shaped);
        }
        return null;
    }

    public ItemStack[] getRecipeItemsShaped(ShapedRecipes shaped) {
        return shaped.recipeItems;
    }

    public ItemStack[] getRecipeItemsOre(ShapedOreRecipe shaped) {
        Object[] inputs = shaped.getInput();
        ItemStack[] items = new ItemStack[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] instanceof ItemStack stack) {
                items[i] = stack;
            } else if (inputs[i] instanceof List<?>alternatives) {
                for (Object candidate : alternatives) {
                    if (candidate instanceof ItemStack stack) {
                        items[i] = stack;
                        break;
                    }
                }
            }
        }
        return items;
    }

    public int getRecipeWidth(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes shaped) {
            return getRecipeWidthShaped(shaped);
        }
        if (recipe instanceof ShapedOreRecipe shaped) {
            return getRecipeWidthOre(shaped);
        }
        return -1;
    }

    public int getRecipeWidthShaped(ShapedRecipes shaped) {
        return shaped.recipeWidth;
    }

    public int getRecipeWidthOre(ShapedOreRecipe shaped) {
        try {
            return ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 4);
        } catch (IllegalArgumentException | SecurityException | ClassCastException exception) {
            return 0;
        }
    }

    public int getRecipeHeight(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes shaped) {
            return getRecipeHeightShaped(shaped);
        }
        if (recipe instanceof ShapedOreRecipe shaped) {
            return getRecipeHeightOre(shaped);
        }
        return -1;
    }

    public int getRecipeHeightShaped(ShapedRecipes shaped) {
        return shaped.recipeHeight;
    }

    public int getRecipeHeightOre(ShapedOreRecipe shaped) {
        try {
            return ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 5);
        } catch (IllegalArgumentException | SecurityException | ClassCastException exception) {
            return 0;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return worldObj.getBlock(x, y, z) == TFBlocks.uncraftingTable
                && player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
    }
}
