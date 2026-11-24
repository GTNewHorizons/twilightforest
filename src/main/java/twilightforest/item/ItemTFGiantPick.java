package twilightforest.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import twilightforest.block.BlockTFGiantBlock;
import twilightforest.block.TFBlocks;

public class ItemTFGiantPick extends ItemPickaxe {

    private final float damageVsEntity;
    private GiantItemIcon giantIcon;

    protected ItemTFGiantPick(Item.ToolMaterial par2EnumToolMaterial) {
        super(par2EnumToolMaterial);
        this.setCreativeTab(TFItems.creativeTab);
        this.damageVsEntity = 4 + par2EnumToolMaterial.getDamageVsEntity();

    }

    /**
     * Return an item rarity from EnumRarity
     *
     * This is automatically rare
     */
    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        // repair with knightmetal ingots
        return par2ItemStack != null && (par2ItemStack.getItem() == TFItems.knightMetal
                || super.getIsRepairable(par1ItemStack, par2ItemStack));
    }

    /**
     * Properly register icon source
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = Items.stone_pickaxe.getIconFromDamage(0);
        this.giantIcon = new GiantItemIcon(this.itemIcon, 0.0625F * 7F, 0.0625F * 2F);

    }

    /**
     * Return the correct icon for rendering based on the supplied ItemStack and render pass.
     *
     * Defers to {@link #getIconFromDamageForRenderPass(int, int)}
     *
     * @param stack to render for
     * @param pass  the multi-render pass
     * @return the icon
     */
    public IIcon getIcon(ItemStack stack, int pass) {
        // render pass 1 gives the giant Icon
        return pass == -1 && this.giantIcon != null ? this.giantIcon : super.getIcon(stack, pass);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List,
            boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        par3List.add(StatCollector.translateToLocal(getUnlocalizedName() + ".tooltip"));
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers();
        String damageAttribute = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
        modifiers.removeAll(damageAttribute);
        modifiers.put(damageAttribute, new AttributeModifier(field_111210_e, "Tool modifier", this.damageVsEntity, 0));
        return modifiers;
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        float speed = super.func_150893_a(stack, block);
        return (block == TFBlocks.giantObsidian || block instanceof BlockTFGiantBlock) ? speed * 64.0F : speed;
    }

}
