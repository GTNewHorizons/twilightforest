package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import twilightforest.block.BlockTFNagastone;
import twilightforest.block.BlockTFNagastone2;
import twilightforest.block.TFBlocks;

public class ItemBlockTFNagastone extends ItemBlock {

    public ItemBlockTFNagastone(Block block) {
        super(block);
        if (this.field_150939_a instanceof BlockTFNagastone) setHasSubtypes(true);
        else setHasSubtypes(false);
        setMaxDamage(0);
    }

    @Override
    public IIcon getIconFromDamage(int i) {
        return TFBlocks.oldNagastone.getIcon(2, i);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        if (this.field_150939_a instanceof BlockTFNagastone2)
            return "tile.TFNagastone." + (((BlockTFNagastone2) this.field_150939_a).isHead ? 0 : 4);
        else return (new StringBuilder()).append(super.getUnlocalizedName()).append(".")
                .append(itemstack.getItemDamage()).toString();
    }

    @Override
    public int getMetadata(int i) {
        return i;
    }

    /**
     * Called to actually place the block, after the location is determined and all permission checks have been made.
     *
     * @param stack  The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side   The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        Block blockToPlace = field_150939_a;
        if (blockToPlace instanceof BlockTFNagastone) {
            if (metadata < 4) blockToPlace = TFBlocks.nagastoneHead;
            else blockToPlace = TFBlocks.nagastoneBody;
        }

        if (!world.setBlock(x, y, z, blockToPlace, 0, 3)) {
            return false;
        }

        if (world.getBlock(x, y, z) == blockToPlace) {
            blockToPlace.onBlockPlacedBy(world, x, y, z, player, stack);
            blockToPlace.onPostBlockPlaced(world, x, y, z, 0);
        }

        return true;
    }
}
