package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class SelectionToolGui extends GuiScreen{
	
	private EntityPlayer player;

	public static final int GUI_ID = 5;
	
	public final int xSizeOfTexture = 176;
	public final int ySizeOfTexture = 88;
	
	//private GuiTextField radiusText;
	//private GuiTextField iterationsText;
	//private GuiTextField sigmaText;
	
	public SelectionToolGui(EntityPlayer player){
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
		
		NBTTagCompound heldnbt = SelectionTool.getNBT(player.getHeldItem());
		
		buttonList.add(new GuiButton(1, this.width / 2 - 170 / 2, this.height / 2 -10 - 80, 170, 20, "Copy Selection To Clipboard"));
		buttonList.add(new GuiButton(2, this.width / 2 - 170 / 2, this.height / 2 -10 - 60, 170, 20, "Paste Clipboard"));
		buttonList.add(new GuiButton(3, this.width / 2 - 170 / 2, this.height / 2 -10 - 40, 170, 20, "Select paste region"));
		buttonList.add(new GuiButton(4, this.width / 2 - 170 / 2, this.height / 2 -10 - 20, 170, 20, "Rotate 90? " + heldnbt.getInteger("Rotation")));
		
		buttonList.add(new GuiButton(5, this.width / 2 - 170 / 2, this.height / 2 -10 - 0, 170, 20, "Flip X? " + heldnbt.getBoolean("flipX")));
		buttonList.add(new GuiButton(6, this.width / 2 - 170 / 2, this.height / 2 -10 + 20, 170, 20, "Flip Y? " + heldnbt.getBoolean("flipY")));
		buttonList.add(new GuiButton(7, this.width / 2 - 170 / 2, this.height / 2 -10 + 40, 170, 20, "Flip Z? " + heldnbt.getBoolean("flipZ")));
		buttonList.add(new GuiButton(8, this.width / 2 - 170 / 2, this.height / 2 -10 + 60, 170, 20, "Clear all in selection: "));
		
		
		buttonList.add(new GuiButton(9, this.width / 2 - 170*4 / 2, this.height / 2 -10 - 0, 170, 20, "Repeatitions:  " + heldnbt.getInteger("repeat")));
		buttonList.add(new GuiButton(10, this.width / 2 - 170*4 / 2, this.height / 2 -10 + 20, 170, 20, "X Movment: " + heldnbt.getInteger("repeatMovmentX")));
		buttonList.add(new GuiButton(11, this.width / 2 - 170*4 / 2, this.height / 2 -10 + 40, 170, 20, "Y Movment: " + heldnbt.getInteger("repeatMovmentY")));
		buttonList.add(new GuiButton(12, this.width / 2 - 170*4 / 2, this.height / 2 -10 + 60, 170, 20, "Z Movment: " + heldnbt.getInteger("repeatMovmentZ")));
		
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
		PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), func_175283_s(), isShiftKeyDown()));
		
	}
	
}
