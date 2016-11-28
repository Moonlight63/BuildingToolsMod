package com.moonlight.buildingtools.neverrainalwaysdayblock;

import com.moonlight.buildingtools.BuildingTools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class debugblock extends Block implements ITileEntityProvider{

	public debugblock() {
		super(Material.ROCK);
		setUnlocalizedName("alwaysdayremoverain_(DO_NOT_USE_ON_SERVERS)");
		setCreativeTab(BuildingTools.tabBT);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new debugTileEntity();
	}

}
