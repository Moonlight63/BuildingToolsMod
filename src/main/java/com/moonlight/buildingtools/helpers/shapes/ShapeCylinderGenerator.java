package com.moonlight.buildingtools.helpers.shapes;

import net.minecraft.util.BlockPos;

public class ShapeCylinderGenerator implements IShapeGenerator {

	@Override
	public void generateShape(int radX, int height, int radZ, IShapeable shapeable, boolean fillmode) {
		
		/*int radiusX = 0, radiusZ = 0;
		
		if(fillmode != 0){
			for(int i = 0; i <= fillmode; i++){
				radiusX = radX - i;
				radiusZ = radZ - i;
				gen(radiusX, radiusZ, height, false, shapeable);
			}
		}
		else{
			gen(radX, radZ, height, true, shapeable);
		}*/
		
		gen(radX, radZ, height, fillmode, shapeable);
		shapeable.shapeFinished();
	}
	
	public void gen(int radiusX, int radiusZ, int height, boolean fill, IShapeable shapeable){
		if (height == 0) { return; }

		final double invRadiusX = 1.0 / (radiusX + 0.5);
		final double invRadiusZ = 1.0 / (radiusZ + 0.5);

		double nextXn = 0;
		forX: for (int x = 0; x <= radiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextZn = 0;
			forZ: for (int z = 0; z <= radiusZ; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadiusZ;

				double distanceSq = MathUtils.lengthSq(xn, zn);
				if (distanceSq > 1) {
					if (z == 0) {
						break forX;
					}
					break forZ;
				}
				
				if(!fill){
					if (MathUtils.lengthSq(nextXn, zn) <= 1
							&& MathUtils.lengthSq(xn, nextZn) <= 1) {
						continue;
					}
				}

				for (int y = -height; y <= height; ++y) {
					shapeable.setBlock(new BlockPos(x, y, z));
					shapeable.setBlock(new BlockPos(-x, y, z));
					shapeable.setBlock(new BlockPos(x, y, -z));
					shapeable.setBlock(new BlockPos(-x, y, -z));
				}
			}
		}
	}

}