package twilightforest.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;

public class ItemTFCrumbleHorn extends ItemTF {

    private static final int CHANCE_HARVEST = 20;
    private static final int CHANCE_CRUMBLE = 5;

    protected ItemTFCrumbleHorn() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
        this.maxStackSize = 1;
        this.setMaxDamage(1024);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player) {

        player.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));

        world.playSoundAtEntity(player, "mob.sheep.say", 1.0F, 0.8F);

        return par1ItemStack;
    }

    /**
     * Called each tick while using an item.
     *
     * @param stack  The Item being used
     * @param player The Player using the item
     * @param count  The amount of time in tick the item has been used for continuously
     */
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (count > 10 && count % 5 == 0 && !player.worldObj.isRemote) {
            int crumbled = doCrumble(player.worldObj, player, stack);

            if (crumbled > 0) {
                stack.damageItem(crumbled, player);

            }

            player.worldObj.playSoundAtEntity(player, "mob.sheep.say", 1.0F, 0.8F);

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
     * Bleeeat!
     */
    private int doCrumble(World world, EntityPlayer player, ItemStack heldItem) {
        double range = 3.0D;
        double radius = 2.0D;
        Vec3 srcVec = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 lookVec = player.getLookVec();
        Vec3 destVec = srcVec.addVector(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range);

        AxisAlignedBB crumbleBox = AxisAlignedBB.getBoundingBox(
                destVec.xCoord - radius,
                destVec.yCoord - radius,
                destVec.zCoord - radius,
                destVec.xCoord + radius,
                destVec.yCoord + radius,
                destVec.zCoord + radius);

        return crumbleBlocksInAABB(world, player, heldItem, crumbleBox);
    }

    /**
     * Crumble block in the box
     */
    private int crumbleBlocksInAABB(World world, EntityPlayer player, ItemStack heldItem,
            AxisAlignedBB par1AxisAlignedBB) {
        // System.out.println("Destroying blocks in " + par1AxisAlignedBB);

        int minX = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int minY = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int minZ = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int maxX = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int maxY = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int maxZ = MathHelper.floor_double(par1AxisAlignedBB.maxZ);

        int crumbled = 0;

        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = minY; dy <= maxY; ++dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    crumbled += crumbleBlock(world, player, heldItem, dx, dy, dz);
                }
            }
        }

        return crumbled;
    }

    /**
     * Crumble a specific block.
     */
    private int crumbleBlock(World world, EntityPlayer player, ItemStack heldItem, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.air || !world.canMineBlock(player, x, y, z)
                || !player.canPlayerEdit(x, y, z, 1, heldItem)) {
            return 0;
        }

        int meta = world.getBlockMetadata(x, y, z);
        Block replacement = null;
        int replacementMeta = 0;
        int flags = 3;
        boolean metadataOnly = false;
        boolean remove = false;
        boolean harvest = false;

        if (block == Blocks.stonebrick && meta == 0) {
            replacement = Blocks.stonebrick;
            replacementMeta = 2;
        } else if (block == Blocks.monster_egg && (meta == 0 || meta == 2)) {
            replacement = Blocks.monster_egg;
            replacementMeta = meta == 0 ? 1 : 4;
        } else if (block == TFBlocks.mazestone && meta == 1) {
            replacement = TFBlocks.mazestone;
            replacementMeta = 4;
        } else if (block == TFBlocks.underBrick && meta == 0) {
            replacement = TFBlocks.underBrick;
            replacementMeta = 2;
        } else if (block == TFBlocks.towerWood && meta == 0) {
            replacement = TFBlocks.towerWood;
            replacementMeta = 2;
        } else if (block == TFBlocks.deadrock && meta == 0) {
            replacement = TFBlocks.deadrock;
            replacementMeta = 2;
        } else if (block == TFBlocks.castleBlock && meta == 0) {
            replacement = TFBlocks.castleBlock;
            replacementMeta = 2;
        } else if (block == TFBlocks.nagastonePillar) {
            replacement = TFBlocks.nagastonePillarWeathered;
            replacementMeta = meta;
        } else if (block == TFBlocks.nagastoneEtched && meta == 0) {
            metadataOnly = true;
            replacementMeta = 2;
        } else if (block == TFBlocks.nagastoneStairsLeft) {
            replacement = TFBlocks.nagastoneStairsWeatheredLeft;
            replacementMeta = meta;
        } else if (block == TFBlocks.nagastoneStairsRight) {
            replacement = TFBlocks.nagastoneStairsWeatheredRight;
            replacementMeta = meta;
        } else if (block == Blocks.stone) {
            replacement = Blocks.cobblestone;
        } else if (block == Blocks.ice) {
            replacement = Blocks.water;
            flags = 1;
        } else if (block == Blocks.cobblestone || block == Blocks.stone_stairs
                || (block == Blocks.double_stone_slab && meta == 3)) {
                    replacement = Blocks.gravel;
                } else
            if (block == Blocks.stone_slab && (meta == 0 || meta == 8)) {
                metadataOnly = true;
                replacementMeta = meta + 3;
            } else if (block == Blocks.double_stone_slab && meta == 0) {
                metadataOnly = true;
                replacementMeta = 3;
            } else if (block == Blocks.stone_slab && (meta == 1 || meta == 9)) {
                replacement = Blocks.sand;
                remove = true;
            } else if (block == Blocks.stone_slab && (meta == 3 || meta == 11)) {
                replacement = Blocks.gravel;
                remove = true;
            } else if ((block == Blocks.sandstone && meta == 0) || block == Blocks.sandstone_stairs
                    || (block == Blocks.double_stone_slab && meta == 1)) {
                        replacement = Blocks.sand;
                    } else
                if (block == Blocks.sandstone && meta > 0) {
                    replacement = Blocks.sandstone;
                } else if (block == Blocks.grass || block == Blocks.mycelium || (block == Blocks.dirt && meta == 2)) {
                    replacement = Blocks.dirt;
                } else if (block == Blocks.gravel || (block == Blocks.dirt && meta != 2)
                        || block == Blocks.sand
                        || block == Blocks.clay
                        || block == Blocks.glass
                        || block == Blocks.glass_pane
                        || block == Blocks.stained_glass
                        || block == Blocks.stained_glass_pane
                        || block == Blocks.glowstone) {
                            if (!block.canHarvestBlock(player, meta)) {
                                return 0;
                            }
                            harvest = true;
                        } else {
                            return 0;
                        }

        if (world.rand.nextInt(harvest ? CHANCE_HARVEST : CHANCE_CRUMBLE) != 0) {
            return 0;
        }

        // a tool that changes or destroys blocks must respect other mods protection hooks
        if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(x, y, z, world, block, meta, player))) {
            return 0;
        }

        boolean changed;
        if (harvest) {
            changed = world.setBlock(x, y, z, Blocks.air, 0, 3);
        } else if (metadataOnly) {
            changed = world.setBlockMetadataWithNotify(x, y, z, replacementMeta, 3);
        } else if (remove && !world.rand.nextBoolean()) {
            changed = world.setBlockToAir(x, y, z);
        } else {
            changed = world.setBlock(x, y, z, replacement, replacementMeta, flags);
        }

        if (!changed) {
            return 0;
        }
        if (harvest) {
            block.harvestBlock(world, player, x, y, z, meta);
        }
        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
        return 1;
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
