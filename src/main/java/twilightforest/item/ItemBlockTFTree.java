package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTFTree extends ItemBlockTFMeta {

    public ItemBlockTFTree(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage() & 3;
        return super.getUnlocalizedName() + "." + meta;
    }
}
