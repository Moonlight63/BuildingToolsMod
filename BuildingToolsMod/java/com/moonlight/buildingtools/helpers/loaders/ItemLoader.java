package com.moonlight.buildingtools.helpers.loaders;

import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.items.tools.brushtool.ToolBrush;
import com.moonlight.buildingtools.items.tools.buildingtool.ToolBuilding;
import com.moonlight.buildingtools.items.tools.erosionTool.ToolErosion;
import com.moonlight.buildingtools.items.tools.filtertool.ToolFilter;
import com.moonlight.buildingtools.items.tools.placetempblock.ToolPlaceTempBlock;
import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;
import com.moonlight.buildingtools.items.tools.tapeMeasure.ToolTapeMeasure;
import com.moonlight.buildingtools.items.tools.undoTool.ToolUndo;

import net.minecraft.item.Item;

public class ItemLoader {
	public static Item toolBrush;
	//public static Item toolTerrainSmoother;
	public static Item toolSelection;
	public static Item toolBuilding;
	public static Item toolTempBlockPlacer;
	public static Item toolFilter;
	public static Item toolUndo;
	public static Item toolTapeMeasure;
	public static Item toolErosion;
	
	//public static Item testBucket = new ItemBucketTest();
	
	
	//public static Item idAdvancer;
	//public static ItemBow autobow;
	
	public static Set<Item> itemsToRegister;
	
	public static void loadItems(){
		toolBrush = new ToolBrush();
		RegisterHelper.registerItem(toolBrush);
		
		//toolTerrainSmoother = new ToolTerrainSmooth();
		//RegisterHelper.registerItem(toolTerrainSmoother);
		
		toolSelection = new ToolSelection();
		RegisterHelper.registerItem(toolSelection);
		
		toolBuilding = new ToolBuilding();
		RegisterHelper.registerItem(toolBuilding);
		
		toolTempBlockPlacer = new ToolPlaceTempBlock();
		RegisterHelper.registerItem(toolTempBlockPlacer);
		
		toolFilter = new ToolFilter();
		RegisterHelper.registerItem(toolFilter);
		
		//idAdvancer = new IdAdvancer();
		//RegisterHelper.registerItem(idAdvancer);
		
		//autobow = new AutoBow();
		//RegisterHelper.registerItem(autobow);
		
		toolUndo = new ToolUndo();
		RegisterHelper.registerItem(toolUndo);
		
		toolTapeMeasure = new ToolTapeMeasure();
		RegisterHelper.registerItem(toolTapeMeasure);
		
		toolErosion = new ToolErosion();
		RegisterHelper.registerItem(toolErosion);
		
		itemsToRegister = Sets.newHashSet();
		itemsToRegister.add(toolBrush);
		//itemsToRegister.add(toolTerrainSmoother);
		itemsToRegister.add(toolSelection);
		itemsToRegister.add(toolBuilding);
		itemsToRegister.add(toolTempBlockPlacer);
		itemsToRegister.add(toolFilter);
		//itemsToRegister.add(idAdvancer);
		itemsToRegister.add(toolUndo);
		itemsToRegister.add(toolTapeMeasure);
		itemsToRegister.add(toolErosion);
	}
}
