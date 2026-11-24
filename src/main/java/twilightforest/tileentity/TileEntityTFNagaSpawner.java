package twilightforest.tileentity;

import twilightforest.entity.TFCreatures;

public class TileEntityTFNagaSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFNagaSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Naga");
    }

}
