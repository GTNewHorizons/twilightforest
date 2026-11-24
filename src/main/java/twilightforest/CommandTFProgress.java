package twilightforest;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTFProgress extends CommandBase {

    private static final String[] BOSSES = { "none", "naga", "lich", "mooshroom", "hydra", "knights", "urghast", "yeti",
            "snowqueen", "giants", "final" };

    @Override
    public String getCommandName() {
        return "tfprogress";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "tfprogress <player> <boss>";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 2) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        int bossIndex = getBossIndex(args[1]);
        if (bossIndex < 0) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        setProgress(player, bossIndex);
        func_152373_a(
                sender,
                this,
                "Setting player %s progress to past boss %s.",
                player.getCommandSenderName(),
                BOSSES[bossIndex]);
    }

    private void setProgress(EntityPlayerMP player, int bossIndex) {
        // give previous achievements, if necessary
        // give achievements
        for (int index = 1; index <= bossIndex; index++) {
            switch (index) {
                case 1 -> {
                    player.triggerAchievement(TFAchievementPage.twilightPortal);
                    player.triggerAchievement(TFAchievementPage.twilightArrival);
                    player.triggerAchievement(TFAchievementPage.twilightHunter);
                    player.triggerAchievement(TFAchievementPage.twilightKillNaga);
                    player.triggerAchievement(TFAchievementPage.twilightProgressNaga);
                }
                case 2 -> {
                    player.triggerAchievement(TFAchievementPage.twilightKillLich);
                    player.triggerAchievement(TFAchievementPage.twilightProgressLich);
                }
                case 3 -> player.triggerAchievement(TFAchievementPage.twilightProgressLabyrinth);
                case 4 -> {
                    player.triggerAchievement(TFAchievementPage.twilightKillHydra);
                    player.triggerAchievement(TFAchievementPage.twilightProgressHydra);
                }
                case 5 -> {
                    player.triggerAchievement(TFAchievementPage.twilightProgressTrophyPedestal);
                    player.triggerAchievement(TFAchievementPage.twilightProgressKnights);
                }
                case 6 -> player.triggerAchievement(TFAchievementPage.twilightProgressUrghast);
                case 7 -> player.triggerAchievement(TFAchievementPage.twilightProgressYeti);
                case 8 -> player.triggerAchievement(TFAchievementPage.twilightProgressGlacier);
                case 9 -> player.triggerAchievement(TFAchievementPage.twilightProgressTroll);
                case 10 -> {
                    player.triggerAchievement(TFAchievementPage.twilightProgressThorns);
                    player.triggerAchievement(TFAchievementPage.twilightProgressCastle);
                }
                default -> throw new IllegalStateException("Unexpected progression stage: " + index);
            }
        }
    }

    private static int getBossIndex(String boss) {
        for (int index = 0; index < BOSSES.length; index++) {
            if (BOSSES[index].equalsIgnoreCase(boss)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return switch (args.length) {
            case 1 -> getListOfStringsMatchingLastWord(args, getListOfPlayers());
            case 2 -> getListOfStringsMatchingLastWord(args, BOSSES);
            default -> null;
        };
    }

    protected String[] getListOfPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
