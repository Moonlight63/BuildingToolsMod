package com.moonlight.buildingtools.buildingGuide;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGuide extends BlockContainer{
	
	public World world;
	public BlockPos pos;
	
	
	
	private Class<? extends TileEntity> teClass = null;

	public BlockGuide() {
		super(Material.ROCK);
		//setCreativeTab(BuildingTools.tabBT);
		setUnlocalizedName("blockGuide");
		setTickRandomly(true);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		
		world.immediateBlockTick(pos, state, new Random());
		
		//world.forceBlockUpdateTick(this, pos, new Random());

		if (te instanceof IActivateAwareTile) return ((IActivateAwareTile)te).onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		return false;
		
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IActivateAwareTile) ((IActivateAwareTile)te).removedByPlayer(state, world, pos, player, willHarvest);
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}
	
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion){
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IActivateAwareTile) ((IActivateAwareTile)te).onBlockExploded(world, pos, explosion);
		super.onBlockExploded(world, pos, explosion);
	}
	
	
	public Class<? extends TileEntity> getTileClass() {
		return teClass;
	}
	
	@SuppressWarnings("unchecked")
	public static <U> U getTileEntity(IBlockAccess world, BlockPos pos, Class<U> T) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && T.isAssignableFrom(te.getClass())) { return (U)te; }
		return null;
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int eventId, int eventParam) {
		super.eventReceived(state, world, pos, eventId, eventParam);
		TileEntity te = getTileEntity(world, pos, TileEntity.class);
		if (te != null) { return te.receiveClientEvent(eventId, eventParam); }
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		final TileEntity te = new TileEntityGuide();
		if (te != null) {
			//((TileEntityGuide)te).setWidth(10);
			//((TileEntityGuide)te).setHeight(10);
			//((TileEntityGuide)te).setDepth(10);
			//((TileEntityGuide)te).recreateShape();
		}
		return te;
	} 
	
	@Override
	public int tickRate(World worldIn)
    {
        return 1;
    }
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		worldIn.rainingStrength = 0;
		worldIn.thunderingStrength = 0;
		worldIn.setRainStrength(0);
		worldIn.setThunderStrength(0);
		worldIn.setWorldTime(1600);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}
}
