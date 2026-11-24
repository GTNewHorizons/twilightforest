package twilightforest.tileentity;

import twilightforest.entity.TFCreatures;

public class TileEntityTFLichSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFLichSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Twilight Lich");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(9D, 4);
    }

}
