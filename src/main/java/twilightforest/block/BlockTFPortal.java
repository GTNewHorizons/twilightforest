package twilightforest.block;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import twilightforest.Coord2D;
import twilightforest.TFAchievementPage;
import twilightforest.TFTeleporter;
import twilightforest.TwilightForestMod;

public class BlockTFPortal extends BlockBreakable {

    private static final List<Coord2D> dirs = Arrays
            .asList(new Coord2D(1, 0), new Coord2D(0, 1), new Coord2D(-1, 0), new Coord2D(0, -1));

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
        Set<Coord2D> poolMap = generatePoolMap(world, dx, dy, dz);
        if (poolMap != null) {
            world.addWeatherEffect(new EntityLightningBolt(world, dx, dy, dz));
            transmuteWaterToPortal(world, dx, dy, dz, poolMap);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the pool it's been given to all portal.
     */
    public void transmuteWaterToPortal(World world, int dx, int dy, int dz, Set<Coord2D> poolMap) {
        for (Coord2D c : poolMap) {
            world.setBlock(c.x() + dx, dy, c.z() + dz, TFBlocks.portal, 0, 2);
        }

        // System.out.println("Transmuting water to portal");
    }

    /**
     * Constructs a set of pool coordinates using flood fill from the generator block. Returns null if pool is too big
     * or has invalid border.
     */
    public static Set<Coord2D> generatePoolMap(World world, int dx, int dy, int dz) {

        Set<Coord2D> poolMap = new HashSet<>();
        Deque<Coord2D> toProcess = new ArrayDeque<>();
        Set<Coord2D> boundary = new HashSet<>();
        int minX = 0, maxX = 0, minZ = 0, maxZ = 0;
        toProcess.add(new Coord2D(0, 0));

        while (!toProcess.isEmpty()) {
            Coord2D block = toProcess.removeLast();
            int x = dx + block.x();
            int z = dz + block.z();
            if (world.getBlock(x, dy, z).getMaterial() == Material.water
                    && world.getBlock(x, dy - 1, z).getMaterial().isSolid()) {
                // duplicate protection - cheaper than toProcess.contains check later
                if (!poolMap.add(block)) continue;

                if (block.x() < minX) minX = block.x();
                else if (block.x() > maxX) maxX = block.x();
                if (block.z() < minZ) minZ = block.z();
                else if (block.z() > maxZ) maxZ = block.z();
                // cheaper to do this here than potentially building an invalid portal with 4 times the blocks
                if (maxX - minX + 1 > TwilightForestMod.portalMaxSize
                        || maxZ - minZ + 1 > TwilightForestMod.portalMaxSize)
                    return null;

                for (Coord2D d : dirs) {
                    Coord2D neighbor = block.add(d);
                    if (!poolMap.contains(neighbor) && !boundary.contains(neighbor)) {
                        toProcess.add(neighbor);
                    }
                }
            } else if (isGrassOrDirt(world, x, dy, z) && isNatureBlock(world, x, dy + 1, z)) {
                boundary.add(block);
            } else {
                return null;
            }
        }
        return poolMap;
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
     * Each twilight portal pool block should touch only other portals, dirt, or grass in cardinal directions. If this
     * is not true, delete this block, presumably causing a chain reaction. Notably the original implementation didn't
     * check for floor validity, so neither does this.
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
        else {
            int grassSides = 0;
            if (isGrassOrDirt(world, x - 1, y, z)) grassSides++;
            if (isGrassOrDirt(world, x + 1, y, z)) grassSides++;
            if (isGrassOrDirt(world, x, y, z - 1)) grassSides++;
            if (isGrassOrDirt(world, x, y, z + 1)) grassSides++;
            good = ((portalSides + grassSides) == 4);
        }
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
