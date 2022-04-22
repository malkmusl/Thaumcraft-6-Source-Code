// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.tools;

import com.google.common.collect.Sets;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.init.Blocks;
import thaumcraft.api.items.ItemsTC;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Iterator;
import net.minecraft.block.Block;
import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import thaumcraft.common.config.ConfigItems;
import java.util.Set;
import net.minecraft.item.Item;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.api.items.IWarpingGear;
import net.minecraft.item.ItemTool;

public class ItemPrimalCrusher extends ItemTool implements IWarpingGear, IThaumcraftItems
{
    public static Item.ToolMaterial material;
    private static final Set isEffective;
    
    public ItemPrimalCrusher() {
        super(3.5f, -2.8f, ItemPrimalCrusher.material, ItemPrimalCrusher.isEffective);
        this.setCreativeTab(ConfigItems.TABTC);
        this.setRegistryName("primal_crusher");
        this.setUnlocalizedName("primal_crusher");
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public ItemMeshDefinition getCustomMesh() {
        return null;
    }
    
    public ModelResourceLocation getCustomModelResourceLocation(final String variant) {
        return new ModelResourceLocation("thaumcraft:" + variant);
    }
    
    public boolean canHarvestBlock(final IBlockState p_150897_1_) {
        return p_150897_1_.getMaterial() != Material.WOOD && p_150897_1_.getMaterial() != Material.LEAVES && p_150897_1_.getMaterial() != Material.PLANTS;
    }
    
    public float getDestroySpeed(final ItemStack stack, final IBlockState state) {
        return (state.getMaterial() != Material.IRON && state.getMaterial() != Material.ANVIL && state.getMaterial() != Material.ROCK) ? super.getDestroySpeed(stack, state) : this.efficiency;
    }
    
    public Set<String> getToolClasses(final ItemStack stack) {
        return ImmutableSet.of("shovel", "pickaxe");
    }
    
    private boolean isEffectiveAgainst(final Block block) {
        for (final Object b : ItemPrimalCrusher.isEffective) {
            if (b == block) {
                return true;
            }
        }
        return false;
    }
    
    public int getItemEnchantability() {
        return 20;
    }
    
    public int getWarp(final ItemStack itemstack, final EntityPlayer player) {
        return 2;
    }
    
    public void onUpdate(final ItemStack stack, final World world, final Entity entity, final int p_77663_4_, final boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }
    }
    
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == CreativeTabs.SEARCH) {
            final ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.DESTRUCTIVE, 1);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.REFINING, 1);
            items.add(w1);
        }
    }
    
    static {
        ItemPrimalCrusher.material = EnumHelper.addToolMaterial("PRIMALVOID", 5, 500, 8.0f, 4.0f, 20).setRepairItem(new ItemStack(ItemsTC.ingots, 1, 1));
        isEffective = Sets.newHashSet((Object[])new Block[] { Blocks.COBBLESTONE, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.GRASS, Blocks.DIRT, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW_LAYER, Blocks.SNOW, Blocks.CLAY, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MYCELIUM, BlocksTC.taintCrust, BlocksTC.taintRock, BlocksTC.taintSoil, BlocksTC.taintFeature, BlocksTC.taintLog, BlocksTC.taintFibre, Blocks.OBSIDIAN });
    }
}