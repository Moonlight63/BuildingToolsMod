package com.moonlight.buildingtools.items.tools.filtertool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.Reference;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class GUIToolFilter extends GuiScreen{
	
	private EntityPlayer player;

	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	public static final GuiButton filter = 		new GuiButton(1, 0, 0, 160, 20, "TopSoil");
	public static final GuiButton radx = 		new GuiButton(2, 0, 0, 160, 20, "");
	public static final GuiButton rady = 		new GuiButton(3, 0, 0, 160, 20, "");
	public static final GuiButton radz = 		new GuiButton(4, 0, 0, 160, 20, "");
	public static final GuiButton depth = 		new GuiButton(5, 0, 0, 160, 20, "");
	
	
	public GUIToolFilter(EntityPlayer player){
		this.player = player;
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
		buttons.clear();
		
		
		NBTTagCompound heldnbt = ToolFilter.getNBT(player.getHeldItem());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		filter.displayString = heldnbt.getInteger("filter") == 1 ? "Topsoil" : heldnbt.getInteger("filter") == 2 ? "Clear Water" : "Clear Junk";
		radx.displayString = "Radius X: " + heldnbt.getInteger("radiusX");
		rady.displayString = (gen.fixedRatio ? "Fixed Ratio: " : "Radius Y: ") + heldnbt.getInteger("radiusY");
		radz.displayString = (gen.fixedRatio ? "Fixed Ratio: " : "Radius Z: ") + heldnbt.getInteger("radiusZ");
		depth.displayString = "Depth: " + heldnbt.getInteger("topsoildepth");
		
		depth.enabled = heldnbt.getInteger("filter") == 1 ? true : false;
		
		buttons.add(filter);
		buttons.add(radx);
		buttons.add(rady);
		buttons.add(radz);
		buttons.add(depth);
		
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
		
	}
	
}
