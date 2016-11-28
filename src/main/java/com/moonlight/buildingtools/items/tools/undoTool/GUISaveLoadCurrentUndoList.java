package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SelectionToolSaveSelectionPacket;
import com.moonlight.buildingtools.network.packethandleing.SendFileSelection;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IScrollButtonListener;
import com.moonlight.buildingtools.utils.ScrollPane;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GUISaveLoadCurrentUndoList extends GuiScreen implements IScrollButtonListener{
	
	private ScrollPane scrollpane;
	public static final GuiButton save = new GuiButton(1, 0, 0, 80, 20, "Save");
	public static File[] filelist;
	
	
	
	public GUISaveLoadCurrentUndoList(EntityPlayer player){
	}
	
	@Override
	public void handleMouseInput() {
		try {
			super.handleMouseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int wheelState = Mouse.getEventDWheel();
		scrollpane.onMouseWheel(wheelState);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        scrollpane.draw(mouseX, mouseY);
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
		System.out.println("GUISaveLoadClipboard.initGui()");
		scrollpane = new ScrollPane(this, this.width/2 - 85, this.height/2 - 100, 180, 140, 400);
        scrollpane.setClip(true);
        
        File savedirectory = BuildingTools.oldUndoDir;
        filelist = savedirectory.listFiles();
        
        for(int i = 0; i < filelist.length; i++){
        	if(filelist[i].isFile())
        		scrollpane.addButton(new GuiButton(i, 0, 0+(21*i), 170, 20, filelist[i].getName().replace(".nbt", "")));
        }
        
        scrollpane.setContentHeight(scrollpane.GetButtons().size() * 21);
        
        save.xPosition = this.width/2 - 85;
        save.yPosition = this.height/2 + 64;
        save.width = 170;
        save.height = 20;
        buttonList.add(save);
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
            
            scrollpane.onMouseClick(mouseX, mouseY, mouseButton);
        //}
    }
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state){
		super.mouseReleased(mouseX, mouseY, state);
		scrollpane.onMouseBtnReleased(0);
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		scrollpane.onMouseMoved(mouseX, mouseY);
	}
	
	protected void actionPerformed(GuiButton button, int mouseButton){
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(this.mc.thePlayer).get();
		player.addPending(new ThreadSaveUndoList(this.mc.thePlayer, this.mc.thePlayer.getName() + "." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
		//PacketDispatcher.sendToServer(new SelectionToolSaveSelectionPacket(saveName.getText(), mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
		this.mc.thePlayer.closeScreen();
	}

	@Override
	public void ScrollButtonPressed(GuiButton button) {
		PacketDispatcher.sendToServer(new SendFileSelection(filelist[button.id].getName()));
		this.mc.thePlayer.closeScreen();
	}

	@Override
	public void GetGuiSliderValue(GuiSlider slider) {
		
	}

}
