package twilightforest.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import twilightforest.TFAchievementPage;

public class ItemTFHydraChops extends ItemTFFood {

    public ItemTFHydraChops(int par2, float par3, boolean par4) {
        super(par2, par3, par4);
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
        // if the player is at zero food, achievements
        if (!world.isRemote && player.getFoodStats().getFoodLevel() <= 0) {
            player.triggerAchievement(TFAchievementPage.twilightHydraChop);
        }
        // then normal effects
        return super.onEaten(itemStack, world, player);
    }
}
