package com.moonlight.buildingtools.items.tools.buildingtool;

import java.io.IOException;

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
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

public class BuildingToolGui extends GuiScreen{
	
	private EntityPlayer player;

	public static final int GUI_ID = 3;
	
	private static final ResourceLocation bgTexture = new ResourceLocation(Reference.MODID.toLowerCase(), "textures/gui/basicGui.png");
	public final int xSizeOfTexture = 176;
	public final int ySizeOfTexture = 88;
	
	//private GuiTextField radiusText;
	//private GuiTextField iterationsText;
	//private GuiTextField sigmaText;
	
	public BuildingToolGui(EntityPlayer player){
		this.player = player;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		this.drawDefaultBackground();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(bgTexture);

		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);
		
		//radiusText.drawTextBox();
		//iterationsText.drawTextBox();
		//sigmaText.drawTextBox();
		
		//radiusText.setText("Radius: " + ((BrushTool)player.getHeldItem().getItem()).getNBT(player.getHeldItem()).getInteger("radius"));
		//iterationsText.setText("Iterations: " + ((BrushTool)player.getHeldItem().getItem()).getNBT(player.getHeldItem()).getInteger("iterations"));
		//sigmaText.setText("Sigma: " + ((BrushTool)player.getHeldItem().getItem()).getNBT(player.getHeldItem()).getInteger("sigma"));
		
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
		
		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		
		NBTTagCompound heldnbt = BuildingTool.getNBT(player.getHeldItem());
		Shapes gen = Shapes.VALUES[heldnbt.getInteger("generator")];
		
		buttonList.add(new GuiButton(1, posX, posY, xSizeOfTexture, 20, (gen.fixedRatio ? "Radius: " : "Radius X: ") + heldnbt.getInteger("radiusX")));
		buttonList.add(new GuiButton(2, posX, posY+25, xSizeOfTexture, 20, (gen.fixedRatio ? "Fixed Ratio: " : "Radius Z: ") + heldnbt.getInteger("radiusZ")));
		
		buttonList.add(new GuiButton(3, posX, posY+50, xSizeOfTexture, 20, "Copy All Blocks? : " + heldnbt.getBoolean("placeAll")));
		
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
		
	}
	
}
