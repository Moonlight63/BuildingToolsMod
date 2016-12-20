package com.moonlight.buildingtools.items.tools.filtertool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendTreeDataToItem;
import com.moonlight.buildingtools.utils.IScrollButtonListener;
import com.moonlight.buildingtools.utils.ScrollPane;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GUIEditTree extends GuiScreen implements IScrollButtonListener{
	
	private EntityPlayer player;
	private ProceduralTreeData data;
	private List<GuiButton> buttonsLeft = new ArrayList<GuiButton>();
	private List<GuiButton> buttonsRight = new ArrayList<GuiButton>();
	private GuiButton selectedButton;
	
	private int page = 0;
	private int currentFloatArray = 0;
	
	private ScrollPane scrollpane1 = new ScrollPane(this, this.width/2 - 85, this.height/2 - 100, 180, 140, 400) ;
	private ScrollPane scrollpane2 = new ScrollPane(this, this.width/2 - 85, this.height/2 - 100, 180, 140, 400);
	
	private List<List<Float>> foliageShapes = new ArrayList<List<Float>>();
	
	public static final GuiSlider treeheight 		= new GuiSlider(0, 0, 0, 170, 20, "Height: ", " blocks", 1, 200, 8, false, true);
	public static final GuiSlider trunkRadBot 		= new GuiSlider(1, 0, 0, 170, 20, "Trunk Raduis Bottom: ", " blocks", 1, 64, 6, false, true);
	public static final GuiSlider trunkRadMid 		= new GuiSlider(2, 0, 0, 170, 20, "Trunk Raduis Middle: ", " blocks", 1, 64, 4, false, true);
	public static final GuiSlider trunkRadTop 		= new GuiSlider(3, 0, 0, 170, 20, "Trunk Raduis Top: ", " blocks", 1, 64, 3, false, true);
	public static final GuiSlider trunkHeight 		= new GuiSlider(4, 0, 0, 170, 20, "Trunk Height: ", " %", 0.1, 1, 0.8f, true, true);
	public static final GuiSlider trunkMidPoint 	= new GuiSlider(5, 0, 0, 170, 20, "Trunk Mid Point: ", " %", 0.1, 1, 0.382f, true, true);
	public static final GuiSlider branchStart 		= new GuiSlider(6, 0, 0, 170, 20, "Branch Start: ", " %", 0.1, 1, 0.2f, true, true);
	public static final GuiSlider foliageStart 		= new GuiSlider(7, 0, 0, 170, 20, "Foliage Start: ", " %", 0.1, 1, 0.35f, true, true);
	public static final GuiSlider branchSlope 		= new GuiSlider(8, 0, 0, 170, 20, "Branch Slope: ", " %", 0.1, 1, 0.381f, true, true);
	public static final GuiSlider leafDensity 		= new GuiSlider(9, 0, 0, 170, 20, "Leaf Density: ", " %", 0.1, 4, 1.0f, true, true);
	public static final GuiSlider branchDensity 	= new GuiSlider(10, 0, 0, 170, 20, "Branch Density: ", " %", 0.05, 1, 1.0f, true, true);
	public static final GuiSlider widthScale	 	= new GuiSlider(11, 0, 0, 170, 20, "Width Scale: ", " %", 0.5, 16, 1.0f, true, true);
	public static final GuiSlider trunkThickness	= new GuiSlider(12, 0, 0, 170, 20, "Trunk Wall: ", " blocks", 1, 4, 1, false, true);
	public static final GuiButtonExt hollowTrunk	 = new GuiButtonExt(13, 0, 0, 170, 20, "Hollow Trunk: ");
	public static final GuiButtonExt clusterShape	 = new GuiButtonExt(14, 0, 0, 170, 20, "Tree Shape: ");
	public static final GuiButtonExt editTreeMats	 = new GuiButtonExt(15, 0, 0, 170, 20, "Change Tree Materials");
	public static final GuiButtonExt editFoliageShape = new GuiButtonExt(16, 0, 0, 170, 20, "Edit Foliage Shapes");
	public static final GuiButtonExt addToPane = new GuiButtonExt(17, 0, 0, 170, 20, "Add New Entry");
	public static final GuiButtonExt done = new GuiButtonExt(18, 0, 0, 170, 20, "Done");
	
	
	public GUIEditTree(EntityPlayer player, ProceduralTreeData data){
		this.player = (player);
		this.data = data;
		System.out.println(this.data.logMat);
		this.foliageShapes = data.GetFoliageShapes();
	}
	
	@Override
	protected void keyTyped(char par1, int par2){
		if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		//System.out.println("Draw Screen");
		
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if(scrollpane1 != null && page == 1)
			scrollpane1.draw(mouseX, mouseY);
		if(scrollpane2 != null && page == 2)
			scrollpane2.draw(mouseX, mouseY);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	@Override
	public void initGui(){
		//System.out.println("GUIEditTree.initGui()");
		//System.out.println(this.data.foliage_shape.toString());
		if(page == 0)
			this.DrawMain();
		else if(page == 1){
			this.ShowEditFoliageShapes();
		}
		else if(page == 2){
			this.EditFloatArray();
		}
				
	}
	
	public void DrawMain(){
		this.buttonList.clear();
		buttonsLeft.clear();
		buttonsRight.clear();
		//System.out.println("GUIEditTree.DrawMain()");
		
		treeheight.setValue(data.GetTreeHeight());
		trunkRadBot.setValue(data.GetTrunkBottom());
		trunkRadMid.setValue(data.GetTrunkMiddle());
		trunkRadTop.setValue(data.GetTrunkTop());
		trunkHeight.setValue(data.GetTrunkHeight());
		trunkMidPoint.setValue(data.GetTrunkMidPoint());
		branchStart.setValue(data.GetBranchStart());
		foliageStart.setValue(data.GetFoliageStart());
		branchSlope.setValue(data.GetBranchSlope());
		leafDensity.setValue(data.GetLeafDensity());
		branchDensity.setValue(data.GetBranchDensity());
		widthScale.setValue(data.GetScaleWidth());
		trunkThickness.setValue(data.trunkWallThickness);
		treeheight.precision = 3;
		trunkRadBot.precision = 3;
		trunkRadMid.precision = 3;
		trunkRadTop.precision = 3;
		trunkHeight.precision = 3;
		trunkMidPoint.precision = 3;
		branchStart.precision = 3;
		foliageStart.precision = 3;
		branchSlope.precision = 3;
		leafDensity.precision = 3;
		branchDensity.precision = 3;
		widthScale.precision = 3;
		trunkThickness.precision = 1;
		treeheight.updateSlider();
		trunkRadBot.updateSlider();
		trunkRadMid.updateSlider();
		trunkRadTop.updateSlider();
		trunkHeight.updateSlider();
		trunkMidPoint.updateSlider();
		branchStart.updateSlider();
		foliageStart.updateSlider();
		branchSlope.updateSlider();
		leafDensity.updateSlider();
		branchDensity.updateSlider();
		widthScale.updateSlider();
		trunkThickness.updateSlider();
		
		buttonsLeft.add(treeheight);
		buttonsLeft.add(trunkRadBot);
		buttonsLeft.add(trunkRadMid);
		buttonsLeft.add(trunkRadTop);
		buttonsLeft.add(trunkHeight);
		buttonsLeft.add(trunkMidPoint);
		buttonsLeft.add(branchStart);
		buttonsLeft.add(foliageStart);
		buttonsLeft.add(branchSlope);
		buttonsRight.add(leafDensity);
		buttonsRight.add(branchDensity);
		buttonsRight.add(widthScale);
		buttonsRight.add(trunkThickness);
		
		hollowTrunk.displayString = "Hollow Trunk: " + (data.hollowTrunk ? "True" : "False");
		buttonsRight.add(hollowTrunk);
		
		clusterShape.displayString = "Tree Shape: " + (data.GetClusterShape() == 1 ? "Cone" : "Round");
		buttonsRight.add(clusterShape);
		
		buttonsRight.add(editTreeMats);
		
		buttonsRight.add(editFoliageShape);
		buttonsRight.add(done);
		
		
		for (GuiButton btn : buttonsLeft){
			btn.xPosition = this.width / 2 - (170 + 1);
			btn.yPosition = ((this.height / 2) - 111) + (22 * buttonsLeft.indexOf(btn));
			buttonList.add(btn);
		}
		
		for (GuiButton btn : buttonsRight){
			btn.xPosition = this.width / 2 - (1);
			btn.yPosition = ((this.height / 2) - 111) + (22 * buttonsRight.indexOf(btn));
			buttonList.add(btn);
		}
	}
	
	
	private void ShowEditFoliageShapes() {
		
		this.buttonList.clear();
		
		this.foliageShapes = this.data.GetFoliageShapes();
		
		System.out.println("Clearing Scroll Buttons");
		scrollpane1.clearButtons();
		scrollpane1.setX(this.width/2 - 85);
		scrollpane1.setY(this.height/2 - 100);
		
		scrollpane1.setClip(true);
		
		System.out.println("Adding Buttons");
		for(int i = 0; i < this.foliageShapes.size(); i++){
			scrollpane1.addButton(new GuiButton(i, 0, 21*i, 170, 20, this.foliageShapes.get(i).toString()));
		}
		
		scrollpane1.setContentHeight(scrollpane1.GetButtons().size() * 21);
		
		addToPane.xPosition = this.width/2 - 85;
		addToPane.yPosition = this.height/2 + 64;
		addToPane.width = 170;
		addToPane.height = 20;
		addToPane.displayString = "Add New Shape";
        buttonList.add(addToPane);
		
        done.xPosition = this.width/2 - 85;
        done.yPosition = this.height/2 + 64 + 21;
        done.width = 170;
        done.height = 20;
        buttonList.add(done);
        
	}
	
	
	private void EditFloatArray(){
		
		this.buttonList.clear();
		
		scrollpane2.clearButtons();
		scrollpane2.setX(this.width/2 - 85);
		scrollpane2.setY(this.height/2 - 100);
		
		scrollpane2.setClip(true);
		
		for(int i = 0; i < this.foliageShapes.get(currentFloatArray).size(); i++){
			//System.out.println(this.foliageShapes.get(currentFloatArray).get(i));
			GuiSlider slider = new GuiSlider(i, 0, 21*i, 170, 20, "Size: ", " Blocks", 1.0f, 64.0f, this.foliageShapes.get(currentFloatArray).get(i), true, true);
			slider.precision = 3;
			slider.updateSlider();
			//slider.setValue(this.foliageShapes.get(currentFloatArray).get(i));
			scrollpane2.addButton(slider);
		}
		
		scrollpane2.setContentHeight(scrollpane2.GetButtons().size() * 21);
		
		addToPane.xPosition = this.width/2 - 85;
		addToPane.yPosition = this.height/2 + 64;
		addToPane.width = 170;
		addToPane.height = 20;
		addToPane.displayString = "Add New Layer";
        buttonList.add(addToPane);
		
        done.xPosition = this.width/2 - 85;
        done.yPosition = this.height/2 + 64 + 21;
        done.width = 170;
        done.height = 20;
        buttonList.add(done);
		
	}
	
	
	protected void actionPerformed(GuiButton button, int mouseButton){
		data.SetTreeHeight(treeheight.getValueInt());
		data.SetTrunkBottom(trunkRadBot.getValueInt());
		data.SetTrunkMiddle(trunkRadMid.getValueInt());
		data.SetTrunkTop(trunkRadTop.getValueInt());
		data.SetTrunkHeight((float) trunkHeight.getValue());
		data.SetTrunkMidPoint((float) trunkMidPoint.getValue());
		data.SetBranchStart((float) branchStart.getValue());
		data.SetFoliageStart((float) foliageStart.getValue());
		data.SetBranchSlope(branchSlope.getValue());
		data.SetLeafDensity(leafDensity.getValue());
		data.SetBranchDensity(branchDensity.getValue());
		data.SetFoliageShapes(foliageShapes);
		data.SetScaleWidth(widthScale.getValue());
		data.trunkWallThickness = trunkThickness.getValueInt();
		
		if(button == hollowTrunk){
			data.hollowTrunk = !data.hollowTrunk;
		}
		
		if(button == clusterShape){
			if(data.clusterShape == 0){
				data.clusterShape = 1;
			}
			else{
				data.clusterShape = 0;
			}
			//initGui();
		}
		
		if(button == editTreeMats){
			this.mc.displayGuiScreen(new GUISetLogMat(player));
		}
		
		if(button == editFoliageShape){
			page = 1;
			//initGui();
		}
		
		if(button == addToPane){
			if(page == 1){
				System.out.println(this.foliageShapes.size());
				this.foliageShapes.add(new ArrayList<Float>());
				//initGui();
			}
			else if(page == 2){
				this.foliageShapes.get(currentFloatArray).add(0.0f);
				//initGui();
			}
		}
		
		if(button == done){
			if(page == 1){
				page = 0;
				//initGui();
			}
			else if(page == 2){
				for (GuiButton guiButton : scrollpane2.GetButtons()) {
					float n = Math.round(((GuiSlider)(guiButton)).getValue()*100)/100.f;
					this.foliageShapes.get(currentFloatArray).set((guiButton.id), n);
				}
				page = 1;
				//initGui();
			}
			else if(page == 0){				
				PacketDispatcher.sendToServer(new SendTreeDataToItem(data));
				this.mc.thePlayer.closeScreen();
			}
		}
		initGui();
	}
	
	@Override
	public void ScrollButtonPressed(GuiButton button) {
		System.out.println("Got Button");
		if(page == 1){
			if(isCtrlKeyDown()){
				System.out.println("Removing Shape");
				this.foliageShapes.remove(button.id);
				initGui();
			}
			else{
				currentFloatArray = button.id;
				page = 2;
				initGui();
			}
		}
		if(page == 2){
			if(isCtrlKeyDown()){
				this.foliageShapes.get(currentFloatArray).remove(button.id);
				initGui();
			}
		}
	}
	
	@Override
	public void GetGuiSliderValue(GuiSlider slider){
		if(page == 2){
			float n = Math.round((slider).getValue()*100)/100.f;
			System.out.println(n);
			if(slider.id < this.foliageShapes.get(currentFloatArray).size())
				this.foliageShapes.get(currentFloatArray).set((slider.id), n);
			else
				System.out.println("Error setting foliage layer size slider. This should fix it's self.");
		}
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
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
                selectedButton = event.getButton();
                event.getButton().playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(event.getButton(), mouseButton);
                if (this.equals(this.mc.currentScreen))
                    MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
            }
        }
        
        if(page == 1 && scrollpane1 != null)
        	scrollpane1.onMouseClick(mouseX, mouseY, mouseButton);
        else if(page == 2 && scrollpane2 != null)
        	scrollpane2.onMouseClick(mouseX, mouseY, mouseButton);
    }
	
	/**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    @Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
        
        if(page == 1 && scrollpane1 != null)
        	scrollpane1.onMouseBtnReleased(mouseX, mouseY, state);
        else if(page == 2 && scrollpane2 != null)
        	scrollpane2.onMouseBtnReleased(mouseX, mouseY, state);
    }
    
    @Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    	if(page == 1 && scrollpane1 != null)
        	scrollpane1.onMouseMoved(mouseX, mouseY);
        else if(page == 2 && scrollpane2 != null)
        	scrollpane2.onMouseMoved(mouseX, mouseY);
	}
    
    @Override
	public void handleMouseInput() {
		try {
			super.handleMouseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int wheelState = Mouse.getEventDWheel();
		if(page == 1 && scrollpane1 != null)
			scrollpane1.onMouseWheel(wheelState);
		else if(page == 2 && scrollpane2 != null)
			scrollpane2.onMouseWheel(wheelState);
	}
}
