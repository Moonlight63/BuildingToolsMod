package com.moonlight.buildingtools.network.packethandleing;

import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SelectionToolSaveSelectionPacket implements IMessage {
	
	String savename = "";
	int mouseButton = 0;
	boolean ctrlDown = false;
	boolean altDown = false;
	boolean shiftDown = false;
	
	
	public SelectionToolSaveSelectionPacket(){
		
	}
	
	/*public SendGuiButtonPressedToItemMessage(byte buttonID){
		this.buttonID = buttonID;
		this.mouseButton = 0;
		this.ctrlDown
	}*/
	
	public SelectionToolSaveSelectionPacket(String savename, int mouseBtn, boolean ctrl, boolean alt, boolean shift){
		this.savename = savename;
		this.mouseButton = mouseBtn;
		this.ctrlDown = ctrl;
		this.altDown = alt;
		this.shiftDown = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.savename = ByteBufUtils.readUTF8String(buf);
		this.mouseButton = buf.readInt();
		this.ctrlDown = buf.readBoolean();
		this.altDown = buf.readBoolean();
		this.shiftDown = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.savename);
		buf.writeInt(mouseButton);
		buf.writeBoolean(ctrlDown);
		buf.writeBoolean(altDown);
		buf.writeBoolean(shiftDown);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SelectionToolSaveSelectionPacket>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SelectionToolSaveSelectionPacket message, MessageContext ctx) {
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ToolSelection){
				
				((ToolSelection)player.getHeldItem().getItem()).SaveSelectionToFile(message.savename, message.mouseButton, message.ctrlDown, message.altDown, message.shiftDown, player.getHeldItem());
			}
			return null;
		}	
		
	}
}
