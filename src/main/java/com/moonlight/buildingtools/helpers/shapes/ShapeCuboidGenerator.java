package com.moonlight.buildingtools.helpers.shapes;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ShapeCuboidGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, boolean fillmode) {

		/*
		 * The up direction is up when doing a side plane, and the origin is of
		 * course bottom left However this is not the case when doing the top
		 * and bottom, as their up direction is depth
		 */

		/*
		 * Optimization: The front and back planes are full sized, left and
		 * right are 2 blocks smaller so that they don't overlap the front and
		 * back planes. Top and bottom planes are 2 blocks smaller in both
		 * directions so they don't overlap any sides. This works for most cases
		 * unless two directions are the same because of rounding in small
		 * numbers.
		 * 
		 * But this should be fairly optimal without adding anything inefficient
		 * to complicate matters. - NeverCast
		 */

		/* Used for shrinking some planes to prevent iterating the same block */
		/*
		 * Basically, the size is size * 2 - 2 unless that's less than 1 and
		 * size is > 0
		 */
		if(fillmode){
			GeometryUtils.makeFilledCube(new BlockPos(-xSize, -ySize, -zSize), xSize*2, ySize*2, zSize*2, shapeable);
		}
		else{
			int xSizeAdj = xSize == 1? 1 : xSize * 2 - 2;
			int zSizeAdj = zSize == 1? 1 : zSize * 2 - 2;
			// front (north)
			GeometryUtils.makePlane(new BlockPos(-xSize, -ySize, -zSize), xSize * 2, ySize * 2, EnumFacing.EAST, EnumFacing.UP, shapeable);
			// back ( south )
			GeometryUtils.makePlane(new BlockPos(xSize, -ySize, zSize), xSize * 2, ySize * 2, EnumFacing.WEST, EnumFacing.UP, shapeable);
			// left ( west )
			GeometryUtils.makePlane(new BlockPos(-xSize, -ySize, zSize - 1), zSizeAdj, ySize * 2, EnumFacing.NORTH, EnumFacing.UP, shapeable);
			// right ( east )
			GeometryUtils.makePlane(new BlockPos(xSize, -ySize, -zSize + 1), zSizeAdj, ySize * 2, EnumFacing.SOUTH, EnumFacing.UP, shapeable);
			// top ( up )
			GeometryUtils.makePlane(new BlockPos(-xSize + 1, ySize, -zSize + 1), xSizeAdj, zSizeAdj, EnumFacing.EAST, EnumFacing.SOUTH, shapeable);
			// bottom ( down )
			GeometryUtils.makePlane(new BlockPos(-xSize + 1, -ySize, -zSize + 1), xSizeAdj, zSizeAdj, EnumFacing.EAST, EnumFacing.SOUTH, shapeable);
			
			shapeable.shapeFinished();
		}
		
	}
}
