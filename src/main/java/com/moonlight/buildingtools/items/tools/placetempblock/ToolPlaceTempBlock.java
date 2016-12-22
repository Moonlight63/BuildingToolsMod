package com.moonlight.buildingtools.items.tools.placetempblock;

import java.util.List;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolPlaceTempBlock extends ToolBase{
	
	public ToolPlaceTempBlock(){
		super();
		setUnlocalizedName("ToolPlaceTempBlock");
		setRegistryName("tempblockplacer");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        list.add("Right click to place a temp block at your current location.");
        list.add("");
        list.add("Shift + Right click to place a temp block at the block you are looking at.");
        
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		if(!worldIn.isRemote){
			if(playerIn.isSneaking()){
				if(targetBlock != null){
					worldIn.setBlockState(targetBlock.offset(targetFace), BlockLoader.tempBlock.getDefaultState());
				}
			}
			else
				worldIn.setBlockState(playerIn.getPosition(), BlockLoader.tempBlock.getDefaultState());
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }

	@Override
	public boolean drawOutline(DrawBlockHighlightEvent event) {
		if(targetBlock != null){
			if(renderer == null){
	    		renderer = new RenderHelper();
	    	}
	        renderer.startDraw();
			renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
			renderer.finalizeDraw();
			
		}
		return true;
	}
	
}
