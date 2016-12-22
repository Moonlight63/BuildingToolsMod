package com.moonlight.buildingtools.items.tools.buildingtool;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;
import com.moonlight.buildingtools.utils.KeyBindsHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GUIBuildersTool extends GuiScreen{
	
	private EntityPlayer player;
	
	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	public static final GuiButton radiusx = 		new GuiButton(1, 0, 0, 160, 20, "");
	public static final GuiButton radiusz = 		new GuiButton(2, 0, 0, 160, 20, "");
	public static final GuiButton allblocks = 		new GuiButton(3, 0, 0, 160, 20, "");

	public static final GuiButton tutorialMode = 	new GuiButton(100, 20, 20, 20, 20, "?");
	
	public GUIBuildersTool(EntityPlayer player){
		this.player = player;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		this.buttonList.clear();
        this.initGui();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (tutorialMode.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"The Building Tool: If you have ever used the builder's wand from Extra utilities,",
				"it’s basically the same thing. It takes an area, and extrudes it out. ",
				"",
				"Buttons can be left clicked to move forward, or right clicked to move back.",
				"",
				"You can also change the tool's radius while outside of the menu by pressing the ",
				"Increase/Decrease button (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu).",
				"",
				"If Copy all blocks is false, only blocks of the same type that you are looking at will be extruded."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (radiusx.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the size of the brush in the X axis.",
				"",
				"You can also use (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu)."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (radiusz.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the size of the brush in the Z axis.",
				"",
				"You can also use (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu)."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (allblocks.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"If this is false, only blocks of the same type that you are looking at will be extruded."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
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
		buttons.clear();
		
		NBTTagCompound heldnbt = ToolBuilding.getNBT(player.getHeldItemMainhand());
		
		radiusx.displayString = "Radius X: " + heldnbt.getInteger("radiusX");
		radiusz.displayString = "Radius Z: " + heldnbt.getInteger("radiusZ");
		allblocks.displayString = "Copy All Blocks? : " + heldnbt.getBoolean("placeAll");
		
		buttons.add(radiusx);
		buttons.add(radiusz);
		buttons.add(allblocks);
		
		buttonList.add(tutorialMode);
		
		for (GuiButton btn : buttons){
			btn.xPosition = this.width / 2 - (160 / 2);
			btn.yPosition = ((this.height / 2) - 111) + (22 * btn.id);
			buttonList.add(btn);
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (int l = 0; l < this.buttonList.size(); ++l)
        {
            GuiButton guibutton = this.buttonList.get(l);

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
				
	}
	
}
