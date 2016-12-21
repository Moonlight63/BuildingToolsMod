package com.moonlight.buildingtools.helpers.shapes;

import com.moonlight.buildingtools.helpers.shapes.GeometryUtils.Octant;

public class ShapeDomeGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, boolean fillmode) {
		GeometryUtils.makeSphere(xSize, ySize, zSize, shapeable, Octant.TOP, fillmode);
		shapeable.shapeFinished();
	}

}
