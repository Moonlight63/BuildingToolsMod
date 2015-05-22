package com.moonlight.buildingtools.trees;

import net.minecraft.util.BlockPos;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class NormalTree extends StickTree{
	
	@Override
	public void makeFoliage(){
		
		System.out.println("creating foliage");
		
		int topy = this.height - 1;
		int start = topy - 2;
		int end = topy + 2;
		
		for(int y = start; y < end; y++){
			int rad = 0;
			if(y>start+1){
				rad = 1;
			}
			else{
				rad = 2;
			}
			
			for(int xoff = -rad; xoff <= rad; xoff++){
				for(int zoff = -rad; zoff <= rad; zoff++){
					if(Math.random()>0.618 && Math.abs(xoff) == Math.abs(zoff) && Math.abs(xoff) == rad){
						continue;
					}
					
					this.foliageSet.add(new ChangeBlockToThis(new BlockPos(xoff, y, zoff), leafBlock));
					
				}
			}
		}
	}

}
