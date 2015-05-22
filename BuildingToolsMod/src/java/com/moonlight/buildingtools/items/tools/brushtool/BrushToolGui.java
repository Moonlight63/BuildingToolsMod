package com.moonlight.buildingtools.items.tools.brushtool;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class BrushToolGui extends GuiScreen{
	
	private EntityPlayer player;

	public static final int GUI_ID = 2;
	
	public final int xSizeOfTexture = 176;
	public final int ySizeOfTexture = 88;
	
	public BrushToolGui(EntityPlayer player){
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
	protected void keyTyped(char par1, int par2)
	{
		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
		{
			this.mc.thePlayer.closeScreen();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui(){
		
		buttonList.clear();
		
		NBTTagCompound heldnbt = BrushTool.getNBT(player.getHeldItem());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		buttonList.add(new GuiButton(1, this.width / 2 - 170 / 2, this.height / 2 -10 - 80, 170, 20, gen.unlocalizedName));
		buttonList.add(new GuiButton(2, this.width / 2 - 170 / 2, this.height / 2 -10 - 60, 170, 20, (gen.fixedRatio ? "Radius: " : "Radius X: ") + heldnbt.getInteger("radiusX")));
		buttonList.add(new GuiButton(3, this.width / 2 - 170 / 2, this.height / 2 -10 - 40, 170, 20, "Radius Y: " + heldnbt.getInteger("radiusY")));
		
		GuiButton zradbutton = new GuiButton(4, this.width / 2 - 170 / 2, this.height / 2 -10 - 20, 170, 20, (gen.fixedRatio ? "Fixed Ratio: " : "Radius Z: ") + heldnbt.getInteger("radiusZ"));
		zradbutton.enabled = !gen.fixedRatio;
		buttonList.add(zradbutton);
		
		if(((BrushTool)player.getHeldItem().getItem()).targetBlock != null)
			buttonList.add(new GuiButton(8, this.width / 2 - 170 / 2, this.height / 2 -10, 170, 20, "Set Block to looking at"));
		
		buttonList.add(new GuiButton(9, this.width / 2 - 170 / 2, this.height / 2 -10 + 20, 170, 20, "Set Block to Air (Erase mode)"));
		
		buttonList.add(new GuiButton(5, this.width / 2 - 170 / 2, this.height / 2 -10 + 40, 170, 20, "Fill Mode: " + heldnbt.getBoolean("fillmode")));
		buttonList.add(new GuiButton(6, this.width / 2 - 170 / 2, this.height / 2 -10 + 60, 170, 20, "Force Falling Blocks: " + heldnbt.getBoolean("forcefall")));
		
		buttonList.add(new GuiButton(7, this.width / 2 - 170 / 2, this.height / 2 -10 + 80, 170, 20, "Replace Mode: " + (heldnbt.getInteger("replacemode") == 1 ? "Replace Air" : (heldnbt.getInteger("replacemode") == 2 ? "Replace Block" : "Replace All"))));
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (int l = 0; l < this.buttonList.size(); ++l)
        {
            GuiButton guibutton = (GuiButton) this.buttonList.get(l);

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
    }
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), func_175283_s(), isShiftKeyDown()));
		if(button.id == 8 || button.id == 9)
			this.mc.thePlayer.closeScreen();
	}
	
}
