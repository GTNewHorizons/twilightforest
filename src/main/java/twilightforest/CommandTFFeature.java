package twilightforest;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

import twilightforest.world.ChunkProviderTwilightForest;
import twilightforest.world.TFWorldChunkManager;
import twilightforest.world.WorldProviderTwilightForest;

public class CommandTFFeature extends CommandBase {

    @Override
    public String getCommandName() {
        return "tffeature";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "tffeature <info|center|reactivate|conquer>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            throw new WrongUsageException("commands.tffeature.usage");
        }

        switch (args[0].toLowerCase(java.util.Locale.ROOT)) {
            case "info" -> showFeatureInfo(sender, getTwilightPlayer(sender));
            case "center" -> showFeatureCenter(sender, getTwilightPlayer(sender));
            case "reactivate" -> changeStructureActivity(sender, getTwilightPlayer(sender), false);
            case "conquer" -> changeStructureActivity(sender, getTwilightPlayer(sender), true);
            default -> throw new WrongUsageException("commands.tffeature.usage");
        }
    }

    private EntityPlayerMP getTwilightPlayer(ICommandSender sender) {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (!(player.worldObj.provider instanceof WorldProviderTwilightForest)) {
            throw new WrongUsageException("commands.tffeature.not_in_twilight_forest");
        }
        return player;
    }

    private void showFeatureInfo(ICommandSender sender, EntityPlayerMP player) {
        // info on current feature
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        // nearest feature
        TFWorldChunkManager manager = (TFWorldChunkManager) player.worldObj.provider.worldChunkMgr;
        TFFeature nearbyFeature = manager.getFeatureAt(x, z, player.worldObj);
        sender.addChatMessage(new ChatComponentTranslation("The nearest feature is %s", nearbyFeature.name));

        // are you in a structure?
        ChunkProviderTwilightForest chunkProvider = getChunkProvider(player);
        if (!chunkProvider.isBlockInStructureBB(x, y, z)) {
            sender.addChatMessage(new ChatComponentTranslation("You are not in the structure for that feature."));
            return;
        }

        sender.addChatMessage(new ChatComponentTranslation("You are in the structure for that feature."));
        sender.addChatMessage(
                new ChatComponentTranslation(
                        "Structure conquer flag = %s.",
                        chunkProvider.isStructureConquered(x, y, z)));

        // are you in a room?

        // what is the spawn list
        // List<SpawnListEntry> spawnList = chunkProvider.getPossibleCreatures(EnumCreatureType.monster,
        // dx, dy, dz);
        // sender.addChatMessage(new ChatComponentTranslation("Spawn list for the area is:"));
        // for (SpawnListEntry entry : spawnList) {
        // sender.addChatMessage(new ChatComponentTranslation(entry.toString()));
        // }
    }

    private void showFeatureCenter(ICommandSender sender, EntityPlayerMP player) {
        int x = MathHelper.floor_double(player.posX);
        // int dy = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        ChunkCoordinates center = TFFeature.getNearestCenterXYZ(x >> 4, z >> 4, player.worldObj);
        TFWorldChunkManager manager = (TFWorldChunkManager) player.worldObj.provider.worldChunkMgr;
        boolean inFeatureChunk = manager.isInFeatureChunk(player.worldObj, x, z);

        sender.addChatMessage(new ChatComponentTranslation("Center of feature = %s.", center));
        sender.addChatMessage(new ChatComponentTranslation("Are in feature chunk = %s.", inFeatureChunk));
    }

    private void changeStructureActivity(ICommandSender sender, EntityPlayerMP player, boolean conquered) {
        // activate current feature
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        // are you in a structure?
        ChunkProviderTwilightForest chunkProvider = getChunkProvider(player);
        if (!chunkProvider.isBlockInStructureBB(x, y, z)) {
            sender.addChatMessage(new ChatComponentTranslation("You are not in a structure."));
            return;
        }

        sender.addChatMessage(
                new ChatComponentTranslation(
                        "Structure conquer flag was %s.  Changing to %s.",
                        chunkProvider.isStructureConquered(x, y, z),
                        conquered));
        chunkProvider.setStructureConquered(x, y, z, conquered);
    }

    private ChunkProviderTwilightForest getChunkProvider(EntityPlayerMP player) {
        return ((WorldProviderTwilightForest) player.worldObj.provider).getChunkProvider();
    }
}
