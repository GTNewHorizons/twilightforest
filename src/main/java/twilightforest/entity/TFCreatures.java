package twilightforest.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import twilightforest.TwilightForestMod;

public final class TFCreatures {

    private static final int DEFAULT_TRACKING_RANGE = 80;
    private static final int DEFAULT_UPDATE_FREQUENCY = 3;

    // preserve the public HashMap field type for existing spawn egg integrations
    // the actual map keeps egg order deterministic in the creative tab
    public static HashMap<Integer, TFEntityEggInfo> entityEggs = new LinkedHashMap<>();

    private TFCreatures() {}

    public static void registerTFCreature(Class<? extends Entity> entityClass, String entityName, int id,
            int backgroundEggColour, int foregroundEggColour) {
        registerTFCreature(
                entityClass,
                entityName,
                id,
                backgroundEggColour,
                foregroundEggColour,
                DEFAULT_TRACKING_RANGE,
                DEFAULT_UPDATE_FREQUENCY,
                true);
    }

    public static void registerTFCreature(Class<? extends Entity> entityClass, String entityName, int id,
            int backgroundEggColour, int foregroundEggColour, int trackingRange, int updateFrequency,
            boolean sendsVelocityUpdates) {
        registerEntity(entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates);
        entityEggs.put(id, new TFEntityEggInfo(id, backgroundEggColour, foregroundEggColour));
    }

    public static void registerTFCreature(Class<? extends Entity> entityClass, String entityName, int id) {
        registerEntity(entityClass, entityName, id, DEFAULT_TRACKING_RANGE, DEFAULT_UPDATE_FREQUENCY, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id,
            int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        if (TwilightForestMod.creatureCompatibility) {
            EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
        }
        EntityRegistry.registerModEntity(
                entityClass,
                entityName,
                id,
                TwilightForestMod.instance,
                trackingRange,
                updateFrequency,
                sendsVelocityUpdates);
    }

    // create a mod entity from its registered spawn ID
    public static Entity createEntityByID(int entityID, World world) {
        EntityRegistration registration = getRegistration(entityID);
        if (registration == null) {
            FMLLog.fine("[TwilightForest] Skipping unregistered Entity with id " + entityID);
            return null;
        }

        Class<?> entityClass = registration.getEntityClass();
        if (entityClass == null || !Entity.class.isAssignableFrom(entityClass)) {
            FMLLog.fine("[TwilightForest] Invalid Entity class for id " + entityID);
            return null;
        }

        try {
            return (Entity) entityClass.getConstructor(World.class).newInstance(world);
        } catch (ReflectiveOperationException | SecurityException exception) {
            FMLLog.warning("[TwilightForest] Failed to instantiate Entity id " + entityID + ": " + exception);
            return null;
        }
    }

    // return the name of the registered entity, or null for an unknown ID
    public static String getStringFromID(int entityID) {
        EntityRegistration registration = getRegistration(entityID);
        return registration == null ? null : registration.getEntityName();
    }

    private static EntityRegistration getRegistration(int entityID) {
        ModContainer container = FMLCommonHandler.instance().findContainerFor(TwilightForestMod.instance);
        return container == null ? null : EntityRegistry.instance().lookupModSpawn(container, entityID);
    }

    // return a name compatible with vanilla spawners and the global ID option
    public static String getSpawnerNameFor(String baseName) {
        return TwilightForestMod.creatureCompatibility ? baseName : "TwilightForest." + baseName;
    }
}
