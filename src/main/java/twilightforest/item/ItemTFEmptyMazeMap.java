package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TFAchievementPage;
import twilightforest.TFFeature;
import twilightforest.TFMazeMapData;
import twilightforest.TwilightForestMod;
import twilightforest.world.WorldProviderTwilightForest;

public class ItemTFEmptyMazeMap extends ItemMapBase {

    boolean mapOres;

    protected ItemTFEmptyMazeMap(boolean mapOres) {
        super();
        this.setCreativeTab(TFItems.creativeTab);
        this.mapOres = mapOres;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.provider.dimensionId != TwilightForestMod.dimensionID) {
            return itemStack;
        }
        ItemStack mapItem = new ItemStack(
                mapOres ? TFItems.oreMap : TFItems.mazeMap,
                1,
                world.getUniqueDataId(ItemTFMazeMap.STR_ID));
        String var5 = "mazemap_" + mapItem.getItemDamage();
        TFMazeMapData mapData = new TFMazeMapData(var5);
        world.setItemData(var5, mapData);
        mapData.scale = 0;
        int step = 128 * (1 << mapData.scale);
        // need to fix center for feature offset
        if (world.provider instanceof WorldProviderTwilightForest && TFFeature.getFeatureForRegion(
                MathHelper.floor_double(player.posX) >> 4,
                MathHelper.floor_double(player.posZ) >> 4,
                world) == TFFeature.labyrinth) {
            ChunkCoordinates mc = TFFeature.getNearestCenterXYZ(
                    MathHelper.floor_double(player.posX) >> 4,
                    MathHelper.floor_double(player.posZ) >> 4,
                    world);
            mapData.xCenter = mc.posX;
            mapData.zCenter = mc.posZ;
            mapData.yCenter = MathHelper.floor_double(player.posY);
        } else {
            mapData.xCenter = (int) (Math.round(player.posX / step) * step) + 10; // mazes are offset slightly
            mapData.zCenter = (int) (Math.round(player.posZ / step) * step) + 10; // mazes are offset slightly
            mapData.yCenter = MathHelper.floor_double(player.posY);
        }
        mapData.dimension = world.provider.dimensionId;
        mapData.markDirty();
        --itemStack.stackSize;

        // cheevos
        if (mapItem.getItem() == TFItems.mazeMap) {
            player.triggerAchievement(TFAchievementPage.twilightMazeMap);
        }
        if (mapItem.getItem() == TFItems.oreMap) {
            player.triggerAchievement(TFAchievementPage.twilightOreMap);
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
