package twilightforest.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;

import com.gtnewhorizons.angelica.api.weather.WeatherColumnProvider;
import com.gtnewhorizons.angelica.api.weather.WeatherColumnSink;

public class TFAngelicaWeatherRenderer extends TFWeatherRenderer implements WeatherColumnProvider {

    private static final int FULL_BRIGHT = 0xF000F0;

    @Override
    public boolean rendersVanillaWeather() {
        return true;
    }

    @Override
    public int beginFrame(WorldClient world, EntityLivingBase view, float partialTicks) {
        refreshColumns(partialTicks, world, view, Minecraft.getMinecraft());
        return ++this.rendererUpdateCount;
    }

    @Override
    public int columnRevision() {
        return columns().revision();
    }

    @Override
    public float fadeRadius() {
        return (float) columns().range();
    }

    @Override
    public void collectColumns(WeatherColumnSink sink) {
        TFWeatherColumns columns = columns();
        for (int layerIndex = 0; layerIndex < TFWeatherColumns.LAYER_COUNT; ++layerIndex) {
            TFWeatherColumns.Layer layer = columns.layer(layerIndex);
            int count = layer.count;
            if (count == 0) {
                continue;
            }

            sink.beginLayer(
                    LAYER_TEXTURES[layerIndex],
                    TFWeatherColumns.PERIOD[layerIndex],
                    TFWeatherColumns.U_SCALE[layerIndex],
                    TFWeatherColumns.V_SCALE[layerIndex],
                    TFWeatherColumns.FADE_SCALE[layerIndex]);
            boolean worldLit = layerIndex == TFWeatherColumns.BIGRAIN;
            for (int i = 0; i < count; ++i) {
                sink.column(
                        layer.x[i],
                        layer.z[i],
                        (float) layer.bottom[i],
                        (float) layer.top[i],
                        layer.uBase[i],
                        layer.uDrift[i],
                        layer.vBase[i],
                        layer.vDrift[i],
                        worldLit ? layer.brightness[i] : FULL_BRIGHT,
                        layer.scrollOffset[i],
                        layer.scrollScale[i],
                        layer.r[i],
                        layer.g[i],
                        layer.b[i],
                        layer.a[i]);
            }
        }
    }
}
