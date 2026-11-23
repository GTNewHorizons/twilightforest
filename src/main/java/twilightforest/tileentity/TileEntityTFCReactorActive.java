package twilightforest.tileentity;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import twilightforest.block.BlockTFTowerTranslucent;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;

public class TileEntityTFCReactorActive extends TileEntity {

    // keep shell placement order, neighbouring updates can observe intermediate blocks
    private static final int[][] REACTOR_SHELL = { { 1, 1, 1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 1, 1, -1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { -1, 1, 1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { -1, 1, -1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 0, 1, 1, BlockTFTowerTranslucent.META_FAKE_GOLD }, { 0, 1, -1, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { 1, 1, 0, BlockTFTowerTranslucent.META_FAKE_GOLD }, { -1, 1, 0, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { 1, 0, 1, BlockTFTowerTranslucent.META_FAKE_GOLD }, { 1, 0, -1, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { -1, 0, 1, BlockTFTowerTranslucent.META_FAKE_GOLD }, { -1, 0, -1, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { 0, 0, 1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 0, 0, -1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 1, 0, 0, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { -1, 0, 0, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 1, -1, 1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 1, -1, -1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { -1, -1, 1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { -1, -1, -1, BlockTFTowerTranslucent.META_FAKE_DIAMOND },
            { 0, -1, 1, BlockTFTowerTranslucent.META_FAKE_GOLD }, { 0, -1, -1, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { 1, -1, 0, BlockTFTowerTranslucent.META_FAKE_GOLD },
            { -1, -1, 0, BlockTFTowerTranslucent.META_FAKE_GOLD } };

    int counter = 0;

    int secX, secY, secZ;
    int terX, terY, terZ;

    public TileEntityTFCReactorActive() {
        Random rand = new Random();

        // determine the two smaller bursts
        this.secX = 3 * (rand.nextBoolean() ? 1 : -1);
        this.secY = 3 * (rand.nextBoolean() ? 1 : -1);
        this.secZ = 3 * (rand.nextBoolean() ? 1 : -1);

        this.terX = 3 * (rand.nextBoolean() ? 1 : -1);
        this.terY = 3 * (rand.nextBoolean() ? 1 : -1);
        this.terZ = 3 * (rand.nextBoolean() ? 1 : -1);

        if (secX == terX && secY == terY && secZ == terZ) {
            terX = -terX;
            terY = -terY;
            terZ = -terZ;
        }
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        counter++;

        if (!worldObj.isRemote) {

            // every 2 seconds for 10 seconds, destroy a new radius
            int offset = 10;

            if (counter % 5 == 0) {
                if (counter == 5) {
                    // transformation!
                    for (int[] entry : REACTOR_SHELL) {
                        worldObj.setBlock(
                                xCoord + entry[0],
                                yCoord + entry[1],
                                zCoord + entry[2],
                                TFBlocks.towerTranslucent,
                                entry[3],
                                2);
                    }
                }

                // primary burst
                int primary = counter - 80;

                if (primary >= offset && primary <= 249) {
                    drawBlob(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            (primary - offset) / 40,
                            Blocks.air,
                            0,
                            primary - offset,
                            false);
                }
                if (primary <= 200) {
                    drawBlob(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            primary / 40,
                            TFBlocks.towerTranslucent,
                            BlockTFTowerTranslucent.META_REACTOR_DEBRIS,
                            counter,
                            false);
                }

                // secondary burst
                int secondary = counter - 120;

                if (secondary >= offset && secondary <= 129) {
                    drawBlob(
                            this.xCoord + secX,
                            this.yCoord + secY,
                            this.zCoord + secZ,
                            (secondary - offset) / 40,
                            Blocks.air,
                            0,
                            secondary - offset,
                            false);
                }
                if (secondary >= 0 && secondary <= 160) {
                    drawBlob(
                            this.xCoord + secX,
                            this.yCoord + secY,
                            this.zCoord + secZ,
                            secondary / 40,
                            Blocks.air,
                            0,
                            secondary,
                            true);
                }

                // tertiary burst
                int tertiary = counter - 160;

                if (tertiary >= offset && tertiary <= 129) {
                    drawBlob(
                            this.xCoord + terX,
                            this.yCoord + terY,
                            this.zCoord + terZ,
                            (tertiary - offset) / 40,
                            Blocks.air,
                            0,
                            tertiary - offset,
                            false);
                }
                if (tertiary >= 0 && tertiary <= 160) {
                    drawBlob(
                            this.xCoord + terX,
                            this.yCoord + terY,
                            this.zCoord + terZ,
                            tertiary / 40,
                            Blocks.air,
                            0,
                            tertiary,
                            true);
                }

            }

            if (counter >= 350) {
                // spawn mini ghasts near the secondary & tertiary points
                for (int i = 0; i < 3; i++) {
                    spawnGhastNear(this.xCoord + secX, this.yCoord + secY, this.zCoord + secZ);
                    spawnGhastNear(this.xCoord + terX, this.yCoord + terY, this.zCoord + terZ);
                }

                // deactivate & explode
                worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2.0F, true);

                worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, Blocks.air, 0, 3);
            }

        } else {
            if (counter % 5 == 0 && counter <= 250) {
                // sound
                worldObj.playSound(
                        this.xCoord + 0.5D,
                        this.yCoord + 0.5D,
                        this.zCoord + 0.5D,
                        "portal.portal",
                        counter / 100F,
                        counter / 100F,
                        false);
            }
        }

    }

    private void spawnGhastNear(int x, int y, int z) {
        EntityTFMiniGhast ghast = new EntityTFMiniGhast(worldObj);
        ghast.setLocationAndAngles(
                x - 1.5 + worldObj.rand.nextFloat() * 3.0,
                y - 1.5 + worldObj.rand.nextFloat() * 3.0,
                z - 1.5 + worldObj.rand.nextFloat() * 3.0,
                worldObj.rand.nextFloat() * 360F,
                0.0F);
        worldObj.spawnEntityInWorld(ghast);
    }

    /**
     * Draw a giant blob of whatevs (okay, it's going to be leaves).
     */
    public void drawBlob(int sx, int sy, int sz, int rad, Block blockValue, int metaValue, int fuzz,
            boolean netherTransform) {
        // then trace out a quadrant
        for (byte dx = 0; dx <= rad; dx++) {
            // transform fuzz
            int fuzzX = (fuzz + dx) % 8;

            for (byte dy = 0; dy <= rad; dy++) {
                int fuzzY = (fuzz + dy) % 8;

                for (byte dz = 0; dz <= rad; dz++) {
                    // determine how far we are from the center.
                    int largest = Math.max(dx, Math.max(dy, dz));
                    int smallest = Math.min(dx, Math.min(dy, dz));
                    int middle = dx + dy + dz - largest - smallest;
                    int distance = largest + (int) (middle * 0.5D + smallest * 0.25D);

                    // only the shell is transformed, preserve the original phase offset pattern
                    if (distance == rad && !(dx == 0 && dy == 0 && dz == 0) && fuzzX >= 0) {
                        int targetX = sx + ((fuzzX & 2) == 0 ? dx : -dx);
                        int targetY = sy + ((fuzzX & 4) == 0 ? dy : -dy);
                        int targetZ = sz + ((fuzzX & 1) == 0 ? dz : -dz);
                        transformBlock(targetX, targetY, targetZ, blockValue, metaValue, fuzzY, netherTransform);
                    }
                }
            }
        }
    }

    /**
     *
     */
    protected void transformBlock(int x, int y, int z, Block blockValue, int metaValue, int fuzz,
            boolean netherTransform) {
        Block whatsThere = worldObj.getBlock(x, y, z);
        int whatsMeta = worldObj.getBlockMetadata(x, y, z);

        if (whatsThere != Blocks.air && whatsThere.getBlockHardness(worldObj, x, y, z) == -1) {
            // don't destroy unbreakable stuff
            return;
        }

        if (fuzz == 0 && whatsThere != Blocks.air) {
            // make pop thing for original block
            worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(whatsThere) + (whatsMeta << 12));
        }

        if (netherTransform && whatsThere != Blocks.air) {
            worldObj.setBlock(x, y, z, Blocks.netherrack, 0, 3);
            // fire on top?
            if (worldObj.getBlock(x, y + 1, z) == Blocks.air && fuzz % 3 == 0) {
                worldObj.setBlock(x, y + 1, z, Blocks.fire, 0, 3);
            }
        } else {
            worldObj.setBlock(x, y, z, blockValue, metaValue, 3);
        }
    }

}
