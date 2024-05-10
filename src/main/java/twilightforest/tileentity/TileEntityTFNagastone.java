package twilightforest.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTFNagastone extends TileEntity {

    public enum ConnectionType {
        NO,
        D,
        U,
        E,
        W,
        N,
        S,
        DE,
        DW,
        DN,
        DS,
        UE,
        UW,
        UN,
        US,
        UD,
        EW,
        EN,
        ES,
        WN,
        WS,
        NS,
        triD,
        triU,
        triE,
        triW,
        triN,
        triS,
        CROSS
    };

    public Boolean neighbourDown;
    public Boolean neighbourUp;
    public Boolean neighbourEast;
    public Boolean neighbourWest;
    public Boolean neighbourNorth;
    public Boolean neighbourSouth;

    public int neighbourCount;

    public TileEntityTFNagastone() {
        super();
        this.NoNeighbours();
    }

    public void NoNeighbours() {
        neighbourDown = false;
        neighbourUp = false;
        neighbourEast = false;
        neighbourWest = false;
        neighbourNorth = false;
        neighbourSouth = false;
        neighbourCount = 0;
    }

    public int UpdateNeighbourCount() {
        neighbourCount = (neighbourDown ? 1 : 0) + (neighbourUp ? 1 : 0)
                + (neighbourEast ? 1 : 0)
                + (neighbourWest ? 1 : 0)
                + (neighbourNorth ? 1 : 0)
                + (neighbourSouth ? 1 : 0);
        return neighbourCount;
    }

    public ConnectionType GetConnectionType(int side) {
        ConnectionType ct = ConnectionType.NO;
        int x = -1;
        switch (side) {
            default:
                break;
            case 0:
            case 1:
                x = this.UpdateNeighbourCount() - (this.neighbourDown ? 1 : 0) - (this.neighbourUp ? 1 : 0);
                switch (x) {
                    default:
                        break;
                    case 1:
                        if (this.neighbourEast) ct = ConnectionType.E;
                        else if (this.neighbourWest) ct = ConnectionType.W;
                        else if (this.neighbourNorth) ct = ConnectionType.N;
                        else if (this.neighbourSouth) ct = ConnectionType.S;
                        break;
                    case 2:
                        if (this.neighbourEast) {
                            if (this.neighbourNorth) ct = ConnectionType.EN;
                            else if (this.neighbourWest) ct = ConnectionType.EW;
                            else ct = ConnectionType.ES;
                        } else if (this.neighbourWest) {

                            if (this.neighbourNorth) ct = ConnectionType.WN;
                            else ct = ConnectionType.WS;
                        } else ct = ConnectionType.NS;

                        break;
                    case 3:
                        if (!this.neighbourEast) ct = ConnectionType.triE;
                        else if (!this.neighbourWest) ct = ConnectionType.triW;
                        else if (!this.neighbourNorth) ct = ConnectionType.triN;
                        else ct = ConnectionType.triS;
                        break;
                    case 4:
                        ct = ConnectionType.CROSS;
                        break;
                }
                break;
            case 2:
            case 3:
                x = this.UpdateNeighbourCount() - (this.neighbourEast ? 1 : 0) - (this.neighbourWest ? 1 : 0);
                switch (x) {
                    default:
                        break;
                    case 1:
                        if (this.neighbourDown) ct = ConnectionType.D;
                        else if (this.neighbourUp) ct = ConnectionType.U;
                        else if (this.neighbourNorth) ct = ConnectionType.N;
                        else if (this.neighbourSouth) ct = ConnectionType.S;
                        break;
                    case 2:
                        if (this.neighbourDown) {
                            if (this.neighbourNorth) ct = ConnectionType.DN;
                            else if (this.neighbourUp) ct = ConnectionType.UD;
                            else ct = ConnectionType.DS;
                        } else if (this.neighbourUp) {

                            if (this.neighbourNorth) ct = ConnectionType.UN;
                            else ct = ConnectionType.US;
                        } else ct = ConnectionType.NS;

                        break;
                    case 3:
                        if (!this.neighbourUp) ct = ConnectionType.triU;
                        else if (!this.neighbourDown) ct = ConnectionType.triD;
                        else if (!this.neighbourNorth) ct = ConnectionType.triN;
                        else ct = ConnectionType.triS;
                        break;
                    case 4:
                        ct = ConnectionType.CROSS;
                        break;
                }
                break;
            case 4:
            case 5:
                x = this.UpdateNeighbourCount() - (this.neighbourNorth ? 1 : 0) - (this.neighbourSouth ? 1 : 0);
                switch (x) {
                    default:
                        break;
                    case 1:
                        if (this.neighbourDown) ct = ConnectionType.D;
                        else if (this.neighbourUp) ct = ConnectionType.U;
                        else if (this.neighbourEast) ct = ConnectionType.E;
                        else if (this.neighbourWest) ct = ConnectionType.W;
                        break;
                    case 2:
                        if (this.neighbourDown) {
                            if (this.neighbourEast) ct = ConnectionType.DE;
                            else if (this.neighbourUp) ct = ConnectionType.UD;
                            else ct = ConnectionType.DW;
                        } else if (this.neighbourUp) {

                            if (this.neighbourEast) ct = ConnectionType.UE;
                            else ct = ConnectionType.UW;
                        } else ct = ConnectionType.EW;

                        break;
                    case 3:
                        if (!this.neighbourUp) ct = ConnectionType.triU;
                        else if (!this.neighbourDown) ct = ConnectionType.triD;
                        else if (!this.neighbourEast) ct = ConnectionType.triE;
                        else ct = ConnectionType.triW;
                        break;
                    case 4:
                        ct = ConnectionType.CROSS;
                        break;
                }
                break;
        }
        return ct;
    }

    @Override
    public Packet getDescriptionPacket() {

        NBTTagCompound tagCompound = new NBTTagCompound();

        this.writeToNBT(tagCompound);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {

        NBTTagCompound tagCompound = packet.func_148857_g();

        this.readFromNBT(tagCompound);
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("NeighDown")) neighbourDown = par1NBTTagCompound.getBoolean("NeighDown");
        if (par1NBTTagCompound.hasKey("NeighUp")) neighbourUp = par1NBTTagCompound.getBoolean("NeighUp");
        if (par1NBTTagCompound.hasKey("NeighEast")) neighbourEast = par1NBTTagCompound.getBoolean("NeighEast");
        if (par1NBTTagCompound.hasKey("NeighWest")) neighbourWest = par1NBTTagCompound.getBoolean("NeighWest");
        if (par1NBTTagCompound.hasKey("NeighNorth")) neighbourNorth = par1NBTTagCompound.getBoolean("NeighNorth");
        if (par1NBTTagCompound.hasKey("NeighSouth")) neighbourSouth = par1NBTTagCompound.getBoolean("NeighSouth");
        if (par1NBTTagCompound.hasKey("NeighbourCount"))
            neighbourCount = par1NBTTagCompound.getInteger("NeighbourCount");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("NeighDown", neighbourDown);
        par1NBTTagCompound.setBoolean("NeighUp", neighbourUp);
        par1NBTTagCompound.setBoolean("NeighEast", neighbourEast);
        par1NBTTagCompound.setBoolean("NeighWest", neighbourWest);
        par1NBTTagCompound.setBoolean("NeighNorth", neighbourNorth);
        par1NBTTagCompound.setBoolean("NeighSouth", neighbourSouth);
        par1NBTTagCompound.setInteger("NeighbourCount", neighbourCount);
    }

    /**
     * Determines if this TileEntity requires update calls.
     * 
     * @return True if you want updateEntity() to be called, false if not
     */
    @Override
    public boolean canUpdate() {
        return true;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity() {
        super.updateEntity();
    }

}
