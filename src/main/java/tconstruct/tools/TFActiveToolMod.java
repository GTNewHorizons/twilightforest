package tconstruct.tools;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.IAmmo;
import twilightforest.TwilightForestMod;
import twilightforest.item.TFItems;

public class TFActiveToolMod extends ActiveToolMod {

    Random random = new Random();

    /* Updating */
    @Override
    public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase
                && !((EntityLivingBase) entity).isSwingInProgress
                && stack.getTagCompound() != null) {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            twilit(tool, stack, (EntityLivingBase) entity, tags);
            precipitate(tool, stack, (EntityLivingBase) entity, tags);
            synergy(tool, stack, (EntityLivingBase) entity, tags);
        }
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
            tags.setInteger("MiningSpeed", (int) (baseSpeed * (1 + (maxHealth - health) / maxHealth / 2)));
        }
    }

    private void synergy(ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags) {
        if (tags.hasKey("Synergy") && entity instanceof EntityPlayer) {
            InventoryPlayer inventory = ((EntityPlayer) entity).inventory;
            int chance = 0;
            for (int i = 0; i < 10; i++)
                if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() == TFItems.steeleafIngot)
                    chance += inventory.mainInventory[i].stackSize;
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

}
