package com.moonlight.buildingtools.items.tools.brushtool;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;
import com.moonlight.buildingtools.utils.KeyBindsHandler;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

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
	
	public static final GuiButton tutorialMode = 	new GuiButton(100, 20, 20, 20, 20, "?");
	
	
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
		
		//GL11.glScalef(0.90f, 0.90f, 1.0f);
		
		if (tutorialMode.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"This is the Brush Tool: Similar to brush tools from mods like VoxelSniper. ",
				"This tool allows you to paint shapes of whatever block (or blocks) you want into your world.",
				"",
				"All buttons can be left clicked to move forward, or right clicked to move back.",
				"",
				"You can also change the tool's radius while outside of the menu by pressing the ",
				"Increase/Decrease button (" + Keyboard.getKeyName(KeyBindsHandler.keyToolIncrease.getMinecraftKeyBinding().getKeyCode())
				+ " / " + Keyboard.getKeyName(KeyBindsHandler.keyToolDecrease.getMinecraftKeyBinding().getKeyCode())
				+ ") (See your options/controls menu).",
				"",
				"Set Block to Looking At: Will do exactly that. ",
				"This provides a quick way to change your paint material without searching in the Paint Block Seletion Menu.",
				"",
				"The Set Block to Air button will change the tool to “Erase Mode”, ",
				"which should be used with the replace block or replace all mode.",
				"",
				"Force falling blocks will make any blocks you paint act like sand and ",
				"instantly fall to the ground. Great for terrain creation.",
				"",
				"Replace mode: If set to air, will only replace Air blocks or Temp Blocks(see temp block placer). ",
				"When set to Block mode, will only replace any block of the same type you are painting onto. ",
				"When set to All, will replace any block. ",
				"When set to Custom, will only replace the blocks you specify in the Replace Block Selection Menu."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (generator.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the shape of the brush."
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
		
		if (radiusy.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes the size of the brush in the Y axis.",
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
		
		if (setblock.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Sets your paint material to the same as the block you are currently looking at."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (setair.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Sets the paint material to air, used for erasing blocks."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (fill.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Sets if the shape is solid or hollow."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (fall.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Will cause any played blocks to fall like sand."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (replace.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Changes what blocks will be replaced."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (showBlockSel.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Paint Block Selection Menu."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
		
		if (showBlockRep.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"Replace Block Selection Menu."
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
		
		NBTTagCompound heldnbt = ToolBrush.getNBT(player.getHeldItemMainhand());
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
		setblock.enabled = ((ToolBrush)player.getHeldItemMainhand().getItem()).targetBlock != null;
		
		showBlockRep.enabled = heldnbt.getInteger("replacemode") == 4;
		
		buttonList.add(tutorialMode);
		
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
		if(button.id == setblock.id || button.id == setair.id)
			this.mc.thePlayer.closeScreen();
		if(button.id == showBlockSel.id)
			this.mc.displayGuiScreen(new GUISetPaintBlock(player));
		if(button.id == showBlockRep.id)
			this.mc.displayGuiScreen(new GUISetReplaceBlocks(player));
	}
	
}
