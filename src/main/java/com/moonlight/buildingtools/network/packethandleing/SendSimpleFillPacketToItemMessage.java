package com.moonlight.buildingtools.network.packethandleing;

import scala.tools.nsc.doc.base.comment.Italic;

import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.brushtool.ToolBrush;
import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendSimpleFillPacketToItemMessage implements IMessage {
	
	int fillBlockID;
	int fillBlockData;
	
	
	public SendSimpleFillPacketToItemMessage(){
		
	}
	
	/*public SendGuiButtonPressedToItemMessage(byte buttonID){
		this.buttonID = buttonID;
		this.mouseButton = 0;
		this.ctrlDown
	}*/
	
	public SendSimpleFillPacketToItemMessage(int ID, int META){
		System.out.println("Creating Message");
		this.fillBlockID = ID;
		this.fillBlockData = META;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.fillBlockID = buf.readInt();
		this.fillBlockData = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fillBlockID);
		buf.writeInt(fillBlockData);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendSimpleFillPacketToItemMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendSimpleFillPacketToItemMessage message, MessageContext ctx) {
			System.out.println(player);
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolSelection){
				((ToolSelection)player.getHeldItemMainhand().getItem()).SimpleFill(message.fillBlockID, message.fillBlockData);
				System.out.println("Sent message to player");
			}
//			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolBrush){
//				((ToolBrush)player.getHeldItemMainhand().getItem()).SimpleFill(message.fillBlockID, message.fillBlockData);
//				System.out.println("Sent message to player");
//			}
			return null;
		}	
		
	}	
}
