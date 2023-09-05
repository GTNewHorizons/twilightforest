package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TFAchievementPage;
import twilightforest.TFMagicMapData;
import twilightforest.TwilightForestMod;

public class ItemTFEmptyMagicMap extends ItemMapBase {

    protected ItemTFEmptyMagicMap() {
        super();
        this.setCreativeTab(TFItems.creativeTab);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.provider.dimensionId != TwilightForestMod.dimensionID) {
            return itemStack;
        }
        ItemStack mapItem = new ItemStack(TFItems.magicMap, 1, world.getUniqueDataId(ItemTFMagicMap.STR_ID));
        String mapName = ItemTFMagicMap.STR_ID + "_" + mapItem.getItemDamage();
        MapData mapData = new TFMagicMapData(mapName);
        world.setItemData(mapName, mapData);
        mapData.scale = 4;
        int step = 128 * (1 << mapData.scale);
        mapData.xCenter = (int) (Math.round(player.posX / step) * step);
        mapData.zCenter = (int) (Math.round(player.posZ / step) * step);
        mapData.dimension = (byte) world.provider.dimensionId;
        mapData.markDirty();
        --itemStack.stackSize;

        // cheevo
        if (mapItem.getItem() == TFItems.magicMap) {
            player.triggerAchievement(TFAchievementPage.twilightMagicMap);
        }

        if (itemStack.stackSize <= 0) {
            return mapItem;
        } else {
            if (!player.inventory.addItemStackToInventory(mapItem.copy())) {
                player.dropPlayerItemWithRandomChoice(mapItem, false);
            }

            return itemStack;
        }
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }
}
