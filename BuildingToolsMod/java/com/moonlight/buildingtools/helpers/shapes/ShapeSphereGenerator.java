package com.moonlight.buildingtools.helpers.shapes;

import com.moonlight.buildingtools.helpers.shapes.GeometryUtils.Octant;

public class ShapeSphereGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int radiusX, int radiusY, int radiusZ, IShapeable shapeable, boolean fillmode) {
		//for(int i = 0; i <= fillmode; i++){
		//	GeometryUtils.makeSphere(radiusX - i, radiusY - i, radiusZ - i, shapeable, Octant.ALL, false);
		//}
		
		GeometryUtils.makeSphere(radiusX, radiusY, radiusZ, shapeable, Octant.ALL, fillmode);
		shapeable.shapeFinished();
	}
}
