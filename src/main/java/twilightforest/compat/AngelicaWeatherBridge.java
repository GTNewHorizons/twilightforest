package twilightforest.compat;

import twilightforest.client.renderer.TFAngelicaWeatherRenderer;
import twilightforest.client.renderer.TFWeatherRenderer;

public final class AngelicaWeatherBridge {

    private AngelicaWeatherBridge() {}

    public static TFWeatherRenderer create() {
        return new TFAngelicaWeatherRenderer();
    }
}
