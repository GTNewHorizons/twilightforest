package twilightforest.tileentity;

import twilightforest.entity.TFCreatures;

public class TileEntityTFMinoshroomSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFMinoshroomSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Minoshroom");
    }

    @Override
    public boolean anyPlayerInRange() {
        return isPlayerInRange(15D, 4);
    }

}
