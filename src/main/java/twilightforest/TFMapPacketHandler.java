package twilightforest;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import twilightforest.item.ItemTFMagicMap;
import twilightforest.item.ItemTFMazeMap;

@Sharable
public class TFMapPacketHandler {

    /**
     * Extract a Packet131MapData packet from a Packet250CustomPayload. This is a little silly, huh?
     */
    public S34PacketMaps readMapPacket(ByteBuf byteBuf) {
        S34PacketMaps mapPacket = new S34PacketMaps();
        try {
            mapPacket.readPacketData(new PacketBuffer(byteBuf));
            return mapPacket;
        } catch (IOException | RuntimeException e) {
            FMLLog.warning("[TwilightForest] Ignoring malformed map packet: %s", e);
            return null;
        }
    }

    /**
     * Make a Packet250CustomPayload that wraps around a MapData packet, and sends on a specific channel
     */
    public static Packet makeMagicMapPacket(String mapChannel, short mapID, byte[] datas) {
        S34PacketMaps mapPacket = new S34PacketMaps(mapID, datas);
        PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
        try {
            mapPacket.writePacketData(payload);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to encode Twilight Forest map packet", e);
        }
        return new FMLProxyPacket(payload, mapChannel);
    }

    /**
     * Packet!
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void incomingPacket(ClientCustomPacketEvent event) {
        String channel = event.packet.channel();
        boolean magicMap = ItemTFMagicMap.STR_ID.equals(channel);
        if (!magicMap && !ItemTFMazeMap.STR_ID.equals(channel)) {
            return;
        }

        World world = TwilightForestMod.proxy.getClientWorld();
        if (world == null) {
            return;
        }

        S34PacketMaps mapPacket = readMapPacket(event.packet.payload());
        if (mapPacket == null) {
            return;
        }

        if (magicMap) {
            TFMagicMapData data = ItemTFMagicMap.getMPMapData(mapPacket.func_149188_c(), world);
            if (data != null) {
                data.updateMPMapData(mapPacket.func_149187_d());
            }
        } else {
            TFMazeMapData data = ItemTFMazeMap.getMPMapData(mapPacket.func_149188_c(), world);
            if (data != null) {
                data.updateMPMapData(mapPacket.func_149187_d());
                Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().func_148246_a(data);
            }
        }
    }
}
