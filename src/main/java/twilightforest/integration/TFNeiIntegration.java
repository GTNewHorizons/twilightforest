package twilightforest.integration;

import net.minecraft.item.ItemStack;

public final class TFNeiIntegration {

    private TFNeiIntegration() {}

    public static void hideItem(ItemStack item) {
        if (item != null) {
            codechicken.nei.api.API.hideItem(item);
        }
    }
}
