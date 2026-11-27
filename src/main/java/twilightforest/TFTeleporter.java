package twilightforest;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.common.FMLLog;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import twilightforest.world.TFWorld;

public class TFTeleporter extends Teleporter {

    private static final int PORTAL_SEARCH_RADIUS = 16;
    private static final int PORTAL_SEARCH_HEIGHT = 128;
    private static final int MIN_PORTAL_HEIGHT = 30;
    private static final int MAX_PORTAL_HEIGHT = PORTAL_SEARCH_HEIGHT - 10;
    private static final int PORTAL_FRAME_MIN = -1;
    private static final int PORTAL_FRAME_MAX = 2;
    private static final int PORTAL_EMERGENCE_VERTICAL_TOLERANCE = 5;

    private static final Block[] NATURE_BLOCKS = { Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.tallgrass,
            Blocks.red_flower, Blocks.yellow_flower };

    protected WorldServer myWorld;
    protected Random rand;

    public TFTeleporter(WorldServer par1WorldServer) {
        super(par1WorldServer);
        myWorld = par1WorldServer;
        if (this.rand == null) {
            this.rand = new Random();
        }
    }

    /**
     * Place an entity in a nearby portal, creating one if necessary.
     */
    public void placeInPortal(Entity entity, double x, double y, double z, float facing) {
        if (!this.placeInExistingPortal(entity, x, y, z, facing)) {
            // if we're in enforced progression mode, check the biomes for safety
            if (entity.worldObj.getGameRules().getGameRuleBooleanValue(TwilightForestMod.ENFORCED_PROGRESSION_RULE)) {
                int px = MathHelper.floor_double(entity.posX);
                int pz = MathHelper.floor_double(entity.posZ);
                if (!isSafeBiomeAt(px, pz, entity)) {
                    FMLLog.fine("[TwilightForest] Portal destination looks unsafe, rerouting!");

                    ChunkCoordinates safeCoords = findSafeCoords(200, px, pz, entity);

                    if (safeCoords != null) {
                        entity.setLocationAndAngles(safeCoords.posX, entity.posY, safeCoords.posZ, 90.0F, 0.0F);
                        x = safeCoords.posX;
                        z = safeCoords.posZ;

                        FMLLog.fine("[TwilightForest] Safely rerouted!");
                    } else {
                        FMLLog.fine(
                                "[TwilightForest] Did not find a safe spot at first try, trying again with longer range.");
                        safeCoords = findSafeCoords(400, px, pz, entity);
                        if (safeCoords != null) {
                            entity.setLocationAndAngles(safeCoords.posX, entity.posY, safeCoords.posZ, 90.0F, 0.0F);
                            x = safeCoords.posX;
                            z = safeCoords.posZ;

                            FMLLog.fine(
                                    "[TwilightForest] Safely rerouted to long range portal.  Return trip not guaranteed.");
                        } else {
                            FMLLog.fine("[TwilightForest] Did not find a safe spot.");
                        }
                    }
                }
            }
            this.makePortal(entity);
            this.placeInExistingPortal(entity, x, y, z, facing);
        }
    }

    /**
     * Find some safe coords within range of the destination coords, or return null.
     */
    private ChunkCoordinates findSafeCoords(int range, int x, int z, Entity entity) {
        if (isSafeBiomeAt(x, z, entity)) {
            return new ChunkCoordinates(x, 100, z);
        }
        Coord2D coords = new Coord2D(0, 0);
        // 25 tries covers a 2 level spiral
        int step = range / 2;
        for (int i = 0; i < 25; i++) {
            coords = coords.spiralNext(step);
            int dx = x + coords.x();
            int dz = z + coords.z();
            if (isSafeBiomeAt(dx, dz, entity)) {
                return new ChunkCoordinates(dx, 100, dz);
            }
        }
        return null;
    }

    /**
     * Check if the destination is safe
     */
    boolean isSafeBiomeAt(int x, int z, Entity entity) {
        BiomeGenBase biomeAt = myWorld.getBiomeGenForCoords(x, z);

        if (biomeAt instanceof TFBiomeBase tfBiome && entity instanceof EntityPlayerMP player) {
            return tfBiome.doesPlayerHaveRequiredAchievement(player);
        }

        // I guess it's safe, or maybe I just have no way to be sure!
        return true;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double par3, double par5, double par7, float par9) {
        return placeInExistingPortal(entity);
    }

    public boolean placeInExistingPortal(Entity entity) {
        Coord2D spiral = new Coord2D(0, 0);
        int portalX = 0;
        int portalY = -1;
        int portalZ = 0;
        double finalDist = -1D;
        int maxRad = 200;
        int baseX = MathHelper.floor_double(entity.posX);
        int baseZ = MathHelper.floor_double(entity.posZ);

        while (spiral.rad() <= maxRad) {
            // spiral outward from entity position until we hit a column with portal in it
            //
            // this could potentially be further optimized if we assume that most people won't be making noodle portals
            // then we could run a sparse check by calling spiralNext with step size 2
            // we only do 1/4 the work and still catch all default portals
            // and only do a full spiral if it fails
            int x = spiral.x() + baseX;
            int z = spiral.z() + baseZ;
            for (int y = TFWorld.MAXHEIGHT - 1; y >= 0; y--) {
                // scan for portals from top of the world
                if (!isBlockPortal(myWorld, x, y, z)) {
                    continue;
                }
                // traverse to the bottom of portal stack
                for (; isBlockPortal(myWorld, x, y - 1, z); y--) {}
                double d1 = x + 0.5D - entity.posX;
                double d3 = z + 0.5D - entity.posZ;
                double d5 = y + 0.5D - entity.posY;
                double sqDist = d1 * d1 + d3 * d3 + d5 * d5;
                if (finalDist < 0 || sqDist < finalDist) {
                    finalDist = sqDist;
                    portalX = x;
                    portalY = y;
                    portalZ = z;
                    maxRad = spiral.rad();
                }
            }
            spiral = spiral.spiralNext();
        }

        // no portal found within range
        if (finalDist == -1) {
            return false;
        }

        for (int dy = 1; dy >= -PORTAL_EMERGENCE_VERTICAL_TOLERANCE;) {
            spiral = new Coord2D(0, 0);
            while (spiral.rad() <= TwilightForestMod.portalMaxSize) {
                // spiral outward from portal column until we find a viable place to deposit entity
                // checking a box 2*MS+1 x 2*PEVT+1 x 2*MS+1 around the portal block
                // prioritizes space above portal
                int x = portalX + spiral.x();
                int y = portalY + dy;
                int z = portalZ + spiral.z();
                if (isSafeExit(x, y, z)) {
                    entity.setLocationAndAngles(x + 0.5, y, z + 0.5, entity.rotationYaw, 0.0F);
                    entity.motionX = entity.motionY = entity.motionZ = 0.0D;
                    return true;
                }
                spiral = spiral.spiralNext();
            }
            // check layers from portal up, then from portal down
            dy = (dy > 0) ? ((dy < PORTAL_EMERGENCE_VERTICAL_TOLERANCE) ? dy + 1 : 0) : dy - 1;
        }
        return false;
    }

    private boolean isSafeExit(int x, int y, int z) {
        // the exit needs a supporting block and 2 blocks of headroom
        if (y <= 0 || y + 1 >= myWorld.getActualHeight()) {
            return false;
        }

        // Must stand on solid ground
        if (!myWorld.getBlock(x, y - 1, z).getMaterial().isSolid()) return false;

        // Space for player
        if (myWorld.getBlock(x, y, z).getMaterial().isSolid()) return false;
        if (myWorld.getBlock(x, y + 1, z).getMaterial().isSolid()) return false;

        // Must not be inside portal blocks
        if (isBlockPortal(myWorld, x, y, z)) return false;
        if (isBlockPortal(myWorld, x, y + 1, z)) return false;

        return true;
    }

    /**
     * Is this block a Twilight Forest portal?
     */
    public boolean isBlockPortal(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == TFBlocks.portal;
    }

    @Override
    public boolean makePortal(Entity entity) {
        ChunkCoordinates spot = findPortalCoords(entity, true);

        if (spot != null) {
            FMLLog.info("[TwilightForest] Found ideal portal spot");
            makePortalAt(myWorld, spot.posX, spot.posY, spot.posZ);
            return true;
        } else {
            FMLLog.info("[TwilightForest] Did not find ideal portal spot, shooting for okay one");
            spot = findPortalCoords(entity, false);
            if (spot != null) {
                FMLLog.info("[TwilightForest] Found okay portal spot");
                makePortalAt(myWorld, spot.posX, spot.posY, spot.posZ);
                return true;
            }
        }

        // well I don't think we can actally just return false and fail here
        FMLLog.info("[TwilightForest] Did not even find an okay portal spot, just making a random one");

        // adjust the portal height based on what world we're traveling to
        double yFactor = myWorld.provider.dimensionId == 0 ? 2 : 0.5;
        // modified copy of base Teleporter method:
        int entityX = MathHelper.floor_double(entity.posX);
        int entityY = MathHelper.floor_double(entity.posY * yFactor);
        int entityZ = MathHelper.floor_double(entity.posZ);

        makePortalAt(myWorld, entityX, entityY, entityZ);

        return false;
    }

    public ChunkCoordinates findPortalCoords(Entity entity, boolean ideal) {
        // adjust the portal height based on what world we're traveling to
        double yFactor = myWorld.provider.dimensionId == 0 ? 2 : 0.5;
        // modified copy of base Teleporter method:
        int entityX = MathHelper.floor_double(entity.posX);
        int entityZ = MathHelper.floor_double(entity.posZ);

        double spotWeight = -1D;

        ChunkCoordinates spot = null;

        int range = PORTAL_SEARCH_RADIUS;
        for (int rx = entityX - range; rx <= entityX + range; rx++) {
            double xWeight = (rx + 0.5D) - entity.posX;
            for (int rz = entityZ - range; rz <= entityZ + range; rz++) {
                double zWeight = (rz + 0.5D) - entity.posZ;

                for (int ry = PORTAL_SEARCH_HEIGHT - 1; ry >= 0; ry--) {
                    if (!myWorld.isAirBlock(rx, ry, rz)) {
                        continue;
                    }
                    for (; ry > 0 && myWorld.isAirBlock(rx, ry - 1, rz); ry--) {}

                    if (ideal ? isIdealPortal(rx, rz, ry) : isOkayPortal(rx, rz, ry)) {
                        double yWeight = (ry + 0.5D) - entity.posY * yFactor;
                        double rPosWeight = xWeight * xWeight + yWeight * yWeight + zWeight * zWeight;

                        if (spotWeight < 0.0D || rPosWeight < spotWeight) {
                            spotWeight = rPosWeight;
                            spot = new ChunkCoordinates(rx, ry, rz);
                        }
                    }
                }
            }
        }

        return spot;
    }

    public boolean isIdealPortal(int rx, int rz, int ry) {
        return isPortalLocationSuitable(rx, rz, ry, true);
    }

    public boolean isOkayPortal(int rx, int rz, int ry) {
        return isPortalLocationSuitable(rx, rz, ry, false);
    }

    private boolean isPortalLocationSuitable(int x, int z, int y, boolean requireGrass) {
        for (int offsetZ = 0; offsetZ < 4; offsetZ++) {
            for (int offsetX = 0; offsetX < 4; offsetX++) {
                for (int offsetY = -1; offsetY < 3; offsetY++) {
                    Material material = myWorld.getBlock(x + offsetX - 1, y + offsetY, z + offsetZ - 1).getMaterial();
                    if (offsetY == -1) {
                        if (requireGrass ? material != Material.grass : !material.isSolid()) {
                            return false;
                        }
                    } else if (!material.isReplaceable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void makePortalAt(World world, int px, int py, int pz) {
        // keep the original 128 block portal search/build range for existing portal compatibility
        py = Math.max(MIN_PORTAL_HEIGHT, Math.min(MAX_PORTAL_HEIGHT, py));

        // sink the portal 1 into the ground
        py--;

        // grass all around it, in the original north to south placement order
        for (int dz = PORTAL_FRAME_MIN; dz <= PORTAL_FRAME_MAX; dz++) {
            for (int dx = PORTAL_FRAME_MIN; dx <= PORTAL_FRAME_MAX; dx++) {
                if (dx == PORTAL_FRAME_MIN || dx == PORTAL_FRAME_MAX
                        || dz == PORTAL_FRAME_MIN
                        || dz == PORTAL_FRAME_MAX) {
                    world.setBlock(px + dx, py, pz + dz, Blocks.grass);
                }
            }
        }

        // dirt under it
        for (int dz = 0; dz < 2; dz++) {
            for (int dx = 0; dx < 2; dx++) {
                world.setBlock(px + dx, py - 1, pz + dz, Blocks.dirt);
            }
        }

        // portal in it
        for (int dz = 0; dz < 2; dz++) {
            for (int dx = 0; dx < 2; dx++) {
                world.setBlock(px + dx, py, pz + dz, TFBlocks.portal, 0, 2);
            }
        }

        // meh, let's just make a bunch of air over it for 4 squares
        for (int dx = PORTAL_FRAME_MIN; dx <= PORTAL_FRAME_MAX; dx++) {
            for (int dz = PORTAL_FRAME_MIN; dz <= PORTAL_FRAME_MAX; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    world.setBlock(px + dx, py + dy, pz + dz, Blocks.air);
                }
            }
        }

        // finally, nature decorations! keep the same draw and placement order
        for (int dz = PORTAL_FRAME_MIN; dz <= PORTAL_FRAME_MAX; dz++) {
            for (int dx = PORTAL_FRAME_MIN; dx <= PORTAL_FRAME_MAX; dx++) {
                if (dx == PORTAL_FRAME_MIN || dx == PORTAL_FRAME_MAX
                        || dz == PORTAL_FRAME_MIN
                        || dz == PORTAL_FRAME_MAX) {
                    world.setBlock(px + dx, py + 1, pz + dz, randNatureBlock(world.rand), 0, 2);
                }
            }
        }
    }

    public Block randNatureBlock(Random random) {
        return NATURE_BLOCKS[random.nextInt(NATURE_BLOCKS.length)];
    }
}
