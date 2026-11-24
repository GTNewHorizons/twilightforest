package twilightforest.tileentity;

import net.minecraft.entity.EntityLiving;

import twilightforest.entity.TFCreatures;
import twilightforest.entity.boss.EntityTFUrGhast;

public class TileEntityTFTowerBossSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFTowerBossSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Tower Boss");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(9.0D, 4);
    }

    /**
     * Any post-creation initialization goes here
     */
    @Override
    protected void initializeCreature(EntityLiving myCreature) {
        if (myCreature instanceof EntityTFUrGhast ghast) {
            ghast.setHomeArea(xCoord, yCoord, zCoord, 46);
        }
    }

}
