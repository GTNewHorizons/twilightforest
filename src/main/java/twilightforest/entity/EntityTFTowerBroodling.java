package twilightforest.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityTFTowerBroodling extends EntityTFSwarmSpider {

    public EntityTFTowerBroodling(World world) {
        this(world, true);
    }

    public EntityTFTowerBroodling(World world, boolean spawnMore) {
        super(world, spawnMore);
        experienceValue = 3; // XP value
        // texture = TwilightForestMod.MODEL_DIR + "towerbroodling.png";
    }

    /**
     * Set monster attributes
     */
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(7.0D); // max health
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D); // attack damage
    }

    /**
     * Spawn another spider!
     *
     * @return
     */
    @Override
    protected boolean spawnAnother() {
        return spawnAnotherSpider(new EntityTFTowerBroodling(worldObj, false));
    }
}
