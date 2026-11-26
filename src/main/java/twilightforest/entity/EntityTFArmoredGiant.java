package twilightforest.entity;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import twilightforest.item.TFItems;

public class EntityTFArmoredGiant extends EntityTFGiantMiner {

    public EntityTFArmoredGiant(World par1World) {
        super(par1World);

        this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
        this.setCurrentItemOrArmor(1, new ItemStack(Items.iron_boots));
        this.setCurrentItemOrArmor(2, new ItemStack(Items.iron_leggings));
        this.setCurrentItemOrArmor(3, new ItemStack(Items.iron_chestplate));
        this.setCurrentItemOrArmor(4, new ItemStack(Items.iron_helmet));

    }

    @Override
    protected Item getDropItem() {
        return TFItems.giantSword;
    }
}
