package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFRoots;
import twilightforest.block.TFBlocks;

public class ItemTFOreMeter extends ItemTF {

    protected ItemTFOreMeter() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player) {

        int useX = MathHelper.floor_double(player.posX);
        int useZ = MathHelper.floor_double(player.posZ);

        if (!world.isRemote) {
            countOreInArea(player, world, useX, useZ, 3);
        }

        return super.onItemRightClick(par1ItemStack, world, player);
    }

    private static final class OreCounts {

        int stone, dirt, gravel, coal, iron, gold, diamond, lapis, redstone, exposedDiamond, roots, oreRoots;

        int total() {
            return stone + dirt + gravel + coal + iron + gold + diamond + lapis + redstone + roots + oreRoots;
        }
    }

    private void countOreInArea(EntityPlayer player, World world, int useX, int useZ, int radius) {
        int centerX = useX >> 4;
        int centerZ = useZ >> 4;
        OreCounts counts = new OreCounts();

        // a single pass replaces 65536 block scans for every ore type
        for (int cx = centerX - radius; cx <= centerX + radius; cx++) {
            for (int cz = centerZ - radius; cz <= centerZ + radius; cz++) {
                Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 256; y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block == Blocks.stone) counts.stone++;
                            else if (block == Blocks.dirt) counts.dirt++;
                            else if (block == Blocks.gravel) counts.gravel++;
                            else if (block == Blocks.coal_ore) counts.coal++;
                            else if (block == Blocks.iron_ore) counts.iron++;
                            else if (block == Blocks.gold_ore) counts.gold++;
                            else if (block == Blocks.lapis_ore) counts.lapis++;
                            else if (block == Blocks.redstone_ore) counts.redstone++;
                            else if (block == Blocks.diamond_ore) {
                                counts.diamond++;
                                int blockX = (cx << 4) + x;
                                int blockZ = (cz << 4) + z;
                                if (isExposed(world, blockX, y, blockZ)) counts.exposedDiamond++;
                            } else if (block == TFBlocks.root) {
                                int meta = chunk.getBlockMetadata(x, y, z);
                                if (meta == BlockTFRoots.ROOT_META) counts.roots++;
                                if (meta == BlockTFRoots.OREROOT_META) counts.oreRoots++;
                            }
                        }
                    }
                }
            }
        }
        // this tool previously scanned all nearby chunks but never displayed the results
        player.addChatMessage(
                new ChatComponentText(
                        "Ore Meter: " + (2 * radius + 1)
                                + "x"
                                + (2 * radius + 1)
                                + " chunks, "
                                + counts.total()
                                + " measured blocks"));
        player.addChatMessage(
                new ChatComponentText(
                        "Coal " + counts.coal
                                + " | Iron "
                                + counts.iron
                                + " | Gold "
                                + counts.gold
                                + " | Diamond "
                                + counts.diamond
                                + " (exposed "
                                + counts.exposedDiamond
                                + ")"));
        player.addChatMessage(
                new ChatComponentText(
                        "Lapis " + counts.lapis
                                + " | Redstone "
                                + counts.redstone
                                + " | Roots "
                                + counts.roots
                                + " | Ore roots "
                                + counts.oreRoots));
    }

    public float percent(int count, int total) {
        return total > 0 ? (float) count / total * 100F : 0F;
    }

    public int countBlockInChunk(World world, Block stone, int cx, int cz) {

        Chunk chunk = world.getChunkFromChunkCoords(cx, cz);

        int count = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    if (chunk.getBlock(x, y, z) == stone) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public int countBlockInChunk(World world, Block blockID, int meta, int cx, int cz) {

        Chunk chunk = world.getChunkFromChunkCoords(cx, cz);

        int count = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    if (chunk.getBlock(x, y, z) == blockID && chunk.getBlockMetadata(x, y, z) == meta) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private static boolean isExposed(World world, int x, int y, int z) {
        // check all 6 faces, the previous version checked Y twice and omitted Z
        return world.isAirBlock(x + 1, y, z) || world.isAirBlock(x - 1, y, z)
                || world.isAirBlock(x, y + 1, z)
                || world.isAirBlock(x, y - 1, z)
                || world.isAirBlock(x, y, z + 1)
                || world.isAirBlock(x, y, z - 1);
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }
}
