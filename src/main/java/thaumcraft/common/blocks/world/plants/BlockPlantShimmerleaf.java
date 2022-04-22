// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.world.plants;

import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.FXDispatcher;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockBush;

public class BlockPlantShimmerleaf extends BlockBush
{
    public BlockPlantShimmerleaf() {
        super(Material.PLANTS);
        this.setUnlocalizedName("shimmerleaf");
        this.setRegistryName("thaumcraft", "shimmerleaf");
        this.setCreativeTab(ConfigItems.TABTC);
        this.setSoundType(SoundType.PLANT);
        this.setLightLevel(0.4f);
    }
    
    protected boolean canSustainBush(final IBlockState state) {
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
    }
    
    public EnumPlantType getPlantType(final IBlockAccess world, final BlockPos pos) {
        return EnumPlantType.Plains;
    }
    
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
        if (rand.nextInt(3) == 0) {
            final float xr = (float)(pos.getX() + 0.5f + rand.nextGaussian() * 0.1);
            final float yr = (float)(pos.getY() + 0.4f + rand.nextGaussian() * 0.1);
            final float zr = (float)(pos.getZ() + 0.5f + rand.nextGaussian() * 0.1);
            FXDispatcher.INSTANCE.drawWispyMotes(xr, yr, zr, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, 10, 0.3f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.7f + world.rand.nextFloat() * 0.3f, 0.0f);
        }
    }
    
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }
}