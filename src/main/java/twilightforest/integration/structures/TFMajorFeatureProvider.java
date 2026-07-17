package twilightforest.integration.structures;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;

import twilightforest.TFFeature;

/**
 * Lightweight dependency injection to allow overriding Twilight Forest structure implementations.
 */
public interface TFMajorFeatureProvider {

    StructureComponent getComponent(World world, Random rand, TFFeature feature, int x, int y, int z);
}
