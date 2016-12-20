package com.moonlight.buildingtools.items.tools;

import java.util.HashSet;
import java.util.Set;

import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.Key.KeyCode;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolBase extends Item implements IKeyHandler, IOutlineDrawer{
	
	
	public static Set<Key.KeyCode> handledKeys;
	public Set<BlockPos> blocksForOutline;
	public World world;
	public BlockPos targetBlock;
	public EnumFacing targetFace;
	
	public ItemStack thisStack;
	public EntityPlayer currPlayer;
	
	public boolean updateVisualizer = true;
	
	public RenderHelper renderer;
	
	static{
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	public static NBTTagCompound getNBT(ItemStack stack){
		if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){		
		if(this.world == null){
			this.world = world;
		}
		if(this.currPlayer != entity)
			this.currPlayer = (EntityPlayer) entity;
		
		if(this.thisStack != itemstack)
			this.thisStack = itemstack;
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTargetBlock(BlockPos pos, EnumFacing side) {
		this.targetBlock = pos;
		this.targetFace = side;
	}

	@Override
	public void handleKey(EntityPlayer player, ItemStack itemStack, KeyCode key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Key.KeyCode> getHandledKeys() {
		return ToolBase.handledKeys;
	}
	
	@Override
	public int getMetadata(int damage)
    {
		System.out.println("Update Visualization");
		updateVisualizer = true;
        return super.getMetadata(damage);
    }
	
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		
	}

}
