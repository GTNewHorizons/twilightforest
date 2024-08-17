package twilightforest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.io.Files;

import twilightforest.integration.TFBaublesIntegration;

public class TFPlayerHandler {

    protected static HashMap<String, InventoryPlayer> playerKeepsMap = new HashMap<>();

    public static void clearPlayerMaps(EntityPlayer player) {
        playerKeepsMap.remove(player.getCommandSenderName());
        TFBaublesIntegration.clearPlayerMap(player);
    }

    public static InventoryPlayer getPlayerKeepInventory(EntityPlayer player) {
        if (!playerKeepsMap.containsKey(player.getCommandSenderName())) {
            InventoryPlayer inventory = new InventoryPlayer(player);
            playerKeepsMap.put(player.getCommandSenderName(), inventory);
        }
        return playerKeepsMap.get(player.getCommandSenderName());
    }

    public static void setPlayerKeepInventory(EntityPlayer player, InventoryPlayer inventory) {
        playerKeepsMap.put(player.getCommandSenderName(), inventory);
    }

    public static void loadPlayerKeepInventory(EntityPlayer player, File file1, File file2) {
        if (player != null && !player.worldObj.isRemote) {
            try {
                NBTTagCompound data = null;
                boolean save = false;
                if (file1 != null && file1.exists()) {
                    try {
                        FileInputStream fileinputstream = new FileInputStream(file1);
                        data = CompressedStreamTools.readCompressed(fileinputstream);
                        fileinputstream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (file1 == null || !file1.exists() || data == null || data.hasNoTags()) {
                    System.out.println(
                            "Data not found for " + player.getCommandSenderName() + ". Trying to load backup data.");
                    if (file2 != null && file2.exists()) {
                        try {
                            FileInputStream fileinputstream = new FileInputStream(file2);
                            data = CompressedStreamTools.readCompressed(fileinputstream);
                            fileinputstream.close();
                            save = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (data != null) {
                    InventoryPlayer keepInventory = new InventoryPlayer(player);
                    keepInventory.readFromNBT(data.getTagList("KeepInventory", 10));
                    playerKeepsMap.put(player.getCommandSenderName(), keepInventory);

                    TFBaublesIntegration.loadPlayerKeepBaubles(player, data);

                    if (save) savePlayerKeepInventory(player, file1, file2);
                }
            } catch (Exception exception1) {
                System.out.println("Error loading keep inventory");
                exception1.printStackTrace();
            }
        }
    }

    public static void savePlayerKeepInventory(EntityPlayer player, File file1, File file2) {
        if (player != null && !player.worldObj.isRemote) {
            try {
                if (file1 != null && file1.exists()) {
                    try {
                        Files.copy(file1, file2);
                    } catch (Exception e) {
                        System.out.println(
                                "Could not backup old keep inventory file for player " + player.getCommandSenderName());
                    }
                }

                try {
                    if (file1 != null) {
                        InventoryPlayer inventory = getPlayerKeepInventory(player);
                        NBTTagCompound data = new NBTTagCompound();
                        data.setTag("KeepInventory", inventory.writeToNBT(new NBTTagList()));

                        TFBaublesIntegration.savePlayerKeepBaubles(player, data);

                        FileOutputStream fileoutputstream = new FileOutputStream(file1);
                        CompressedStreamTools.writeCompressed(data, fileoutputstream);
                        fileoutputstream.close();

                    }
                } catch (Exception e) {
                    System.out
                            .println("Could not save keep inventory file for player " + player.getCommandSenderName());
                    e.printStackTrace();
                    if (file1.exists()) {
                        try {
                            file1.delete();
                        } catch (Exception e2) {}
                    }
                }
            } catch (Exception exception1) {
                System.out.println("Error saving keep inventory");
                exception1.printStackTrace();
            }
        }
    }
}
