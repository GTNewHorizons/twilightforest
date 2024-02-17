package tconstruct.tools;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.entity.ProjectileBase;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.weaponry.AmmoItem;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.weaponry.entity.ArrowEntity;
import tconstruct.weaponry.entity.BoltEntity;
import twilightforest.integration.TFTinkerConstructIntegration.MaterialID;

public class TFToolEvents {

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

        event.toolTip.add(
                1,
                "" + colorFromID(event.itemStack.getTagCompound().getCompoundTag("InfiTool").getInteger("TwilitID"))
                        + StatCollector.translateToLocal("material.twilit.ability"));
        if (event.itemStack.getItem() instanceof ArrowAmmo || event.itemStack.getItem() instanceof BoltAmmo)
            if (event.itemStack.getTagCompound().getCompoundTag("InfiTool").getInteger("Accessory") == 5)
                event.toolTip.add(
                        2,
                        "" + ChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocal("material.raven_feather.ability"));
        if (event.itemStack.getTagCompound().getCompoundTag("InfiTool").hasKey("TFFiery")) {
            event.toolTip
                    .add(2, "" + ChatFormatting.GOLD + StatCollector.translateToLocal("modifier.tooltip.Auto-Smelt"));
            event.toolTip.add(3, "" + ChatFormatting.GOLD + StatCollector.translateToLocal("modifier.tooltip.Fiery"));
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

}
