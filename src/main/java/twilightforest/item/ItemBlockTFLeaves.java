package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTFLeaves extends ItemBlockTFMeta {

    public ItemBlockTFLeaves(Block leaves) {
        super(leaves);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage() & 3;
        return super.getUnlocalizedName() + "." + meta;
    }
}
