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

    // bit positions represent the 4 directions visible on each face
    // the lookup preserves the original priority and connection type names
    private static final ConnectionType[] HORIZONTAL_CONNECTIONS = { ConnectionType.NO, ConnectionType.E,
            ConnectionType.W, ConnectionType.EW, ConnectionType.N, ConnectionType.EN, ConnectionType.WN,
            ConnectionType.triS, ConnectionType.S, ConnectionType.ES, ConnectionType.WS, ConnectionType.triN,
            ConnectionType.NS, ConnectionType.triW, ConnectionType.triE, ConnectionType.CROSS };

    private static final ConnectionType[] NORTH_SOUTH_CONNECTIONS = { ConnectionType.NO, ConnectionType.D,
            ConnectionType.U, ConnectionType.UD, ConnectionType.N, ConnectionType.DN, ConnectionType.UN,
            ConnectionType.triS, ConnectionType.S, ConnectionType.DS, ConnectionType.US, ConnectionType.triN,
            ConnectionType.NS, ConnectionType.triU, ConnectionType.triD, ConnectionType.CROSS };

    private static final ConnectionType[] EAST_WEST_CONNECTIONS = { ConnectionType.NO, ConnectionType.D,
            ConnectionType.U, ConnectionType.UD, ConnectionType.E, ConnectionType.DE, ConnectionType.UE,
            ConnectionType.triW, ConnectionType.W, ConnectionType.DW, ConnectionType.UW, ConnectionType.triE,
            ConnectionType.EW, ConnectionType.triU, ConnectionType.triD, ConnectionType.CROSS };

    // keep this legacy public method name for callers in the block renderer
    public ConnectionType GetConnectionType(int side) {
        switch (side) {
            case 0:
            case 1:
                UpdateNeighbourCount();
                return HORIZONTAL_CONNECTIONS[connectionMask(
                        neighbourEast,
                        neighbourWest,
                        neighbourNorth,
                        neighbourSouth)];
            case 2:
            case 3:
                UpdateNeighbourCount();
                return NORTH_SOUTH_CONNECTIONS[connectionMask(
                        neighbourDown,
                        neighbourUp,
                        neighbourNorth,
                        neighbourSouth)];
            case 4:
            case 5:
                UpdateNeighbourCount();
                return EAST_WEST_CONNECTIONS[connectionMask(neighbourDown, neighbourUp, neighbourEast, neighbourWest)];
            default:
                return ConnectionType.NO;
        }
    }

    private static int connectionMask(boolean first, boolean second, boolean third, boolean fourth) {
        return (first ? 1 : 0) | (second ? 2 : 0) | (third ? 4 : 0) | (fourth ? 8 : 0);
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
        // Connection state changes are handled by block updates and NBT packets.
        return false;
    }

}
