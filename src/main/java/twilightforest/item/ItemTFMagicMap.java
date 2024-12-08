package twilightforest.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.MapData.MapInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.TFFeature;
import twilightforest.TFMagicMapData;
import twilightforest.TFMapPacketHandler;
import twilightforest.TwilightForestMod;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class ItemTFMagicMap extends ItemMap {

    public static final String STR_ID = "magicmap";

    protected ItemTFMagicMap() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public static TFMagicMapData getMPMapData(int par0, World world) {
        String mapName = STR_ID + "_" + par0;
        TFMagicMapData mapData = (TFMagicMapData) world.loadItemData(TFMagicMapData.class, mapName);

        if (mapData == null) {
            mapData = new TFMagicMapData(mapName);
            world.setItemData(mapName, mapData);
        }

        return mapData;
    }

    @Override
    public TFMagicMapData getMapData(ItemStack itemStack, World world) {
        String mapName = STR_ID + "_" + itemStack.getItemDamage();
        TFMagicMapData mapData = (TFMagicMapData) world.loadItemData(TFMagicMapData.class, mapName);

        if (mapData == null && !world.isRemote) {
            itemStack.setItemDamage(world.getUniqueDataId(STR_ID));
            mapName = STR_ID + "_" + itemStack.getItemDamage();
            mapData = new TFMagicMapData(mapName);
            mapData.xCenter = world.getWorldInfo().getSpawnX();
            mapData.zCenter = world.getWorldInfo().getSpawnZ();
            mapData.scale = 4;
            mapData.dimension = world.provider.dimensionId;
            mapData.markDirty();
            world.setItemData(mapName, mapData);
        }

        return mapData;
    }

    public void updateMapData(World world, Entity entity, TFMagicMapData mapData) {
        if (world.provider.dimensionId == mapData.dimension && entity instanceof EntityPlayer) {
            ChunkProviderTwilightForest chunkProvider = ((WorldProviderTwilightForest) world.provider)
                    .getChunkProvider();
            short xSize = 128;
            short zSize = 128;
            int scaleFactor = 1 << mapData.scale;
            int xCenter = mapData.xCenter;
            int zCenter = mapData.zCenter;
            int xDraw = MathHelper.floor_double(entity.posX - (double) xCenter) / scaleFactor + xSize / 2;
            int zDraw = MathHelper.floor_double(entity.posZ - (double) zCenter) / scaleFactor + zSize / 2;
            int drawSize = 512 / scaleFactor;
            // int drawSize = 1024 / scaleFactor;

            MapInfo mapInfo = mapData.func_82568_a((EntityPlayer) entity);
            ++mapInfo.field_82569_d;

            for (int xStep = xDraw - drawSize + 1; xStep < xDraw + drawSize; ++xStep) {
                if ((xStep & 15) == (mapInfo.field_82569_d & 15)) {
                    int highNumber = 255;
                    int lowNumber = 0;

                    for (int zStep = zDraw - drawSize - 1; zStep < zDraw + drawSize; ++zStep) {
                        if (xStep >= 0 && zStep >= -1 && xStep < xSize && zStep < zSize) {
                            int xOffset = xStep - xDraw;
                            int zOffset = zStep - zDraw;
                            boolean var20 = xOffset * xOffset + zOffset * zOffset > (drawSize - 2) * (drawSize - 2);
                            int xDraw2 = (xCenter / scaleFactor + xStep - xSize / 2) * scaleFactor;
                            int zDraw2 = (zCenter / scaleFactor + zStep - zSize / 2) * scaleFactor;
                            int[] biomeFrequencies = new int[256];
                            int zStep2;
                            int xStep2;

                            int biomeID;

                            for (xStep2 = 0; xStep2 < scaleFactor; ++xStep2) {
                                for (zStep2 = 0; zStep2 < scaleFactor; ++zStep2) {
                                    biomeID = world.getBiomeGenForCoords(xDraw2 + xStep2, zDraw2 + zStep2).biomeID;

                                    biomeFrequencies[biomeID]++;

                                    // make rivers and streams 3x more prominent
                                    if (biomeID == BiomeGenBase.river.biomeID
                                            || biomeID == TFBiomeBase.stream.biomeID) {
                                        biomeFrequencies[biomeID] += 2;
                                    }
                                    // add in TF features
                                    if (world.getWorldChunkManager() instanceof TFWorldChunkManager tfManager) {

                                        if (tfManager.isInFeatureChunk(world, xDraw2 + xStep2, zDraw2 + zStep2)
                                                && zStep >= 0
                                                && xOffset * xOffset + zOffset * zOffset < drawSize * drawSize) {
                                            mapData.addFeatureToMap(
                                                    TFFeature.getNearestFeature(
                                                            (xDraw2 + xStep2) >> 4,
                                                            (zDraw2 + zStep2) >> 4,
                                                            world),
                                                    xDraw2,
                                                    zDraw2,
                                                    chunkProvider
                                                            .isStructureConquered(xDraw2 + xStep2, 0, zDraw2 + zStep2));
                                        }
                                    }
                                }
                            }

                            // figure out which color is the most prominent and make that one appear on the
                            // map
                            byte biomeIDToShow = 0;
                            int highestFrequency = 0;

                            for (int i = 0; i < 256; i++) {
                                if (biomeFrequencies[i] > highestFrequency) {
                                    biomeIDToShow = (byte) i;
                                    highestFrequency = biomeFrequencies[i];
                                }
                            }

                            // increase biomeID by one so we can properly show oceans
                            biomeIDToShow++;

                            if (zStep >= 0 && xOffset * xOffset + zOffset * zOffset < drawSize * drawSize
                                    && (!var20 || (xStep + zStep & 1) != 0)) {
                                byte existingColor = mapData.colors[xStep + zStep * xSize];

                                if (existingColor != biomeIDToShow) {
                                    if (highNumber > zStep) {
                                        highNumber = zStep;
                                    }

                                    if (lowNumber < zStep) {
                                        lowNumber = zStep;
                                    }

                                    mapData.colors[xStep + zStep * xSize] = biomeIDToShow;
                                }
                            }
                        }
                    }
                    if (highNumber <= lowNumber) {
                        mapData.setColumnDirty(xStep, highNumber, lowNumber);
                    }
                }
            }
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
        if (!world.isRemote) {
            TFMagicMapData mapData = this.getMapData(itemStack, world);

            if (entity instanceof EntityPlayer var7) {
                mapData.updateVisiblePlayers(var7, itemStack);
            }

            if (par5) {
                this.updateMapData(world, entity, mapData);
            }
        }
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        // we don't need to do anything here, I think
    }

    /**
     * Return an item rarity from EnumRarity
     */
    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.uncommon;
    }

    /**
     * Do the enchanted shimmer thing
     */
    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return false;
    }

    /**
     * returns null if no update is to be sent
     * 
     * We have re-written this to provide a Packet250CustomPayload to be sent, since the map data packet is only for the
     * actual map map.
     * 
     * Also every 4 player update packets we send is actually a feature icon update packet.
     */
    @Override
    public Packet func_150911_c(ItemStack itemStack, World world, EntityPlayer player) {
        // System.out.println("Getting magic map packet");

        byte[] mapBytes = this.getMapData(itemStack, world).getUpdatePacketData(itemStack, world, player);

        if (mapBytes == null) {
            return null;
        } else {
            // hijack random player data packets for our feature data packets?
            if (mapBytes[0] == 1 && world.rand.nextInt(4) == 0) {
                this.getMapData(itemStack, world).checkExistingFeatures(world);
                mapBytes = this.getMapData(itemStack, world).makeFeatureStorageArray();
            }

            // short mapItemID = (short) TFItems.magicMap;
            short uniqueID = (short) itemStack.getItemDamage();

            return TFMapPacketHandler.makeMagicMapPacket(ItemTFMagicMap.STR_ID, uniqueID, mapBytes);
        }
    }

    // Add the map number to the tooltip
    public String getItemStackDisplayName(ItemStack itemStack) {
        return (StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(itemStack) + ".name") + " #"
                + itemStack.getItemDamage()).trim();
    }

    // Properly register icon source
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister
                .registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
    }

}
