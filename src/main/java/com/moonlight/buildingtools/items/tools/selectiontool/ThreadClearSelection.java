package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ThreadClearSelection implements BlockChangeBase, IShapeable{
	
	protected StructureBoundingBox structureBoundingBox;
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	
	protected boolean isFinished = false;
	protected Set<ChangeBlockToThis> selectionSet = new LinkedHashSet<ChangeBlockToThis>();
	protected Set<Entity> entitySet = new LinkedHashSet<Entity>();
	
	public boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	public ThreadClearSelection(BlockPos blockpos1, BlockPos blockpos2, World world){
		this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		
		int p1x = (blockpos1.getX() <= blockpos2.getX()) ? blockpos1.getX() : blockpos1.getX() + 1;
        int p1y = (blockpos1.getY() <= blockpos2.getY()) ? blockpos1.getY() : blockpos1.getY() + 1;
        int p1z = (blockpos1.getZ() <= blockpos2.getZ()) ? blockpos1.getZ() : blockpos1.getZ() + 1;
        int p2x = (blockpos2.getX() < blockpos1.getX()) ? blockpos2.getX() : blockpos2.getX() + 1;
        int p2y = (blockpos2.getY() < blockpos1.getY()) ? blockpos2.getY() : blockpos2.getY() + 1;
        int p2z = (blockpos2.getZ() < blockpos1.getZ()) ? blockpos2.getZ() : blockpos2.getZ() + 1;
		
		this.entityDetectionBox = new AxisAlignedBB(new BlockPos(p1x, p1y, p1z), new BlockPos(p2x, p2y, p2z));
		this.world = world;		
	}
	
	@Override
	public void setBlock(BlockPos bpos){
		if(bpos.getY() > 0 && bpos.getY() < 256 && !world.isAirBlock(bpos)){
			currentlyCalculating = true;
			world.setBlockToAir(bpos);
		}
	}
	
	//protected int count = 0;
	public void perform(){
		if(!currentlyCalculating){
			if(!selectionCalculated){
				GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
				List<Entity> entitiesInBox = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(entityDetectionBox.minX,  entityDetectionBox.minY, entityDetectionBox.minZ, entityDetectionBox.maxX, entityDetectionBox.maxY, entityDetectionBox.maxZ));
				//PAINTINGS AND ITEM FRAMES
				if(!entitiesInBox.isEmpty()){
					for(Entity e : entitiesInBox){
						world.removeEntity(e);
					}
				}
				selectionCalculated = true;
				currentlyCalculating = false;
			}
			else{
				isFinished = true;				
			}
		}
	}
	
	public boolean isFinished(){
		return isFinished;
	}

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}

}
