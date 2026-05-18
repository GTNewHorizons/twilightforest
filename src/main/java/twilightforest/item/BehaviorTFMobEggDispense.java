package twilightforest.item;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class BehaviorTFMobEggDispense extends BehaviorDefaultDispenseItem {

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
        EnumFacing facing = EnumFacing.getFront(blockSource.getBlockMetadata());
        double x = blockSource.getX() + (double) facing.getFrontOffsetX();
        double y = blockSource.getY() + 0.2D;
        double z = blockSource.getZ() + (double) facing.getFrontOffsetZ();
        ItemTFSpawnEgg.spawnCreature(blockSource.getWorld(), itemStack.getItemDamage(), x, y, z);
        itemStack.splitStack(1);
        return itemStack;
    }
}
