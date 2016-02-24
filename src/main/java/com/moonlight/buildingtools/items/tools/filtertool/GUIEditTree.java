package com.moonlight.buildingtools.items.tools.filtertool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.Shapes;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GUIEditTree extends GuiScreen{
	
	private EntityPlayer player;
	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	
	public static final GuiSlider treeheight 		= new GuiSlider(0, 0, 0, 160, 20, "Height: ", " blocks", 0, 200, 8, false, true);
	public static final GuiSlider trunkRadBot 		= new GuiSlider(1, 0, 0, 160, 20, "Trunk Raduis Bottom: ", " blocks", 0, 64, 6, false, true);
	public static final GuiSlider trunkRadMid 		= new GuiSlider(2, 0, 0, 160, 20, "Trunk Raduis Middle: ", " blocks", 0, 64, 4, false, true);
	public static final GuiSlider trunkRadTop 		= new GuiSlider(3, 0, 0, 160, 20, "Trunk Raduis To: ", " blocks", 0, 64, 3, false, true);
	public static final GuiSlider trunkHeight 		= new GuiSlider(4, 0, 0, 160, 20, "Trunk Height: ", " %", 0, 1, 0.8f, true, true);
	public static final GuiSlider trunkMidPoint 	= new GuiSlider(5, 0, 0, 160, 20, "Trunk Mid Point: ", " %", 0, 1, 0.382f, true, true);
	public static final GuiSlider branchStart 		= new GuiSlider(6, 0, 0, 160, 20, "Branch Start: ", " %", 0, 1, 0.2f, true, true);
	public static final GuiSlider foliageStart 		= new GuiSlider(7, 0, 0, 160, 20, "Foliage Start: ", " %", 0, 1, 0.35f, true, true);
	public static final GuiSlider branchSlope 		= new GuiSlider(8, 0, 0, 160, 20, "Branch Slope: ", " %", 0, 1, 0.381f, true, true);
	public static final GuiSlider leafDensity 		= new GuiSlider(9, 0, 0, 160, 20, "Leaf Density: ", " %", 0, 4, 1.0f, true, true);
	public static final GuiSlider branchDensity 	= new GuiSlider(10, 0, 0, 160, 20, "Branch Density: ", " %", 0, 4, 1.0f, true, true);
	
	
	public GUIEditTree(EntityPlayer player){
		this.player = (player);
	}
	
	@Override
	protected void keyTyped(char par1, int par2){
		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.buttonList.clear();
        this.initGui();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		
		buttonList.clear();		
		
		NBTTagCompound heldnbt = ToolFilter.getNBT(player.getHeldItem());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		buttons.add(treeheight);
		buttons.add(trunkRadBot);
		buttons.add(trunkRadMid);
		buttons.add(trunkRadTop);
		buttons.add(trunkHeight);
		buttons.add(trunkMidPoint);
		buttons.add(branchStart);
		buttons.add(foliageStart);
		buttons.add(branchSlope);
		buttons.add(leafDensity);
		buttons.add(branchDensity);
		
		for (GuiButton btn : buttons){
			btn.xPosition = this.width / 2 - (160 / 2);
			btn.yPosition = ((this.height / 2) - 111) + (22 * btn.id);
			buttonList.add(btn);
		}
				
	}
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		//PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
	}

}
