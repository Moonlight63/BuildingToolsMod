package com.moonlight.buildingtools.network.packethandleing;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.moonlight.buildingtools.utils.IOutlineDrawer;

public class SendRaytraceResult implements IMessage {
	
	BlockPos target;
	EnumFacing face;
	boolean setNull;
	
	
	public SendRaytraceResult(){
		this.target = null;
		this.face = null;
	}
	
	public SendRaytraceResult(BlockPos pos, EnumFacing side){
		this.target = pos;
		this.face = side;
		this.setNull = (pos == null && side == null);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.setNull = buf.readBoolean();
		if(!this.setNull){
			this.target = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
			this.face = EnumFacing.getFront(buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(setNull);
		if(!setNull){
			buf.writeInt(this.target.getX());
			buf.writeInt(this.target.getY());
			buf.writeInt(this.target.getZ());
			buf.writeInt(this.face.getIndex());
		}
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendRaytraceResult>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendRaytraceResult message, MessageContext ctx) {
			//System.out.println(player);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IOutlineDrawer){
				
				((IOutlineDrawer)player.getHeldItem().getItem()).setTargetBlock(message.target, message.face);
			}
			return null;
		}	
		
	}	
}
