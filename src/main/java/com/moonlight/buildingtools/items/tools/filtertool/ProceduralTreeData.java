package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ProceduralTreeData {
	
	int height = 64;
	int trunkRadBot = 4;
	int trunkRadMid = 2;
	int trunkRadTop = 1;
	
	float trunkHeight = 0.8f;
	float trunkMidPoint = 0.382f;
	float branchStart = 0.2f;
	float foliageStart = 0.35f;
	
	double branchSlope = 0.381D;
	double leafDensity = 1.0D;
	double branchDensity = 0.25D;
	
	List<List<Float>> foliage_shape = new ArrayList<List<Float>>(Arrays.asList(new ArrayList<Float>(Arrays.asList(2.0f, 3.0f, 3.0f, 2.0f, 1.0f))));    
	
	int clusterShape = 0;
	
    double scaleWidth = 1.0D;
    boolean hollowTrunk = false;
    int trunkWallThickness = 1;
    IBlockState logMat = Blocks.LOG.getDefaultState();
    IBlockState leafMat = Blocks.LEAVES.getDefaultState();
	
	public ProceduralTreeData(){
		
	}
	
	public void SetScaleWidth(double scale){
		this.scaleWidth = scale;
	}
	public double GetScaleWidth(){
		return this.scaleWidth;
	}
	
	public void SetHollowTrunk(boolean hollow){
		this.hollowTrunk = hollow;
	}
	public boolean GetHollowTrunk(){
		return this.hollowTrunk;
	}
	
	public void SetTrunkWallThickness(int scale){
		this.trunkWallThickness = scale;
	}
	public int GetTrunkWallThickness(){
		return this.trunkWallThickness;
	}
	
	public void SetTreeHeight(int height){
		this.height = height;
	}
	
	public void SetTrunkBottom(int rad){
		this.trunkRadBot = rad;
	}
	
	public void SetTrunkMiddle(int rad){
		this.trunkRadMid = rad;
	}

	public void SetTrunkTop(int rad){
		this.trunkRadTop = rad;
	}
	
	public void SetTrunkHeight(float percent){
		this.trunkHeight = percent;
	}
	
	public void SetTrunkMidPoint(float percent){
		this.trunkMidPoint = percent;
	}
	
	public void SetBranchStart(float percent){
		this.branchStart = percent;
	}
	
	public void SetFoliageStart(float percent){
		this.foliageStart = percent;
	}
	
	public void SetBranchSlope(double percent){
		this.branchSlope = percent;
	}
	
	public void SetLeafDensity(double percent){
		this.leafDensity = percent;
	}
	
	public void SetBranchDensity(double percent){
		this.branchDensity = percent;
	}
	
	public void SetFoliageShapes(List<List<Float>> shapes){
		this.foliage_shape = shapes;
	}
	
	public void SetClusterShape(int shapeIndex){
		this.clusterShape = shapeIndex;
	}
	
	
	
	public int GetTreeHeight(){
		return this.height;
	}
	
	public int GetTrunkBottom(){
		return this.trunkRadBot;
	}
	
	public int GetTrunkMiddle(){
		return this.trunkRadMid;
	}

	public int GetTrunkTop(){
		return this.trunkRadTop;
	}
	
	public float GetTrunkHeight(){
		return this.trunkHeight;
	}
	
	public float GetTrunkMidPoint(){
		return this.trunkMidPoint;
	}
	
	public float GetBranchStart(){
		return this.branchStart;
	}
	
	public float GetFoliageStart(){
		return this.foliageStart;
	}
	
	public double GetBranchSlope(){
		return this.branchSlope;
	}
	
	public double GetLeafDensity(){
		return this.leafDensity;
	}
	
	public double GetBranchDensity(){
		return this.branchDensity;
	}
	
	public List<List<Float>> GetFoliageShapes(){
		return this.foliage_shape;
	}
	
	public int GetClusterShape(){
		return this.clusterShape;
	}
	
	public int[] GetMatValues(){
		return new int[]{Block.getIdFromBlock(logMat.getBlock()), logMat.getBlock().getMetaFromState(logMat), Block.getIdFromBlock(leafMat.getBlock()), leafMat.getBlock().getMetaFromState(leafMat)};
	}
	
	public void SetMatValues(int id1, int meat1, int id2, int meta2){
    	this.logMat = Block.getBlockById(id1).getStateFromMeta(meat1);
    	this.leafMat = Block.getBlockById(id2).getStateFromMeta(meta2);
    }
	
}
