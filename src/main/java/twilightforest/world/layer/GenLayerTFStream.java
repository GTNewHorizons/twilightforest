// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package twilightforest.world.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import twilightforest.biomes.TFBiomeBase;

public class GenLayerTFStream extends GenLayer {

    private static final int BORDER_RADIUS = 1;
    private static final int NO_STREAM = -1;

    public GenLayerTFStream(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    @Override
    public int[] getInts(int x, int z, int width, int depth) {
        int inputWidth = width + BORDER_RADIUS * 2;
        int[] input = parent.getInts(x - BORDER_RADIUS, z - BORDER_RADIUS, inputWidth, depth + BORDER_RADIUS * 2);
        int[] output = IntCache.getIntCache(width * depth);
        int streamId = TFBiomeBase.stream.biomeID;

        for (int dz = 0; dz < depth; dz++) {
            int inputRow = (dz + BORDER_RADIUS) * inputWidth + BORDER_RADIUS;
            int outputRow = dz * width;

            for (int dx = 0; dx < width; dx++) {
                int index = inputRow + dx;
                int center = input[index];

                output[outputRow + dx] = shouldStream(
                        center,
                        input[index - BORDER_RADIUS],
                        input[index - inputWidth],
                        input[index + BORDER_RADIUS],
                        input[index + inputWidth]) ? streamId : NO_STREAM;
            }
        }

        return output;
    }

    boolean shouldStream(int center, int left, int top, int right, int bottom) {
        return shouldStream(center, left) || shouldStream(center, right)
                || shouldStream(center, top)
                || shouldStream(center, bottom);
    }

    boolean shouldStream(int biome1, int biome2) {
        if (biome1 == biome2 || biome1 == -biome2) {
            return false;
        }

        // glacier and snow have no border
        if (isPair(biome1, biome2, TFBiomeBase.glacier.biomeID, TFBiomeBase.tfSnow.biomeID)) {
            return false;
        }

        // mushrooms
        if (isPair(biome1, biome2, TFBiomeBase.deepMushrooms.biomeID, TFBiomeBase.mushrooms.biomeID)) {
            return false;
        }

        // fire swamp
        if (isPair(biome1, biome2, TFBiomeBase.tfSwamp.biomeID, TFBiomeBase.fireSwamp.biomeID)) {
            return false;
        }

        // highlands
        if (isPair(biome1, biome2, TFBiomeBase.highlands.biomeID, TFBiomeBase.highlandsCenter.biomeID)) {
            return false;
        }

        // dark forest
        if (isPair(biome1, biome2, TFBiomeBase.darkForest.biomeID, TFBiomeBase.darkForestCenter.biomeID)) {
            return false;
        }

        // no lake border
        if (biome1 == TFBiomeBase.tfLake.biomeID || biome2 == TFBiomeBase.tfLake.biomeID) {
            return false;
        }

        // clearing and oak savanna never form streams
        if (biome1 == TFBiomeBase.clearing.biomeID || biome2 == TFBiomeBase.clearing.biomeID
                || biome1 == TFBiomeBase.oakSavanna.biomeID
                || biome2 == TFBiomeBase.oakSavanna.biomeID) {
            return false;
        }

        // thorns need no stream
        return biome1 != TFBiomeBase.thornlands.biomeID && biome2 != TFBiomeBase.thornlands.biomeID;
    }

    private static boolean isPair(int biome1, int biome2, int first, int second) {
        return (biome1 == first && biome2 == second) || (biome1 == second && biome2 == first);
    }
}
