package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.Reference;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;
import com.moonlight.buildingtools.utils.IScrollButtonListener;
import com.moonlight.buildingtools.utils.ScrollPane;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GUISaveLoadClipboard extends GuiScreen implements IScrollButtonListener{
	
	private ScrollPane scrollpane;
	
	private EntityPlayer player;
	
	public static GuiTextField saveName;
	public static final GuiButton save = new GuiButton(1, 0, 0, 80, 20, "Save");
	public static File[] filelist;
	
	public static List<String[]> hoverTextList = new ArrayList<String[]>();
	
	
	
	public GUISaveLoadClipboard(EntityPlayer player){
		this.player = player;
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
        
        saveName.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		for (int i = 0; i < scrollpane.GetButtons().size(); i++) {
			if (scrollpane.GetButtons().get(i) instanceof GuiButton) {
				GuiButton btn = scrollpane.GetButtons().get(i);
				if (btn.isMouseOver()) { // Tells you if the button is hovered by mouse
					List<String> temp = Arrays.asList(hoverTextList.get(i));
					drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
				}
			}
		}
		
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	@Override
	protected void keyTyped(char par1, int par2){
		if(!saveName.isFocused()){
			if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()){
				this.mc.thePlayer.closeScreen();
			}
		}
		else{
			saveName.textboxKeyTyped(par1, par2);
		}
	}
	
	@Override
	public void initGui(){
		System.out.println("GUISaveLoadClipboard.initGui()");
		scrollpane = new ScrollPane(this, this.width/2 - 85, this.height/2 - 100, 180, 140, 400);
        scrollpane.setClip(true);
        
        File savedirectory = BuildingTools.clipboardSaveDir;
        filelist = savedirectory.listFiles();
        
        List<File> selectList = new ArrayList<File>();
        
        for(int i = 0; i < filelist.length; i++){
        	if(filelist[i].isFile()){
        		String fileName = filelist[i].getName();
        		
        		if(fileName.endsWith(".json")){
        			selectList.add(filelist[i]);
        		}
        	}
        }
        
        
        
        for(int i = 0; i < selectList.size(); i++){
        	if(selectList.get(i).isFile()){
        		
        		try {
	        		String fileName = selectList.get(i).getName();
	        		
	        		String saveVersion;
	        		if(new File(savedirectory, "/" + fileName.replace(".json", ".version")).exists()){
	        			BufferedReader is = new BufferedReader(new FileReader(new File(savedirectory, "/" + fileName.replace(".json", ".version"))));
	        			saveVersion = is.readLine();
	        			is.close();
	        		}
	        		else {
						saveVersion = "UNKNOWN";
					}
	        		
	        		String flag = "Errors may occur! Please back up your save!";
	        		if(saveVersion.contentEquals(Reference.VERSION)){
	        			flag = "Everything should be fine!";
	        		}
	        		
	        		String[] text = { 	"This selection was saved", 
	        							"with version " + saveVersion + " of this mod.", 
	        							"This is version " + Reference.VERSION + ".",
	        							flag};
	        		
	        		hoverTextList.add(i, text);
	        		
					scrollpane.addButton(new GuiButton(i, 0, 0+(21*i), 170, 20, fileName.replace(".json", "")));
        		
        		} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
        		
        	}
        }
        
        scrollpane.setContentHeight(scrollpane.GetButtons().size() * 21);
        
        NBTTagCompound heldnbt = ToolSelection.getNBT(player.getHeldItemMainhand());
        if(heldnbt.getCompoundTag("bpos1").getBoolean("set") && heldnbt.getCompoundTag("bpos2").getBoolean("set")){
        	save.enabled = true;
		}
		else{
			save.enabled = false;
		}
        
        saveName = new GuiTextField(0, fontRendererObj, this.width/2 - 85, this.height/2 + 46, 170, 16);
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
            
            scrollpane.onMouseClick(mouseX, mouseY, mouseButton);
            saveName.mouseClicked(mouseX, mouseY, mouseButton);
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
	
	//@Override
	protected void actionPerformed(GuiButton button, int mouseButton){
		NBTTagCompound commandPacket = new NBTTagCompound();
    	
    	commandPacket.setTag("Commands", new NBTTagCompound());
    	commandPacket.getCompoundTag("Commands").setString("1", "SaveFile");
    	commandPacket.setString("File", saveName.getText());
    	
    	PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
		//PacketDispatcher.sendToServer(new SelectionToolSaveSelectionPacket(saveName.getText(), mouseButton, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
		this.mc.thePlayer.closeScreen();
	}

	@Override
	public void ScrollButtonPressed(GuiButton button) {
		
		NBTTagCompound commandPacket = new NBTTagCompound();
    	
    	commandPacket.setTag("Commands", new NBTTagCompound());
    	commandPacket.getCompoundTag("Commands").setString("1", "LoadFile");
    	commandPacket.setString("File", button.displayString);
    	
    	PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
		
		//PacketDispatcher.sendToServer(new SendFileSelection(button.displayString));
		this.mc.thePlayer.closeScreen();
	}

	@Override
	public void GetGuiSliderValue(GuiSlider slider) {
		
	}

}
