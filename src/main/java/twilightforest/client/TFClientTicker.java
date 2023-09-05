package twilightforest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import twilightforest.world.WorldProviderTwilightForest;

public class TFClientTicker {

    /**
     * On the tick, we kill the vignette
     */
    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;

        // only fire if we're in the twilight forest
        if (world != null && (world.provider instanceof WorldProviderTwilightForest)) {
            // vignette
            if (mc.ingameGUI != null) {
                mc.ingameGUI.prevVignetteBrightness = 0.0F;
            }

        }
    }
}
