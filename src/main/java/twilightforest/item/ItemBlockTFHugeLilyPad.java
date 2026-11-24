package twilightforest.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import twilightforest.block.TFBlocks;

public class ItemBlockTFHugeLilyPad extends ItemLilyPad {

    private static final long ORIENTATION_SEED = 8890919293L;
    private static final int[][] OFFSETS = { { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } };

    public ItemBlockTFHugeLilyPad(Block block) {
        super(block);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition target = getMovingObjectPositionFromPlayer(world, player, true);
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || stack.stackSize <= 0) {
            return stack;
        }

        int x = target.blockX & ~1;
        int y = target.blockY;
        int z = target.blockZ & ~1;

        for (int[] offset : OFFSETS) {
            if (!canPlacePadOn(stack, world, player, x + offset[0], y, z + offset[1])) {
                return stack;
            }
        }

        // only the server commits the 4 block transaction, clients receive normal block updates
        if (world.isRemote) {
            return stack;
        }

        List<BlockSnapshot> snapshots = new ArrayList<>(OFFSETS.length);
        for (int[] offset : OFFSETS) {
            snapshots.add(BlockSnapshot.getBlockSnapshot(world, x + offset[0], y + 1, z + offset[1]));
        }

        int orientation = getOrientation(x, z);
        for (int i = 0; i < OFFSETS.length; i++) {
            int[] offset = OFFSETS[i];
            if (!world.setBlock(x + offset[0], y + 1, z + offset[1], TFBlocks.hugeLilyPad, orientation | i, 2)) {
                restoreSnapshots(snapshots);
                return stack;
            }
        }

        if (ForgeEventFactory.onPlayerMultiBlockPlace(player, snapshots, ForgeDirection.UP).isCanceled()) {
            restoreSnapshots(snapshots);
            return stack;
        }

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return stack;
    }

    private static int getOrientation(int x, int z) {
        Random random = new Random(ORIENTATION_SEED);
        random.setSeed((x * random.nextLong()) ^ (z * random.nextLong()) ^ ORIENTATION_SEED);
        return random.nextInt(4) << 2;
    }

    private static void restoreSnapshots(List<BlockSnapshot> snapshots) {
        for (BlockSnapshot snapshot : snapshots) {
            snapshot.restore(true, false);
        }
    }

    public boolean canPlacePadOn(ItemStack stack, World world, EntityPlayer player, int x, int y, int z) {
        return world.canMineBlock(player, x, y, z) && player.canPlayerEdit(x, y, z, 1, stack)
                && player.canPlayerEdit(x, y + 1, z, 1, stack)
                && world.getBlock(x, y, z).getMaterial() == Material.water
                && world.getBlockMetadata(x, y, z) == 0
                && world.isAirBlock(x, y + 1, z);
    }
}
