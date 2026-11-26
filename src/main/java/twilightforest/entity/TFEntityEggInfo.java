package twilightforest.entity;

import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentTranslation;

// similar to vanilla EntityEggInfo but registers Twilight Forests kill stats
public class TFEntityEggInfo {

    public final int spawnedID;
    public final int primaryColor;
    public final int secondaryColor;
    public final StatBase killedStat;
    public final StatBase killedByStat;

    public TFEntityEggInfo(int entityID, int backgroundColor, int spotColor) {
        this.spawnedID = entityID;
        this.primaryColor = backgroundColor;
        this.secondaryColor = spotColor;
        this.killedStat = makeEntityKillStat(this);
        this.killedByStat = makeEntityKilledByStat(this);
    }

    public static StatBase makeEntityKillStat(TFEntityEggInfo eggInfo) {
        return registerStat(eggInfo.spawnedID, "stat.killEntity.", "stat.entityKill");
    }

    public static StatBase makeEntityKilledByStat(TFEntityEggInfo eggInfo) {
        return registerStat(eggInfo.spawnedID, "stat.entityKilledBy.", "stat.entityKilledBy");
    }

    private static StatBase registerStat(int entityID, String statPrefix, String translationKey) {
        String entityName = TFCreatures.getStringFromID(entityID);
        if (entityName == null) {
            return null;
        }
        return new StatBase(
                statPrefix + entityName,
                new ChatComponentTranslation(
                        translationKey,
                        new ChatComponentTranslation("entity." + entityName + ".name"))).registerStat();
    }
}
