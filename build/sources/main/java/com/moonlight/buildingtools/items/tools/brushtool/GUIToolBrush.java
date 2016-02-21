package com.moonlight.buildingtools.items.tools.brushtool;

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

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class GUIToolBrush extends GuiScreen{
	
	private EntityPlayer player;
	
	private Set<GuiButton> buttons = new LinkedHashSet<GuiButton>();
	public static final GuiButton generator = 		new GuiButton(1, 0, 0, 160, 20, "");
	public static final GuiButton radiusx = 		new GuiButton(2, 0, 0, 160, 20, "");
	public static final GuiButton radiusy = 		new GuiButton(3, 0, 0, 160, 20, "");
	public static final GuiButton radiusz = 		new GuiButton(4, 0, 0, 160, 20, "");
	public static final GuiButton setblock = 		new GuiButton(5, 0, 0, 160, 20, "");
	public static final GuiButton setair = 			new GuiButton(6, 0, 0, 160, 20, "");
	public static final GuiButton fill = 			new GuiButton(7, 0, 0, 160, 20, "");
	public static final GuiButton fall = 			new GuiButton(8, 0, 0, 160, 20, "");
	public static final GuiButton replace = 		new GuiButton(9, 0, 0, 160, 20, "");
	public static final GuiButton showBlockSel = 	new GuiButton(10, 0, 0, 20, 20, "");
	public static final GuiButton showBlockRep = 	new GuiButton(11, 0, 0, 20, 20, "");
	
	public GUIToolBrush(EntityPlayer player){
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
		buttons.clear();
		
		NBTTagCompound heldnbt = ToolBrush.getNBT(player.getHeldItem());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		generator.displayString = gen.getLocalizedName();
		radiusx.displayString = (gen.fixedRatio ? "Radius: " : "Radius X: ") + heldnbt.getInteger("radiusX");
		radiusy.displayString = "Radius Y: " + heldnbt.getInteger("radiusY");
		radiusz.displayString = (gen.fixedRatio ? "Fixed Ratio: " : "Radius Z: ") + heldnbt.getInteger("radiusZ");
		setblock.displayString = "Set Block to looking at";
		setair.displayString = "Set Block to Air (Erase mode)";
		fill.displayString = "Fill Mode: " + heldnbt.getBoolean("fillmode");
		fall.displayString = "Force Falling Blocks: " + heldnbt.getBoolean("forcefall");
		replace.displayString = "Replace Mode: " + (
				heldnbt.getInteger("replacemode") == 1 ? "Replace Air" :
					heldnbt.getInteger("replacemode") == 2 ? "Replace Block" :
						heldnbt.getInteger("replacemode") == 3 ? "Replace All" :
							"Replace Custom"
						);
		
		radiusz.enabled = !gen.fixedRatio;
		setblock.enabled = ((ToolBrush)player.getHeldItem().getItem()).targetBlock != null;
		
		showBlockRep.enabled = heldnbt.getInteger("replacemode") == 4;
		
		buttons.add(generator);
		buttons.add(radiusx);
		buttons.add(radiusy);
		buttons.add(radiusz);
		buttons.add(setblock);
		buttons.add(setair);
		buttons.add(fill);
		buttons.add(fall);
		buttons.add(replace);
		
		for (GuiButton btn : buttons){
			btn.xPosition = this.width / 2 - (160 / 2);
			btn.yPosition = ((this.height / 2) - 121) + (22 * btn.id);
			buttonList.add(btn);
		}
		
		showBlockSel.yPosition = ((this.height / 2) - 121) + (22 * 5);
		showBlockSel.xPosition = this.width / 2 + (160 / 2) + 2;
		
		buttonList.add(showBlockSel);
		
		
		showBlockRep.yPosition = ((this.height / 2) - 121) + (22 * 9);
		showBlockRep.xPosition = this.width / 2 + (160 / 2) + 2;
		
		buttonList.add(showBlockRep);
		
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
		PacketDispatcher.sendToServer(new SendGuiButtonPressedToItemMessage((byte) button.id, mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
		if(button.id == setblock.id || button.id == setair.id)
			this.mc.thePlayer.closeScreen();
		if(button.id == showBlockSel.id)
			this.mc.displayGuiScreen(new GUISetPaintBlock(player));
		if(button.id == showBlockRep.id)
			this.mc.displayGuiScreen(new GUISetReplaceBlocks(player));
	}
	
}
