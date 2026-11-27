package twilightforest.tileentity;

import twilightforest.entity.TFCreatures;

public class TileEntityTFAlphaYetiSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFAlphaYetiSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Yeti Boss");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(30D, 4);
    }

}
