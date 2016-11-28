package com.moonlight.buildingtools.items.tools.placetempblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import com.moonlight.buildingtools.BuildingTools;

public class BlockTemporary extends Block {

	public BlockTemporary() {
		super(Material.ROCK);
		//setCreativeTab(BuildingTools.tabBT);
		setUnlocalizedName("tempBlock");
	}
	@Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
	
	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
	
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return true;
    }
    
    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos){
        return true;
    }
    
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
    	return 1000;
    }
    
    public float getAmbientOcclusionLightValue()
    {
        return 1.0f;
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public boolean isFullCube()
    {
        return false;
    }
    
    

}
