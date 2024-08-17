package twilightforest.tileentity;

import net.minecraft.entity.player.EntityPlayer;

import twilightforest.entity.TFCreatures;

public class TileEntityTFAlphaYetiSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFAlphaYetiSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Yeti Boss");
    }

    @Override
    public boolean anyPlayerInRange() {
        EntityPlayer closestPlayer = worldObj.getClosestPlayer(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 30D);

        return closestPlayer != null && closestPlayer.posY > yCoord - 4;
    }

}
