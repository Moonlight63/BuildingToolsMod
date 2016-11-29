package com.moonlight.buildingtools.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.brushtool.GUIToolBrush;
import com.moonlight.buildingtools.items.tools.buildingtool.GUIBuildersTool;
import com.moonlight.buildingtools.items.tools.erosionTool.GUIErosionTool;
import com.moonlight.buildingtools.items.tools.filtertool.GUIToolFilter;
import com.moonlight.buildingtools.items.tools.selectiontool.GUISaveLoadClipboard;
import com.moonlight.buildingtools.items.tools.selectiontool.GUISelectionTool;
import com.moonlight.buildingtools.items.tools.undoTool.GUISaveLoadCurrentUndoList;


public class GuiHandler implements IGuiHandler {
	
	public static final int GUIBrushTool = 1;
	public static final int GUIBuildingTool = 2;
	public static final int GUIFilterTool = 3;
	public static final int GUISelectionTool = 4;
	public static final int GUIErosionTool = 5;
	public static final int GUIFileSave = 6;
	public static final int GUIBlockSelection = 7;
	public static final int GUIUndoSave = 8;
	
	public GuiHandler(){
		NetworkRegistry.INSTANCE.registerGuiHandler(BuildingTools.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {
		
		switch (ID) {
		
		case GUIBrushTool:
			return new GUIToolBrush(player);
		case GUIBuildingTool:
			return new GUIBuildersTool(player);
		case GUIFilterTool:
			return new GUIToolFilter(player);
		case GUISelectionTool:
			return new GUISelectionTool(player);
		case GUIErosionTool:
			return new GUIErosionTool(player);
		case GUIFileSave:
			return new GUISaveLoadClipboard(player);
		case GUIUndoSave:
			return new GUISaveLoadCurrentUndoList(player);
		default:
			break;
		}
		
		return null;
	}

}
