// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.ore;

import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.init.Items;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thaumcraft.common.blocks.BlockTC;

public class BlockOreTC extends BlockTC
{
    public BlockOreTC(final String name) {
        super(Material.ROCK, name);
        this.setResistance(5.0f);
        this.setSoundType(SoundType.STONE);
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return (state.getBlock() == BlocksTC.oreQuartz) ? Items.QUARTZ : ((state.getBlock() == BlocksTC.oreAmber) ? ItemsTC.amber : Item.getItemFromBlock(state.getBlock()));
    }
    
    public int quantityDropped(final Random random) {
        return (this == BlocksTC.oreAmber) ? (1 + random.nextInt(2)) : 1;
    }
    
    public List<ItemStack> getDrops(final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune) {
        final List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
        if (this == BlocksTC.oreAmber && drops != null) {
            final Random rand = (world instanceof World) ? ((World)world).rand : BlockOreTC.RANDOM;
            for (int a = 0; a < drops.size(); ++a) {
                final ItemStack is = drops.get(a);
                if (is != null && !is.isEmpty() && is.getItem() == ItemsTC.amber && rand.nextFloat() < 0.066) {
                    drops.set(a, new ItemStack(ItemsTC.curio, 1, 1));
                }
            }
        }
        return drops;
    }
    
    public int getExpDrop(final IBlockState state, final IBlockAccess world, final BlockPos pos, final int fortune) {
        final Random rand = (world instanceof World) ? ((World)world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
            int j = 0;
            if (this == BlocksTC.oreAmber || this == BlocksTC.oreQuartz) {
                j = MathHelper.getInt(rand, 1, 4);
            }
            return j;
        }
        return 0;
    }
    
    public int quantityDroppedWithBonus(final int fortune, final Random random) {
        if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(this.getBlockState().getValidStates().iterator().next(), random, fortune)) {
            int j = random.nextInt(fortune + 2) - 1;
            if (j < 0) {
                j = 0;
            }
            return this.quantityDropped(random) * (j + 1);
        }
        return this.quantityDropped(random);
    }
}