package com.moonlight.buildingtools.network.packethandleing;

import java.util.List;

import scala.tools.nsc.doc.base.comment.Italic;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.ContainerBlockSelMenu;
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

public class SendAdvancedFillPacketToItemMessage implements IMessage {
	
	List<Integer> fillBlockID = Lists.<Integer>newArrayList();
	List<Integer> fillBlockData = Lists.<Integer>newArrayList();
	List<Integer> fillChance = Lists.<Integer>newArrayList();
	int count;
	
	
	public SendAdvancedFillPacketToItemMessage(){
		
	}
	
	public SendAdvancedFillPacketToItemMessage(List<Integer> ID, List<Integer> META, List<Integer> CHANCE){
		System.out.println("Creating Message");
		System.out.println(ID + "   " + META + "   " + CHANCE);
		this.fillBlockID = ID;
		this.fillBlockData = META;
		this.fillChance = CHANCE;
		this.count = ID.size();
		System.out.println("COUNT = " + ID.size());
		System.out.println(fillBlockID + "   " + fillBlockData + "   " + fillChance);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.count = buf.readInt();
		
		try {
			for(int i = 0; i < count; i++){
				this.fillBlockID.add(i, buf.readInt());
			}
			for(int i = 0; i < count; i++){
				this.fillBlockData.add(i, buf.readInt());
			}
			for(int i = 0; i < count; i++){
				this.fillChance.add(i, buf.readInt());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(fillBlockID + "   " + fillBlockData + "   " + fillChance);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(count);
		for(int i = 0; i < count; i++){
			buf.writeInt(fillBlockID.get(i));
		}
		for(int i = 0; i < count; i++){
			buf.writeInt(fillBlockData.get(i));
		}
		for(int i = 0; i < count; i++){
			buf.writeInt(fillChance.get(i));
		}
		System.out.println(fillBlockID + "   " + fillBlockData + "   " + fillChance);
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendAdvancedFillPacketToItemMessage>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendAdvancedFillPacketToItemMessage message, MessageContext ctx) {
			System.out.println(player);
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolSelection){
				((ToolSelection)player.getHeldItemMainhand().getItem()).AdvancedFill(message.fillBlockID, message.fillBlockData, message.fillChance);
				System.out.println("Sent message to player");
			}
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolBrush){
				((ToolBrush)player.getHeldItemMainhand().getItem()).setFillBlocks(message.fillBlockID, message.fillBlockData, message.fillChance);
				System.out.println("Sent message to player");
			}
			return null;
		}	
		
	}	
}
