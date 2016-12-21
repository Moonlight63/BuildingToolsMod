package com.moonlight.buildingtools.items.tools;

import java.util.List;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ContainerBlockSelMenu extends Container{
	
	private static InventoryBasic blocksINV = new InventoryBasic("tmp", true, 45);
    public static List<ItemStack> itemList = Lists.<ItemStack>newArrayList();
    
    public static int gridX = 9;
    public static int gridY = 5;
    
    public static int startX = 9;
    public static int startY = 18;
    
    public static int space = 18;

    public ContainerBlockSelMenu(){

        for (int i = 0; i < gridY; ++i){
            for (int j = 0; j < gridX; ++j){
                this.addSlotToContainer(new CustomSlot(blocksINV, i * gridX + j, startX + j * space, startY + i * space));
            }
        }

        ContainerBlockSelMenu.scrollTo(0.0F);
    }

    @Override
	public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

    /**
     * Updates the gui slots ItemStack's based on scroll position.
     */
    public static void scrollTo(float scroll){
    	
        int i = (ContainerBlockSelMenu.itemList.size() + gridX - 1) / gridX - gridY;
        int j = (int)(scroll * i + 0.5D);

        if (j < 0){
            j = 0;
        }

        for (int k = 0; k < gridY; ++k){
            for (int l = 0; l < gridX; ++l){
                int i1 = l + (k + j) * gridX;

                if (i1 >= 0 && i1 < ContainerBlockSelMenu.itemList.size()){
                    blocksINV.setInventorySlotContents(l + k * gridX, ContainerBlockSelMenu.itemList.get(i1));
                }
                else{
                    blocksINV.setInventorySlotContents(l + k * gridX, new ItemStack((Item)null));
                }
            }
        }
    }
    
    
    public class CustomSlot extends Slot{
    	
    	private RGBA bgColor = RGBA.White.setAlpha(0);

		public CustomSlot(IInventory inventoryIn, int index, int xPosition,
				int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
			
		}
		
		public void setColor (RGBA color){
			bgColor = color;
		}
		
		public boolean getColorSet (){
			return bgColor == RGBA.White.setAlpha(0);
		}
		
		public void clearColor(){
			if(bgColor != RGBA.White.setAlpha(0))
				bgColor = RGBA.White.setAlpha(0);
		}
		
		/**
	     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
	     */
	    public void drawRect(int guiLeft, int guiTop)
	    {	
	        Tessellator tessellator = Tessellator.getInstance();
	        VertexBuffer worldrenderer = tessellator.getBuffer();
	        GlStateManager.enableBlend();
	        GlStateManager.disableTexture2D();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.color(bgColor.red / 255f, bgColor.green / 255f, bgColor.blue / 255f, bgColor.alpha / 255f);
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
	        worldrenderer.pos((double)this.xDisplayPosition + guiLeft, (double)this.yDisplayPosition + guiTop + 16, 0.0D).endVertex();
	        worldrenderer.pos((double)this.xDisplayPosition + guiLeft + 16, (double)this.yDisplayPosition + guiTop + 16, 0.0D).endVertex();
	        worldrenderer.pos((double)this.xDisplayPosition + guiLeft + 16, (double)this.yDisplayPosition + guiTop, 0.0D).endVertex();
	        worldrenderer.pos((double)this.xDisplayPosition + guiLeft, (double)this.yDisplayPosition + guiTop, 0.0D).endVertex();
	        tessellator.draw();
	        GlStateManager.enableTexture2D();
	        GlStateManager.disableBlend();
	    }
    	
    }
    
    
    
}