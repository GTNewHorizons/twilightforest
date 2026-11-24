package twilightforest.tileentity;

import net.minecraft.tileentity.TileEntity;

import twilightforest.TwilightForestMod;

public class TileEntityTFSmoker extends TileEntity {

    private static final int SMOKE_INTERVAL_TICKS = 4;

    public long counter;

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        if (++counter % SMOKE_INTERVAL_TICKS == 0) {
            TwilightForestMod.proxy.spawnParticle(
                    this.worldObj,
                    "hugesmoke",
                    this.xCoord + 0.5,
                    this.yCoord + 0.95,
                    this.zCoord + 0.5,
                    Math.cos(counter / 10.0) * 0.05,
                    0.25D,
                    Math.sin(counter / 10.0) * 0.05);
        }
    }
}
