package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.compat.Mods;

public class ItemBlockTFTree extends ItemBlockTFMeta {

    public ItemBlockTFTree(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage() & 3;
        return super.getUnlocalizedName() + "." + meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        if ((stack.getItemDamage() & 3) == 0) {
            list.add(StatCollector.translateToLocal("tile.TFMagicLogSpecial.0.tooltip.redstone"));
            if (Mods.gregtech_nh.isLoaded()) {
                list.add(StatCollector.translateToLocal("tile.TFMagicLogSpecial.0.tooltip.softMallet"));
            }
        }
    }
}
