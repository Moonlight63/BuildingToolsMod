package com.moonlight.buildingtools.helpers.shapes;

import net.minecraft.util.BlockPos;

public interface IShapeable {
	public void setBlock(BlockPos bpos);
	public void shapeFinished();
}
