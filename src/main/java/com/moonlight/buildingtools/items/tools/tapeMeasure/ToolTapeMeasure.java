package com.moonlight.buildingtools.items.tools.tapeMeasure;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.RGBA;

public class ToolTapeMeasure extends Item implements IOutlineDrawer{
	
	private BlockPos firstPos = BlockPos.ORIGIN;
	public BlockPos targetBlock;
	public EnumFacing targetFace;
	
	public ToolTapeMeasure(){
		super();
		setUnlocalizedName("tape");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}	
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){
		
		if(world.isRemote){
			RayTracing.instance().fire(1000, true);
			RayTraceResult target = RayTracing.instance().getTarget();
		
			if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK){				
				PacketDispatcher.sendToServer(new SendRaytraceResult(target.getBlockPos(), target.sideHit));
				this.targetBlock = target.getBlockPos();
				this.targetFace = target.sideHit;
			}
			else{
				PacketDispatcher.sendToServer(new SendRaytraceResult(null, null));
				this.targetBlock = null;
				this.targetFace = null;
			}
		}
		
	}
	
	public void setTargetBlock(BlockPos pos, EnumFacing side){
		this.targetBlock = pos;
		this.targetFace = side;
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
		
	public boolean onItemUse(ItemStack stack,
            EntityPlayer playerIn,
            World worldIn,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ){
		
		onItemRightClick(worldIn, playerIn, EnumHand.MAIN_HAND);
		
		return true;
	}

	@Override
	public boolean drawOutline(DrawBlockHighlightEvent event) {
		
		if(firstPos != BlockPos.ORIGIN){
			RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), firstPos, RGBA.Green.setAlpha(150), 1.5f, event.getPartialTicks());
		}
		if(targetBlock != null){
			RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), targetBlock, RGBA.White.setAlpha(150), 1.0f, event.getPartialTicks());
		}
		return true;
	}
	
}
