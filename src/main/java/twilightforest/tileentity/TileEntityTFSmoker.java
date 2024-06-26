package twilightforest.tileentity;

import net.minecraft.tileentity.TileEntity;

import twilightforest.TwilightForestMod;

public class TileEntityTFSmoker extends TileEntity {

    public long counter = 0;

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        if (++counter % 4 == 0) {
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
