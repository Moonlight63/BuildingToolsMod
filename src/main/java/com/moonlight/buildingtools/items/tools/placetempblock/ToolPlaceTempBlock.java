package com.moonlight.buildingtools.items.tools.placetempblock;

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
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.RGBA;

public class ToolPlaceTempBlock extends Item implements IOutlineDrawer{
	
	public BlockPos targetBlock;
	public EnumFacing targetFace;
	public World world;
	
	public ItemStack thisStack;
	
	private RenderHelper renderer;
	
	public ToolPlaceTempBlock(){
		super();
		setUnlocalizedName("tempblockplacer");
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
			if(playerIn.isSneaking()){
				if(targetBlock != null){
					worldIn.setBlockState(targetBlock.offset(targetFace), BlockLoader.tempBlock.getDefaultState());
				}
			}
			else
				worldIn.setBlockState(playerIn.getPosition(), BlockLoader.tempBlock.getDefaultState());
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
		if(targetBlock != null)
			RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.getPartialTicks());
		return true;
	}
	
}
