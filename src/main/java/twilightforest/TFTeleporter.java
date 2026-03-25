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

    protected WorldServer myWorld;
    protected Random rand;
    private final int portalEmergenceVerticalTolerance = 5;

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
        if (isSafeBiomeAt(x, z, entity)) return new ChunkCoordinates(x, 100, z);
        Coord2D coords = new Coord2D(0, 0);
        int dx, dz;
        // 25 tries covers a 2 level spiral
        int step = range / 2;
        for (int i = 0; i < 25; i++) {
            coords = coords.spiralNext(step);
            dx = x + coords.x();
            dz = z + coords.z();
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

        } else {
            // I guess it's safe, or maybe I just have no way to be sure!
            return true;
        }
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
            // then we could run a sparse check by making spiralNext take a step argument
            // with step size 2 we only do 1/4 the work and still catch all default portals
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
        if (finalDist == -1) return false;

        for (int dy = 1; dy > -portalEmergenceVerticalTolerance;) {
            spiral = new Coord2D(0, 0);
            while (spiral.rad() < TwilightForestMod.portalMaxSize) {
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
            dy = (dy > 0) ? ((dy < portalEmergenceVerticalTolerance) ? dy + 1 : 0) : dy - 1;
        }
        return false;
    }

    private boolean isSafeExit(int x, int y, int z) {
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
     * Is this block either our portal or an existing nether portal?
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

        byte range = 16;
        for (int rx = entityX - range; rx <= entityX + range; rx++) {
            double xWeight = (rx + 0.5D) - entity.posX;
            for (int rz = entityZ - range; rz <= entityZ + range; rz++) {
                double zWeight = (rz + 0.5D) - entity.posZ;

                for (int ry = 128 - 1; ry >= 0; ry--) {
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
        for (int potentialZ = 0; potentialZ < 4; potentialZ++) {
            for (int potentialX = 0; potentialX < 4; potentialX++) {
                for (int potentialY = -1; potentialY < 3; potentialY++) {
                    int tx = rx + (potentialX - 1);
                    int ty = ry + potentialY;
                    int tz = rz + (potentialZ - 1);
                    if (potentialY == -1 && myWorld.getBlock(tx, ty, tz).getMaterial() != Material.grass
                            || potentialY >= 0 && !myWorld.getBlock(tx, ty, tz).getMaterial().isReplaceable()) {
                        return false;
                    }
                }

            }

        }
        return true;
    }

    public boolean isOkayPortal(int rx, int rz, int ry) {
        for (int potentialZ = 0; potentialZ < 4; potentialZ++) {
            for (int potentialX = 0; potentialX < 4; potentialX++) {
                for (int potentialY = -1; potentialY < 3; potentialY++) {
                    int tx = rx + (potentialX - 1);
                    int ty = ry + potentialY;
                    int tz = rz + (potentialZ - 1);
                    if (potentialY == -1 && !myWorld.getBlock(tx, ty, tz).getMaterial().isSolid()
                            || potentialY >= 0 && !myWorld.getBlock(tx, ty, tz).getMaterial().isReplaceable()) {
                        return false;
                    }
                }

            }

        }
        return true;
    }

    private void makePortalAt(World world, int px, int py, int pz) {

        if (py < 30) {
            py = 30;
        }
        world.getClass();
        if (py > 128 - 10) {
            world.getClass();
            py = 128 - 10;
        }

        // sink the portal 1 into the ground
        py--;

        // grass all around it
        world.setBlock(px - 1, py + 0, pz - 1, Blocks.grass);
        world.setBlock(px + 0, py + 0, pz - 1, Blocks.grass);
        world.setBlock(px + 1, py + 0, pz - 1, Blocks.grass);
        world.setBlock(px + 2, py + 0, pz - 1, Blocks.grass);

        world.setBlock(px - 1, py + 0, pz + 0, Blocks.grass);
        world.setBlock(px + 2, py + 0, pz + 0, Blocks.grass);

        world.setBlock(px - 1, py + 0, pz + 1, Blocks.grass);
        world.setBlock(px + 2, py + 0, pz + 1, Blocks.grass);

        world.setBlock(px - 1, py + 0, pz + 2, Blocks.grass);
        world.setBlock(px + 0, py + 0, pz + 2, Blocks.grass);
        world.setBlock(px + 1, py + 0, pz + 2, Blocks.grass);
        world.setBlock(px + 2, py + 0, pz + 2, Blocks.grass);

        // dirt under it
        world.setBlock(px + 0, py - 1, pz + 0, Blocks.dirt);
        world.setBlock(px + 1, py - 1, pz + 0, Blocks.dirt);
        world.setBlock(px + 0, py - 1, pz + 1, Blocks.dirt);
        world.setBlock(px + 1, py - 1, pz + 1, Blocks.dirt);

        // portal in it
        world.setBlock(px + 0, py + 0, pz + 0, TFBlocks.portal, 0, 2);
        world.setBlock(px + 1, py + 0, pz + 0, TFBlocks.portal, 0, 2);
        world.setBlock(px + 0, py + 0, pz + 1, TFBlocks.portal, 0, 2);
        world.setBlock(px + 1, py + 0, pz + 1, TFBlocks.portal, 0, 2);

        // meh, let's just make a bunch of air over it for 4 squares
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                for (int dy = 1; dy <= 5; dy++) {
                    world.setBlock(px + dx, py + dy, pz + dz, Blocks.air);
                }
            }
        }

        // finally, "nature decorations"!
        world.setBlock(px - 1, py + 1, pz - 1, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 0, py + 1, pz - 1, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 1, py + 1, pz - 1, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 2, py + 1, pz - 1, randNatureBlock(world.rand), 0, 2);

        world.setBlock(px - 1, py + 1, pz + 0, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 2, py + 1, pz + 0, randNatureBlock(world.rand), 0, 2);

        world.setBlock(px - 1, py + 1, pz + 1, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 2, py + 1, pz + 1, randNatureBlock(world.rand), 0, 2);

        world.setBlock(px - 1, py + 1, pz + 2, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 0, py + 1, pz + 2, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 1, py + 1, pz + 2, randNatureBlock(world.rand), 0, 2);
        world.setBlock(px + 2, py + 1, pz + 2, randNatureBlock(world.rand), 0, 2);
    }

    public Block randNatureBlock(Random random) {
        Block[] block = { Blocks.brown_mushroom, Blocks.red_mushroom, Blocks.tallgrass, Blocks.red_flower,
                Blocks.yellow_flower };

        return block[random.nextInt(block.length)];
    }

}
