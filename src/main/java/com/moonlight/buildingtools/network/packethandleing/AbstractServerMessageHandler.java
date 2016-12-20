package com.moonlight.buildingtools.network.packethandleing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

//class for messages sent to and handled on the server:
public abstract class AbstractServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>{
	// implementing a final version of the client message handler both prevents it from
	// appearing automatically and prevents us from ever accidentally overriding it	
	@Override
	public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
		return null;
	}
}
