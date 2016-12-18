package com.moonlight.buildingtools.items.tools.tapeMeasure;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolTapeMeasure extends ToolBase{
	
	private BlockPos firstPos = BlockPos.ORIGIN;
	
	public ToolTapeMeasure(){
		super();
		setUnlocalizedName("ToolTapeMeasure");
		setRegistryName("tape");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}	
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		if(!worldIn.isRemote){
			if(this.firstPos != BlockPos.ORIGIN){
				System.out.println(firstPos);
				if (Math.abs(this.firstPos.getX() - targetBlock.getX()) != 0) {
					playerIn.addChatMessage(new TextComponentString("X: " + (this.firstPos.getX() - targetBlock.getX())));
				}
				if (Math.abs(this.firstPos.getY() - targetBlock.getY()) != 0) {
					playerIn.addChatMessage(new TextComponentString("Y: " + (this.firstPos.getY() - targetBlock.getY())));
				}
				if (Math.abs(this.firstPos.getZ() - targetBlock.getZ()) != 0) {
					playerIn.addChatMessage(new TextComponentString("Z: " + (this.firstPos.getZ() - targetBlock.getZ())));
				}
				firstPos = BlockPos.ORIGIN;
			}
			else{
				playerIn.addChatMessage(new TextComponentString("Position 1 Set"));
				firstPos = targetBlock;
			}
		}
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }
	
	@Override
	public boolean drawOutline(DrawBlockHighlightEvent event) {
		
		if(renderer == null){
    		renderer = new RenderHelper();
    	}
    	
        if(targetBlock != null){        	
        	renderer.startDraw();
		
			if(firstPos != BlockPos.ORIGIN){
				renderer.addOutlineToBuffer(event.getPlayer(), firstPos, RGBA.Green.setAlpha(150), event.getPartialTicks());
				//RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), firstPos, RGBA.Green.setAlpha(150), 1.5f, event.getPartialTicks());
			}
			if(targetBlock != null){
				renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.White.setAlpha(150), event.getPartialTicks());
				//RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), targetBlock, RGBA.White.setAlpha(150), 1.0f, event.getPartialTicks());
			}
			
			renderer.finalizeDraw();
        }
		return true;
	}
	
}
