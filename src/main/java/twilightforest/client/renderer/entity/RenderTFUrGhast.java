package twilightforest.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.client.model.ModelTFGhast;
import twilightforest.entity.EntityTFTowerGhast;

public class RenderTFUrGhast extends RenderTFTowerGhast {

    private static final ResourceLocation textureLocClosed = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerboss.png");
    private static final ResourceLocation textureLocOpen = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerboss_openeyes.png");
    private static final ResourceLocation textureLocAttack = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerboss_fire.png");

    public RenderTFUrGhast(ModelTFGhast modelTFGhast, float f, float scale) {
        super(modelTFGhast, f, scale);
    }

    /**
     * Return our specific texture
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        if (par1Entity instanceof EntityTFTowerGhast) {
            return switch (((EntityTFTowerGhast) par1Entity).getAttackStatus()) {
                default -> textureLocClosed;
                case 1 -> textureLocOpen;
                case 2 -> textureLocAttack;
            };
        }

        // fallback
        return textureLocClosed;
    }
}
