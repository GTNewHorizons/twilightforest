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
            int crumbled = doCrumble(player.worldObj, player);

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
    private int doCrumble(World world, EntityPlayer player) {
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

        return crumbleBlocksInAABB(world, player, crumbleBox);
    }

    /**
     * Crumble block in the box
     */
    private int crumbleBlocksInAABB(World world, EntityPlayer player, AxisAlignedBB par1AxisAlignedBB) {
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
                    crumbled += crumbleBlock(world, player, dx, dy, dz);
                }
            }
        }

        return crumbled;
    }

    /**
     * Crumble a specific block.
     */
    private int crumbleBlock(World world, EntityPlayer player, int dx, int dy, int dz) {
        int cost = 0;

        Block currentID = world.getBlock(dx, dy, dz);

        if (currentID != Blocks.air) {
            int currentMeta = world.getBlockMetadata(dx, dy, dz);

            if (currentID == Blocks.stonebrick && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.stonebrick, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.monster_egg && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.monster_egg, 1, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.monster_egg && currentMeta == 2 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.monster_egg, 4, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.mazestone && currentMeta == 1 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.mazestone, 4, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.underBrick && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.underBrick, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.towerWood && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.towerWood, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.deadrock && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.deadrock, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.castleBlock && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.castleBlock, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.nagastonePillar && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.nagastonePillarWeathered, currentMeta, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.nagastoneEtched && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlockMetadataWithNotify(dx, dy, dz, 2, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.nagastoneStairsLeft && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.nagastoneStairsWeatheredLeft, currentMeta, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == TFBlocks.nagastoneStairsRight && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, TFBlocks.nagastoneStairsWeatheredRight, currentMeta, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.stone && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.cobblestone, 0, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.ice && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.water, 0, 1);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if ((currentID == Blocks.cobblestone || currentID == Blocks.stone_stairs
                    || (currentID == Blocks.double_stone_slab && currentMeta == 3))
                    && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.gravel, 0, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.stone_slab && (currentMeta == 0 || currentMeta == 8)
                    && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlockMetadataWithNotify(dx, dy, dz, currentMeta + 3, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.double_stone_slab && currentMeta == 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlockMetadataWithNotify(dx, dy, dz, 3, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.stone_slab && (currentMeta == 1 || currentMeta == 9)
                    && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                if (world.rand.nextBoolean()) world.setBlock(dx, dy, dz, Blocks.sand, 0, 3);
                else world.setBlockToAir(dx, dy, dz);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.stone_slab && (currentMeta == 3 || currentMeta == 11)
                    && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                if (world.rand.nextBoolean()) world.setBlock(dx, dy, dz, Blocks.gravel, 0, 3);
                else world.setBlockToAir(dx, dy, dz);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (((currentID == Blocks.sandstone && currentMeta == 0) || currentID == Blocks.sandstone_stairs
                    || (currentID == Blocks.double_stone_slab && currentMeta == 1))
                    && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.sand, 0, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.sandstone && currentMeta > 0 && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.sandstone, 0, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if ((currentID == Blocks.grass || currentID == Blocks.mycelium
                    || (currentID == Blocks.dirt && currentMeta == 2)) && world.rand.nextInt(CHANCE_CRUMBLE) == 0) {
                world.setBlock(dx, dy, dz, Blocks.dirt, 0, 3);
                world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                cost++;
            }

            if (currentID == Blocks.gravel || (currentID == Blocks.dirt && currentMeta != 2)
                    || currentID == Blocks.sand
                    || currentID == Blocks.clay
                    || currentID == Blocks.glass
                    || currentID == Blocks.glass_pane
                    || currentID == Blocks.stained_glass
                    || currentID == Blocks.stained_glass_pane
                    || currentID == Blocks.glowstone) {
                if (currentID.canHarvestBlock(player, currentMeta) && world.rand.nextInt(CHANCE_HARVEST) == 0) {
                    world.setBlock(dx, dy, dz, Blocks.air, 0, 3);
                    currentID.harvestBlock(world, player, dx, dy, dz, currentMeta);
                    world.playAuxSFX(2001, dx, dy, dz, Block.getIdFromBlock(currentID) + (currentMeta << 12));
                    cost++;
                }
            }

        }

        return cost;
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
