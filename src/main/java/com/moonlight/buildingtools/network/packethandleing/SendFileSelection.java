package com.moonlight.buildingtools.network.packethandleing;

import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;
import com.moonlight.buildingtools.items.tools.undoTool.ToolUndo;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendFileSelection implements IMessage {
	
	String file;
	
	public SendFileSelection(){
		
	}
	
	public SendFileSelection(String file){
		this.file = file;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.file = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.file);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendFileSelection>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendFileSelection message, MessageContext ctx) {
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolSelection){
				((ToolSelection)player.getHeldItemMainhand().getItem()).LoadSelectionFromFile(message.file, player.getHeldItemMainhand());
			}
			
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolUndo){
				((ToolUndo)player.getHeldItemMainhand().getItem()).loadUndos(message.file);
			}
			return null;
		}	
		
	}
}
