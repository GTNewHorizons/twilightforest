package tconstruct.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import mantle.world.WorldHelper;
import tconstruct.armor.player.TFTPlayerStats;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.weaponry.entity.ArrowEntity;
import tconstruct.weaponry.entity.BoltEntity;
import twilightforest.integration.TFTinkerConstructIntegration;
import twilightforest.integration.TFTinkerConstructIntegration.MaterialID;
import twilightforest.item.TFItems;

public class TFToolEvents {

    Random random = new Random();

    @SubscribeEvent
    public void craftTool(ToolCraftEvent.NormalTool event) {
        NBTTagCompound toolTag = event.toolTag.getCompoundTag("InfiTool");
        List<Integer> twilitMaterials = new ArrayList<Integer>();
        twilitMaterials.add(MaterialID.FieryMetal);
        twilitMaterials.add(MaterialID.Knightmetal);
        twilitMaterials.add(MaterialID.NagaScale);
        twilitMaterials.add(MaterialID.Steeleaf);
        boolean hasTwilit = false;
        int twilitID = 5;
        if (twilitMaterials.contains(toolTag.getInteger("Head"))) {
            hasTwilit = true;
            twilitID = toolTag.getInteger("Head");
        } else {
            if (twilitMaterials.contains(toolTag.getInteger("Handle"))) {
                hasTwilit = true;
                twilitID = toolTag.getInteger("Handle");
            } else {
                if (twilitMaterials.contains(toolTag.getInteger("Accessory"))) {
                    hasTwilit = true;
                    twilitID = toolTag.getInteger("Accessory");
                } else {
                    if (twilitMaterials.contains(toolTag.getInteger("Extra"))) {
                        hasTwilit = true;
                        twilitID = toolTag.getInteger("Extra");
                    } else {
                        if (event.tool instanceof AmmoItem && toolTag.getInteger("Accessory") == 5) {
                            hasTwilit = true;
                        }
                    }
                }
            }
        }
        if (hasTwilit) {
            toolTag.setBoolean("IsInTF", true);
            toolTag.setInteger("MiningSpeed", toolTag.getInteger("MiningSpeed") + 200);
            toolTag.setInteger("TwilitID", twilitID);
        }

        if (toolTag.getInteger("Head") == MaterialID.NagaScale || toolTag.getInteger("Handle") == MaterialID.NagaScale
                || toolTag.getInteger("Accessory") == MaterialID.NagaScale
                || toolTag.getInteger("Extra") == MaterialID.NagaScale) {
            toolTag.setInteger("PrecipitateSpeed", toolTag.getInteger("MiningSpeed"));
        }

        if (toolTag.getInteger("Head") == MaterialID.Steeleaf || toolTag.getInteger("Handle") == MaterialID.Steeleaf
                || toolTag.getInteger("Accessory") == MaterialID.Steeleaf
                || toolTag.getInteger("Extra") == MaterialID.Steeleaf) {
            toolTag.setBoolean("Synergy", true);
        }

        if (toolTag.getInteger("Head") == MaterialID.Knightmetal
                || toolTag.getInteger("Handle") == MaterialID.Knightmetal
                || toolTag.getInteger("Accessory") == MaterialID.Knightmetal
                || toolTag.getInteger("Extra") == MaterialID.Knightmetal) {
            toolTag.setBoolean("Stalwart", true);
        }

        if (toolTag.getInteger("Head") == MaterialID.FieryMetal || toolTag.getInteger("Handle") == MaterialID.FieryMetal
                || toolTag.getInteger("Accessory") == MaterialID.FieryMetal
                || toolTag.getInteger("Extra") == MaterialID.FieryMetal) {
            toolTag.setBoolean("Lava", true);
            toolTag.setBoolean("TFFiery", true);
            toolTag.setInteger("Fiery", 1);
        }
    }

    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {
        if (event.itemStack == null || event.itemStack.getItem() == null
                || !event.itemStack.hasTagCompound()
                || !event.itemStack.getTagCompound().hasKey("InfiTool")
                || !event.itemStack.getTagCompound().getCompoundTag("InfiTool").hasKey("IsInTF"))
            return;

        NBTTagCompound tags = event.itemStack.getTagCompound().getCompoundTag("InfiTool");
        int tooltipIndex = 1;
        int lastEmptyIndex = -1;
        int twilitID = tags.getInteger("TwilitID");
        ChatFormatting color = colorFromID(twilitID);
        for (int i = event.toolTip.size() - 1; i > 1; i--) {
            if (event.toolTip.get(i).contains(color.toString())) tooltipIndex = i;
            if (lastEmptyIndex == -1 && event.toolTip.get(i).isEmpty() && !event.toolTip.get(i - 1).isEmpty())
                lastEmptyIndex = i;
        }
        if (lastEmptyIndex == -1) lastEmptyIndex = event.toolTip.size();
        if (twilitID == 5) tooltipIndex = lastEmptyIndex;

        event.toolTip.add(tooltipIndex, "" + color + StatCollector.translateToLocal("material.twilit.ability"));
        if (event.itemStack.getItem() instanceof ArrowAmmo || event.itemStack.getItem() instanceof BoltAmmo)
            if (tags.getInteger("Accessory") == 5) event.toolTip.add(
                    lastEmptyIndex + 1,
                    "" + ChatFormatting.DARK_GRAY + StatCollector.translateToLocal("material.raven_feather.ability"));
        if (tags.hasKey("TFFiery")) {
            event.toolTip.add(
                    tooltipIndex + 1,
                    "" + ChatFormatting.GOLD + StatCollector.translateToLocal("modifier.tooltip.Auto-Smelt"));
            event.toolTip.add(
                    tooltipIndex + 2,
                    "" + ChatFormatting.GOLD + StatCollector.translateToLocal("modifier.tooltip.Fiery"));
        }
    }

    private ChatFormatting colorFromID(int materialID) {
        ChatFormatting cf;
        switch (materialID) {
            default:
                cf = ChatFormatting.DARK_GRAY;
                break;
            case MaterialID.FieryMetal:
                cf = ChatFormatting.GOLD;
                break;
            case MaterialID.Knightmetal:
                cf = ChatFormatting.GREEN;
                break;
            case MaterialID.NagaScale:
            case MaterialID.Steeleaf:
                cf = ChatFormatting.DARK_GREEN;
                break;
        }
        return cf;
    }

    @SubscribeEvent
    public void onArrowSpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof ArrowEntity || event.entity instanceof BoltEntity) {
            ProjectileBase entity = (ProjectileBase) event.entity;
            ItemStack entityItem = entity.getEntityItem();
            int accessory = entityItem.getTagCompound().getCompoundTag("InfiTool").getInteger("Accessory");
            if (accessory == 5) {
                entity.setInvisible(true);
                entity.renderDistanceWeight = 0;
            }
        }
    }

    @SubscribeEvent
    public void onProjectileHit(LivingHurtEvent event) {
        if (event.source instanceof EntityDamageSourceIndirect) {
            EntityDamageSourceIndirect damageSource = (EntityDamageSourceIndirect) event.source;
            Entity damageSourceEntity = getDamageSourceEntity(damageSource);
            if (damageSourceEntity instanceof ProjectileBase) {
                ProjectileBase entity = (ProjectileBase) damageSourceEntity;
                ItemStack projectile = entity.getEntityItem();
                if (projectile.getTagCompound().getCompoundTag("InfiTool").hasKey("Stalwart")) {
                    Entity indirectEntity = getIndirectEntity(damageSource);
                    if (indirectEntity instanceof EntityLivingBase) {
                        stalwartBuff((EntityLivingBase) indirectEntity);
                    }
                }
                if (damageSourceEntity instanceof ArrowEntity || damageSourceEntity instanceof BoltEntity) {
                    int accessory = projectile.getTagCompound().getCompoundTag("InfiTool").getInteger("Accessory");
                    if (accessory == 5) {
                        setIndirectEntity(damageSource, null);
                    }
                }
            }
        }
    }

    private void stalwartBuff(EntityLivingBase entity) {
        if (random.nextInt(10) == 0) {
            PotionEffect potionEffect = entity.getActivePotionEffect(Potion.resistance);
            int amplifier = 0;
            int duration = 200;
            if (potionEffect != null) {
                amplifier = (potionEffect.getAmplifier() < 2) ? potionEffect.getAmplifier() + 1 : 2;
                duration = (potionEffect.getDuration() < 401) ? potionEffect.getDuration() + 200 : 600;
            }
            entity.addPotionEffect(new PotionEffect(Potion.resistance.id, duration, amplifier));
        }
    }

    @SubscribeEvent
    public void onProjectileKill(LivingDeathEvent event) {
        if (event.source instanceof EntityDamageSourceIndirect) {
            EntityDamageSourceIndirect damageSource = (EntityDamageSourceIndirect) event.source;
            Entity damageSourceEntity = getDamageSourceEntity(damageSource);
            if (damageSourceEntity instanceof ArrowEntity || damageSourceEntity instanceof BoltEntity) {
                ProjectileBase entity = (ProjectileBase) damageSourceEntity;
                int accessory = entity.getEntityItem().getTagCompound().getCompoundTag("InfiTool")
                        .getInteger("Accessory");
                if (accessory == 5) {
                    setIndirectEntity(damageSource, null);
                }
            }
        }
    }

    private Entity getDamageSourceEntity(EntityDamageSourceIndirect damageSource) {
        try {
            Field field;
            field = damageSource.getClass().getDeclaredField("damageSourceEntity");
            field.setAccessible(true);
            return (Entity) field.get(damageSource);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setDamageSourceEntity(EntityDamageSourceIndirect damageSource, Entity entity) {
        try {
            Field field;
            field = damageSource.getClass().getDeclaredField("damageSourceEntity");
            field.setAccessible(true);
            field.set(damageSource, entity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private Entity getIndirectEntity(EntityDamageSourceIndirect damageSource) {
        try {
            Field field;
            field = damageSource.getClass().getDeclaredField("indirectEntity");
            field.setAccessible(true);
            return (Entity) field.get(damageSource);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setIndirectEntity(EntityDamageSourceIndirect damageSource, Entity entity) {
        try {
            Field field;
            field = damageSource.getClass().getDeclaredField("indirectEntity");
            field.setAccessible(true);
            field.set(damageSource, entity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void bucketFill(FillBucketEvent evt) {
        if (evt.current.getItem() == Items.bucket && evt.target.typeOfHit == MovingObjectType.BLOCK) {
            int hitX = evt.target.blockX;
            int hitY = evt.target.blockY;
            int hitZ = evt.target.blockZ;

            if (evt.entityPlayer != null
                    && !evt.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, evt.target.sideHit, evt.current)) {
                return;
            }

            Block bID = evt.world.getBlock(hitX, hitY, hitZ);
            for (int id = 0; id < TFTinkerConstructIntegration.fluidBlocks.length; id++) {
                if (bID == TFTinkerConstructIntegration.fluidBlocks[id]) {
                    if (evt.entityPlayer.capabilities.isCreativeMode) {
                        WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                    } else {
                        WorldHelper.setBlockToAir(evt.world, hitX, hitY, hitZ);
                        evt.setResult(Result.ALLOW);
                        evt.result = new ItemStack(TFTinkerConstructIntegration.buckets, 1, id);
                    }
                }
            }
        }
    }

    // Giving the tutorial book

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && TFTPlayerStats.get((EntityPlayer) event.entity) == null) {
            TFTPlayerStats.register((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        Item item = event.item.getEntityItem().getItem();
        EntityPlayer player = event.entityPlayer;
        TFTPlayerStats stats = TFTPlayerStats.get(player);

        if (!stats.twilightManual && (item == TFItems.feather || item == TFItems.nagaScale
                || item == TFItems.fieryBlood
                || item == TFItems.fieryTears
                || item == TFItems.fieryIngot
                || item == TFItems.steeleafIngot
                || item == TFItems.armorShard
                || item == TFItems.shardCluster
                || item == TFItems.knightMetal)) {
            ItemStack diary = new ItemStack(TFTinkerConstructIntegration.manualBook);
            if (!player.inventory.addItemStackToInventory(diary)) {
                AbilityHelper.spawnItemAtPlayer(player, diary);
            }
            stats.twilightManual = true;
        }
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) {
        Item item = event.crafting.getItem();
        EntityPlayer player = event.player;
        TFTPlayerStats stats = TFTPlayerStats.get(player);

        if (!stats.twilightManual
                && (item == TFItems.fieryIngot || item == TFItems.shardCluster || item == TFItems.knightMetal)) {
            ItemStack diary = new ItemStack(TFTinkerConstructIntegration.manualBook);
            if (!player.inventory.addItemStackToInventory(diary)) {
                AbilityHelper.spawnItemAtPlayer(player, diary);
            }
            stats.twilightManual = true;
        }
    }

}
