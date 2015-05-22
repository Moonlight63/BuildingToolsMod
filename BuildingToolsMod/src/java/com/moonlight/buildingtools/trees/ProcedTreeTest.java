package com.moonlight.buildingtools.trees;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.helpers.shapes.MathUtils;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class ProcedTreeTest extends Tree implements IShapeable{
	
	public Set<int[]> foliage_coords = new LinkedHashSet<int[]>();
	public List<Double> foliage_shape = new ArrayList<Double>();
	public double branchslope = 0.382;
	public int FOLIAGEDENSITY = 1;
	public double TRUNKHEIGHT = 0.5;
	
	public double shapeFunc(int y){
		if(/*Math.random() < 100 / Math.pow(this.height, 2) &&*/ y > this.trunkheight){
			System.out.println("Shape Func  " + y);
			return this.height * 0.4;
		}
		return 0;
	}
	
	public void CrossSection(BlockPos center, double radius, int diraxis, Set settoaddto, IBlockState stateToAdd){
		
		int[] cent = {center.getX(), center.getY(), center.getZ()};
		int[] coord = new int[3];
		
		double rad = radius + 0.618;
		if(rad <= 0)return;
		
		int secidx1 = (diraxis - 1) % 3;
		int secidx2 = (diraxis + 1) % 3;
		
		for( double xoff = -rad; xoff <= rad; xoff++){
			for( double zoff = -rad; zoff <= rad; zoff++){
				
				double thisDist = Math.sqrt(Math.pow((Math.abs(xoff) + 0.5), 2) + Math.pow((Math.abs(zoff) + 0.5), 2));
				
				if(thisDist > radius)continue;
				
				int pri = cent[diraxis];
				int sec1 = (int) (cent[secidx1] + xoff);
				int sec2 = (int) (cent[secidx2] + zoff);
				coord[diraxis] = pri;
				coord[secidx1] = sec1;
				coord[secidx2] = sec2;
				
				settoaddto.add(new ChangeBlockToThis(new BlockPos(coord[0], coord[1], coord[2]), stateToAdd));
				
			}
		}
		
	}
	
	public void foliagecluster (BlockPos center){
		List<Double> lvlrad = this.foliage_shape;
		for(double i : lvlrad){
			CrossSection(center, i, 1, foliageSet, leafBlock);
		}
	}
	
	
	public void makeBranches(){
		
		for(int[] coord : foliage_coords){
			double dist = Math.sqrt(MathUtils.lengthSq(coord[0], coord[2]));
			
			double slope = this.branchslope + (0.5 - Math.random()) * 0.16;
			
			double branchy = coord[1] - dist * slope;
			
			System.out.println(branchy);
			System.out.println(coord[0] + ",  " + coord[1] + ",  " + coord[2]);
			
			GeometryUtils.line3D(new Vec3(0, branchy, 0), new Vec3(coord[0], coord[1], coord[2]), this);
		}
		
	}
	
	
	@Override
	public void makeTrunk(){
		
		//Shapes.Cylinder.generator.generateShape(0, height, 0, this, true);
		//Shapes.Cylinder.generator.generateShape(1, height/3, 1, this, true);
		
		makeBranches();
		
	}
	
	@Override
	public void makeFoliage(){
		Set<int[]> foliagecoords = this.foliage_coords;
		
		for(int[] coord : foliagecoords){
			//foliagecluster(new BlockPos(coord[0], coord[1], coord[2]));
			//logSet.add(new ChangeBlockToThis(new BlockPos(coord[0], coord[1], coord[2]), logBlock));
		}
		
	}
	
	public void prepare(){
		
		System.out.println("Called Prepare");
		
		trunkheight = height;
		trunkheight = (int) (TRUNKHEIGHT * trunkheight);
		foliage_shape.add((double) 2);
		foliage_shape.add((double) 3);
		foliage_shape.add((double) 3);
		foliage_shape.add((double) 2.5);
		foliage_shape.add((double) 1.6);
		
		int numofclusterspery = (int) Math.pow((FOLIAGEDENSITY  * height / 19.0), 2);
		if(numofclusterspery < 1){numofclusterspery = 1;}
		
		for(int y = this.height+1; y > 0; y--){
			for(int i = 0; i < numofclusterspery; i++){
				double shapefac = shapeFunc(y);
				
				if(shapefac == 0){continue;}
				
				double r = (Math.sqrt(Math.random() + 0.328) * shapefac);
				
				double theta = Math.random() * 2 * Math.PI;
				double x = r * Math.sin(theta);
				double z = r * Math.cos(theta);
				
				int[] temparray = {(int)x, y, (int)z};
				System.out.println(y + ",  " + shapefac);
				//System.out.println(temparray);
				foliage_coords.add(temparray);
			}
		}
		
		
		
		
	}


	@Override
	public void setBlock(BlockPos bpos) {
		// TODO Auto-generated method stub
		//System.out.println(bpos);
		for(int y = -2; y < 2; y++){
			
			for(int xoff = -2; xoff <= 2; xoff++){
				for(int zoff = -2; zoff <= 2; zoff++){		
					if(xoff != 0 && y != 0 && zoff != 0)
						this.foliageSet.add(new ChangeBlockToThis(new BlockPos(xoff, y, zoff).add(bpos), leafBlock));
				}
			}
		}
		this.logSet.add(new ChangeBlockToThis(bpos, logBlock));
	}

}
