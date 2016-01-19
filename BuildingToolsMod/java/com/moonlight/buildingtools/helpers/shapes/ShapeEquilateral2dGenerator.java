package com.moonlight.buildingtools.helpers.shapes;

public class ShapeEquilateral2dGenerator implements IShapeGenerator {

	private int sides;

	public ShapeEquilateral2dGenerator(int sides) {
		this.sides = sides;
	}

	@Override
	public void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, boolean fillmode) {
		int firstX = 0;
		int firstZ = 0;
		int previousX = 0;
		int previousZ = 0;
		
		for(int y0 = 0; y0 < ySize; y0++){

			for (int i = 0; i < sides; i++) {
				double d = 2 * Math.PI * i / sides;
				
				int x = 0;
				int z = 0;
				
				//if(!(fillmode == 0)){
					
					
					
				//	for(int x2 = xSize-fillmode-1;x2 <= xSize;x2++){
				//		x = (int)Math.round(Math.cos(d) * x2);
				//		z = (int)Math.round(Math.sin(d) * x2);
				//	}
					
					x = (int)Math.round(Math.cos(d) * xSize);
					z = (int)Math.round(Math.sin(d) * xSize);
				//}else{
				//	for(int x2 = 0;x2 <= xSize;x2++){
				//		x = (int)Math.round(Math.cos(d) * x2);
				//		z = (int)Math.round(Math.sin(d) * x2);
				//	}
				//}
				
				
				if (i == 0) {
					firstX = previousX = x;
					firstZ = previousZ = z;
				} else {
					GeometryUtils.line2D(y0, previousX, previousZ, x, z, shapeable, fillmode);
					previousX = x;
					previousZ = z;
				}
			}
			GeometryUtils.line2D(y0, previousX, previousZ, firstX, firstZ, shapeable, fillmode);
		}
		
	}

}
