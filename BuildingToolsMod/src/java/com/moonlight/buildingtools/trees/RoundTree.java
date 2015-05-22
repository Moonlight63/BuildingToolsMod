package com.moonlight.buildingtools.trees;

public class RoundTree extends ProceduralTree{

	
	@Override
	public double shapeFunc(int y){
		
		double twigs = super.shapeFunc(y);
		
		if(twigs != 0){
			return twigs;
		}
		
		if(y < this.height * (0.282 + 0.1 * Math.sqrt(Math.random()))){
			return 0;
		}
		
		int raduis = this.height / 2;
		int adj = this.height / 2 - y;
		
		double dist;
		
		if(adj == 0) dist = raduis;
		else if (Math.abs(adj) >= raduis) dist = 0;
		else dist = Math.sqrt(Math.pow(raduis, 2) - Math.pow(adj, 2));
		
		dist = dist * 0.618;
		
		return dist;
		
	}
	
	@Override
	public void prepare(){
		this.branchslope = 0.382;
		super.prepare();
		foliage_shape.add((double) 2);
		foliage_shape.add((double) 3);
		foliage_shape.add((double) 3);
		foliage_shape.add((double) 2.5);
		foliage_shape.add((double) 1.6);
		
		trunkradius = (int) (trunkradius * 0.8);
		trunkheight = (int) (TRUNKHEIGHT * trunkheight);
	}
	
}
