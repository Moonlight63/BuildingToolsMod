package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.utils.RGBA;

@SideOnly(Side.CLIENT)
public final class ContainerBlockSelMenu extends Container{
	
	private static InventoryBasic blocksINV = new InventoryBasic("tmp", true, 45);
	
    public static List<ItemStack> itemList = Lists.<ItemStack>newArrayList();

    public ContainerBlockSelMenu(){

        for (int i = 0; i < 5; ++i){
            for (int j = 0; j < 9; ++j){
                this.addSlotToContainer(new CustomSlot(blocksINV, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }

        ContainerBlockSelMenu.scrollTo(0.0F);
    }

    public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

    /**
     * Updates the gui slots ItemStack's based on scroll position.
     */
    public static void scrollTo(float p_148329_1_){
        int i = (ContainerBlockSelMenu.itemList.size() + 9 - 1) / 9 - 5;
        int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);

        if (j < 0){
            j = 0;
        }

        for (int k = 0; k < 5; ++k){
            for (int l = 0; l < 9; ++l){
                int i1 = l + (k + j) * 9;

                if (i1 >= 0 && i1 < ContainerBlockSelMenu.itemList.size()){
                    blocksINV.setInventorySlotContents(l + k * 9, (ItemStack)ContainerBlockSelMenu.itemList.get(i1));
                }
                else{
                    blocksINV.setInventorySlotContents(l + k * 9, (ItemStack)null);
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
            //int j1 = this.xDisplayPosition;
            //int k1 = this.yDisplayPosition;
            GlStateManager.colorMask(true, true, true, false);
            //ContainerBlockSelMenu.drawRect(j1, k1, j1, k1, RGBA.Red.setAlpha(50));
            //this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
			
			// TODO Auto-generated constructor stub
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
//	        if (left < right)
//	        {
//	            int i = left;
//	            left = right;
//	            right = i;
//	        }
//	
//	        if (top < bottom)
//	        {
//	            int j = top;
//	            top = bottom;
//	            bottom = j;
//	        }
	
	        Tessellator tessellator = Tessellator.getInstance();
	        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
	        GlStateManager.enableBlend();
	        GlStateManager.disableTexture2D();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        GlStateManager.color((float)bgColor.red / 255f, (float)bgColor.green / 255f, (float)bgColor.blue / 255f, (float)bgColor.alpha / 255f);
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