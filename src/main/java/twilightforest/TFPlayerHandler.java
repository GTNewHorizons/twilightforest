package twilightforest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import cpw.mods.fml.common.FMLLog;
import twilightforest.integration.TFBaublesIntegration;

public class TFPlayerHandler {

    private static final String KEEP_INVENTORY_TAG = "KeepInventory";
    protected static HashMap<String, InventoryPlayer> playerKeepsMap = new HashMap<>();

    public static void clearPlayerMaps(EntityPlayer player) {
        playerKeepsMap.remove(player.getCommandSenderName());
        TFBaublesIntegration.clearPlayerMap(player);
    }

    public static void clearCaches() {
        playerKeepsMap.clear();
    }

    public static InventoryPlayer getPlayerKeepInventory(EntityPlayer player) {
        return playerKeepsMap.computeIfAbsent(player.getCommandSenderName(), ignored -> new InventoryPlayer(player));
    }

    public static void setPlayerKeepInventory(EntityPlayer player, InventoryPlayer inventory) {
        playerKeepsMap.put(player.getCommandSenderName(), inventory);
    }

    public static void loadPlayerKeepInventory(EntityPlayer player, File primaryFile, File backupFile) {
        if (player == null || player.worldObj.isRemote) {
            return;
        }

        NBTTagCompound data = readKeepData(primaryFile);
        boolean restoredBackup = false;
        if (data == null || data.hasNoTags()) {
            // data not found for the player. try loading the backup
            data = readKeepData(backupFile);
            restoredBackup = data != null && !data.hasNoTags();
        }
        if (data == null || data.hasNoTags()) {
            return;
        }

        InventoryPlayer inventory = new InventoryPlayer(player);
        inventory.readFromNBT(data.getTagList(KEEP_INVENTORY_TAG, 10));
        playerKeepsMap.put(player.getCommandSenderName(), inventory);
        TFBaublesIntegration.loadPlayerKeepBaubles(player, data);

        if (restoredBackup) {
            // restore the primary without overwriting the good backup
            writeKeepData(player, primaryFile, backupFile, false);
        }
    }

    public static void savePlayerKeepInventory(EntityPlayer player, File primaryFile, File backupFile) {
        if (player != null && !player.worldObj.isRemote) {
            writeKeepData(player, primaryFile, backupFile, true);
        }
    }

    private static NBTTagCompound readKeepData(File file) {
        if (file == null || !file.isFile()) {
            return null;
        }
        try (FileInputStream input = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(input);
        } catch (IOException | RuntimeException e) {
            FMLLog.warning("[TwilightForest] Could not read keep inventory file %s: %s", file, e);
            return null;
        }
    }

    private static void writeKeepData(EntityPlayer player, File primaryFile, File backupFile, boolean makeBackup) {
        if (primaryFile == null) {
            return;
        }

        NBTTagCompound data = new NBTTagCompound();
        InventoryPlayer inventory = getPlayerKeepInventory(player);
        data.setTag(KEEP_INVENTORY_TAG, inventory.writeToNBT(new NBTTagList()));
        TFBaublesIntegration.savePlayerKeepBaubles(player, data);

        File temporary = null;
        try {
            File parent = primaryFile.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Could not create player data directory " + parent);
            }

            temporary = File.createTempFile(primaryFile.getName() + ".", ".tmp", parent);
            try (FileOutputStream output = new FileOutputStream(temporary)) {
                CompressedStreamTools.writeCompressed(data, output);
            }

            if (makeBackup && primaryFile.isFile() && backupFile != null) {
                try {
                    java.nio.file.Files
                            .copy(primaryFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    FMLLog.warning(
                            "[TwilightForest] Could not back up keep inventory for %s: %s",
                            player.getCommandSenderName(),
                            e);
                }
            }

            try {
                java.nio.file.Files.move(
                        temporary.toPath(),
                        primaryFile.toPath(),
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(temporary.toPath(), primaryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException e) {
            FMLLog.warning(
                    "[TwilightForest] Could not save keep inventory for %s: %s",
                    player.getCommandSenderName(),
                    e);
        } finally {
            if (temporary != null && temporary.exists() && !temporary.delete()) {
                FMLLog.warning("[TwilightForest] Could not delete temporary keep inventory file %s", temporary);
            }
        }
    }
}
