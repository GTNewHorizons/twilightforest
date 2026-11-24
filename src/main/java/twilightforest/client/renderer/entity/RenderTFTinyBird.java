package twilightforest.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import twilightforest.TwilightForestMod;
import twilightforest.entity.passive.EntityTFTinyBird;

public class RenderTFTinyBird extends RenderTFBird {

    private static final ResourceLocation textureLocSparrow = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "tinybirdbrown.png");
    private static final ResourceLocation textureLocFinch = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "tinybirdgold.png");
    private static final ResourceLocation textureLocCardinal = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "tinybirdred.png");
    private static final ResourceLocation textureLocBluebird = new ResourceLocation(
            TwilightForestMod.MODEL_DIR + "tinybirdblue.png");

    public RenderTFTinyBird(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2, "tinybirdbrown.png");

    }

    /**
     * Return our specific texture
     */
    @Override
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
