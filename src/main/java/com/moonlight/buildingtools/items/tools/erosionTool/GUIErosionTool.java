package com.moonlight.buildingtools.items.tools.erosionTool;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;
import com.moonlight.buildingtools.utils.KeyBindsHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GUIErosionTool extends GuiScreen{
	
	private EntityPlayer player;
	
	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	public static final GuiButton presetButton = new GuiButton(1, 0, 0, 170, 20, "");
	public static final GuiButton radius = new GuiButton(2, 0, 0, 170, 20, "");
	
	public static final GuiButton tutorialMode = 	new GuiButton(100, 20, 20, 20, 20, "?");
	
	public GUIErosionTool(EntityPlayer player){
		this.player = player;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.buttonList.clear();
        this.initGui();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (tutorialMode.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"The Erosion Tool: A full and complete recreation of VoxelSniper’s Erode Brush.",
				"",
				"Buttons can be left clicked to move forward, or right clicked to move back.",
				"",
				"You can also change the tool's radius while outside of the menu by pressing the ",
				"Increase/Decrease button (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu).",
				"",
				"Melt Mode: Removes landscape by pushing it away from the user.",
				"",
				"Fill Mode: Fills edges of holes and pulls landscape out.",
				"",
				"Smooth Mode: Smooths the edges of the landscape.",
				"",
				"Lift Mode: Similar to the building tool, but in a round shape.",
				"",
				"Float Clean Mode: Cleans up floating blocks and fills air blocks that are completely surrounded."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (radius.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the size of the brush.",
				"",
				"You can also use (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu)."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (presetButton.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the mode of the brush. See '?' for an explanation of each mode."
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
		
		NBTTagCompound heldnbt = ToolErosion.getNBT(player.getHeldItemMainhand());
		ErosionVisuallizer.Preset gen = ErosionVisuallizer.Preset.values()[heldnbt.getInteger("preset")];
		
		presetButton.displayString = gen.name();
		radius.displayString = "Raduis: " + heldnbt.getInteger("radius");
		
		buttons.add(presetButton);
		buttons.add(radius);
		
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
