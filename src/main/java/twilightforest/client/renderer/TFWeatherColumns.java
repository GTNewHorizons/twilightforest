package twilightforest.client.renderer;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import twilightforest.biomes.TFBiomeBase;
import twilightforest.biomes.TFBiomeDarkForest;
import twilightforest.biomes.TFBiomeFinalPlateau;
import twilightforest.biomes.TFBiomeFireSwamp;
import twilightforest.biomes.TFBiomeGlacier;
import twilightforest.biomes.TFBiomeHighlands;
import twilightforest.biomes.TFBiomeSnow;
import twilightforest.biomes.TFBiomeSwamp;
import twilightforest.biomes.TFBiomeThornlands;

public final class TFWeatherColumns {

    public static final int BLIZZARD = 0;
    public static final int MOSQUITO = 1;
    public static final int ASHES = 2;
    public static final int DARKSTREAM = 3;
    public static final int BIGRAIN = 4;
    public static final int SPARKLES = 5;
    public static final int LAYER_COUNT = 6;

    public static final int[] PERIOD = { 512, 512, 1024, 512, 32, 512 };
    public static final float[] U_SCALE = { 0.03F, 0.03F, 0.001F, 0.0F, 0.0F, 0.01F };
    public static final float[] V_SCALE = { 0.001F, 0.003F, 0.001F, 0.01F, 0.0F, 0.01F };
    public static final float[] FADE_SCALE = { 0.3F, 0.0F, 0.3F, 0.3F, 0.3F, 0.3F };

    private static final int REBUILD_TICKS = 20;
    private static final int NEAR_RANGE = 15;
    private static final float UNFADED_ALPHA = 2.0F;

    public static final class Layer {

        public int count;
        public int[] x = new int[64];
        public int[] z = new int[64];
        public int[] bottom = new int[64];
        public int[] top = new int[64];
        public int[] brightness = new int[64];
        public int[] scrollOffset = new int[64];
        public float[] rx = new float[64];
        public float[] rz = new float[64];
        public float[] uBase = new float[64];
        public float[] uDrift = new float[64];
        public float[] vBase = new float[64];
        public float[] vDrift = new float[64];
        public float[] scrollScale = new float[64];
        public float[] r = new float[64];
        public float[] g = new float[64];
        public float[] b = new float[64];
        public float[] a = new float[64];

        private int add(int x, int z, float rx, float rz, int bottom, int top) {
            if (count == this.x.length) {
                int n = count * 2;
                this.x = Arrays.copyOf(this.x, n);
                this.z = Arrays.copyOf(this.z, n);
                this.bottom = Arrays.copyOf(this.bottom, n);
                this.top = Arrays.copyOf(this.top, n);
                brightness = Arrays.copyOf(brightness, n);
                scrollOffset = Arrays.copyOf(scrollOffset, n);
                this.rx = Arrays.copyOf(this.rx, n);
                this.rz = Arrays.copyOf(this.rz, n);
                uBase = Arrays.copyOf(uBase, n);
                uDrift = Arrays.copyOf(uDrift, n);
                vBase = Arrays.copyOf(vBase, n);
                vDrift = Arrays.copyOf(vDrift, n);
                scrollScale = Arrays.copyOf(scrollScale, n);
                r = Arrays.copyOf(r, n);
                g = Arrays.copyOf(g, n);
                b = Arrays.copyOf(b, n);
                a = Arrays.copyOf(a, n);
            }
            int i = count++;
            this.x[i] = x;
            this.z[i] = z;
            this.rx[i] = rx;
            this.rz[i] = rz;
            this.bottom[i] = bottom;
            this.top[i] = top;
            return i;
        }

        private void motion(int i, int scrollOffset, float scrollScale, float uBase, float uDrift, float vBase,
                float vDrift) {
            this.scrollOffset[i] = scrollOffset;
            this.scrollScale[i] = scrollScale;
            this.uBase[i] = uBase;
            this.uDrift[i] = uDrift;
            this.vBase[i] = vBase;
            this.vDrift[i] = vDrift;
        }

        private void tint(int i, int brightness, float r, float g, float b, float a) {
            this.brightness[i] = brightness;
            this.r[i] = r;
            this.g[i] = g;
            this.b[i] = b;
            this.a[i] = a;
        }
    }

    private final Layer[] layers = new Layer[LAYER_COUNT];
    private final Random random = new Random();

    private boolean built;
    private long builtTime;
    private int serial;
    private boolean nearLockedBiome;
    private boolean nearLockedStructure;
    private int range;

    private int keyX;
    private int keyY;
    private int keyZ;
    private boolean keyFancy;
    private boolean keyCreative;
    private boolean keyEnforced;
    private StructureBoundingBox keyBox;

    public TFWeatherColumns() {
        for (int i = 0; i < LAYER_COUNT; i++) {
            layers[i] = new Layer();
        }
    }

    public Layer layer(int index) {
        return layers[index];
    }

    public boolean isNearLockedBiome() {
        return nearLockedBiome;
    }

    public boolean isNearLockedStructure() {
        return nearLockedStructure;
    }

    public int range() {
        return range;
    }

    public int revision() {
        return serial;
    }

    public void update(WorldClient world, EntityLivingBase view, boolean fancy, boolean creative, boolean enforced,
            StructureBoundingBox box, long worldTime, float partialTicks, float[] rainXCoords, float[] rainYCoords) {
        int px = MathHelper.floor_double(view.posX);
        int py = MathHelper.floor_double(view.posY);
        int pz = MathHelper.floor_double(view.posZ);
        double offY = view.lastTickPosY + (view.posY - view.lastTickPosY) * (double) partialTicks;
        int floorY = MathHelper.floor_double(offY);

        if (built && px == keyX
                && py == keyY
                && pz == keyZ
                && fancy == keyFancy
                && creative == keyCreative
                && enforced == keyEnforced
                && (box == keyBox || box != null && keyBox != null
                        && box.minX == keyBox.minX
                        && box.minY == keyBox.minY
                        && box.minZ == keyBox.minZ
                        && box.maxX == keyBox.maxX
                        && box.maxY == keyBox.maxY
                        && box.maxZ == keyBox.maxZ)
                && worldTime - builtTime >= 0
                && worldTime - builtTime < REBUILD_TICKS) {
            return;
        }

        keyX = px;
        keyY = py;
        keyZ = pz;
        keyFancy = fancy;
        keyCreative = creative;
        keyEnforced = enforced;
        keyBox = box;
        builtTime = worldTime;
        built = true;
        serial++;

        for (int i = 0; i < LAYER_COUNT; i++) {
            layers[i].count = 0;
        }
        nearLockedBiome = false;
        nearLockedStructure = false;
        range = fancy ? 15 : 5;

        if (!enforced || creative) {
            return;
        }

        if (view instanceof EntityPlayer player) {
            scanLockedBiome(world, player, px, py, pz, floorY, rainXCoords, rainYCoords);
        }

        // draw locked structure thing
        nearLockedStructure = box != null
                && box.intersectsWith(px - NEAR_RANGE, pz - NEAR_RANGE, px + NEAR_RANGE, pz + NEAR_RANGE);
        if (nearLockedStructure) {
            buildStructure(box, px, py, pz, rainXCoords, rainYCoords);
        }
    }

    private void scanLockedBiome(WorldClient world, EntityPlayer player, int px, int py, int pz, int floorY,
            float[] rainXCoords, float[] rainYCoords) {
        // check nearby for locked biome
        for (int dz = pz - NEAR_RANGE; dz <= pz + NEAR_RANGE; ++dz) {
            for (int dx = px - NEAR_RANGE; dx <= px + NEAR_RANGE; ++dx) {
                BiomeGenBase biome = world.getBiomeGenForCoords(dx, dz);

                if (biome instanceof TFBiomeBase && !((TFBiomeBase) biome).doesPlayerHaveRequiredAchievement(player)) {
                    nearLockedBiome = true;

                    if (Math.abs(dx - px) <= range && Math.abs(dz - pz) <= range) {
                        int rainIndex = (dz - pz + 16) * 32 + dx - px + 16;
                        buildColumn(
                                world,
                                biome,
                                dx,
                                dz,
                                py,
                                floorY,
                                rainXCoords[rainIndex] * 0.5F,
                                rainYCoords[rainIndex] * 0.5F);
                    }
                }
            }
        }
    }

    private void buildColumn(WorldClient world, BiomeGenBase biome, int dx, int dz, int py, int floorY, float rainX,
            float rainZ) {
        int rainMin = Math.max(0, py - range);
        int rainMax = Math.max(0, py + range * 2);
        int rainFloor = Math.max(0, floorY);

        if (rainMin != rainMax) {
            seedColumn(dx, dz);

            if (biome instanceof TFBiomeSnow || biome instanceof TFBiomeGlacier) {
                // SNOW

                Layer l = layers[BLIZZARD];
                int i = l.add(dx, dz, rainX, rainZ, rainMin, rainMax);
                l.motion(
                        i,
                        0,
                        1.0F,
                        random.nextFloat(),
                        (float) random.nextGaussian(),
                        random.nextFloat(),
                        (float) random.nextGaussian());
                l.tint(
                        i,
                        (world.getLightBrightnessForSkyBlocks(dx, rainFloor, dz, 0) * 3 + 15728880) / 4,
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F);
            } else if (biome instanceof TFBiomeSwamp) {
                // MOSQUITOES

                Layer l = layers[MOSQUITO];
                int i = l.add(dx, dz, rainX, rainZ, rainMin, rainMax);
                l.motion(
                        i,
                        0,
                        0.0F,
                        random.nextFloat(),
                        (float) random.nextGaussian(),
                        random.nextFloat(),
                        (float) random.nextGaussian());
                l.tint(
                        i,
                        983055,
                        random.nextFloat() * 0.3F,
                        random.nextFloat() * 0.3F,
                        random.nextFloat() * 0.3F,
                        UNFADED_ALPHA);
            } else if (biome instanceof TFBiomeFireSwamp) {
                // ASHES

                Layer l = layers[ASHES];
                int i = l.add(dx, dz, rainX, rainZ, rainMin, rainMax);
                l.motion(
                        i,
                        0,
                        -1.0F,
                        random.nextFloat(),
                        (float) random.nextGaussian(),
                        random.nextFloat(),
                        (float) random.nextGaussian());
                float bright = random.nextFloat() * 0.2F + 0.8F;
                l.tint(i, 983055, bright, bright, bright, 1.0F);
            } else if (biome instanceof TFBiomeDarkForest && random.nextInt(2) == 0) {
                // DARKTHINGS

                int darkRainMax = Math.min(rainMax, world.getPrecipitationHeight(dx, dz));
                int darkRainMin = Math.min(rainMin, darkRainMax);
                Layer l = layers[DARKSTREAM];
                int i = l.add(dx, dz, rainX, rainZ, darkRainMin, darkRainMax);
                l.motion(i, 0, -1.0F, 0.0F, 0.0F, random.nextFloat(), (float) random.nextGaussian());
                l.tint(i, 983055, 1.0F, 1.0F, 1.0F, random.nextFloat());
            } else if (biome instanceof TFBiomeHighlands || biome instanceof TFBiomeThornlands
                    || biome instanceof TFBiomeFinalPlateau) {
                        // GREENRAIN

                        Layer l = layers[BIGRAIN];
                        int i = l.add(dx, dz, rainX, rainZ, rainMin, rainMax);
                        l.motion(
                                i,
                                (dx * dx * 3121 + dx * 45238971 + dz * dz * 418711 + dz * 13761) & 31,
                                3.0F + random.nextFloat(),
                                random.nextFloat(),
                                0.0F,
                                random.nextFloat(),
                                0.0F);
                        l.tint(i, world.getLightBrightnessForSkyBlocks(dx, rainFloor, dz, 0), 1.0F, 1.0F, 1.0F, 1.0F);
                    }
        }
    }

    private void buildStructure(StructureBoundingBox box, int px, int py, int pz, float[] rainXCoords,
            float[] rainYCoords) {
        int rainMin = MathHelper.clamp_int(py - range, box.minY - 4, box.maxY + 4);
        int rainMax = MathHelper.clamp_int(py + range * 2, box.minY - 4, box.maxY + 4);
        if (rainMin == rainMax) {
            return;
        }

        Layer l = layers[SPARKLES];
        for (int dz = pz - range; dz <= pz + range; ++dz) {
            for (int dx = px - range; dx <= px + range; ++dx) {
                if (!box.intersectsWith(dx, dz, dx, dz)) {
                    continue;
                }

                int rainIndex = (dz - pz + 16) * 32 + dx - px + 16;
                seedColumn(dx, dz);
                int i = l.add(dx, dz, rainXCoords[rainIndex] * 0.5F, rainYCoords[rainIndex] * 0.5F, rainMin, rainMax);
                l.motion(
                        i,
                        0,
                        1.0F,
                        random.nextFloat(),
                        (float) random.nextGaussian(),
                        random.nextFloat(),
                        (float) random.nextGaussian());
                l.tint(i, 983055, 1.0F, 1.0F, 1.0F, random.nextFloat());
            }
        }
    }

    private void seedColumn(int dx, int dz) {
        random.setSeed(dx * dx * 3121 + dx * 45238971 ^ dz * dz * 418711 + dz * 13761);
    }
}
