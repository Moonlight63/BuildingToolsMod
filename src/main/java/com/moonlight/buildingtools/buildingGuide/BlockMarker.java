package com.moonlight.buildingtools.buildingGuide;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMarker extends Block {

	public BlockMarker() {
		super(Material.ROCK);
		//setCreativeTab(BuildingTools.tabBT);
		setUnlocalizedName("markerBlock");
		//lightValue = 15;
		// TODO Auto-generated constructor stub
	}
	
	/*@Override
	public int getRenderType()
    {
        return -1;
    }*/
	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid){
        return false;
    }
    
    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos){
        return true;
    }
    
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		// TODO Auto-generated method stub
		
	}
    
    /*@Override
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        int i = worldIn.getCombinedLight(pos, block.getLightValue(worldIn, pos));
        
        System.out.print(i);

        if (i == 0 && block instanceof BlockSlab)
        {
            pos = pos.down();
            block = worldIn.getBlockState(pos).getBlock();
            return worldIn.getCombinedLight(pos, block.getLightValue(worldIn, pos));
        }
        else
        {
            return i;
        }
    }*/
    
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
