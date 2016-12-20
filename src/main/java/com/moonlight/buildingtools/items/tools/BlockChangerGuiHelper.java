package com.moonlight.buildingtools.items.tools;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.helpers.FontHelper;
import com.moonlight.buildingtools.items.tools.brushtool.ToolBrush;
import com.moonlight.buildingtools.items.tools.buildingtool.ToolBuilding;
import com.moonlight.buildingtools.items.tools.filtertool.ToolFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        if (!(mc.thePlayer instanceof EntityPlayer)) return;

        EntityPlayer player = mc.thePlayer;

        if (player == null || !mc.inGameHasFocus || !Minecraft.isGuiEnabled() || mc.gameSettings.showDebugInfo) return;

        if (player.inventory.getCurrentItem() == null/* || !(player.inventory.getCurrentItem().getItem() instanceof BlockChangerBase)*/)
            return;

        
        
        if ((player.inventory.getCurrentItem().getItem() instanceof ToolBrush)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	ItemStack source = new ItemStack(ToolBrush.getNBT(currItem).getCompoundTag("sourceblock"));
        	
        	// Null check prevents NPEs in vanilla renderItemAndEffectIntoGUI for items which drop the wrong thing
            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
            ri.renderItemAndEffectIntoGUI(source, 4, 4);
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            String am = "";
	        
	        am = source.getDisplayName();

	        FontHelper.drawItemQuantity(mc.fontRendererObj, 5, 5, am);
	        FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + ToolBrush.getNBT(currItem).getInteger("radiusX") + ",  " + ToolBrush.getNBT(currItem).getInteger("radiusY") + ",  " + ToolBrush.getNBT(currItem).getInteger("radiusZ"));
        }
        
        else if ((player.inventory.getCurrentItem().getItem() instanceof ToolBuilding)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + ToolBuilding.getNBT(currItem).getInteger("radiusX") + ",  " + ToolBuilding.getNBT(currItem).getInteger("radiusZ"));
        }
        
        else if ((player.inventory.getCurrentItem().getItem() instanceof ToolFilter)){
        	ItemStack currItem = player.inventory.getCurrentItem();
        	FontHelper.renderText(mc.fontRendererObj, 25, 8, 1.0, "Radius: " + ToolFilter.getNBT(currItem).getInteger("radiusX") + ",  " + ToolFilter.getNBT(currItem).getInteger("radiusY") + ",  " + ToolFilter.getNBT(currItem).getInteger("radiusZ") + ", Depth: " +  ToolFilter.getNBT(currItem).getInteger("topsoildepth"));
        }
        
        else{
        	return;
        }

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }
}
