package com.moonlight.buildingtools.items.tools.filtertool;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GUIToolFilter extends GuiScreen{
	
	private EntityPlayer player;

	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	public static final GuiButton filter = 		new GuiButton(1, 0, 0, 160, 20, "TopSoil");
	public static final GuiButton radx = 		new GuiButton(2, 0, 0, 160, 20, "");
	public static final GuiButton rady = 		new GuiButton(3, 0, 0, 160, 20, "");
	public static final GuiButton radz = 		new GuiButton(4, 0, 0, 160, 20, "");
	public static final GuiButton depth = 		new GuiButton(5, 0, 0, 160, 20, "");
	public static final GuiButton treeType = 	new GuiButton(7, 0, 0, 160, 20, "Tree Type: ");
	public static final GuiButton editTree = 	new GuiButton(8, 0, 0, 160, 20, "Edit Tree");
	
	
	
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
	
	@Override
	public void initGui(){
		
		buttonList.clear();
		buttons.clear();
		
		
		NBTTagCompound heldnbt = ToolFilter.getNBT(player.getHeldItemMainhand());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		//filter.displayString = heldnbt.getInteger("filter") == 1 ? "Topsoil" : heldnbt.getInteger("filter") == 2 ? "Clear Water" : "Clear Junk";
		
		switch (heldnbt.getInteger("filter")) {
		case 1:
			filter.displayString = "Topsoil";
			radx.visible = true;
			rady.visible = true;
			radz.visible = true;
			depth.visible = true;
			treeType.visible = false;
			editTree.visible = false;
			break;
			
		case 2:
			filter.displayString = "Clear Water";
			radx.visible = true;
			rady.visible = true;
			radz.visible = true;
			depth.visible = false;
			treeType.visible = false;
			editTree.visible = false;
			break;
			
		case 3:
			filter.displayString = "Clear Junk";
			radx.visible = true;
			rady.visible = true;
			radz.visible = true;
			depth.visible = false;
			treeType.visible = false;
			editTree.visible = false;
			break;
	
		case 4:
			filter.displayString = "BoneMeal-ify";
			radx.visible = true;
			rady.visible = true;
			radz.visible = true;
			depth.visible = false;
			treeType.visible = false;
			editTree.visible = false;
			break;
			
		case 5:
			filter.displayString = "Create Tree (UNFINISHED)";
			radx.visible = false;
			rady.visible = false;
			radz.visible = false;
			depth.visible = false;
			treeType.visible = true;
			treeType.displayString = ETreeTypes.values()[heldnbt.getInteger("treetype")].name();
			editTree.visible = treeType.displayString == "Custom";
			break;

		default:
			break;
		}
		
		radx.displayString = "Radius X: " + heldnbt.getInteger("radiusX");
		rady.displayString = (gen.fixedRatio ? "Fixed Ratio: " : "Radius Y: ") + heldnbt.getInteger("radiusY");
		radz.displayString = (gen.fixedRatio ? "Fixed Ratio: " : "Radius Z: ") + heldnbt.getInteger("radiusZ");
		depth.displayString = "Depth: " + heldnbt.getInteger("topsoildepth");
		
		treeType.xPosition = this.width / 2 - (160 / 2);
		treeType.yPosition = ((this.height / 2) - 111) + (44);
		
		editTree.xPosition = this.width / 2 - (160 / 2);
		editTree.yPosition = ((this.height / 2) - 111) + (66);
		
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
		
		buttonList.add(treeType);
		buttonList.add(editTree);
				
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (int l = 0; l < this.buttonList.size(); ++l)
        {
            GuiButton guibutton = (GuiButton)this.buttonList.get(l);

            if (guibutton.mousePressed(this.mc, mouseX, mouseY))
            {
                ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                if (MinecraftForge.EVENT_BUS.post(event))
                    break;
                event.getButton().playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(event.getButton(), mouseButton);
                if (this.equals(this.mc.currentScreen))
                    MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
            }
        }
    }
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		if(button == editTree){
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.displayGuiScreen(new GUIEditTree(this.player, ((ToolFilter)(player.getHeldItemMainhand().getItem())).treeData));
		}
		else{
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
	
}
