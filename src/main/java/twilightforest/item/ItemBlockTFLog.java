package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTFLog extends ItemBlockTFMeta {

    public ItemBlockTFLog(Block log) {
        super(log);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage() & 3;
        return super.getUnlocalizedName() + "." + meta;
    }
}
