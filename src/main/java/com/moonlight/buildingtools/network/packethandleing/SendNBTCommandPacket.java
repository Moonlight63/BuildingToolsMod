package com.moonlight.buildingtools.network.packethandleing;

import com.moonlight.buildingtools.items.tools.ToolBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendNBTCommandPacket implements IMessage {

	NBTTagCompound nbtTagComopound;
	
	public SendNBTCommandPacket(){
	}
	
	public SendNBTCommandPacket(NBTTagCompound compound){
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
	
	public static class Handler extends AbstractServerMessageHandler<SendNBTCommandPacket>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendNBTCommandPacket message, MessageContext ctx) {
			//System.out.print("Returning from onMessege \n");
			
			if(player.getHeldItemMainhand() != null && message.nbtTagComopound != null && player.getHeldItemMainhand().getItem() instanceof ToolBase){
				((ToolBase)player.getHeldItemMainhand().getItem()).ReadNBTCommand(message.nbtTagComopound);
			}
			
//			if(player.getHeldItemMainhand() != null && message.nbtTagComopound != null){
//				player.getHeldItemMainhand().setTagCompound(message.nbtTagComopound);
//				player.getHeldItemMainhand().getItem().getMetadata(0);
//			}
			
			return null;
		}	
	}		
}