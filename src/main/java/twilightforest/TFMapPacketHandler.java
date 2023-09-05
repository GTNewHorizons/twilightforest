package twilightforest;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S34PacketMaps;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapPacket;
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
            e.printStackTrace();
        }

        FMLProxyPacket pkt = new FMLProxyPacket(payload, mapChannel);

        return pkt;
    }

    /**
     * Packet!
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void incomingPacket(ClientCustomPacketEvent event) {
        if (event.packet.channel().equals(ItemTFMagicMap.STR_ID)) {
            S34PacketMaps mapPacket = readMapPacket(event.packet.payload());
            ItemTFMagicMap.getMPMapData(mapPacket.func_149188_c(), TwilightForestMod.proxy.getClientWorld())
                    .updateMPMapData(mapPacket.func_149187_d());
        } else if (event.packet.channel().equals(ItemTFMazeMap.STR_ID)) {
            S34PacketMaps mapPacket = readMapPacket(event.packet.payload());
            TFMazeMapData data = ItemTFMazeMap
                    .getMPMapData(mapPacket.func_149188_c(), TwilightForestMod.proxy.getClientWorld());
            data.updateMPMapData(mapPacket.func_149187_d());
            Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().func_148246_a(data);
        }
    }
}
