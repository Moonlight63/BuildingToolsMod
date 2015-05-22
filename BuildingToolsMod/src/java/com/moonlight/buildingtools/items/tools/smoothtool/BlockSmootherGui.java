package com.moonlight.buildingtools.items.tools.smoothtool;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.Reference;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendGuiButtonPressedToItemMessage;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class BlockSmootherGui extends GuiScreen{
	
	private EntityPlayer player;

	public static final int GUI_ID = 1;
	
	private static final ResourceLocation bgTexture = new ResourceLocation(Reference.MODID.toLowerCase(), "textures/gui/basicGui.png");
	public final int xSizeOfTexture = 176;
	public final int ySizeOfTexture = 88;
	
	private GuiTextField radiusText;
	private GuiTextField iterationsText;
	private GuiTextField sigmaText;
	
	public BlockSmootherGui(EntityPlayer player){
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
		
		radiusText.drawTextBox();
		iterationsText.drawTextBox();
		sigmaText.drawTextBox();
		
		radiusText.setText("Radius: " + BlockSmoother.getNBT(player.getHeldItem()).getInteger("radius"));
		iterationsText.setText("Iterations: " + BlockSmoother.getNBT(player.getHeldItem()).getInteger("iterations"));
		sigmaText.setText("Sigma: " + BlockSmoother.getNBT(player.getHeldItem()).getInteger("sigma"));
		
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
		
		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		
		/*buttonList.add(new GuiButton(1, this.width / 2 - 12 - 29, posY + 4, 24, 20, "-"));
		radiusText = new GuiTextField(0, fontRendererObj, this.width / 2 - 12, posY + 5, 24, 18);
		radiusText.setText("" + ((BlockSmoother)player.getHeldItem().getItem()).getNBT(player.getHeldItem()).getInteger("radius"));
		buttonList.add(new GuiButton(0, this.width / 2 - 12 + 29, posY + 4, 24, 20, "+"));*/
		
		buttonList.add(new GuiButton(0, posX + 5, posY + 4, 24, 20, "-"));
		radiusText = new GuiTextField(0, fontRendererObj, posX + 34, posY + 5, 108, 18);
		buttonList.add(new GuiButton(1, posX + 147, posY + 4, 24, 20, "+"));
		
		buttonList.add(new GuiButton(2, posX + 5, posY + 28, 24, 20, "-"));
		iterationsText = new GuiTextField(0, fontRendererObj, posX + 34, posY + 29, 108, 18);
		buttonList.add(new GuiButton(3, posX + 147, posY + 28, 24, 20, "+"));
		
		buttonList.add(new GuiButton(4, posX + 5, posY + 52, 24, 20, "-"));
		sigmaText = new GuiTextField(0, fontRendererObj, posX + 34, posY + 53, 108, 18);
		buttonList.add(new GuiButton(5, posX + 147, posY + 52, 24, 20, "+"));
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
