package com.moonlight.buildingtools.helpers.loaders;

import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.items.tools.bowTest.AutoBow;
import com.moonlight.buildingtools.items.tools.brushtool.BrushTool;
import com.moonlight.buildingtools.items.tools.buildingtool.BuildingTool;
import com.moonlight.buildingtools.items.tools.filtertool.FilterTool;
import com.moonlight.buildingtools.items.tools.idAdvancer.IdAdvancer;
import com.moonlight.buildingtools.items.tools.placetempblock.TempBlockTool;
import com.moonlight.buildingtools.items.tools.selectiontool.SelectionTool;
import com.moonlight.buildingtools.items.tools.smoothtool.BlockSmoother;
import com.moonlight.buildingtools.items.tools.tapeMeasure.TapeMeasure;
import com.moonlight.buildingtools.items.tools.undoTool.UndoTool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;

public class ItemLoader {
	public static Item brushTool;
	public static Item blockSmoother;
	public static Item selectionTool;
	public static Item buildingTool;
	public static Item tempblockplacer;
	public static Item filterTool;
	public static Item idAdvancer;
	public static ItemBow autobow;
	public static Item undoTool;
	public static Item tapeMeasure;
	
	public static Set<Item> itemsToRegister;
	
	public static void loadItems(){
		brushTool = new BrushTool();
		RegisterHelper.registerItem(brushTool);
		
		blockSmoother = new BlockSmoother();
		RegisterHelper.registerItem(blockSmoother);
		
		selectionTool = new SelectionTool();
		RegisterHelper.registerItem(selectionTool);
		
		buildingTool = new BuildingTool();
		RegisterHelper.registerItem(buildingTool);
		
		tempblockplacer = new TempBlockTool();
		RegisterHelper.registerItem(tempblockplacer);
		
		filterTool = new FilterTool();
		RegisterHelper.registerItem(filterTool);
		
		idAdvancer = new IdAdvancer();
		RegisterHelper.registerItem(idAdvancer);
		
		autobow = new AutoBow();
		RegisterHelper.registerItem(autobow);
		
		undoTool = new UndoTool();
		RegisterHelper.registerItem(undoTool);
		
		tapeMeasure = new TapeMeasure();
		RegisterHelper.registerItem(tapeMeasure);
		
		
		itemsToRegister = Sets.newHashSet();
		itemsToRegister.add(brushTool);
		itemsToRegister.add(blockSmoother);
		itemsToRegister.add(selectionTool);
		itemsToRegister.add(buildingTool);
		itemsToRegister.add(tempblockplacer);
		itemsToRegister.add(filterTool);
		itemsToRegister.add(idAdvancer);
		itemsToRegister.add(undoTool);
		itemsToRegister.add(tapeMeasure);
	}
}
