package twilightforest.integration;

import net.minecraft.item.ItemStack;

public class TFNeiIntegration {

    public static void hideItem(ItemStack item) {
        codechicken.nei.api.API.hideItem(item);
    }
}
