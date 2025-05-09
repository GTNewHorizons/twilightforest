package twilightforest.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.block.Block;

public class StalactiteOreGen {

    private static final OrePlacer DEFAULT_ORE_PLACER = new DefaultOrePlacer();
    private static final List<Function<Block, Optional<OrePlacer>>> orePlacerSources = new ArrayList<>();

    /**
     * Add an OrePlacer source that can determine which OrePlacer, if any, it wants to provide to a stalactite wanting
     * to generate with a given Block.
     */
    public static void addOrePlacerSource(Function<Block, Optional<OrePlacer>> orePlacerSource) {
        orePlacerSources.add(orePlacerSource);
    }

    static OrePlacer getOrePlacer(Block block) {
        // Given this only runs once per stalactite generated, I think we can afford the assumption there could
        // eventually be several alternate sources of ore placers.
        return orePlacerSources.stream().map(f -> f.apply(block))
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).findFirst()
                .orElse(DEFAULT_ORE_PLACER);
    }
}
