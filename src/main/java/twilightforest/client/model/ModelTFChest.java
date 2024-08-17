package twilightforest.client.model;

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;

public class ModelTFChest extends ModelChest {

    public ModelTFChest() {
        super();
        chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
        this.chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.01F);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7.0F;
        this.chestLid.rotationPointZ = 15.0F;
    }

}
