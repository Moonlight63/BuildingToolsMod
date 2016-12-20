package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GUISelectionTool extends GuiScreen{
	
	private EntityPlayer player;
	
	private Set<GuiButton> buttonsLeft = new LinkedHashSet<GuiButton>();
	private Set<GuiButton> buttonsRight = new LinkedHashSet<GuiButton>();
	
	public static final GuiButton copytoclipboard = 	new GuiButton(1, 0, 0, 160, 20, "Copy Selection To Clipboard");
	public static final GuiButton pasteclipboard = 		new GuiButton(2, 0, 0, 160, 20, "Paste Clipboard");
	public static final GuiButton rotate90 = 			new GuiButton(3, 0, 0, 160, 20, "Rotate 90: ");
	public static final GuiButton flipx = 				new GuiButton(4, 0, 0, 160, 20, "Flip X: ");
	public static final GuiButton flipy = 				new GuiButton(5, 0, 0, 160, 20, "Flip Y: ");
	public static final GuiButton flipz = 				new GuiButton(6, 0, 0, 160, 20, "Flip Z: ");
	public static final GuiButton replace = 			new GuiButton(7, 0, 0, 160, 20, "Replace Mode");
	
	public static final GuiButton clearselction = 		new GuiButton(8, 0, 0, 160, 20, "Clear Selection");
	public static final GuiButton selectpaste = 		new GuiButton(9, 0, 0, 160, 20, "Select paste region");
	public static final GuiButton repeat = 				new GuiButton(10, 0, 0, 160, 20, "Repetitions: ");
	public static final GuiButton moveX = 				new GuiButton(11, 0, 0, 160, 20, "X Movment: ");
	public static final GuiButton moveY = 				new GuiButton(12, 0, 0, 160, 20, "Y Movment: ");
	public static final GuiButton moveZ = 				new GuiButton(13, 0, 0, 160, 20, "Z Movment: ");
	public static final GuiButton file = 				new GuiButton(14, 0, 0, 160, 20, "File Save / Load (WIP)");
	
	
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
	
	@Override
	public void initGui(){
		
		buttonList.clear();
		buttonsLeft.clear();
		buttonsRight.clear();
		
		NBTTagCompound heldnbt = ToolSelection.getNBT(player.getHeldItemMainhand());
		PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
		
		rotate90.displayString = "Rotate 90: " + heldnbt.getInteger("Rotation");
		flipx.displayString = "Flip X: " + heldnbt.getBoolean("flipX");
		flipy.displayString = "Flip Y: " + heldnbt.getBoolean("flipY");
		flipz.displayString = "Flip Z: " + heldnbt.getBoolean("flipZ");
		
		if(heldnbt.getCompoundTag("bpos1").getBoolean("set") && heldnbt.getCompoundTag("bpos2").getBoolean("set")){
			copytoclipboard.enabled = true;
		}
		else{
			copytoclipboard.enabled = false;
		}
		
		if(playerwrap.currentCopyClipboard.isEmpty())
			pasteclipboard.enabled = false;
		else
			pasteclipboard.enabled = true;
		
		buttonsLeft.add(copytoclipboard);
		buttonsLeft.add(pasteclipboard);
		buttonsLeft.add(rotate90);
		buttonsLeft.add(flipx);
		buttonsLeft.add(flipy);
		buttonsLeft.add(flipz);
		buttonsLeft.add(replace);
		
		for (GuiButton btn : buttonsLeft){
			btn.xPosition = this.width / 2 - (160 + 1);
			btn.yPosition = ((this.height / 2) - 111) + (22 * btn.id);
			buttonList.add(btn);
		}
		
		
		repeat.displayString = "Repetitions: " + heldnbt.getInteger("repeat");
		moveX.displayString = "X Movment: " + heldnbt.getInteger("repeatMovmentX");
		moveY.displayString = "Y Movment: " + heldnbt.getInteger("repeatMovmentY");
		moveZ.displayString = "Z Movment: " + heldnbt.getInteger("repeatMovmentZ");
		
		replace.enabled = true;
		
		buttonsRight.add(clearselction);
		buttonsRight.add(selectpaste);
		buttonsRight.add(repeat);
		buttonsRight.add(moveX);
		buttonsRight.add(moveY);
		buttonsRight.add(moveZ);
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
                    event.getButton().playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(event.getButton(), mouseButton);
                    if (this.equals(this.mc.currentScreen))
                        MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                }
            }
        //}
    }
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		
		NBTTagCompound commandPacket = new NBTTagCompound();
    	
    	commandPacket.setTag("Commands", new NBTTagCompound());
    	commandPacket.getCompoundTag("Commands").setString("1", "GetButton");
    	commandPacket.setInteger("ButtonID", button.id);
    	commandPacket.setInteger("Mouse", mouseButton);
    	commandPacket.setBoolean("CTRL", isCtrlKeyDown());
    	commandPacket.setBoolean("ALT", isAltKeyDown());
    	commandPacket.setBoolean("SHIFT", isShiftKeyDown());
    	
    	PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
		
		if(button.id == copytoclipboard.id || button.id == pasteclipboard.id || button.id == selectpaste.id || button.id == clearselction.id)
			this.mc.thePlayer.closeScreen();
		else if (button.id == file.id){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUISaveLoadClipboard(this.player));
		}
		else if (button.id == replace.id){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUIReplaceTool(this.player));
		}
	}
	
}
