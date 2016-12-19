package com.moonlight.buildingtools.network.packethandleing;

import java.util.List;

import scala.tools.nsc.doc.base.comment.Italic;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.ContainerBlockSelMenu;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.brushtool.ToolBrush;
import com.moonlight.buildingtools.items.tools.filtertool.ToolFilter;
import com.moonlight.buildingtools.items.tools.selectiontool.ToolSelection;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendAdvancedReplacePacketToItemMessage implements IMessage {
	
	List<Integer> replaceBlockID = Lists.<Integer>newArrayList();
	List<Integer> replaceBlockData = Lists.<Integer>newArrayList();
	int count2;
	
	public SendAdvancedReplacePacketToItemMessage(){
		
	}
	
	public SendAdvancedReplacePacketToItemMessage(List<Integer> ID2, List<Integer> META2){
		System.out.println("Creating Message");
		this.replaceBlockID = ID2;
		this.replaceBlockData = META2;
		this.count2 = ID2.size();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.count2 = buf.readInt();
		try {
			
			for(int i = 0; i < count2; i++){
				this.replaceBlockID.add(i, buf.readInt());
			}
			for(int i = 0; i < count2; i++){
				this.replaceBlockData.add(i, buf.readInt());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count2);
		
		for(int i = 0; i < count2; i++){
			buf.writeInt(replaceBlockID.get(i));
		}
		for(int i = 0; i < count2; i++){
			buf.writeInt(replaceBlockData.get(i));
		}
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendAdvancedReplacePacketToItemMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendAdvancedReplacePacketToItemMessage message, MessageContext ctx) {
			System.out.println(player);
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolSelection){
				((ToolSelection)player.getHeldItemMainhand().getItem()).setReplaceBlocks(message.replaceBlockID, message.replaceBlockData);
				System.out.println("Sent message to player");
			}
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolBrush){
				((ToolBrush)player.getHeldItemMainhand().getItem()).setReplaceBlocks(message.replaceBlockID, message.replaceBlockData);
				System.out.println("Sent message to player");
			}
			
			return null;
		}	
		
	}	
}

