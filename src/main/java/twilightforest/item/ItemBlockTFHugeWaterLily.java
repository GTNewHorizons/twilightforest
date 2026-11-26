package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import twilightforest.block.TFBlocks;

public class ItemBlockTFHugeWaterLily extends ItemBlock {

    public ItemBlockTFHugeWaterLily(Block block) {
        super(block);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition target = getMovingObjectPositionFromPlayer(world, player, true);
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || stack.stackSize <= 0) {
            return stack;
        }

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, target.sideHit, stack)
                || !player.canPlayerEdit(x, y + 1, z, 1, stack)
                || world.getBlock(x, y, z).getMaterial() != Material.water
                || world.getBlockMetadata(x, y, z) != 0
                || !world.isAirBlock(x, y + 1, z)
                || world.isRemote) {
            return stack;
        }

        BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, x, y + 1, z);
        if (!world.setBlock(x, y + 1, z, TFBlocks.hugeWaterLily)) {
            return stack;
        }

        if (ForgeEventFactory.onPlayerBlockPlace(player, snapshot, ForgeDirection.UP).isCanceled()) {
            snapshot.restore(true, false);
            return stack;
        }

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return stack;
    }
}
