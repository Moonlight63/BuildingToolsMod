package com.moonlight.buildingtools.items.tools.placetempblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.moonlight.buildingtools.BuildingTools;

public class BlockTemporary extends Block {

	public BlockTemporary() {
		super(Material.rock);
		setCreativeTab(BuildingTools.tabBT);
		setUnlocalizedName("tempBlock");
	}
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }
	
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return true;
    }
    
    @Override
    public boolean isReplaceable(World worldIn, BlockPos pos){
    	return true;
    }
    
    @Override
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
    	return 1000;
    }
    
    @Override
    public float getAmbientOcclusionLightValue()
    {
        return 1.0f;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    public boolean isFullCube()
    {
        return false;
    }
    
    

}
