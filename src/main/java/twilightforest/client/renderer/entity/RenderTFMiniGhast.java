package twilightforest.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.entity.EntityTFTowerGhast;

public class RenderTFMiniGhast extends RenderLiving {

    private static final ResourceLocation textureLocClosed = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerghast.png");
    private static final ResourceLocation textureLocOpen = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerghast_openeyes.png");
    private static final ResourceLocation textureLocAttack = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "towerghast_fire.png");

    public RenderTFMiniGhast(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);

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
