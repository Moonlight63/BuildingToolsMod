package com.moonlight.buildingtools.network.packethandleing;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;

public class SendSimpleReplacePacketToItemMessage implements IMessage {
	
	int fillBlockID;
	int fillBlockData;
	int replaceBlockID;
	int replaceBlockData;
	
	
	public SendSimpleReplacePacketToItemMessage(){
		
	}
	
	/*public SendGuiButtonPressedToItemMessage(byte buttonID){
		this.buttonID = buttonID;
		this.mouseButton = 0;
		this.ctrlDown
	}*/
	
	public SendSimpleReplacePacketToItemMessage(int ID, int META, int ID2, int META2){
		System.out.println("Creating Message");
		this.fillBlockID = ID;
		this.fillBlockData = META;
		
		this.replaceBlockID = ID2;
		this.replaceBlockData = META2;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.fillBlockID = buf.readInt();
		this.fillBlockData = buf.readInt();
		
		this.replaceBlockID = buf.readInt();
		this.replaceBlockData = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fillBlockID);
		buf.writeInt(fillBlockData);
		
		buf.writeInt(replaceBlockID);
		buf.writeInt(replaceBlockData);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendSimpleReplacePacketToItemMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendSimpleReplacePacketToItemMessage message, MessageContext ctx) {
			System.out.println(player);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ToolSelection){
				((ToolSelection)player.getHeldItem().getItem()).SimpleReplace(message.fillBlockID, message.fillBlockData, message.replaceBlockID, message.replaceBlockData);
				System.out.println("Sent message to player");
			}
			return null;
		}	
		
	}	
}
