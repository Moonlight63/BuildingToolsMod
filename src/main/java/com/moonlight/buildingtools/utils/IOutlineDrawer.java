package com.moonlight.buildingtools.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface IOutlineDrawer {
	public BlockPos targetBlock = null;
	public EnumFacing targetFace = null;
	
	public boolean drawOutline(DrawBlockHighlightEvent event);
	public void setTargetBlock(BlockPos pos, EnumFacing side);
	
}
