package twilightforest.item;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFGenerator;

public class ItemTFOreMagnet extends ItemTF {

    private static final float WIGGLE = 10F;

    private IIcon[] icons;
    private String[] iconNames = new String[] { "oreMagnet", "oreMagnet1", "oreMagnet2" };

    protected ItemTFOreMagnet() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
        this.maxStackSize = 1;
        this.setMaxDamage(12);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player) {
        player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }

    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World world, EntityPlayer player, int useRemaining) {
        int useTime = this.getMaxItemUseDuration(par1ItemStack) - useRemaining;

        if (!world.isRemote && useTime > 10) {
            // player.addChatMessage("Ore Magnet!");

            int moved = doMagnet(world, player, 0, 0);

            if (moved == 0) {
                moved = doMagnet(world, player, WIGGLE, 0);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, WIGGLE, WIGGLE);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, 0, WIGGLE);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, -WIGGLE, WIGGLE);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, -WIGGLE, 0);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, -WIGGLE, -WIGGLE);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, 0, -WIGGLE);
            }
            if (moved == 0) {
                moved = doMagnet(world, player, WIGGLE, -WIGGLE);
            }

            // player.addChatMessage("Cost: " + moved);

            if (moved > 0) {
                par1ItemStack.damageItem(moved, player);

                if (par1ItemStack.stackSize == 0) {
                    player.destroyCurrentEquippedItem();
                }

                world.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
            }
        }

    }

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack        The item stack to get the icon for. (Usually this, and usingItem will be the same if
     *                     usingItem is not null)
     * @param renderPass   The pass to get the icon for, 0 is default.
     * @param player       The player holding the item
     * @param usingItem    The item the player is actively using. Can be null if not using anything.
     * @param useRemaining The ticks remaining for the active item.
     * @return The icon index
     */
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (usingItem != null && usingItem.getItem() == this) {
            int useTime = usingItem.getMaxItemUseDuration() - useRemaining;
            if (useTime >= 20) {
                return (useTime >> 2) % 2 == 0 ? this.icons[2] : this.icons[1];

            }
            if (useTime > 10) {
                return this.icons[1];
            }
        }
        return this.icons[0];

    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.iconNames.length; ++i) {
            this.icons[i] = par1IconRegister.registerIcon(TwilightForestMod.ID + ":" + iconNames[i]);
        }
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    /**
     * Magnet from the player's position and facing to the specified offset
     */
    protected int doMagnet(World world, EntityPlayer player, float yawOffset, float pitchOffset) {

        // find vector 32 blocks from look
        double range = 32.0D;
        Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 lookVec = getOffsetLook(player, yawOffset, pitchOffset);
        Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);

        int useX = MathHelper.floor_double(srcVec.xCoord);
        int useY = MathHelper.floor_double(srcVec.yCoord);
        int useZ = MathHelper.floor_double(srcVec.zCoord);

        int destX = MathHelper.floor_double(destVec.xCoord);
        int destY = MathHelper.floor_double(destVec.yCoord);
        int destZ = MathHelper.floor_double(destVec.zCoord);

        return doMagnet(world, player, useX, useY, useZ, destX, destY, destZ);
    }

    private static final int MAX_VEIN_SIZE = 24;

    // public overload retained for integrations that invoke the magnet
    public static int doMagnet(World world, int useX, int useY, int useZ, int destX, int destY, int destZ) {
        return doMagnet(world, null, useX, useY, useZ, destX, destY, destZ);
    }

    private static int doMagnet(World world, EntityPlayer player, int useX, int useY, int useZ, int destX, int destY,
            int destZ) {
        if (world.isRemote) {
            return 0;
        }
        ChunkCoordinates[] line = TFGenerator.getBresehnamArrayCoords(useX, useY, useZ, destX, destY, destZ);
        ChunkCoordinates base = null;
        ChunkCoordinates ore = null;
        Block oreBlock = null;
        int oreMeta = 0;
        boolean nether = false;

        for (ChunkCoordinates pos : line) {
            if (!validHeight(world, pos.posY)) {
                continue;
            }
            Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
            int meta = world.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
            if (base == null) {
                if (isReplaceable(world, block, meta, pos.posX, pos.posY, pos.posZ)) {
                    base = pos;
                } else if (isNetherReplaceable(world, block, meta, pos.posX, pos.posY, pos.posZ)) {
                    base = pos;
                    nether = true;
                }
            }
            if (block != Blocks.air && isOre(block, meta)) {
                ore = pos;
                oreBlock = block;
                oreMeta = meta;
                break;
            }
        }
        if (base == null || ore == null) {
            return 0;
        }

        int offsetX = base.posX - ore.posX;
        int offsetY = base.posY - ore.posY;
        int offsetZ = base.posZ - ore.posZ;
        Map<ChunkCoordinates, TileEntity> vein = findVein(world, ore, oreBlock, oreMeta);
        int moved = 0;

        for (Map.Entry<ChunkCoordinates, TileEntity> entry : vein.entrySet()) {
            ChunkCoordinates source = entry.getKey();
            int targetX = source.posX + offsetX;
            int targetY = source.posY + offsetY;
            int targetZ = source.posZ + offsetZ;
            if (!validHeight(world, targetY) || !canEdit(player, world, source.posX, source.posY, source.posZ)
                    || !canEdit(player, world, targetX, targetY, targetZ)) {
                continue;
            }
            Block displaced = world.getBlock(targetX, targetY, targetZ);
            int displacedMeta = world.getBlockMetadata(targetX, targetY, targetZ);
            if (!(displaced == Blocks.air
                    || (nether ? isNetherReplaceable(world, displaced, displacedMeta, targetX, targetY, targetZ)
                            : isReplaceable(world, displaced, displacedMeta, targetX, targetY, targetZ)))) {
                continue;
            }
            // never delete a tile entity in the destination, even on a replacement block
            if (world.getTileEntity(targetX, targetY, targetZ) != null
                    || world.getBlock(source.posX, source.posY, source.posZ) != oreBlock
                    || world.getBlockMetadata(source.posX, source.posY, source.posZ) != oreMeta) {
                continue;
            }
            if (player != null) {
                net.minecraftforge.event.world.BlockEvent.BreakEvent breakEvent = new net.minecraftforge.event.world.BlockEvent.BreakEvent(
                        source.posX,
                        source.posY,
                        source.posZ,
                        world,
                        oreBlock,
                        oreMeta,
                        player);
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(breakEvent)) {
                    continue;
                }
            }

            TileEntity originalTile = entry.getValue();
            net.minecraftforge.common.util.BlockSnapshot destinationSnapshot = net.minecraftforge.common.util.BlockSnapshot
                    .getBlockSnapshot(world, targetX, targetY, targetZ);
            if (!world.setBlock(targetX, targetY, targetZ, oreBlock, oreMeta, 2)) {
                continue;
            }
            if (player != null && net.minecraftforge.event.ForgeEventFactory
                    .onPlayerBlockPlace(player, destinationSnapshot, net.minecraftforge.common.util.ForgeDirection.UP)
                    .isCanceled()) {
                destinationSnapshot.restore(true, false);
                continue;
            }
            if (!world
                    .setBlock(source.posX, source.posY, source.posZ, nether ? Blocks.netherrack : Blocks.stone, 0, 2)) {
                // a failed source update musnt duplicate ore at the destination
                destinationSnapshot.restore(true, false);
                continue;
            }
            if (originalTile != null) {
                // setBlock has already removed the original TE, transfer it after both writes succeed
                world.removeTileEntity(targetX, targetY, targetZ);
                originalTile.xCoord = targetX;
                originalTile.yCoord = targetY;
                originalTile.zCoord = targetZ;
                originalTile.validate();
                world.setTileEntity(targetX, targetY, targetZ, originalTile);
                originalTile.markDirty();
            }
            moved++;
        }
        return moved;
    }

    private static boolean validHeight(World world, int y) {
        return y >= 0 && y < world.getActualHeight();
    }

    private static boolean canEdit(EntityPlayer player, World world, int x, int y, int z) {
        return player == null || (world.canMineBlock(player, x, y, z)
                && player.canPlayerEdit(x, y, z, 1, player.getCurrentEquippedItem()));
    }

    private static Map<ChunkCoordinates, TileEntity> findVein(World world, ChunkCoordinates start, Block ore,
            int meta) {
        Map<ChunkCoordinates, TileEntity> vein = new LinkedHashMap<>();
        Set<ChunkCoordinates> visited = new HashSet<>();
        ArrayDeque<ChunkCoordinates> pending = new ArrayDeque<>();
        pending.add(start);
        while (!pending.isEmpty() && vein.size() < MAX_VEIN_SIZE) {
            ChunkCoordinates at = pending.removeFirst();
            if (!visited.add(at) || !validHeight(world, at.posY)
                    || world.getBlock(at.posX, at.posY, at.posZ) != ore
                    || world.getBlockMetadata(at.posX, at.posY, at.posZ) != meta) {
                continue;
            }
            vein.put(at, world.getTileEntity(at.posX, at.posY, at.posZ));
            pending.add(new ChunkCoordinates(at.posX + 1, at.posY, at.posZ));
            pending.add(new ChunkCoordinates(at.posX - 1, at.posY, at.posZ));
            pending.add(new ChunkCoordinates(at.posX, at.posY + 1, at.posZ));
            pending.add(new ChunkCoordinates(at.posX, at.posY - 1, at.posZ));
            pending.add(new ChunkCoordinates(at.posX, at.posY, at.posZ + 1));
            pending.add(new ChunkCoordinates(at.posX, at.posY, at.posZ - 1));
        }
        return vein;
    }

    /**
     * Get the player look vector, but offset by the specified parameters. We use to scan the area around where the
     * player is looking in the likely case there's no ore in the exact look direction.
     */
    private Vec3 getOffsetLook(EntityPlayer player, float yawOffset, float pitchOffset) {
        float var2 = MathHelper.cos(-(player.rotationYaw + yawOffset) * 0.017453292F - (float) Math.PI);
        float var3 = MathHelper.sin(-(player.rotationYaw + yawOffset) * 0.017453292F - (float) Math.PI);
        float var4 = -MathHelper.cos(-(player.rotationPitch + pitchOffset) * 0.017453292F);
        float var5 = MathHelper.sin(-(player.rotationPitch + pitchOffset) * 0.017453292F);
        return Vec3.createVectorHelper(var3 * var4, var5, var2 * var4);
    }

    private static boolean isReplaceable(World world, Block block, int meta, int x, int y, int z) {
        // nteger keys previously compared by identity and failed to protect previously found ores
        if (block == Blocks.air || isOre(block, meta)) {
            return false;
        }
        return block == Blocks.dirt || block == Blocks.grass
                || block == Blocks.gravel
                || block.isReplaceableOreGen(world, x, y, z, Blocks.stone);
    }

    private static boolean isNetherReplaceable(World world, Block block, int meta, int x, int y, int z) {
        if (block == Blocks.air || isOre(block, meta)) {
            return false;
        }
        return block == Blocks.netherrack || block.isReplaceableOreGen(world, x, y, z, Blocks.netherrack);
    }

    public static boolean isOre(Block blockID, int meta) {

        if (blockID == Blocks.coal_ore) {
            return true;
        }
        if (blockID == Blocks.iron_ore) {
            return true;
        }
        if (blockID == Blocks.diamond_ore) {
            return true;
        }
        if (blockID == Blocks.emerald_ore) {
            return true;
        }
        if (blockID == Blocks.gold_ore) {
            return true;
        }
        if (blockID == Blocks.lapis_ore) {
            return true;
        }
        if (blockID == Blocks.redstone_ore) {
            return true;
        }
        if (blockID == Blocks.lit_redstone_ore) {
            return true;
        }
        if (blockID == Blocks.quartz_ore) {
            return true;
        }
        if (blockID == TFBlocks.root && meta == BlockTFRoots.OREROOT_META) {
            return true;
        }
        return blockID.getUnlocalizedName().toLowerCase(Locale.ROOT).contains("ore");
    }

}
