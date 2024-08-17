package twilightforest.tileentity;

import net.minecraft.entity.player.EntityPlayer;

import twilightforest.entity.TFCreatures;

public class TileEntityTFMinoshroomSpawner extends TileEntityTFBossSpawner {

    public TileEntityTFMinoshroomSpawner() {
        this.mobID = TFCreatures.getSpawnerNameFor("Minoshroom");
    }

    @Override
    public boolean anyPlayerInRange() {
        EntityPlayer closestPlayer = worldObj.getClosestPlayer(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 15D);

        return closestPlayer != null && closestPlayer.posY > yCoord - 4;
    }

}
