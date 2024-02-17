package tconstruct.tools;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.IAmmo;
import twilightforest.TwilightForestMod;
import twilightforest.block.TFBlocks;
import twilightforest.item.TFItems;

public class TFActiveToolMod extends ActiveToolMod {

    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase
                && !((EntityLivingBase) entity).isSwingInProgress
                && stack.getTagCompound() != null) {
            if (entity instanceof EntityPlayer && (((EntityPlayer) entity).isUsingItem())) return;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            twilit(tool, stack, (EntityLivingBase) entity, tags);
            precipitate(tool, stack, (EntityLivingBase) entity, tags);
            synergy(tool, stack, (EntityLivingBase) entity, tags);
        }
    }

    /* Attacking */

    @Override
    public int baseAttackDamage(int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags,
            NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity) {
        stalwart(tool, stack, player, tags.getCompoundTag("InfiTool"));
        return 0;
    }

    private void twilit(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("IsInTF")) {
            boolean isInTF = entity.dimension == TwilightForestMod.dimensionID;
            boolean wasInTF = tags.getBoolean("IsInTF");
            if (isInTF != wasInTF) {
                if (isInTF) {
                    tags.setInteger("MiningSpeed", tags.getInteger("MiningSpeed") + 200);
                    tags.setInteger("Attack", tags.getInteger("Attack") - 2);
                    tags.setInteger("BaseAttack", tags.getInteger("BaseAttack") - 2);
                } else {
                    tags.setInteger("MiningSpeed", tags.getInteger("MiningSpeed") - 200);
                    tags.setInteger("Attack", tags.getInteger("Attack") + 2);
                    tags.setInteger("BaseAttack", tags.getInteger("BaseAttack") + 2);
                }
                tags.setBoolean("IsInTF", isInTF);
            }
        }
    }

    private void precipitate(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("PrecipitateSpeed")) {
            int baseSpeed = tags.getInteger("PrecipitateSpeed");
            float health = entity.getHealth();
            float maxHealth = entity.getMaxHealth();
            tags.setInteger("MiningSpeed", (int) (baseSpeed * (1 + (maxHealth - health) / maxHealth)));
        }
    }

    private void synergy(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("Synergy") && stack.getItemDamage() > 0
                && !tags.hasKey("Broken")
                && entity instanceof EntityPlayer) {
            InventoryPlayer inventory = ((EntityPlayer) entity).inventory;
            int chance = 0;
            for (int i = 0; i < 9; i++) if (inventory.mainInventory[i] != null)
                if (inventory.mainInventory[i].getItem() == TFItems.steeleafIngot)
                    chance += inventory.mainInventory[i].stackSize;
                else if (inventory.mainInventory[i].getItem() == Item.getItemFromBlock(TFBlocks.steeleafStorage))
                    chance += inventory.mainInventory[i].stackSize * 9;
            int check = 1150; // Will probably change later
            // REGROWING AMMO :OOoo
            if (tool instanceof IAmmo && random.nextInt(check * 3) < chance) // ammo regenerates at a much slower
                                                                             // rate
            {
                IAmmo ammothing = (IAmmo) tool;
                if (ammothing.getAmmoCount(stack) > 0) // must have ammo
                    ammothing.addAmmo(1, stack);
            }
            // selfrepairing tool. LAAAAAME
            else if (random.nextInt(check) < chance) {
                AbilityHelper.healTool(stack, 1, entity, true);
            }
        }
    }

    private void stalwart(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("Stalwart")) {
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
    }

}
