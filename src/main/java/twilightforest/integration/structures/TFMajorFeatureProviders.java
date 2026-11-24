package twilightforest.integration.structures;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFFeature;

public final class TFMajorFeatureProviders {

    private static final List<TFMajorFeatureProvider> PROVIDERS = new CopyOnWriteArrayList<>();

    private TFMajorFeatureProviders() {}

    public static void addProvider(TFMajorFeatureProvider provider) {
        PROVIDERS.add(Objects.requireNonNull(provider, "provider"));
    }

    public static StructureComponent getTFMajorFeature(World world, Random rand, TFFeature feature, int x, int y,
            int z) {
        for (TFMajorFeatureProvider provider : PROVIDERS) {
            StructureComponent component = provider.getComponent(world, rand, feature, x, y, z);
            if (component != null) {
                return component;
            }
        }
        return null;
    }
}
