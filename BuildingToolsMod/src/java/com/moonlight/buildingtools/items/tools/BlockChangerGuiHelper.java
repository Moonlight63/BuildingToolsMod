package com.moonlight.buildingtools.items.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.FontHelper;
import com.moonlight.buildingtools.items.tools.brushtool.BrushTool;
import com.moonlight.buildingtools.items.tools.buildingtool.BuildingTool;
import com.moonlight.buildingtools.items.tools.filtertool.FilterTool;
import com.moonlight.buildingtools.items.tools.smoothtool.BlockSmoother;

public class BlockChangerGuiHelper extends Gui
{
    private Minecraft mc;
    private RenderItem ri;

    public BlockChangerGuiHelper(Minecraft mc)
    {
        super();

        this.mc = mc;
        
        ri = this.mc.getRenderItem();
    }

    @SubscribeEvent
    public void onGameOverlayRender(RenderGameOverlayEvent event)
    {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        if (!(mc.thePlayer instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) mc.thePlayer;

        if (player == null || !mc.inGameHasFocus || !Minecraft.isGuiEnabled() || mc.gameSettings.showDebugInfo) return;

        if (player.inventory.getCurrentItem() == null/* || !(player.inventory.getCurrentItem().getItem() instanceof BlockChangerBase)*/)
            return;

        
        
        if ((player.inventory.getCurrentItem().getItem() instanceof BrushTool)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	ItemStack source = ItemStack.loadItemStackFromNBT(BrushTool.getNBT(currItem).getCompoundTag("sourceblock"));
        	
        	// Null check prevents NPEs in vanilla renderItemAndEffectIntoGUI for items which drop the wrong thing
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
            ri.renderItemAndEffectIntoGUI(source, 4, 4);
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            String am = "";
	        
	        if(source != null){
	        	am = source.getDisplayName();
	        }else{
	        	am = "no block";
	        }

	        FontHelper.drawItemQuantity(mc.fontRendererObj, 5, 5, am);
	        FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + BrushTool.getNBT(currItem).getInteger("radiusX") + ",  " + BrushTool.getNBT(currItem).getInteger("radiusY") + ",  " + BrushTool.getNBT(currItem).getInteger("radiusZ"));
        }
        
        else if ((player.inventory.getCurrentItem().getItem() instanceof BuildingTool)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + BuildingTool.getNBT(currItem).getInteger("radiusX") + ",  " + BuildingTool.getNBT(currItem).getInteger("radiusZ"));
        }
        
        else if ((player.inventory.getCurrentItem().getItem() instanceof FilterTool)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + FilterTool.getNBT(currItem).getInteger("radiusX") + ",  " + FilterTool.getNBT(currItem).getInteger("radiusY") + ",  " + FilterTool.getNBT(currItem).getInteger("radiusZ") + ", Depth: " +  FilterTool.getNBT(currItem).getInteger("topsoildepth"));
        }
        
        else if ((player.inventory.getCurrentItem().getItem() instanceof BlockSmoother)){
        	ItemStack currItem = player.inventory.getCurrentItem();
	        FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + ((BlockSmoother)currItem.getItem()).getTargetRadius(currItem));
        }
        
        else{
        	return;
        }

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }
}
