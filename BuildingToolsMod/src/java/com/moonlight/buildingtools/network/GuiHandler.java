package com.moonlight.buildingtools.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.brushtool.BrushToolGui;
import com.moonlight.buildingtools.items.tools.buildingtool.BuildingToolGui;
import com.moonlight.buildingtools.items.tools.filtertool.FilterToolGui;
import com.moonlight.buildingtools.items.tools.selectiontool.SelectionToolGui;
import com.moonlight.buildingtools.items.tools.smoothtool.BlockSmootherGui;


public class GuiHandler implements IGuiHandler {
	
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
		case BlockSmootherGui.GUI_ID:
			return new BlockSmootherGui(player);
		case BrushToolGui.GUI_ID:
			return new BrushToolGui(player);
		case BuildingToolGui.GUI_ID:
			return new BuildingToolGui(player);
		case FilterToolGui.GUI_ID:
			return new FilterToolGui(player);
		case SelectionToolGui.GUI_ID:
			return new SelectionToolGui(player);

		default:
			break;
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
