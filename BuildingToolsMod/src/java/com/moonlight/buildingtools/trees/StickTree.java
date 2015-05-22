package com.moonlight.buildingtools.trees;

import net.minecraft.util.BlockPos;

import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class StickTree extends Tree {

	@Override
	public void makeTrunk() {
		
		System.out.println("creating trunk");
		
		for(int i = 0; i < this.height; i++){
			logSet.add(new ChangeBlockToThis(new BlockPos(0, i, 0), this.logBlock));
		}
	}

	@Override
	public void makeFoliage() {
		// TODO Auto-generated method stub
	}

}
