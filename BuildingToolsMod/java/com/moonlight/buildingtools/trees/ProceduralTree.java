package com.moonlight.buildingtools.trees;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class ProceduralTree extends Tree{
	
	public int trunkradius = 0;
	public List<Double> foliage_shape = new ArrayList<Double>();
	public Set<int[]> foliage_coords = new LinkedHashSet<int[]>();
	public int branchdensity;
	public double branchslope;
	
	public boolean ROOTBUTTRESSES = false;
	public int TRUNKTHICKNESS = 1;
	public int BRANCHDENSITY = 1;
	public int FOLIAGEDENSITY = 1;
	public double TRUNKHEIGHT = 0.7;
	
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
	
	public double shapeFunc(int y){
		if(Math.random() < 100 / Math.pow(this.height, 2) && y < this.trunkheight){
			return this.height * 0.12;
		}
		return 0;
	}
	
	public void foliagecluster (BlockPos center){
		List<Double> lvlrad = this.foliage_shape;
		for(double i : lvlrad){
			CrossSection(center, i, 1, foliageSet, leafBlock);
		}
	}
	
	public void taperedcylinder(BlockPos start, BlockPos end, int StartSize, int endSize, IBlockState blockdata){
		
		int[] delta = {end.subtract(start).getX(), end.subtract(start).getY(), end.subtract(start).getZ()};
		int[] startarray = {start.getX(), start.getY(), start.getZ()};
				
		int maxdist = 0;
		int primidx = 0;
		if(Math.abs(delta[0]) > Math.abs(delta[1]) && Math.abs(delta[0]) > Math.abs(delta[2])){
			maxdist = delta[0];
			primidx = 0;
		}
		else if(Math.abs(delta[1]) > Math.abs(delta[0]) && Math.abs(delta[1]) > Math.abs(delta[2])){
			maxdist = delta[1];
			primidx = 1;
		}
		else if(Math.abs(delta[2]) > Math.abs(delta[0]) && Math.abs(delta[2]) > Math.abs(delta[1])){
			maxdist = delta[2];
			primidx = 2;
		}
		
		int secidx1 = (primidx - 1) % 3;
		int secidx2 = (primidx + 1) % 3;
		
		int primsign;
		int secdelta1 = delta[secidx1 == -1 ? 2 : secidx1];
		int secdelta2 = delta[secidx2 == 3 ? 0 : secidx2];
		int secfac1;
		int secfac2;
		if(Math.abs(delta[primidx]) != 0){
			primsign = (delta[primidx]/Math.abs(delta[primidx]));
			secfac1 = (secdelta1) / delta[primidx];
			secfac2 = (secdelta2) / delta[primidx];
		}
		else{
			primsign = delta[primidx];
			secfac1 = (secdelta1);
			secfac2 = (secdelta2);
		}
		
		int[] coord = new int[3];
		
		int endoffset = delta[primidx] + primsign;
		
		for(int primoffset = 0; Math.abs(primoffset)<Math.abs(endoffset); primoffset += primsign){
			int primloc = startarray[primidx] + primoffset;
			int secloc1 = startarray[secidx1 == -1 ? 2 : secidx1] + primoffset * secfac1;
			int secloc2 = startarray[secidx2 == 3 ? 0 : secidx2] + primoffset * secfac2;
			coord[primidx] = primloc;
			coord[secidx1 == -1 ? 2 : secidx1] = secloc1;
			coord[secidx2 == 3 ? 0 : secidx2] = secloc2;
			int primdist = Math.abs(delta[primidx]);
			int radius = endSize + (StartSize - endSize) * Math.abs(delta[primidx] - primoffset) / primdist;
			CrossSection(new BlockPos(coord[0], coord[1], coord[2]), radius, primidx, logSet, blockdata);
		}
	}
	
	@Override
	public void makeFoliage(){
		Set<int[]> foliagecoords = this.foliage_coords;
		
		for(int[] coord : foliagecoords){
			foliagecluster(new BlockPos(coord[0], coord[1], coord[2]));
			logSet.add(new ChangeBlockToThis(new BlockPos(coord[0], coord[1], coord[2]), logBlock));
		}
		
	}
	
	public void makeBranches(){
		double topy = this.trunkheight + 0.5;
		int endrad = this.trunkradius * (1-this.trunkheight / this.height);
		
		if(endrad < 1.0){endrad = 1;}
		
		for(int[] coord : this.foliage_coords){
			
			double dist = Math.sqrt((Math.pow(coord[0], 2) + Math.pow(coord[2], 2)));
			
			int ydist = coord[1];
			
			double value = (this.branchdensity * 220 * height) / (Math.pow((ydist + dist), 3));
			if(value < Math.random()){continue;}
			
			int posy = coord[1];
			
			double slope = this.branchslope + (0.5 - Math.random()) * 0.16;
			double branchy;
			int basesize;
			if(coord[1] - dist * slope > topy){
				int threshold = 1 / this.height;
				if(Math.random() < threshold){
					continue;
				}
				branchy = topy;
				basesize = endrad;
			}
			else{
				branchy = posy - dist * slope;
				basesize = (int) (endrad + (trunkradius - endrad) * (topy - branchy) / trunkheight);
			}
			
			double startsize = basesize * (1 + Math.random()) * 0.618 * Math.pow((dist / height), 0.618);
			
			double rndr = Math.sqrt(Math.random()) * basesize * 0.618;
			
			double rndang = Math.random() * 2 * Math.PI;
			
			double rndx = (rndr * Math.sin(rndang) + 0.5);
			
			double rndz = (rndr * Math.cos(rndang) + 0.5);
			
			int[] startcoord = {(int) rndx, (int) branchy, (int) rndz};
			
			if(startsize < 1){startsize = 1;}
			
			int endsize = 1;
			
			taperedcylinder(new BlockPos(startcoord[0], startcoord[1], startcoord[2]), new BlockPos(coord[0], coord[1], coord[2]), (int) startsize, endsize, logBlock);
			
		}
		
	}
	
	
	//TODO MAKEROOTS;
	
	@Override
	public void makeTrunk(){
		
		double midy = this.trunkheight * 0.382;
		double topy = this.trunkheight * 0.5;
		
		int endsizefactor = this.trunkheight / this.height;
		
		double midrad = trunkradius * (1 - endsizefactor * 0.5);
		int endrad = trunkradius * (1 - endsizefactor);
		
		if(endrad < 1){endrad = 1;}
		if(midrad < endrad){midrad = endrad;}
		
		//TODO ROOTBUTTRESSES;
		
		int startrad = trunkradius;
		
		
		if(ROOTBUTTRESSES){
			
		}
		else{
			
		}
		
		taperedcylinder(new BlockPos(0, 0, 0), new BlockPos(0, midy, 0), startrad, (int) midrad, logBlock);
		taperedcylinder(new BlockPos(0, midy, 0), new BlockPos(0, topy, 0), (int) midrad, endrad, logBlock);
		
		makeBranches();
		
		//TODO ROOTS
		/*if(ROOTS){
			makeRoots();
		}*/
		
		//TODO Hollow Trunk;
		/*if(trunkradius > 2 && HOLLOWTRUNK){
			
		}*/
		
	}
	
	@Override
	public void prepare(){
		
		trunkradius = (int) (0.618 * Math.sqrt(height * TRUNKTHICKNESS));
		if(trunkradius < 1){trunkradius = 1;}
		
		//TODO BROKENTRUNK
		trunkheight = height;
		int yend = height;
		
		branchdensity = BRANCHDENSITY / FOLIAGEDENSITY;
		
		double topy = trunkheight + 0.5;
		
		double numofclusterspery = 1.5 + Math.pow((FOLIAGEDENSITY * height / 19.0), 2);
		if(numofclusterspery < 1){numofclusterspery = 1;}
		
		for(int y = yend; y > 0; y--){
			for(int i = 0; i < numofclusterspery; i++){
				double shapefac = shapeFunc(y);
				
				if(shapefac == 0){continue;}
				
				double r = (Math.sqrt(Math.random() + 0.328) * shapefac);
				
				double theta = Math.random() * 2 * Math.PI;
				double x = r * Math.sin(theta);
				double z = r * Math.cos(theta);
				
				int[] temparray = {(int)x, y, (int)z};
				foliage_coords.add(temparray);
			}
		}
		
	}

}


















