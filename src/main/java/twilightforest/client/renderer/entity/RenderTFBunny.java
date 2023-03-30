package twilightforest.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.entity.passive.EntityTFBunny;

public class RenderTFBunny extends RenderLiving {

    final ResourceLocation textureLocDutch;
    final ResourceLocation textureLocWhite;
    final ResourceLocation textureLocBrown;

    public RenderTFBunny(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);

        textureLocDutch = new ResourceLocation(TwilightForestMod.MODEL_DIR + "bunnydutch.png");
        textureLocWhite = new ResourceLocation(TwilightForestMod.MODEL_DIR + "bunnywhite.png");
        textureLocBrown = new ResourceLocation(TwilightForestMod.MODEL_DIR + "bunnybrown.png");
    }

    /**
     * Return our specific texture
     */
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
