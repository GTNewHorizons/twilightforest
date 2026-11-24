package twilightforest.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.entity.passive.EntityTFBunny;

public class RenderTFBunny extends RenderLiving {

    private static final ResourceLocation textureLocDutch = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "bunnydutch.png");
    private static final ResourceLocation textureLocWhite = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "bunnywhite.png");
    private static final ResourceLocation textureLocBrown = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "bunnybrown.png");

    public RenderTFBunny(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);

    }

    /**
     * Return our specific texture
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        if (par1Entity instanceof EntityTFBunny) {
            return switch (((EntityTFBunny) par1Entity).getBunnyType()) {
                default -> textureLocDutch;
                case 2 -> textureLocWhite;
                case 3 -> textureLocBrown;
            };
        }

        // fallback
        return textureLocDutch;
    }

}
