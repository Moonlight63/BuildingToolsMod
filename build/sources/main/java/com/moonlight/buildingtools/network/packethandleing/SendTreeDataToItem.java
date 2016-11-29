package com.moonlight.buildingtools.network.packethandleing;

import java.util.ArrayList;
import java.util.List;

import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.filtertool.ProceduralTreeData;
import com.moonlight.buildingtools.items.tools.filtertool.ToolFilter;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SendTreeDataToItem implements IMessage {
	
	ProceduralTreeData data;
	
	public SendTreeDataToItem(){}
	
	public SendTreeDataToItem(ProceduralTreeData data){
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = new ProceduralTreeData();
		List<List<Float>> tempFloat = new ArrayList<List<Float>>();
		int shapeCount = buf.readInt();
		for(int i = 0; i < shapeCount; i++){
			List<Float> tempFloats = new ArrayList<Float>();
			int shapeValues = buf.readInt();
			for(int k = 0; k < shapeValues; k++){
				tempFloats.add(buf.readFloat());
			}
			tempFloat.add(tempFloats);
		}
		data.SetFoliageShapes(tempFloat);
		
		data.SetTreeHeight(buf.readInt());
		data.SetTrunkBottom(buf.readInt());
		data.SetTrunkMiddle(buf.readInt());
		data.SetTrunkTop(buf.readInt());
		data.SetTrunkWallThickness(buf.readInt());
		data.SetTrunkHeight(buf.readFloat());
		data.SetTrunkMidPoint(buf.readFloat());
		data.SetBranchStart(buf.readFloat());
		data.SetFoliageStart(buf.readFloat());
		data.SetBranchSlope(buf.readDouble());
		data.SetLeafDensity(buf.readDouble());
		data.SetBranchDensity(buf.readDouble());
		data.SetScaleWidth(buf.readDouble());
		data.SetMatValues(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
		data.SetHollowTrunk(buf.readBoolean());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.data.GetFoliageShapes().size());
		for(int i = 0; i < data.GetFoliageShapes().size(); i++){
			buf.writeInt(data.GetFoliageShapes().get(i).size());
			for(int k = 0; k < data.GetFoliageShapes().get(i).size(); k++){
				buf.writeFloat(data.GetFoliageShapes().get(i).get(k));
			}
		}
		
		buf.writeInt(data.GetTreeHeight());
		buf.writeInt(data.GetTrunkBottom());
		buf.writeInt(data.GetTrunkMiddle());
		buf.writeInt(data.GetTrunkTop());
		buf.writeInt(data.GetTrunkWallThickness());
		buf.writeFloat(data.GetTrunkHeight());
		buf.writeFloat(data.GetTrunkMidPoint());
		buf.writeFloat(data.GetBranchStart());
		buf.writeFloat(data.GetFoliageStart());
		buf.writeDouble(data.GetBranchSlope());
		buf.writeDouble(data.GetLeafDensity());
		buf.writeDouble(data.GetBranchDensity());
		buf.writeDouble(data.GetScaleWidth());
		buf.writeInt(data.GetMatValues()[0]);
		buf.writeInt(data.GetMatValues()[1]);
		buf.writeInt(data.GetMatValues()[2]);
		buf.writeInt(data.GetMatValues()[3]);
		buf.writeBoolean(data.GetHollowTrunk());
	}
	
	
	public static class Handler extends AbstractServerMessageHandler<SendTreeDataToItem>{

		@Override
		public IMessage handleServerMessage(EntityPlayer player, SendTreeDataToItem message, MessageContext ctx) {
			//System.out.println(player);
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ToolFilter){
				((ToolFilter)player.getHeldItemMainhand().getItem()).SetTreeData(message.data);;
			}
			return null;
		}	
		
	}	
}
