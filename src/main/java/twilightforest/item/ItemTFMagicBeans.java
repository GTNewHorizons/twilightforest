package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import twilightforest.block.TFBlocks;
import twilightforest.world.WorldProviderTwilightForest;

public class ItemTFMagicBeans extends ItemTF {

    public ItemTFMagicBeans() {
        this.makeRare();
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        if (stack.stackSize <= 0 || world.getBlock(x, y, z) != TFBlocks.uberousSoil
                || !world.canMineBlock(player, x, y, z)
                || !player.canPlayerEdit(x, y + 1, z, side, stack)) {
            return false;
        }
        int minY = y + 1;
        int maxY = Math.min(world.getActualHeight(), Math.max(y + 100, (int) (getCloudHeight(world) + 25)));
        if (minY >= maxY) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        if (!makeHugeStalk(world, player, stack, x, z, minY, maxY)) {
            return false;
        }
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }

    /**
     * Try to find the given world's cloud height
     */
    private float getCloudHeight(World world) {
        if (world.provider instanceof WorldProviderTwilightForest) {
            return WorldProviderTwilightForest.CLOUD_HEIGHT;
        }
        return world.provider.terrainType.getCloudHeight();
    }

    private boolean makeHugeStalk(World world, EntityPlayer player, ItemStack stack, int x, int z, int minY, int maxY) {
        int yOffset = world.rand.nextInt(100);

        float cScale = world.rand.nextFloat() * 0.25F + 0.125F; // spiral tightness scaling
        float rScale = world.rand.nextFloat() * 0.25F + 0.125F; // radius change scaling

        // offset x and z to make stalk start at origin
        float radius = 4F + MathHelper.sin((minY + yOffset) * rScale) * 3F; // make radius a little wavy
        x -= MathHelper.sin((minY + yOffset) * cScale) * radius;
        z -= MathHelper.cos((minY + yOffset) * cScale) * radius;

        // leaves!
        int nextLeafY = minY + 10 + world.rand.nextInt(20);

        // make stalk
        boolean isClear = true;
        boolean placedAny = false;
        for (int dy = minY; dy < maxY && isClear; dy++) {
            // make radius a little wavy
            radius = 5F + MathHelper.sin((dy + yOffset) * rScale) * 2.5F;

            // find center of stalk
            float cx = x + MathHelper.sin((dy + yOffset) * cScale) * radius;
            float cz = z + MathHelper.cos((dy + yOffset) * cScale) * radius;

            float stalkThickness = 2.5F;

            // reduce thickness near top
            if (maxY - dy < 5) {
                stalkThickness *= (maxY - dy) / 5F;
            }

            int minX = MathHelper.floor_float(x - radius - stalkThickness);
            int maxX = MathHelper.ceiling_float_int(x + radius + stalkThickness);
            int minZ = MathHelper.floor_float(z - radius - stalkThickness);
            int maxZ = MathHelper.ceiling_float_int(z + radius + stalkThickness);

            // generate stalk
            for (int dx = minX; dx < maxX; dx++) {
                for (int dz = minZ; dz < maxZ; dz++) {
                    if ((dx - cx) * (dx - cx) + (dz - cz) * (dz - cz) < stalkThickness * stalkThickness) {
                        boolean placed = this.tryToPlaceStalk(world, player, stack, dx, dy, dz);
                        placedAny |= placed;
                        isClear &= placed;
                    }
                }
            }

            // leaves?
            if (dy == nextLeafY) {
                // make leaf blob

                int lx = (int) (x + MathHelper.sin((dy + yOffset) * cScale) * (radius + stalkThickness));
                int lz = (int) (z + MathHelper.cos((dy + yOffset) * cScale) * (radius + stalkThickness));

                this.placeLeaves(world, player, stack, lx, dy, lz);

                nextLeafY = dy + 5 + world.rand.nextInt(10);
            }
        }
        return placedAny;
    }

    private void placeLeaves(World world, EntityPlayer player, ItemStack stack, int x, int y, int z) {
        // dont overwrite a protected or obstructing block in the middle of a leaf cluster
        this.tryToPlaceStalk(world, player, stack, x, y, z);

        // small squares
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                this.tryToPlaceLeaves(world, player, stack, x + dx, y - 1, z + dz);
                this.tryToPlaceLeaves(world, player, stack, x + dx, y + 1, z + dz);
            }
        }
        // larger square
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (!((dx == 2 || dx == -2) && (dz == 2 || dz == -2))) {
                    this.tryToPlaceLeaves(world, player, stack, x + dx, y, z + dz);
                }
            }
        }
    }

    /**
     * Place the stalk block only if the destination is clear. Return false if blocked.
     */
    private boolean tryToPlaceStalk(World world, EntityPlayer player, ItemStack stack, int x, int y, int z) {
        Block previous = world.getBlock(x, y, z);
        if (previous != Blocks.air && !previous.isReplaceable(world, x, y, z)
                && !previous.canBeReplacedByLeaves(world, x, y, z)
                && !previous.isLeaves(world, x, y, z)
                && !previous.canSustainLeaves(world, x, y, z)) {
            return false;
        }
        if (!canReplace(world, player, stack, x, y, z)) {
            return false;
        }
        return world.setBlock(x, y, z, TFBlocks.hugeStalk);
    }

    private void tryToPlaceLeaves(World world, EntityPlayer player, ItemStack stack, int x, int y, int z) {
        Block previous = world.getBlock(x, y, z);
        if ((previous == Blocks.air || previous.canBeReplacedByLeaves(world, x, y, z))
                && canReplace(world, player, stack, x, y, z)) {
            world.setBlock(x, y, z, TFBlocks.leaves3, 1, 2);
        }
    }

    private boolean canReplace(World world, EntityPlayer player, ItemStack stack, int x, int y, int z) {
        if (y < 0 || y >= world.getActualHeight()
                || !world.canMineBlock(player, x, y, z)
                || !player.canPlayerEdit(x, y, z, 1, stack)) {
            return false;
        }
        Block previous = world.getBlock(x, y, z);
        if (previous == Blocks.air) {
            return true;
        }
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(
                x,
                y,
                z,
                world,
                previous,
                world.getBlockMetadata(x, y, z),
                player);
        return !MinecraftForge.EVENT_BUS.post(event);
    }

}
