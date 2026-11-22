package twilightforest.tileentity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.FMLLog;
import twilightforest.block.BlockTFTowerTranslucent;
import twilightforest.block.TFBlocks;

public class TileEntityTFReverter extends TileEntity {

    private static final int REVERT_CHANCE = 10;
    private static final int SLOW_SCAN_INTERVAL_TICKS = 20;
    private static final int SLOW_SCAN_THRESHOLD = 20;
    private static final int OUTLINE_COUNT = 12;
    private static final int OUTLINE_PARTICLES = 16;

    public int radius = 4;
    public int diameter = 2 * radius + 1;
    public double requiredPlayerRange = 16;
    public Random rand = new Random();
    private int tickCount;

    private boolean slowScan;
    private int ticksSinceChange;

    private Block[] blockData;
    private byte[] metaData;

    /**
     * Determines if this TileEntity requires update calls.
     *
     * @return True if you want updateEntity() to be called, false if not
     */
    @Override
    public boolean canUpdate() {
        return true;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        if (!anyPlayerInRange()) {
            // remove the snapshot when the last player leaves the area
            blockData = null;
            metaData = null;
            tickCount = 0;
            return;
        }

        tickCount++;
        if (worldObj.isRemote) {
            spawnClientParticles();
        } else {
            updateReversion();
        }
    }

    private void spawnClientParticles() {
        double x = (float) xCoord + worldObj.rand.nextFloat();
        double y = (float) yCoord + worldObj.rand.nextFloat();
        double z = (float) zCoord + worldObj.rand.nextFloat();
        // this.worldObj.spawnParticle("smoke", var1, var3, var5, 0.0D, 0.0D, 0.0D);
        worldObj.spawnParticle("reddust", x, y, z, 0.0D, 0.0D, 0.0D);

        // occasionally make a little red dust line to outline our radius
        if (rand.nextInt(REVERT_CHANCE) == 0) {
            makeRandomOutline();
            makeRandomOutline();
            makeRandomOutline();
        }
    }

    private void updateReversion() {
        // take a snapshot when first activated, then revert subsequent changes
        if (blockData == null || metaData == null) {
            captureBlockData();
            slowScan = true;
        }

        if (!slowScan || tickCount % SLOW_SCAN_INTERVAL_TICKS == 0) {
            if (scanAndRevertChanges()) {
                slowScan = false;
                ticksSinceChange = 0;
            } else if (++ticksSinceChange > SLOW_SCAN_THRESHOLD) {
                slowScan = true;
            }
        }
    }

    /**
     * Display a random one of the 12 possible outlines
     */
    private void makeRandomOutline() {
        makeOutline(this.rand.nextInt(OUTLINE_COUNT));
    }

    /**
     * Display a specific outline
     */
    private void makeOutline(int outline) {
        // src
        double sx = this.xCoord;
        double sy = this.yCoord;
        double sz = this.zCoord;
        // dest
        double dx = this.xCoord;
        double dy = this.yCoord;
        double dz = this.zCoord;

        switch (outline) {
            case 0, 8 -> {
                sx -= radius;
                dx += radius + 1;
                sz -= radius;
                dz -= radius;
            }
            case 1, 9 -> {
                sx -= radius;
                dx -= radius;
                sz -= radius;
                dz += radius + 1;
            }
            case 2, 10 -> {
                sx -= radius;
                dx += radius + 1;
                sz += radius + 1;
                dz += radius + 1;
            }
            case 3, 11 -> {
                sx += radius + 1;
                dx += radius + 1;
                sz -= radius;
                dz += radius + 1;
            }
            case 4 -> {
                sx -= radius;
                dx -= radius;
                sz -= radius;
                dz -= radius;
            }
            case 5 -> {
                sx += radius + 1;
                dx += radius + 1;
                sz -= radius;
                dz -= radius;
            }
            case 6 -> {
                sx += radius + 1;
                dx += radius + 1;
                sz += radius + 1;
                dz += radius + 1;
            }
            case 7 -> {
                sx -= radius;
                dx -= radius;
                sz += radius + 1;
                dz += radius + 1;
            }
        }

        switch (outline) {
            case 0, 1, 2, 3 -> {
                sy += radius + 1;
                dy += radius + 1;
            }
            case 4, 5, 6, 7 -> {
                sy -= radius;
                dy += radius + 1;
            }
            case 8, 9, 10, 11 -> {
                sy -= radius;
                dy -= radius;
            }
        }

        if (rand.nextBoolean()) {
            drawParticleLine(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, dx, dy, dz);
        } else {
            drawParticleLine(sx, sy, sz, this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
        }
        drawParticleLine(sx, sy, sz, dx, dy, dz);
    }

    /**
     * Make a trail of particles from one point to another
     */
    protected void drawParticleLine(double srcX, double srcY, double srcZ, double destX, double destY, double destZ) {
        // make particle trail
        int particles = OUTLINE_PARTICLES;
        for (int i = 0; i < particles; i++) {
            double trailFactor = i / (particles - 1.0D);

            double tx = srcX + (destX - srcX) * trailFactor + rand.nextFloat() * 0.005;
            double ty = srcY + (destY - srcY) * trailFactor + rand.nextFloat() * 0.005;
            double tz = srcZ + (destZ - srcZ) * trailFactor + rand.nextFloat() * 0.005;
            worldObj.spawnParticle("reddust", tx, ty, tz, 0, 0, 0);
        }
    }

    private boolean scanAndRevertChanges() {
        int index = 0;
        boolean reverted = false;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block blockID = worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z);
                    byte meta = (byte) worldObj.getBlockMetadata(this.xCoord + x, this.yCoord + y, this.zCoord + z);

                    if (blockData[index] != blockID || metaData[index] != meta) {
                        if (revertBlock(
                                this.xCoord + x,
                                this.yCoord + y,
                                this.zCoord + z,
                                blockID,
                                meta,
                                blockData[index],
                                metaData[index])) {
                            reverted = true;
                        } else {
                            blockData[index] = blockID;
                            metaData[index] = meta;
                        }
                    }

                    index++;
                }
            }
        }

        return reverted;
    }

    private boolean revertBlock(int x, int y, int z, Block thereBlockID, byte thereMeta, Block replaceBlockID,
            byte replaceMeta) {
        if (thereBlockID == Blocks.air && !replaceBlockID.getMaterial().blocksMovement()) {
            // do not revert

            FMLLog.fine(
                    "[TwilightForest]  Not replacing block " + replaceBlockID + " because it doesn't block movement");

            return false;
        }
        if (isUnrevertable(thereBlockID, thereMeta, replaceBlockID, replaceMeta)) {
            // do not revert
            return false;
        } else if (this.rand.nextInt(REVERT_CHANCE) == 0) {
            // don't revert everything instantly
            if (replaceBlockID != Blocks.air) {
                replaceBlockID = TFBlocks.towerTranslucent;
                replaceMeta = BlockTFTowerTranslucent.META_REVERTER_REPLACEMENT;
            }

            // play a little animation
            if (thereBlockID == Blocks.air) {
                worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(replaceBlockID) + (replaceMeta << 12));
            } else if (replaceBlockID == Blocks.air) {
                worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(thereBlockID) + (thereMeta << 12));
                thereBlockID.dropBlockAsItem(worldObj, x, y, z, thereMeta, 0);
            }

            worldObj.setBlock(x, y, z, replaceBlockID, replaceMeta, 2);

        }

        return true;
    }

    private boolean isUnrevertable(Block thereBlockID, byte thereMeta, Block replaceBlockID, byte replaceMeta) {
        if (thereBlockID == TFBlocks.towerDevice || replaceBlockID == TFBlocks.towerDevice) {
            return true;
        }
        if ((thereBlockID == TFBlocks.towerTranslucent
                && thereMeta != BlockTFTowerTranslucent.META_REVERTER_REPLACEMENT)
                || (replaceBlockID == TFBlocks.towerTranslucent
                        && replaceMeta != BlockTFTowerTranslucent.META_REVERTER_REPLACEMENT)) {
            return true;
        }
        if ((thereBlockID == Blocks.redstone_lamp && replaceBlockID == Blocks.lit_redstone_lamp)
                || (thereBlockID == Blocks.lit_redstone_lamp && replaceBlockID == Blocks.redstone_lamp)) {
            return true;
        }
        if (thereBlockID == Blocks.water || thereBlockID == Blocks.flowing_water
                || replaceBlockID == Blocks.water
                || replaceBlockID == Blocks.flowing_water) {
            return true;
        }
        return replaceBlockID == Blocks.tnt;
    }

    private void captureBlockData() {
        diameter = radius * 2 + 1;
        blockData = new Block[diameter * diameter * diameter];
        metaData = new byte[diameter * diameter * diameter];

        int index = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block blockID = worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z);
                    int meta = worldObj.getBlockMetadata(this.xCoord + x, this.yCoord + y, this.zCoord + z);

                    blockData[index] = blockID;
                    metaData[index] = (byte) meta;

                    index++;
                }
            }
        }

        // System.out.println("Captured data for " + index + " blocks");
    }

    /**
     * Returns true if there is a player in range (using World.getClosestPlayer)
     */
    public boolean anyPlayerInRange() {
        return this.worldObj.getClosestPlayer(
                (double) this.xCoord + 0.5D,
                (double) this.yCoord + 0.5D,
                (double) this.zCoord + 0.5D,
                this.requiredPlayerRange) != null;
    }
}
