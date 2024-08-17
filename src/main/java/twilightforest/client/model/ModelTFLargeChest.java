package twilightforest.client.model;

import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.model.ModelRenderer;

public class ModelTFLargeChest extends ModelLargeChest {

    public ModelTFLargeChest() {
        super();
        this.chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.chestLid.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.01F);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7.0F;
        this.chestLid.rotationPointZ = 15.0F;
    }

}
