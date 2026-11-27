package twilightforest.integration;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public final class TFAvaritiaIntegration {

    private static final String MOD_ID = "Avaritia";
    private static final String MATTER_CLUSTER_CLASS = "fox.spiteful.avaritia.items.ItemMatterCluster";
    private static final String REWARD_CLUSTER_NAME = "Boss Reward Cluster";

    private TFAvaritiaIntegration() {}

    public static List<ItemStack> createRewardClusters(List<ItemStack> loot) {
        if (loot.isEmpty() || !Loader.isModLoaded(MOD_ID)) return loot;

        try {
            // preserve the original loot if the API fails or modifies its input
            List<ItemStack> input = new ArrayList<>(loot.size());
            for (ItemStack stack : loot) input.add(stack.copy());

            Class<?> clusterClass = Class.forName(MATTER_CLUSTER_CLASS);
            Object result = clusterClass.getMethod("makeClusters", List.class).invoke(null, input);
            if (!(result instanceof List<?>clusters) || clusters.isEmpty()) {
                throw new IllegalStateException("Avaritia returned no reward clusters");
            }

            List<ItemStack> rewards = new ArrayList<>(clusters.size());
            for (Object value : clusters) {
                if (!(value instanceof ItemStack cluster) || !clusterClass.isInstance(cluster.getItem())
                        || cluster.stackSize != 1) {
                    throw new IllegalStateException("Avaritia returned an invalid reward cluster");
                }
                cluster.setStackDisplayName(REWARD_CLUSTER_NAME);
                rewards.add(cluster);
            }
            return rewards;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            FMLLog.warning("[TwilightForest] Could not create boss reward clusters; dropping loot normally: %s", e);
            return loot;
        }
    }
}
