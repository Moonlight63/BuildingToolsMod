package com.moonlight.buildingtools.items.tools.erosionTool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;

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
		
		for (GuiButton btn : buttons){
			btn.xPosition = this.width / 2 - (160 / 2);
			btn.yPosition = ((this.height / 2) - 111) + (22 * btn.id);
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
		
		
		//PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
	}
	
}
