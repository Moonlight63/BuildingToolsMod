package com.moonlight.buildingtools.helpers.shapes;

import com.moonlight.buildingtools.helpers.shapes.GeometryUtils.Octant;

public class ShapeDomeGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, boolean fillmode) {
		//for(int i = 0; i <= fillmode; i++){
		//	GeometryUtils.makeSphere(xSize-i, ySize-i, zSize-i, shapeable, Octant.TOP, false);
		//}
		
		GeometryUtils.makeSphere(xSize, ySize, zSize, shapeable, Octant.TOP, fillmode);
	}

}
