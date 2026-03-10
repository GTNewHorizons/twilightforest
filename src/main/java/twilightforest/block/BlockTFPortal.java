package twilightforest.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TFAchievementPage;
import twilightforest.TFTeleporter;
import twilightforest.TwilightForestMod;

public class BlockTFPortal extends BlockBreakable {

    private static final int PORTAL_MIN_SIZE = 2;
    private static final int PORTAL_MAX_SIZE = 5;

    public BlockTFPortal() {
        super("TFPortal", Material.portal, false);
        this.setHardness(-1F);
        this.setStepSound(Block.soundTypeGlass);
        this.setLightLevel(0.75F);
        // this.setCreativeTab(TFItems.creativeTab);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.portal.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        // don't load anything
    }

    /**
     * The function name says it all. Tries to create a portal at the specified location. In this case, the location is
     * the location of a pool with very specific parameters.
     */
    public boolean tryToCreatePortal(World world, int dx, int dy, int dz) {
        if (isGoodPortalPool(world, dx, dy, dz)) {
            world.addWeatherEffect(new EntityLightningBolt(world, dx, dy, dz));
            transmuteWaterToPortal(world, dx, dy, dz);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the pool it's been given to all portal. Runs getPositionInPool again, no shape check, assumes square
     */
    public void transmuteWaterToPortal(World world, int dx, int dy, int dz) {

        int[] positionInPool = getPositionInPool(world, dx, dy, dz);
        int minX = dx - positionInPool[1];
        int maxX = minX + positionInPool[0] - 1;
        int minZ = dz - positionInPool[2];
        int maxZ = minZ + positionInPool[0] - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.setBlock(x, dy, z, TFBlocks.portal, 0, 2);
            }
        }

        // System.out.println("Transmuting water to portal");
    }

    /**
     * Get coordinates of item within the pool RET: {pool size, offset of generator in x, offset of generator in z}
     */
    public static int[] getPositionInPool(World world, int dx, int dy, int dz) {
        if (world.getBlock(dx, dy, dz).getMaterial() != Material.water) return null;
        final int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        int[] edgeOffsets = { -1, -1, -1, -1 };

        // check for water in cardinal directions until we hit an edge
        for (int i = 0; i < 4; i++) {
            for (int d = 1; d <= PORTAL_MAX_SIZE; d++) {
                if (world.getBlock(dx + d * dirs[i][0], dy, dz + d * dirs[i][1]).getMaterial() != Material.water) {
                    edgeOffsets[i] = d - 1;
                    break;
                }
            }
        }

        for (int d : edgeOffsets) {
            // pool too big, didn't find an edge
            if (d == -1) return null;
        }

        int dimx = 1 + edgeOffsets[0] + edgeOffsets[1];
        int dimz = 1 + edgeOffsets[2] + edgeOffsets[3];

        // not a square
        if (dimx != dimz) return null;

        // pool too big or too small
        if (dimx > PORTAL_MAX_SIZE || dimx < PORTAL_MIN_SIZE) return null;

        return new int[] { dimx, edgeOffsets[0], edgeOffsets[2] };
    }

    /**
     * Returns true if we're in a square pool within acceptable size range, with proper edges
     */
    public static boolean isGoodPortalPool(World world, int dx, int dy, int dz) {
        int[] positionInPool = getPositionInPool(world, dx, dy, dz);
        if (positionInPool == null) return false;

        boolean flag = true;
        int minX = dx - positionInPool[1];
        int maxX = minX + positionInPool[0] - 1;
        int minZ = dz - positionInPool[2];
        int maxZ = minZ + positionInPool[0] - 1;

        // check water and pool floor
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                flag &= world.getBlock(x, dy, z).getMaterial() == Material.water;
                flag &= world.getBlock(x, dy - 1, z).getMaterial().isSolid();
            }
        }

        // check grass edges and nature blocks
        for (int x = minX; x < maxX; x++) {
            flag &= isGrassOrDirt(world, x, dy, minZ - 1);
            flag &= isGrassOrDirt(world, x, dy, maxZ + 1);
            flag &= isNatureBlock(world, x, dy + 1, minZ - 1);
            flag &= isNatureBlock(world, x, dy + 1, maxZ + 1);
        }
        for (int z = minZ; z < maxZ; z++) {
            flag &= isGrassOrDirt(world, minX - 1, dy, z);
            flag &= isGrassOrDirt(world, maxX + 1, dy, z);
            flag &= isNatureBlock(world, minX - 1, dy + 1, z);
            flag &= isNatureBlock(world, maxX + 1, dy + 1, z);
        }

        return flag;
    }

    /**
     * Does the block at this location count as a "nature" block for portal purposes?
     */
    public static boolean isNatureBlock(World world, int dx, int dy, int dz) {
        Material mat = world.getBlock(dx, dy, dz).getMaterial();

        if (mat == Material.plants || mat == Material.vine || mat == Material.leaves) {
            return true;
        }

        // plants = tallgrass
        // vine = flower

        return false;
    }

    /**
     * Each twilight portal pool block should have (a) dirt or grass on two neighbouring sides and portal on the other
     * two (b) dirt or grass on one side and portal on the other three (c) portal on all sides If this is not true,
     * delete this block, presumably causing a chain reaction.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block notUsed) {
        boolean good = true;
        int portalSides = 0;
        if (world.getBlock(x - 1, y, z) == this) portalSides++;
        if (world.getBlock(x + 1, y, z) == this) portalSides++;
        if (world.getBlock(x, y, z - 1) == this) portalSides++;
        if (world.getBlock(x, y, z + 1) == this) portalSides++;

        if (portalSides == 4) good = true;
        else if (portalSides == 3) {
            good = isGrassOrDirt(world, x - 1, y, z) || isGrassOrDirt(world, x + 1, y, z)
                    || isGrassOrDirt(world, x, y, z - 1)
                    || isGrassOrDirt(world, x, y, z + 1);
        } else if (portalSides == 2) {
            good = (isGrassOrDirt(world, x - 1, y, z) || isGrassOrDirt(world, x + 1, y, z))
                    && (isGrassOrDirt(world, x, y, z - 1) || isGrassOrDirt(world, x, y, z + 1));
        } else good = false;

        // if we're not good, remove this block
        if (!good) {
            world.setBlock(x, y, z, Blocks.water, 0, 3);
        }
    }

    protected static boolean isGrassOrDirt(World world, int dx, int dy, int dz) {
        Material mat = world.getBlock(dx, dy, dz).getMaterial();
        return mat == Material.grass || mat == Material.ground;
        // grass = grass
        // ground = dirt
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (entity.ridingEntity == null && entity.riddenByEntity == null && entity.timeUntilPortal <= 0) {
            if (entity instanceof EntityPlayerMP playerMP) {

                if (playerMP.timeUntilPortal > 0) {
                    // do not switch dimensions if the player has any time on this thinger
                    playerMP.timeUntilPortal = 10;
                } else {

                    // send to twilight
                    if (playerMP.dimension != TwilightForestMod.dimensionID) {
                        playerMP.triggerAchievement(TFAchievementPage.twilightPortal);
                        // playerMP.triggerAchievement(TFAchievementPage.twilightArrival);
                        FMLLog.info(
                                "[TwilightForest] Player touched the portal block.  Sending the player to dimension "
                                        + TwilightForestMod.dimensionID);

                        playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(
                                playerMP,
                                TwilightForestMod.dimensionID,
                                new TFTeleporter(
                                        playerMP.mcServer.worldServerForDimension(TwilightForestMod.dimensionID)));
                        // playerMP.addExperienceLevel(0);
                        // playerMP.triggerAchievement(TFAchievementPage.twilightPortal);
                        playerMP.triggerAchievement(TFAchievementPage.twilightArrival);

                        // set respawn point for TF dimension to near the arrival portal
                        int spawnX = MathHelper.floor_double(playerMP.posX);
                        int spawnY = MathHelper.floor_double(playerMP.posY);
                        int spawnZ = MathHelper.floor_double(playerMP.posZ);

                        playerMP.setSpawnChunk(
                                new ChunkCoordinates(spawnX, spawnY, spawnZ),
                                true,
                                TwilightForestMod.dimensionID);
                    } else {
                        // System.out.println("Player touched the portal block. Sending the player to dimension 0");
                        // playerMP.travelToDimension(0);
                        playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(
                                playerMP,
                                0,
                                new TFTeleporter(playerMP.mcServer.worldServerForDimension(0)));
                        // playerMP.addExperienceLevel(0);
                    }
                }
            } else {
                if (entity.dimension != TwilightForestMod.dimensionID) {
                    // sendEntityToDimension(entity, TwilightForestMod.dimensionID);
                } else {
                    sendEntityToDimension(entity, 0);
                }
            }
        }

    }

    /**
     * This copy of the entity.travelToDimension method exists so that we can use our own teleporter
     */
    public void sendEntityToDimension(Entity entity, int dimensionID) {
        // transfer a random entity?
        if (!entity.worldObj.isRemote && !entity.isDead) {
            entity.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            int dim = entity.dimension;
            WorldServer worldserver = minecraftserver.worldServerForDimension(dim);
            WorldServer worldserver1 = minecraftserver.worldServerForDimension(dimensionID);
            entity.dimension = dimensionID;
            entity.worldObj.removeEntity(entity);
            entity.isDead = false;
            entity.worldObj.theProfiler.startSection("reposition");
            minecraftserver.getConfigurationManager()
                    .transferEntityToWorld(entity, dim, worldserver, worldserver1, new TFTeleporter(worldserver1));
            entity.worldObj.theProfiler.endStartSection("reloading");
            Entity transferEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), worldserver1);

            if (transferEntity != null) {
                transferEntity.copyDataFrom(entity, true);
                worldserver1.spawnEntityInWorld(transferEntity);
            }

            entity.isDead = true;
            entity.worldObj.theProfiler.endSection();
            worldserver.resetUpdateEntityTick();
            worldserver1.resetUpdateEntityTick();
            entity.worldObj.theProfiler.endSection();
        }
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSoundEffect(
                    i + 0.5D,
                    j + 0.5D,
                    k + 0.5D,
                    "portal.portal",
                    1.0F,
                    random.nextFloat() * 0.4F + 0.8F);
        }
        for (int l = 0; l < 4; l++) {
            double d = i + random.nextFloat();
            double d1 = j + random.nextFloat();
            double d2 = k + random.nextFloat();
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = random.nextInt(2) * 2 - 1;
            d3 = (random.nextFloat() - 0.5D) * 0.5D;
            d4 = (random.nextFloat() - 0.5D) * 0.5D;
            d5 = (random.nextFloat() - 0.5D) * 0.5D;
            if (world.getBlock(i - 1, j, k) == this || world.getBlock(i + 1, j, k) == this) {
                d2 = k + 0.5D + 0.25D * i1;
                d5 = random.nextFloat() * 2.0F * i1;
            } else {
                d = i + 0.5D + 0.25D * i1;
                d3 = random.nextFloat() * 2.0F * i1;
            }
            world.spawnParticle("portal", d, d1, d2, d3, d4, d5);
        }

    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }
}
