package com.moonlight.buildingtools.buildingGuide;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public interface IActivateAwareTile {
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ);
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest);
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion);
}
