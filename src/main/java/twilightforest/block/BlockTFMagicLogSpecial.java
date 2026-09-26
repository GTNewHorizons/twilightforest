package twilightforest.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import twilightforest.TFGenericPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.compat.Mods;
import twilightforest.item.ItemTFOreMagnet;
import twilightforest.item.TFItems;
import twilightforest.tileentity.TileEntityTFTimewoodClock;

public class BlockTFMagicLogSpecial extends BlockTFMagicLog {

    protected BlockTFMagicLogSpecial() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return (meta & 3) == META_TIME ? META_TIME : super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return (metadata & 3) == META_TIME;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityTFTimewoodClock();
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public Item getItemDropped(int par1, Random rand, int par3) {
        return Item.getItemFromBlock(TFBlocks.magicLog); // change into normal magic log
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int orient = meta & 12;
        int woodType = meta & 3;

        if (orient == 12) {
            // off blocks
            return switch (woodType) {
                default -> (side == 1 || side == 0) ? SPR_TIMETOP : SPR_TIMECLOCKOFF;
                case META_TRANS -> (side == 1 || side == 0) ? SPR_TRANSTOP : SPR_TRANSHEARTOFF;
                case META_MINE -> (side == 1 || side == 0) ? SPR_MINETOP : SPR_MINEGEMOFF;
                case META_SORT -> (side == 1 || side == 0) ? SPR_SORTTOP : SPR_SORTEYEOFF;
            };
        } else {
            return switch (woodType) {
                default -> (side == 1 || side == 0) ? SPR_TIMETOP
                        : (isTimeClockDisabled(orient) ? SPR_TIMECLOCKOFF : SPR_TIMECLOCK);
                case META_TRANS -> orient == 0 && (side == 1 || side == 0) ? SPR_TRANSTOP
                        : (orient == 4 && (side == 5 || side == 4) ? SPR_TRANSTOP
                                : (orient == 8 && (side == 2 || side == 3) ? SPR_TRANSTOP : SPR_TRANSHEART));
                case META_MINE -> orient == 0 && (side == 1 || side == 0) ? SPR_MINETOP
                        : (orient == 4 && (side == 5 || side == 4) ? SPR_MINETOP
                                : (orient == 8 && (side == 2 || side == 3) ? SPR_MINETOP : SPR_MINEGEM));
                case META_SORT -> orient == 0 && (side == 1 || side == 0) ? SPR_SORTTOP
                        : (orient == 4 && (side == 5 || side == 4) ? SPR_SORTTOP
                                : (orient == 8 && (side == 2 || side == 3) ? SPR_SORTTOP : SPR_SORTEYE));
            };
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 3) == META_TIME && side != 0 && side != 1 && isBlockIndirectlyPowered(world, x, y, z)) {
            return SPR_TIMECLOCKOFF;
        }
        return getIcon(side, meta);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        int orient = meta & 12;
        int woodType = meta & 3;

        if (woodType == META_TIME) {
            orient = normalizeTimeClockState(world, x, y, z, orient);
        }

        if (woodType == META_TIME ? isTimeClockDisabled(orient) || world.isBlockIndirectlyGettingPowered(x, y, z)
                : orient == 12) {
            // block is off, do not tick
            return;
        }
        if (!world.isRemote) {
            switch (woodType) {
                case 0 -> {
                    // tree of time effect
                    TileEntityTFTimewoodClock clock = getTimewoodClock(world, x, y, z);
                    if (clock == null || !clock.isMuted()) {
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.1F, 0.5F);
                    }
                    doTreeOfTimeEffect(world, x, y, z, rand);
                }
                case 1 ->
                    // tree of transformation effect
                    doTreeOfTransformationEffect(world, x, y, z, rand);
                case 2 ->
                    // miner's tree effect
                    doMinersTreeEffect(world, x, y, z, rand);
                case 3 ->
                    // sorting tree effect
                    doSortingTreeEffect(world, x, y, z, rand);
            }
        }
        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 3) != META_TIME) {
            return;
        }
        world.markBlockForUpdate(x, y, z);
        if (!world.isRemote && !isTimeClockDisabled(meta & 12) && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
            world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
            float par8, float par9) {
        int meta = world.getBlockMetadata(x, y, z);

        int orient = meta & 12;
        int woodType = meta & 3;

        if (woodType == META_TIME) {
            orient = normalizeTimeClockState(world, x, y, z, orient);
            ItemStack heldItem = player.getHeldItem();
            if (Mods.gregtech_nh.isLoaded() && heldItem != null && GregTechCompat.isSoftMallet(heldItem)) {
                if (!world.isRemote && GregTechCompat.damageSoftMallet(heldItem, player)) {
                    TileEntityTFTimewoodClock clock = getTimewoodClock(world, x, y, z);
                    if (clock != null) {
                        clock.setMuted(!clock.isMuted());
                    }
                }
                return true;
            }

            int newOrient = orient ^ 12;
            world.setBlockMetadataWithNotify(x, y, z, woodType | newOrient, 3);
            if (!isTimeClockDisabled(newOrient)) {
                world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
            }
            return true;
        }

        if (orient == 0) {
            // turn off
            world.setBlockMetadataWithNotify(x, y, z, woodType | 12, 3);
            return true;
        } else if (orient == 12) {
            // turn on
            world.setBlockMetadataWithNotify(x, y, z, woodType | 0, 3);
            world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
            return true;
        }
        return false;
    }

    private static boolean isTimeClockDisabled(int orient) {
        return (orient & 8) != 0;
    }

    private static int normalizeTimeClockState(World world, int x, int y, int z, int orient) {
        if (orient != 4 && orient != 8) {
            return orient;
        }
        int normalized = orient == 8 ? 12 : 0;
        if (!world.isRemote) {
            TileEntityTFTimewoodClock clock = getTimewoodClock(world, x, y, z);
            if (clock != null) {
                clock.setMuted(true);
            }
            world.setBlockMetadataWithNotify(x, y, z, normalized, 3);
        }
        return normalized;
    }

    private static TileEntityTFTimewoodClock getTimewoodClock(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof TileEntityTFTimewoodClock ? (TileEntityTFTimewoodClock) tileEntity : null;
    }

    private static boolean isBlockIndirectlyPowered(IBlockAccess world, int x, int y, int z) {
        return getIndirectPowerLevel(world, x, y - 1, z, 0) > 0 || getIndirectPowerLevel(world, x, y + 1, z, 1) > 0
                || getIndirectPowerLevel(world, x, y, z - 1, 2) > 0
                || getIndirectPowerLevel(world, x, y, z + 1, 3) > 0
                || getIndirectPowerLevel(world, x - 1, y, z, 4) > 0
                || getIndirectPowerLevel(world, x + 1, y, z, 5) > 0;
    }

    private static int getIndirectPowerLevel(IBlockAccess world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        if (!block.shouldCheckWeakPower(world, x, y, z, side)) {
            return block.isProvidingWeakPower(world, x, y, z, side);
        }
        int power = 0;
        for (int i = 0; i < 6; i++) {
            power = Math.max(
                    power,
                    world.isBlockProvidingPowerTo(
                            x + Facing.offsetsXForSide[i],
                            y + Facing.offsetsYForSide[i],
                            z + Facing.offsetsZForSide[i],
                            i));
        }
        return power;
    }

    private static final class GregTechCompat {

        @Optional.Method(modid = "gregtech_nh")
        private static boolean isSoftMallet(ItemStack stack) {
            return GTUtility.isStackInList(stack, GregTechAPI.sSoftMalletList);
        }

        @Optional.Method(modid = "gregtech_nh")
        private static boolean damageSoftMallet(ItemStack stack, EntityPlayer player) {
            return GTModHandler.damageOrDechargeItem(stack, 1, 1000, player);
        }
    }

    /**
     * The tree of time adds extra ticks to blocks, so that they have twice the normal chance to get a random tick
     */
    private void doTreeOfTimeEffect(World world, int x, int y, int z, Random rand) {
        int numticks = 8 * 3 * this.tickRate(world);

        for (int i = 0; i < numticks; i++) {
            // find a nearby block, offset [-16, +16] on each axis (matches upstream timeCoreRange = 16)
            int dx = rand.nextInt(33) - 16;
            int dy = rand.nextInt(33) - 16;
            int dz = rand.nextInt(33) - 16;

            int targetBlockX = x + dx;
            int targetBlockY = y + dy;
            int targetBlockZ = z + dz;

            // prevent chunk loads
            if (!world.blockExists(targetBlockX, targetBlockY, targetBlockZ)) {
                continue;
            }

            Block targetBlock = world.getBlock(targetBlockX, targetBlockY, targetBlockZ);

            // give any randomly-ticking block an extra tick, unless pack-excluded (TimeCoreExcludedBlocks)
            if (targetBlock.getTickRandomly() && !getExcludedBlocks().contains(targetBlock)) {
                targetBlock.updateTick(world, targetBlockX, targetBlockY, targetBlockZ, rand);
            }
        }
    }

    /**
     * Resolve the configured exclusion names to Block instances once, then reuse. Done lazily because all blocks must
     * be registered before {@link Block#getBlockFromName} can find them.
     */
    private static Set<Block> excludedBlocks;

    private static Set<Block> getExcludedBlocks() {
        if (excludedBlocks == null) {
            Set<Block> resolved = Collections.newSetFromMap(new IdentityHashMap<>());
            for (String name : TwilightForestMod.timeCoreExcludedBlocks) {
                Block block = Block.getBlockFromName(name);
                if (block != null) {
                    resolved.add(block);
                }
            }
            excludedBlocks = resolved;
        }
        return excludedBlocks;
    }

    /**
     * The tree of transformation transforms the biome in the area near it into the enchanted forest biome.
     * 
     * TODO: also change entities
     */
    private void doTreeOfTransformationEffect(World world, int x, int y, int z, Random rand) {
        for (int i = 0; i < 1; i++) {
            int dx = rand.nextInt(32) - 16;
            int dz = rand.nextInt(32) - 16;

            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note.harp", 0.1F, rand.nextFloat() * 2F);

            if (Math.sqrt(dx * dx + dz * dz) < 16) {
                BiomeGenBase biomeAt = world.getBiomeGenForCoords(x + dx, z + dz);

                if (biomeAt != TFBiomeBase.enchantedForest) {
                    // I wonder how possible it is to change this

                    Chunk chunkAt = world.getChunkFromBlockCoords(x + dx, z + dz);
                    int biomeIndex = ((z + dz) & 15) << 4 | ((x + dx) & 15);

                    if (Mods.endlessids.isLoaded()) {
                        ((ChunkBiomeHook) chunkAt)
                                .getBiomeShortArray()[biomeIndex] = (short) TFBiomeBase.enchantedForest.biomeID;
                    } else {
                        chunkAt.getBiomeArray()[biomeIndex] = (byte) TFBiomeBase.enchantedForest.biomeID;
                    }

                    world.markBlockForUpdate((x + dx), y, (z + dz));

                    // System.out.println("Set biome at " + (x + dx) + ", " + (z + dz) + " to enchanted forest.");

                    // send chunk?!

                    if (world instanceof WorldServer) {
                        sendChangedBiome(world, x + dx, z + dz);
                    }

                }

            }
        }
    }

    /**
     * Send a tiny update packet to the client to inform it of the changed biome
     */
    private void sendChangedBiome(World world, int x, int z) {
        FMLProxyPacket message = TFGenericPacketHandler
                .makeBiomeChangePacket(x, z, TFBiomeBase.enchantedForest.biomeID);

        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                world.provider.dimensionId,
                x,
                128,
                z,
                128);

        TwilightForestMod.genericChannel.sendToAllAround(message, targetPoint);
        // FMLLog.info("Sent chunk update packet from tree.");

    }

    /**
     * The miner's tree generates the ore magnet effect randomly every second
     */
    private void doMinersTreeEffect(World world, int x, int y, int z, Random rand) {
        int dx = rand.nextInt(64) - 32;
        int dy = rand.nextInt(64) - 32;
        int dz = rand.nextInt(64) - 32;

        // world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 0.1F, 0.5F);

        int moved = ItemTFOreMagnet.doMagnet(world, x, y, z, x + dx, y + dy, z + dz);

        if (moved > 0) {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.endermen.portal", 0.1F, 1.0F);
        } else {
            // System.out.println("Miner block did not find ore at " + (x + dx) + ", " + (y + dy) + ", " + (z +
            // dz) + ", nope.");
        }
    }

    /**
     * The sorting tree finds two chests nearby and then attempts to sort a random item.
     */

    // Compare stacks first
    private boolean canMergeStacks(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(a, b);
    }

    private void doSortingTreeEffect(World world, int x, int y, int z, Random rand) {
        // find all the chests nearby
        int XSEARCH = 16;
        int YSEARCH = 16;
        int ZSEARCH = 16;

        ArrayList<IInventory> chests = new ArrayList<>();
        int itemCount = 0;

        int minX = x - XSEARCH;
        int maxX = x + XSEARCH;
        int minY = y - YSEARCH;
        int maxY = y + YSEARCH;
        int minZ = z - ZSEARCH;
        int maxZ = z + ZSEARCH;

        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
                    continue;
                }

                Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

                for (Object obj : chunk.chunkTileEntityMap.values()) {
                    // only scan TE
                    if (!(obj instanceof TileEntity)) {
                        continue;
                    }

                    TileEntity te = (TileEntity) obj;
                    int tx = te.xCoord;
                    int ty = te.yCoord;
                    int tz = te.zCoord;

                    if (tx < minX || tx > maxX || ty < minY || ty > maxY || tz < minZ || tz > maxZ) {
                        continue;
                    }

                    if (world.getBlock(tx, ty, tz) != Blocks.chest) {
                        continue;
                    }

                    IInventory thisChest = Blocks.chest.func_149951_m(world, tx, ty, tz);
                    IInventory testChest = te instanceof IInventory ? (IInventory) te : null;

                    // make sure we haven't counted this chest
                    if (thisChest != null && !checkIfChestsContains(chests, testChest)) {
                        int itemsInChest = 0;
                        for (int i = 0; i < thisChest.getSizeInventory(); i++) {
                            if (thisChest.getStackInSlot(i) != null) {
                                itemsInChest++;
                                itemCount++;
                            }
                        }

                        // only add non-empty chests
                        if (itemsInChest > 0) {
                            chests.add(thisChest);
                        }
                    }
                }
            }
        }

        // FMLLog.info("Found " + chests.size() + " non-empty chests, containing " + itemCount + " items");

        // find a random item in one of the chests
        ItemStack beingSorted = null;
        int sortedChestNum = -1;
        int sortedSlotNum = -1;

        if (itemCount > 0) {
            int itemNumber = rand.nextInt(itemCount);
            int currentNumber = 0;

            for (int i = 0; i < chests.size(); i++) {
                IInventory chest = chests.get(i);
                for (int slotNum = 0; slotNum < chest.getSizeInventory(); slotNum++) {
                    ItemStack currentItem = chest.getStackInSlot(slotNum);

                    if (currentItem != null) {
                        if (currentNumber++ == itemNumber) {
                            beingSorted = currentItem;
                            sortedChestNum = i;
                            sortedSlotNum = slotNum;
                        }
                    }
                }
            }
        }

        // FMLLog.info("Decided to sort item " + beingSorted);

        if (beingSorted != null) {
            int matchChestNum = -1;
            int matchCount = 0;

            // decide where to put it, if anywhere
            for (int chestNum = 0; chestNum < chests.size(); chestNum++) {
                IInventory chest = chests.get(chestNum);
                int currentChestMatches = 0;

                for (int slotNum = 0; slotNum < chest.getSizeInventory(); slotNum++) {

                    ItemStack currentItem = chest.getStackInSlot(slotNum);
                    if (currentItem != null && isSortingMatch(beingSorted, currentItem)) {
                        currentChestMatches += currentItem.stackSize;
                    }
                }

                if (currentChestMatches > matchCount) {
                    matchCount = currentChestMatches;
                    matchChestNum = chestNum;
                }
            }

            // soooo, did we find a better match?
            if (matchChestNum >= 0 && matchChestNum != sortedChestNum) {
                IInventory moveChest = chests.get(matchChestNum);
                IInventory oldChest = chests.get(sortedChestNum);

                // is there an empty inventory slot in the new chest?
                int moveSlot = getEmptySlotIn(moveChest);

                if (moveSlot >= 0) {
                    // remove old item
                    oldChest.setInventorySlotContents(sortedSlotNum, null);

                    // add new item
                    moveChest.setInventorySlotContents(moveSlot, beingSorted);

                    // FMLLog.info("Moved sorted item " + beingSorted + " to chest " + matchChestNum + ", slot " +
                    // moveSlot);
                }
            }

            // if the stack is not full, combine items from other stacks
            if (beingSorted.stackSize < beingSorted.getMaxStackSize()) {
                for (IInventory chest : chests) {
                    for (int slotNum = 0; slotNum < chest.getSizeInventory(); slotNum++) {
                        ItemStack currentItem = chest.getStackInSlot(slotNum);

                        if (currentItem != null && currentItem != beingSorted
                                && canMergeStacks(beingSorted, currentItem)) {
                            if (currentItem.stackSize <= (beingSorted.getMaxStackSize() - beingSorted.stackSize)) {
                                chest.setInventorySlotContents(slotNum, null);
                                beingSorted.stackSize += currentItem.stackSize;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isSortingMatch(ItemStack beingSorted, ItemStack currentItem) {
        if (beingSorted == null || currentItem == null) {
            return false;
        }
        CreativeTabs tabA = beingSorted.getItem().getCreativeTab();
        CreativeTabs tabB = currentItem.getItem().getCreativeTab();
        return tabA != null && tabA == tabB;
    }

    /**
     * Is the chest we're testing part of our chest list already?
     */
    private boolean checkIfChestsContains(ArrayList<IInventory> chests, IInventory testChest) {
        for (IInventory chest : chests) {
            if (chest.equals(testChest)) {
                return true;
            }

            if (chest instanceof InventoryLargeChest && ((InventoryLargeChest) chest).isPartOfLargeChest(testChest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return an empty slot number in the chest, or -1 if the chest is full
     */
    private int getEmptySlotIn(IInventory chest) {
        for (int i = 0; i < chest.getSizeInventory(); i++) {
            if (chest.getStackInSlot(i) == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {

    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 15;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
        itemList.add(new ItemStack(item, 1, 0));
        itemList.add(new ItemStack(item, 1, 1));
        itemList.add(new ItemStack(item, 1, 2));
        itemList.add(new ItemStack(item, 1, 3));
    }
}
