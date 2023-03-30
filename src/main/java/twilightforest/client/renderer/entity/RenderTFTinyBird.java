package twilightforest.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.entity.passive.EntityTFTinyBird;

public class RenderTFTinyBird extends RenderTFBird {

    final ResourceLocation textureLocSparrow;
    final ResourceLocation textureLocFinch;
    final ResourceLocation textureLocCardinal;
    final ResourceLocation textureLocBluebird;

    public RenderTFTinyBird(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2, "tinybirdbrown.png");

        textureLocSparrow = new ResourceLocation(TwilightForestMod.MODEL_DIR + "tinybirdbrown.png");
        textureLocFinch = new ResourceLocation(TwilightForestMod.MODEL_DIR + "tinybirdgold.png");
        textureLocCardinal = new ResourceLocation(TwilightForestMod.MODEL_DIR + "tinybirdred.png");
        textureLocBluebird = new ResourceLocation(TwilightForestMod.MODEL_DIR + "tinybirdblue.png");
    }

    /**
     * Return our specific texture
     */
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        if (par1Entity instanceof EntityTFTinyBird) {
            return switch (((EntityTFTinyBird) par1Entity).getBirdType()) {
                default -> textureLocSparrow;
                case 1 -> textureLocBluebird;
                case 2 -> textureLocCardinal;
                case 3 -> textureLocFinch;
            };
        }

        // fallback
        return textureLocSparrow;
    }
}
