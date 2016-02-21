package com.moonlight.buildingtools.network.packethandleing;

import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendGuiButtonPressedToItemMessage implements IMessage {
	
	byte buttonID = 0;
	int mouseButton = 0;
	boolean ctrlDown = false;
	boolean altDown = false;
	boolean shiftDown = false;
	
	
	public SendGuiButtonPressedToItemMessage(){
		
	}
	
	/*public SendGuiButtonPressedToItemMessage(byte buttonID){
		this.buttonID = buttonID;
		this.mouseButton = 0;
		this.ctrlDown
	}*/
	
	public SendGuiButtonPressedToItemMessage(byte buttonID, int mouseBtn, boolean ctrl, boolean alt, boolean shift){
		this.buttonID = buttonID;
		this.mouseButton = mouseBtn;
		this.ctrlDown = ctrl;
		this.altDown = alt;
		this.shiftDown = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.buttonID = buf.readByte();
		this.mouseButton = buf.readInt();
		this.ctrlDown = buf.readBoolean();
		this.altDown = buf.readBoolean();
		this.shiftDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(buttonID);
		buf.writeInt(mouseButton);
		buf.writeBoolean(ctrlDown);
		buf.writeBoolean(altDown);
		buf.writeBoolean(shiftDown);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendGuiButtonPressedToItemMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendGuiButtonPressedToItemMessage message, MessageContext ctx) {
			//System.out.println(player);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IGetGuiButtonPressed){
				
				((IGetGuiButtonPressed)player.getHeldItem().getItem()).GetGuiButtonPressed(message.buttonID, message.mouseButton, message.ctrlDown, message.altDown, message.shiftDown, player.getHeldItem());
			}
			return null;
		}	
		
	}	
}
