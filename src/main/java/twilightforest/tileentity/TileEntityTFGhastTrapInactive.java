package twilightforest.tileentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFTowerDevice;
import twilightforest.block.TFBlocks;
import twilightforest.entity.EntityTFMiniGhast;

public class TileEntityTFGhastTrapInactive extends TileEntity {

    private static final int MAX_CHARGE = 3;

    int counter;
    Random rand = new Random();

    private final Set<UUID> chargedGhasts = new HashSet<>();
    private int chargeLevel;

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
        if (!worldObj.isRemote && isCharged()) {
            // a powered trap loaded from disk may not receive a fresh notification
            if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
                TFBlocks.towerDevice.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, TFBlocks.towerDevice);
            }
            return;
        }
        // check to see if there are any dying mini ghasts within our scan range
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                this.xCoord + 1,
                this.yCoord + 1,
                this.zCoord + 1).expand(10D, 16D, 10D);

        List<EntityTFMiniGhast> nearbyGhasts = worldObj.getEntitiesWithinAABB(EntityTFMiniGhast.class, aabb);

        if (!worldObj.isRemote) {
            int previousCharge = chargeLevel;
            for (EntityTFMiniGhast ghast : nearbyGhasts) {
                if (chargeLevel >= MAX_CHARGE) {
                    break;
                }
                if (!chargedGhasts.contains(ghast.getUniqueID()) && ghast.consumeTrapCharge()) {
                    chargedGhasts.add(ghast.getUniqueID());
                    ++chargeLevel;
                }
            }
            if (chargeLevel != previousCharge) {
                markDirty();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                if (isCharged() && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
                    TFBlocks.towerDevice.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, TFBlocks.towerDevice);
                }
            }
            return;
        }

        for (EntityTFMiniGhast ghast : nearbyGhasts) {
            if (ghast.deathTime > 0) {
                makeParticlesTo(ghast);
            }
        }

        counter++;

        if (this.worldObj.isRemote) {
            // occasionally make a redstone line to a mini ghast
            if (this.counter % 20 == 0 && nearbyGhasts.size() > 0) {
                EntityTFMiniGhast highlight = nearbyGhasts.get(rand.nextInt(nearbyGhasts.size()));

                this.makeParticlesTo(highlight);
            }

            if (chargeLevel >= 1 && counter % 10 == 0) {
                ((BlockTFTowerDevice) TFBlocks.towerDevice)
                        .sparkle(worldObj, this.xCoord, this.yCoord, this.zCoord, worldObj.rand);

                worldObj.playSound(
                        this.xCoord + 0.5D,
                        this.yCoord + 1.5D,
                        this.zCoord + 0.5D,
                        "note.harp",
                        1.0F,
                        1.0F,
                        false);
            }
            if (chargeLevel >= 2) {
                worldObj.spawnParticle(
                        "smoke",
                        this.xCoord + 0.1 + rand.nextFloat() * 0.8,
                        this.yCoord + 1.05,
                        this.zCoord + 0.1 + rand.nextFloat() * 0.8,
                        (rand.nextFloat() - rand.nextFloat()) * 0.05,
                        0.00,
                        (rand.nextFloat() - rand.nextFloat()) * 0.05);
                if (counter % 10 == 0) {
                    worldObj.playSound(
                            this.xCoord + 0.5D,
                            this.yCoord + 1.5D,
                            this.zCoord + 0.5D,
                            "note.harp",
                            1.2F,
                            0.8F,
                            false);
                }
            }
            if (chargeLevel >= 3) {
                worldObj.spawnParticle(
                        "largesmoke",
                        this.xCoord + 0.1 + rand.nextFloat() * 0.8,
                        this.yCoord + 1.05,
                        this.zCoord + 0.1 + rand.nextFloat() * 0.8,
                        (rand.nextFloat() - rand.nextFloat()) * 0.05,
                        0.05,
                        (rand.nextFloat() - rand.nextFloat()) * 0.05);
                ((BlockTFTowerDevice) TFBlocks.towerDevice)
                        .sparkle(worldObj, this.xCoord, this.yCoord, this.zCoord, worldObj.rand);
                if (counter % 5 == 0) {
                    worldObj.playSound(
                            this.xCoord + 0.5D,
                            this.yCoord + 1.5D,
                            this.zCoord + 0.5D,
                            "note.harp",
                            1.5F,
                            2F,
                            false);
                }
            }
        }
    }

    private void makeParticlesTo(Entity highlight) {
        double sx = this.xCoord + 0.5D;
        double sy = this.yCoord + 1.0D;
        double sz = this.zCoord + 0.5D;

        double dx = sx - highlight.posX;
        double dy = sy - highlight.posY - highlight.getEyeHeight();
        double dz = sz - highlight.posZ;

        for (int i = 0; i < 5; i++) {
            TwilightForestMod.proxy.spawnParticle(this.worldObj, "ghasttrap", sx, sy, sz, -dx, -dy, -dz);
        }

    }

    public boolean isCharged() {
        return chargeLevel >= MAX_CHARGE;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("TrapCharge", chargeLevel);
        NBTTagList ghasts = new NBTTagList();
        for (UUID uuid : chargedGhasts) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("Most", uuid.getMostSignificantBits());
            entry.setLong("Least", uuid.getLeastSignificantBits());
            ghasts.appendTag(entry);
        }
        tag.setTag("ChargedGhasts", ghasts);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        chargeLevel = Math.max(0, Math.min(MAX_CHARGE, tag.getInteger("TrapCharge")));
        chargedGhasts.clear();
        NBTTagList ghasts = tag.getTagList("ChargedGhasts", 10);
        for (int i = 0; i < ghasts.tagCount() && chargedGhasts.size() < MAX_CHARGE; i++) {
            NBTTagCompound entry = ghasts.getCompoundTagAt(i);
            chargedGhasts.add(new UUID(entry.getLong("Most"), entry.getLong("Least")));
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("TrapCharge", chargeLevel);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 3, tag);
    }

    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        chargeLevel = Math.max(0, Math.min(MAX_CHARGE, packet.func_148857_g().getInteger("TrapCharge")));
    }
}
