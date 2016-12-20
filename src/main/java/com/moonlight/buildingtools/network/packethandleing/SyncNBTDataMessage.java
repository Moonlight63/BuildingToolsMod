package com.moonlight.buildingtools.network.packethandleing;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncNBTDataMessage implements IMessage {

	NBTTagCompound nbtTagComopound;
	
	public SyncNBTDataMessage(){
	}
	
	public SyncNBTDataMessage(NBTTagCompound compound){
		this.nbtTagComopound = compound;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbtTagComopound);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.nbtTagComopound = ByteBufUtils.readTag(buf);
	}
	
	public static class Handler extends AbstractServerMessageHandler<SyncNBTDataMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SyncNBTDataMessage message, MessageContext ctx) {
			//System.out.print("Returning from onMessege \n");
			if(player.getHeldItemMainhand() != null && message.nbtTagComopound != null){
				player.getHeldItemMainhand().setTagCompound(message.nbtTagComopound);
				//player.getHeldItemMainhand().getItem().getMetadata(0);
			}
			
			return null;
		}	
	}		
}