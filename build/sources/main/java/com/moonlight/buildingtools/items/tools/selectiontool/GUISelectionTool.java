package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class GUISelectionTool extends GuiScreen{
	
	private EntityPlayer player;
	
	private Set<GuiButton> buttonsLeft = new LinkedHashSet<GuiButton>();
	private Set<GuiButton> buttonsRight = new LinkedHashSet<GuiButton>();
	
	public static final GuiButton copytoclipboard = 	new GuiButton(1, 0, 0, 160, 20, "Copy Selection To Clipboard");
	public static final GuiButton pasteclipboard = 		new GuiButton(2, 0, 0, 160, 20, "Paste Clipboard");
	public static final GuiButton selectpaste = 		new GuiButton(3, 0, 0, 160, 20, "Select paste region");
	public static final GuiButton clearselction = 		new GuiButton(4, 0, 0, 160, 20, "Clear Selection");
	public static final GuiButton rotate90 = 			new GuiButton(5, 0, 0, 160, 20, "Rotate 90: ");
	public static final GuiButton flipx = 				new GuiButton(6, 0, 0, 160, 20, "Flip X: ");
	public static final GuiButton flipy = 				new GuiButton(7, 0, 0, 160, 20, "Flip Y: ");
	public static final GuiButton flipz = 				new GuiButton(8, 0, 0, 160, 20, "Flip Z: ");
	
	public static final GuiButton clearsel = 			new GuiButton(9, 0, 0, 160, 20, "Delete blocks in selection");
	public static final GuiButton repeat = 				new GuiButton(10, 0, 0, 160, 20, "Repetitions: ");
	public static final GuiButton moveX = 				new GuiButton(11, 0, 0, 160, 20, "X Movment: ");
	public static final GuiButton moveY = 				new GuiButton(12, 0, 0, 160, 20, "Y Movment: ");
	public static final GuiButton moveZ = 				new GuiButton(13, 0, 0, 160, 20, "Z Movment: ");
	public static final GuiButton fill = 				new GuiButton(14, 0, 0, 160, 20, "Fill Mode");
	public static final GuiButton replace = 			new GuiButton(15, 0, 0, 160, 20, "Replace Mode");
	public static final GuiButton file = 				new GuiButton(16, 0, 0, 160, 20, "File Save / Load (WIP)");
	
	
	public GUISelectionTool(EntityPlayer player){
		this.player = player;
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
	
	@Override
	protected void keyTyped(char par1, int par2){
		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		
		buttonList.clear();
		buttonsLeft.clear();
		buttonsRight.clear();
		
		NBTTagCompound heldnbt = ToolSelection.getNBT(player.getHeldItem());
		
		rotate90.displayString = "Rotate 90: " + heldnbt.getInteger("Rotation");
		flipx.displayString = "Flip X: " + heldnbt.getBoolean("flipX");
		flipy.displayString = "Flip Y: " + heldnbt.getBoolean("flipY");
		flipz.displayString = "Flip Z: " + heldnbt.getBoolean("flipZ");
		
		buttonsLeft.add(copytoclipboard);
		buttonsLeft.add(pasteclipboard);
		buttonsLeft.add(selectpaste);
		buttonsLeft.add(clearselction);
		buttonsLeft.add(rotate90);
		buttonsLeft.add(flipx);
		buttonsLeft.add(flipy);
		buttonsLeft.add(flipz);
		
		for (GuiButton btn : buttonsLeft){
			btn.xPosition = this.width / 2 - (160 + 1);
			btn.yPosition = ((this.height / 2) - 111) + (22 * btn.id);
			buttonList.add(btn);
		}
		
		
		repeat.displayString = "Repetitions: " + heldnbt.getInteger("repeat");
		moveX.displayString = "X Movment: " + heldnbt.getInteger("repeatMovmentX");
		moveY.displayString = "Y Movment: " + heldnbt.getInteger("repeatMovmentY");
		moveZ.displayString = "Z Movment: " + heldnbt.getInteger("repeatMovmentZ");
		
		fill.enabled = true;
		replace.enabled = true;
		
		buttonsRight.add(clearsel);
		buttonsRight.add(repeat);
		buttonsRight.add(moveX);
		buttonsRight.add(moveY);
		buttonsRight.add(moveZ);
		buttonsRight.add(fill);
		buttonsRight.add(replace);
		buttonsRight.add(file);
		
		for (GuiButton btn : buttonsRight){
			btn.xPosition = (this.width / 2) + 1;
			btn.yPosition = ((this.height / 2) - 111) + (22 * (btn.id - buttonsLeft.size()));
			buttonList.add(btn);
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        //if (mouseButton == 0)
        //{
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                    if (MinecraftForge.EVENT_BUS.post(event))
                        break;
                    //this.selectedButton = event.button;
                    event.button.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(event.button, mouseButton);
                    if (this.equals(this.mc.currentScreen))
                        MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.button, this.buttonList));
                }
            }
        //}
    }
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
		if(button.id == copytoclipboard.id || button.id == pasteclipboard.id || button.id == selectpaste.id || button.id == clearselction.id
				 || button.id == clearsel.id)
			this.mc.thePlayer.closeScreen();
		else if (button.id == file.id){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUISaveLoadClipboard(this.player));
		}
		else if (button.id == fill.id){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUIFillTool(this.player));
		}
		else if (button.id == replace.id){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUIReplaceTool(this.player));
		}
	}
	
}
