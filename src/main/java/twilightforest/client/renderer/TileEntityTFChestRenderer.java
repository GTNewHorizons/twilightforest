package twilightforest.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TwilightForestMod;
import twilightforest.block.BlockTFChest;
import twilightforest.client.model.ModelTFChest;
import twilightforest.client.model.ModelTFLargeChest;
import twilightforest.tileentity.TileEntityTFChest;

@SideOnly(Side.CLIENT)
public class TileEntityTFChestRenderer extends TileEntityChestRenderer {

    public static final String[] names = { "twilight", "canopy", "mangrove", "darkwood", "time", "trans", "mining",
            "sort" };
    private static final ResourceLocation[] doubleTextures = new ResourceLocation[names.length];
    private static final ResourceLocation[] textures = new ResourceLocation[names.length];

    static {
        for (int i = 0; i < names.length; i++) {
            doubleTextures[i] = new ResourceLocation(TwilightForestMod.MODEL_DIR + "chest/" + names[i] + "_double.png");
            textures[i] = new ResourceLocation(TwilightForestMod.MODEL_DIR + "chest/" + names[i] + ".png");
        }
    }

    // this is very much not threadsafe
    private TileEntityChest tileEntityBeingRendered = null;

    public TileEntityTFChestRenderer() {
        field_147510_h/* modelChest */ = new ModelTFChest();
        field_147511_i/* modelLargeChest */ = new ModelTFLargeChest();
    }

    @Override
    protected void bindTexture(ResourceLocation resourceLocation) {
        BlockTFChest.WoodType type = getWoodType(tileEntityBeingRendered);
        if (tileEntityBeingRendered.adjacentChestXPos == null && tileEntityBeingRendered.adjacentChestZPos == null) {
            super.bindTexture(textures[type.ordinal()]);
        } else {
            super.bindTexture(doubleTextures[type.ordinal()]);
        }
    }

    @Override
    public void renderTileEntityAt(TileEntityChest tileEntity, double x, double y, double z, float f) {
        TileEntityChest previous = tileEntityBeingRendered;
        tileEntityBeingRendered = tileEntity;
        try {
            super.renderTileEntityAt(tileEntity, x, y, z, f);
        } finally {
            tileEntityBeingRendered = previous;
        }
    }

    private BlockTFChest.WoodType getWoodType(TileEntityChest tile) {
        if (!(tile instanceof TileEntityTFChest tileTFChest)) return BlockTFChest.WoodType.CANOPY;

        if (tileTFChest.cachedMaterial != BlockTFChest.WoodType.NULL) return tileTFChest.cachedMaterial;
        if (tile.getBlockType() instanceof BlockTFChest blockTFChest) return blockTFChest.getWoodType();
        return BlockTFChest.WoodType.CANOPY;
    }
}
