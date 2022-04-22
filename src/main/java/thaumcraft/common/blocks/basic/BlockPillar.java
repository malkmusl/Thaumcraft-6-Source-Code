// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.blocks.basic;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import java.util.Random;
import net.minecraft.block.properties.PropertyDirection;
import thaumcraft.common.blocks.BlockTC;

public class BlockPillar extends BlockTC
{
    public static final PropertyDirection FACING;
    private final Random rand;
    
    public BlockPillar(final String name) {
        super(Material.ROCK, name);
        this.rand = new Random();
        this.setHardness(2.5f);
        this.setSoundType(SoundType.STONE);
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)BlockPillar.FACING, (Comparable)EnumFacing.NORTH);
        this.setDefaultState(bs);
    }
    
    public EnumPushReaction getMobilityFlag(final IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
    
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
    
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    }
    
    public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
        return new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
    }
    
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        final IBlockState bs = this.blockState.getBaseState();
        bs.withProperty((IProperty)BlockPillar.FACING, (Comparable)placer.getHorizontalFacing());
        return bs;
    }
    
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3).getOpposite();
        state = state.withProperty((IProperty)BlockPillar.FACING, (Comparable)enumfacing);
        worldIn.setBlockState(pos, state, 3);
    }
    
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemById(0);
    }
    
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (state.getBlock() == BlocksTC.pillarArcane) {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTC.stoneArcane, 2));
        }
        if (state.getBlock() == BlocksTC.pillarAncient) {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTC.stoneAncient, 2));
        }
        if (state.getBlock() == BlocksTC.pillarEldritch) {
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTC.stoneEldritchTile, 2));
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    public IBlockState getStateFromMeta(final int meta) {
        final EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
        return this.getBlockState().getBaseState().withProperty((IProperty)BlockPillar.FACING, (Comparable)enumfacing);
    }
    
    public static int calcMeta(EnumFacing enumfacing) {
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        final IBlockState state = BlocksTC.pillarArcane.getBlockState().getBaseState();
        return BlocksTC.pillarArcane.getMetaFromState(state.withProperty((IProperty)BlockPillar.FACING, (Comparable)enumfacing));
    }
    
    public int getMetaFromState(final IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)BlockPillar.FACING)).getHorizontalIndex();
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {BlockPillar.FACING});
    }
    
    static {
        FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    }
}