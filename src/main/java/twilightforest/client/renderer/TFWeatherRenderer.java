package twilightforest.client.renderer;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import twilightforest.TwilightForestMod;

/**
 * Copypasta of EntityRenderer.renderRainSnow() hacked to include progression environmental effects
 */
public class TFWeatherRenderer extends IRenderHandler {

    private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
    private static final ResourceLocation locationBlizzardPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "blizzard.png");
    private static final ResourceLocation locationMosquitoPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "mosquitoes.png");
    private static final ResourceLocation locationAshesPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "ashes.png");
    private static final ResourceLocation locationDarkstreamPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "darkstream.png");
    private static final ResourceLocation locationBigrainPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "bigrain.png");
    private static final ResourceLocation locationSparklesPng = new ResourceLocation(
            TwilightForestMod.ENVRIO_DIR + "sparkles.png");
    protected static final ResourceLocation[] LAYER_TEXTURES = { locationBlizzardPng, locationMosquitoPng,
            locationAshesPng, locationDarkstreamPng, locationBigrainPng, locationSparklesPng };

    /** Rain X coords */
    float[] rainXCoords;
    /** Rain Y coords */
    float[] rainYCoords;
    protected int rendererUpdateCount;
    private Random random;
    private final TFWeatherColumns columns = new TFWeatherColumns();

    private StructureBoundingBox protectedBox;

    public TFWeatherRenderer() {
        this.random = new Random();
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {

        ++this.rendererUpdateCount;

        // do normal weather rendering
        renderNormalWeather(partialTicks, mc);

        refreshColumns(partialTicks, world, mc.renderViewEntity, mc);
        renderLocked(partialTicks, mc);
    }

    private void renderNormalWeather(float partialTicks, Minecraft mc) {
        float rainStrength = mc.theWorld.getRainStrength(partialTicks);

        if (rainStrength > 0.0F) {
            mc.entityRenderer.enableLightmap(partialTicks);

            this.initializeRainCoords();

            EntityLivingBase entitylivingbase = mc.renderViewEntity;
            WorldClient worldclient = mc.theWorld;
            int k2 = MathHelper.floor_double(entitylivingbase.posX);
            int l2 = MathHelper.floor_double(entitylivingbase.posY);
            int i3 = MathHelper.floor_double(entitylivingbase.posZ);
            Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            double d0 = entitylivingbase.lastTickPosX
                    + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * (double) partialTicks;
            double d1 = entitylivingbase.lastTickPosY
                    + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * (double) partialTicks;
            double d2 = entitylivingbase.lastTickPosZ
                    + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * (double) partialTicks;
            int k = MathHelper.floor_double(d1);
            byte range = 5;

            if (mc.gameSettings.fancyGraphics) {
                range = 10;
            }

            byte b1 = -1;
            float f5 = (float) this.rendererUpdateCount + partialTicks;

            if (mc.gameSettings.fancyGraphics) {
                range = 10;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            for (int l = i3 - range; l <= i3 + range; ++l) {
                for (int i1 = k2 - range; i1 <= k2 + range; ++i1) {
                    int j1 = (l - i3 + 16) * 32 + i1 - k2 + 16;
                    float f6 = this.rainXCoords[j1] * 0.5F;
                    float f7 = this.rainYCoords[j1] * 0.5F;
                    BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(i1, l);

                    if (biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow()) {
                        int k1 = worldclient.getPrecipitationHeight(i1, l);
                        int l1 = l2 - range;
                        int i2 = l2 + range;

                        if (l1 < k1) {
                            l1 = k1;
                        }

                        if (i2 < k1) {
                            i2 = k1;
                        }

                        float f8 = 1.0F;

                        int j2 = Math.max(k1, k);

                        if (l1 != i2) {
                            this.random.setSeed(i1 * i1 * 3121 + i1 * 45238971 ^ l * l * 418711 + l * 13761);
                            float f9 = biomegenbase.getFloatTemperature(i1, l1, l);
                            float downwardsMotion;
                            double xDist;

                            if (worldclient.getWorldChunkManager().getTemperatureAtHeight(f9, k1) >= 0.15F) {
                                if (b1 != 0) {
                                    if (b1 >= 0) {
                                        tessellator.draw();
                                    }

                                    b1 = 0;
                                    mc.getTextureManager().bindTexture(locationRainPng);
                                    tessellator.startDrawingQuads();
                                }

                                downwardsMotion = ((float) (this.rendererUpdateCount + i1 * i1 * 3121
                                        + i1 * 45238971
                                        + l * l * 418711
                                        + l * 13761 & 31) + partialTicks) / 32.0F * (3.0F + this.random.nextFloat());
                                double d3 = (double) ((float) i1 + 0.5F) - entitylivingbase.posX;
                                xDist = (double) ((float) l + 0.5F) - entitylivingbase.posZ;
                                float f12 = MathHelper.sqrt_double(d3 * d3 + xDist * xDist) / (float) range;
                                float f13 = 1.0F;
                                tessellator.setBrightness(worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0));
                                tessellator.setColorRGBA_F(
                                        f13,
                                        f13,
                                        f13,
                                        ((1.0F - f12 * f12) * 0.5F + 0.5F) * rainStrength);
                                tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 - f6) + 0.5D,
                                        l1,
                                        (double) ((float) l - f7) + 0.5D,
                                        0.0F * f8,
                                        (float) l1 * f8 / 4.0F + downwardsMotion * f8);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 + f6) + 0.5D,
                                        l1,
                                        (double) ((float) l + f7) + 0.5D,
                                        1.0F * f8,
                                        (float) l1 * f8 / 4.0F + downwardsMotion * f8);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 + f6) + 0.5D,
                                        i2,
                                        (double) ((float) l + f7) + 0.5D,
                                        1.0F * f8,
                                        (float) i2 * f8 / 4.0F + downwardsMotion * f8);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 - f6) + 0.5D,
                                        i2,
                                        (double) ((float) l - f7) + 0.5D,
                                        0.0F * f8,
                                        (float) i2 * f8 / 4.0F + downwardsMotion * f8);
                                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                            } else {
                                if (b1 != 1) {
                                    if (b1 >= 0) {
                                        tessellator.draw();
                                    }

                                    b1 = 1;
                                    mc.getTextureManager().bindTexture(locationSnowPng);
                                    tessellator.startDrawingQuads();
                                }

                                downwardsMotion = ((float) (this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
                                float f16 = this.random.nextFloat() + f5 * 0.01F * (float) this.random.nextGaussian();
                                float f11 = this.random.nextFloat() + f5 * (float) this.random.nextGaussian() * 0.001F;
                                xDist = (double) ((float) i1 + 0.5F) - entitylivingbase.posX;
                                double zDist = (double) ((float) l + 0.5F) - entitylivingbase.posZ;
                                float f14 = MathHelper.sqrt_double(xDist * xDist + zDist * zDist) / (float) range;
                                float f15 = 1.0F;
                                tessellator.setBrightness(
                                        (worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4);
                                tessellator.setColorRGBA_F(
                                        f15,
                                        f15,
                                        f15,
                                        ((1.0F - f14 * f14) * 0.3F + 0.5F) * rainStrength);
                                tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 - f6) + 0.5D,
                                        l1,
                                        (double) ((float) l - f7) + 0.5D,
                                        0.0F * f8 + f16,
                                        (float) l1 * f8 / 4.0F + downwardsMotion * f8 + f11);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 + f6) + 0.5D,
                                        l1,
                                        (double) ((float) l + f7) + 0.5D,
                                        1.0F * f8 + f16,
                                        (float) l1 * f8 / 4.0F + downwardsMotion * f8 + f11);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 + f6) + 0.5D,
                                        i2,
                                        (double) ((float) l + f7) + 0.5D,
                                        1.0F * f8 + f16,
                                        (float) i2 * f8 / 4.0F + downwardsMotion * f8 + f11);
                                tessellator.addVertexWithUV(
                                        (double) ((float) i1 - f6) + 0.5D,
                                        i2,
                                        (double) ((float) l - f7) + 0.5D,
                                        0.0F * f8 + f16,
                                        (float) i2 * f8 / 4.0F + downwardsMotion * f8 + f11);
                                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                            }
                        }
                    }
                }
            }

            if (b1 >= 0) {
                tessellator.draw();
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            mc.entityRenderer.disableLightmap(partialTicks);
        }
    }

    protected void refreshColumns(float partialTicks, WorldClient world, EntityLivingBase view, Minecraft mc) {
        this.initializeRainCoords();
        boolean enforced = world.getGameRules().getGameRuleBooleanValue(TwilightForestMod.ENFORCED_PROGRESSION_RULE);
        boolean creative = mc.thePlayer.capabilities.isCreativeMode;
        this.columns.update(
                world,
                view,
                mc.gameSettings.fancyGraphics,
                creative,
                enforced,
                this.protectedBox,
                world.getTotalWorldTime(),
                partialTicks,
                this.rainXCoords,
                this.rainYCoords);
    }

    protected TFWeatherColumns columns() {
        return this.columns;
    }

    private void renderLocked(float partialTicks, Minecraft mc) {
        if (!this.columns.isNearLockedBiome() && !this.columns.isNearLockedStructure()) {
            return;
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int layer = 0; layer < TFWeatherColumns.LAYER_COUNT; ++layer) {
            drawLayer(layer, partialTicks, mc);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        mc.entityRenderer.disableLightmap(partialTicks);
    }

    private void drawLayer(int layerIndex, float partialTicks, Minecraft mc) {
        TFWeatherColumns.Layer layer = this.columns.layer(layerIndex);
        int count = layer.count;
        if (count == 0) {
            return;
        }

        EntityLivingBase view = mc.renderViewEntity;
        Tessellator tessellator = Tessellator.instance;
        double offX = view.lastTickPosX + (view.posX - view.lastTickPosX) * (double) partialTicks;
        double offY = view.lastTickPosY + (view.posY - view.lastTickPosY) * (double) partialTicks;
        double offZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * (double) partialTicks;
        float preciseCount = (float) this.rendererUpdateCount + partialTicks;
        float range = (float) this.columns.range();
        int tick = this.rendererUpdateCount;
        int period = TFWeatherColumns.PERIOD[layerIndex];
        int mask = period - 1;
        float periodF = (float) period;
        float uScale = TFWeatherColumns.U_SCALE[layerIndex];
        float vScale = TFWeatherColumns.V_SCALE[layerIndex];
        float fadeScale = TFWeatherColumns.FADE_SCALE[layerIndex];

        mc.getTextureManager().bindTexture(LAYER_TEXTURES[layerIndex]);
        tessellator.startDrawingQuads();
        tessellator.setTranslation(-offX, -offY, -offZ);

        for (int i = 0; i < count; ++i) {
            int dx = layer.x[i];
            int dz = layer.z[i];
            float rainX = layer.rx[i];
            float rainZ = layer.rz[i];
            int rainMin = layer.bottom[i];
            int rainMax = layer.top[i];

            float countFactor = ((float) (tick + layer.scrollOffset[i] & mask) + partialTicks) / periodF
                    * layer.scrollScale[i];
            float uFactor = layer.uBase[i] + preciseCount * uScale * layer.uDrift[i];
            float vFactor = layer.vBase[i] + preciseCount * vScale * layer.vDrift[i];
            double xRange = (double) ((float) dx + 0.5F) - view.posX;
            double zRange = (double) ((float) dz + 0.5F) - view.posZ;
            float distanceFromPlayer = MathHelper.sqrt_double(xRange * xRange + zRange * zRange) / range;

            tessellator.setBrightness(layer.brightness[i]);
            tessellator.setColorRGBA_F(
                    layer.r[i],
                    layer.g[i],
                    layer.b[i],
                    ((1.0F - distanceFromPlayer * distanceFromPlayer) * fadeScale + 0.5F) * layer.a[i]);
            tessellator.addVertexWithUV(
                    (double) ((float) dx - rainX) + 0.5D,
                    rainMin,
                    (double) ((float) dz - rainZ) + 0.5D,
                    0.0F + uFactor,
                    (float) rainMin / 4.0F + countFactor + vFactor);
            tessellator.addVertexWithUV(
                    (double) ((float) dx + rainX) + 0.5D,
                    rainMin,
                    (double) ((float) dz + rainZ) + 0.5D,
                    1.0F + uFactor,
                    (float) rainMin / 4.0F + countFactor + vFactor);
            tessellator.addVertexWithUV(
                    (double) ((float) dx + rainX) + 0.5D,
                    rainMax,
                    (double) ((float) dz + rainZ) + 0.5D,
                    1.0F + uFactor,
                    (float) rainMax / 4.0F + countFactor + vFactor);
            tessellator.addVertexWithUV(
                    (double) ((float) dx - rainX) + 0.5D,
                    rainMax,
                    (double) ((float) dz - rainZ) + 0.5D,
                    0.0F + uFactor,
                    (float) rainMax / 4.0F + countFactor + vFactor);
        }

        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
    }

    private void initializeRainCoords() {
        if (this.rainXCoords == null) {
            this.rainXCoords = new float[1024];
            this.rainYCoords = new float[1024];

            for (int i = 0; i < 32; ++i) {
                for (int j = 0; j < 32; ++j) {
                    float f2 = (float) (j - 16);
                    float f3 = (float) (i - 16);
                    float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);
                    this.rainXCoords[i << 5 | j] = -f3 / f4;
                    this.rainYCoords[i << 5 | j] = f2 / f4;
                }
            }
        }
    }

    public StructureBoundingBox getProtectedBox() {
        return protectedBox;
    }

    public void setProtectedBox(StructureBoundingBox protectedBox) {
        this.protectedBox = protectedBox;

        // System.out.println("Set protected box to " + protectedBox);
    }

}

// if (l > 0 && this.random.nextInt(3) < this.rainSoundCounter++)
// {
// this.rainSoundCounter = 0;
//
// if (d1 > entitylivingbase.posY + 1.0D &&
// worldclient.getPrecipitationHeight(MathHelper.floor_double(entitylivingbase.posX),
// MathHelper.floor_double(entitylivingbase.posZ)) > MathHelper.floor_double(entitylivingbase.posY))
// {
// this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.1F, 0.5F, false);
// }
// else
// {
// this.mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.2F, 1.0F, false);
// }
// }
