package com.moonlight.buildingtools.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface IOutlineDrawer {
	public BlockPos targetBlock = null;
	public EnumFacing targetFace = null;
	
	public boolean drawOutline(DrawBlockHighlightEvent event);
	public void setTargetBlock(BlockPos pos, EnumFacing side);
	
}
