package twilightforest.integration.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFFeature;

public class TFMajorFeatureProviders {

    private static final List<TFMajorFeatureProvider> providers = new ArrayList<>();

    private TFMajorFeatureProviders() {}

    public static void addProvider(TFMajorFeatureProvider provider) {
        providers.add(provider);
    }

    public static StructureComponent getTFMajorFeature(World world, Random rand, TFFeature feature, int x, int y,
            int z) {
        for (int i = 0; i < providers.size(); i++) {
            StructureComponent tfMajorFeature = providers.get(i).getComponent(world, rand, feature, x, y, z);
            if (tfMajorFeature != null) {
                return tfMajorFeature;
            }
        }
        return null;
    }
}
