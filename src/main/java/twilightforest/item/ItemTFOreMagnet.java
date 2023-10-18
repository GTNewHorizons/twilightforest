package twilightforest.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

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

        return doMagnet(world, useX, useY, useZ, destX, destY, destZ);
    }

    private static final HashSet<Integer[]> FOUND_ORE_BLOCKS = new HashSet<>();

    /**
     * This function makes the magnet work
     */
    public static int doMagnet(World world, int useX, int useY, int useZ, int destX, int destY, int destZ) {
        int blocksMoved = 0;
        // get blocks in line from src to dest
        ChunkCoordinates[] lineArray = TFGenerator.getBresehnamArrayCoords(useX, useY, useZ, destX, destY, destZ);

        // System.out.println("Searching from " + useX + ", " + useY + ", " + useZ + " to " + destX + ", " +
        // destY + ", " + destZ);

        // find some ore?
        Block foundID = Blocks.air;
        int foundMeta = -1;
        int foundX = -1;
        int foundY = -1;
        int foundZ = -1;
        int baseX = -1;
        int baseY = -1;
        int baseZ = -1;
        boolean isNetherrack = false;

        for (ChunkCoordinates coord : lineArray) {
            Block searchID = world.getBlock(coord.posX, coord.posY, coord.posZ);
            int searchMeta = world.getBlockMetadata(coord.posX, coord.posY, coord.posZ);

            // keep track of where the dirt/stone we first find is.s
            if (baseY == -1) {
                if (isReplaceable(world, searchID, searchMeta, coord.posX, coord.posY, coord.posZ)) {
                    baseX = coord.posX;
                    baseY = coord.posY;
                    baseZ = coord.posZ;

                } else if (isNetherReplaceable(world, searchID, searchMeta, coord.posX, coord.posY, coord.posZ)) {
                    isNetherrack = true;
                    baseX = coord.posX;
                    baseY = coord.posY;
                    baseZ = coord.posZ;
                }
            }

            if (searchID != Blocks.air && isOre(searchID, searchMeta)) {
                // System.out.println("I found ore: " + searchID + " at " + coord.PosX + ", " + coord.PosY + ", " +
                // coord.PosZ);

                foundID = searchID;
                foundMeta = searchMeta;
                foundX = coord.posX;
                foundY = coord.posY;
                foundZ = coord.posZ;
                FOUND_ORE_BLOCKS.add(new Integer[] { Block.getIdFromBlock(foundID), foundMeta });
                break;
            }
        }

        // System.out.println("I found ground at " + baseX + ", " + baseY + ", " + baseZ);

        if (baseY != -1 && foundID != Blocks.air) {
            // find the whole vein
            HashMap<ChunkCoordinates, Optional<TileEntity>> veinBlocks = new HashMap<>();
            findVein(world, foundX, foundY, foundZ, foundID, foundMeta, veinBlocks);

            // move it up into minable blocks or dirt
            int offX = baseX - foundX;
            int offY = baseY - foundY;
            int offZ = baseZ - foundZ;

            for (Map.Entry<ChunkCoordinates, Optional<TileEntity>> entry : veinBlocks.entrySet()) {
                ChunkCoordinates coord = entry.getKey();
                int replaceX = coord.posX + offX;
                int replaceY = coord.posY + offY;
                int replaceZ = coord.posZ + offZ;

                Block replaceID = world.getBlock(replaceX, replaceY, replaceZ);
                int replaceMeta = world.getBlockMetadata(replaceX, replaceY, replaceZ);

                if ((isNetherrack ? isNetherReplaceable(world, replaceID, replaceMeta, replaceX, replaceY, replaceZ)
                        : isReplaceable(world, replaceID, replaceMeta, replaceX, replaceY, replaceZ))
                        || replaceID == Blocks.air) {
                    // set vein to stone / netherrack
                    world.setBlock(
                            coord.posX,
                            coord.posY,
                            coord.posZ,
                            isNetherrack ? Blocks.netherrack : Blocks.stone,
                            0,
                            2);

                    // set close to ore material
                    world.setBlock(replaceX, replaceY, replaceZ, foundID, foundMeta, 2);

                    // Tile Entity Handleing
                    entry.getValue().ifPresent(tile -> {
                        // Set XYZ on the Tile
                        {
                            tile.xCoord = replaceX;
                            tile.yCoord = replaceY;
                            tile.zCoord = replaceZ;
                        }
                        // Validate the tile and set the new position on the chunk-data
                        {
                            tile.validate();
                            world.setTileEntity(replaceX, replaceY, replaceZ, tile);
                        }
                        // remove the old position from the chunk-data and re-Validate the tile
                        {
                            world.removeTileEntity(coord.posX, coord.posY, coord.posZ);
                            tile.validate();
                        }
                        // mark the tile as dirty so it gets saved to disk
                        tile.markDirty();
                    });
                    blocksMoved++;
                }
                /*
                 * else {
                 * System.out.println("Not moving a block because we did not find a replaceable block to move to"); }
                 */
            }

            // player.addChatMessage("Moved blocks! " + blocksMoved);
        }
        return blocksMoved;
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

    private static boolean isReplaceable(World world, Block replaceID, int replaceMeta, int x, int y, int z) {
        // Make found Ores NOT replaceable
        if (FOUND_ORE_BLOCKS.contains(new Integer[] { Block.getIdFromBlock(replaceID), replaceMeta })) return false;

        if (replaceID == Blocks.dirt) {
            return true;
        }
        if (replaceID == Blocks.grass) {
            return true;
        }
        if (replaceID == Blocks.gravel) {
            return true;
        }
        return replaceID != Blocks.air && replaceID.isReplaceableOreGen(world, x, y, z, Blocks.stone);
    }

    private static boolean isNetherReplaceable(World world, Block replaceID, int replaceMeta, int x, int y, int z) {
        if (FOUND_ORE_BLOCKS.contains(new Integer[] { Block.getIdFromBlock(replaceID), replaceMeta })) return false;

        if (replaceID == Blocks.netherrack) {
            return true;
        }
        return replaceID != Blocks.air && replaceID.isReplaceableOreGen(world, x, y, z, Blocks.netherrack);
    }

    private static final int MAX_FIND_VEIN_RECURSION_DEPTH = 24;

    private static boolean findVein(World world, int x, int y, int z, Block oreID, int oreMeta,
            Map<ChunkCoordinates, Optional<TileEntity>> veinBlocks) {
        ChunkCoordinates here = new ChunkCoordinates(x, y, z);

        // is this already on the list?
        if (veinBlocks.containsKey(here)) {
            return false;
        }

        // let's limit it to 24 blocks at a time
        if (veinBlocks.size() >= MAX_FIND_VEIN_RECURSION_DEPTH) {
            return false;
        }

        // otherwise, check if we're still in the vein
        if (world.getBlock(x, y, z) == oreID && world.getBlockMetadata(x, y, z) == oreMeta) {
            veinBlocks.put(here, Optional.ofNullable(world.getTileEntity(x, y, z)));
            // recurse in 6 directions
            {
                findVein(world, x + 1, y, z, oreID, oreMeta, veinBlocks);
                findVein(world, x - 1, y, z, oreID, oreMeta, veinBlocks);
                findVein(world, x, y + 1, z, oreID, oreMeta, veinBlocks);
                findVein(world, x, y - 1, z, oreID, oreMeta, veinBlocks);
                findVein(world, x, y, z + 1, oreID, oreMeta, veinBlocks);
                findVein(world, x, y, z - 1, oreID, oreMeta, veinBlocks);
            }
            return true;
        } else {
            return false;
        }
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
        return blockID.getUnlocalizedName().toLowerCase().contains("ore");
    }

}
