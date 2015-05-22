package com.moonlight.buildingtools.helpers.loaders;

import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.items.tools.bowTest.AutoBow;
import com.moonlight.buildingtools.items.tools.brushtool.BrushTool;
import com.moonlight.buildingtools.items.tools.buildingtool.BuildingTool;
import com.moonlight.buildingtools.items.tools.filtertool.FilterTool;
import com.moonlight.buildingtools.items.tools.idAdvancer.IdAdvancer;
import com.moonlight.buildingtools.items.tools.placetempblock.TempBlockTool;
import com.moonlight.buildingtools.items.tools.selectiontool.CopyTool;
import com.moonlight.buildingtools.items.tools.smoothtool.BlockSmoother;
import com.moonlight.buildingtools.items.tools.undoTool.UndoTool;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;

public class ItemLoader {
	public static Item brushtool;
	public static Item blockSmoother;
	public static Item copyTool;
	public static Item buildingTool;
	public static Item tempblockplacer;
	public static Item filterTool;
	public static Item idAdvancer;
	public static ItemBow autobow;
	public static Item undoTool;
	
	public static Set<Item> itemsToRegister;
	
	public static void loadItems(){
		brushtool = new BrushTool();
		RegisterHelper.registerItem(brushtool);
		
		blockSmoother = new BlockSmoother();
		RegisterHelper.registerItem(blockSmoother);
		
		copyTool = new CopyTool();
		RegisterHelper.registerItem(copyTool);
		
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
		
		
		itemsToRegister = Sets.newHashSet();
		itemsToRegister.add(brushtool);
		itemsToRegister.add(blockSmoother);
		itemsToRegister.add(copyTool);
		itemsToRegister.add(buildingTool);
		itemsToRegister.add(tempblockplacer);
		itemsToRegister.add(filterTool);
		itemsToRegister.add(idAdvancer);
		itemsToRegister.add(undoTool);
	}
}
