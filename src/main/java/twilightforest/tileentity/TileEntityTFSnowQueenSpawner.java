package twilightforest.tileentity;

import twilightforest.entity.TFCreatures;

public class TileEntityTFSnowQueenSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFSnowQueenSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Snow Queen");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(9.0D, 4);
    }
}
