package com.moonlight.buildingtools.network;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.helpers.WorldEventHandler;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistryProvider;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Pair;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
	
	// In your server proxy (mine is named CommonProxy):
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	
	public void preinit(FMLPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
	}
	
	
	public void init(FMLInitializationEvent event){
		new GuiHandler();
	}
	
	
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
	 return ctx.getServerHandler().playerEntity;
	}
	
	public PlayerRegistry getPlayerRegistry(){
		return new PlayerRegistry(new PlayerRegistryProviderServer());
	}
	
	public void registerRenderInformation() {}		
	
	private class PlayerRegistryProviderServer implements PlayerRegistryProvider{

        /**
         * {@inheritDoc}
         */
        public Optional<Pair<String, PlayerWrapper>> get(String name)
        {
            EntityPlayer player = null;
            for (Object e : MinecraftServer.getServer().getConfigurationManager().playerEntityList)
            {
            	System.out.println("\n\n\n\n" + e + "\n\n\n\n");
                EntityPlayer entity = (EntityPlayer) e;
                if (entity.getName().equals(name))
                {
                    player = entity;
                    break;
                }
            }
            if (player == null)
            {
                return Optional.absent();
            }
            return Optional.of(new Pair<String, PlayerWrapper>(player.getName(), new PlayerWrapper(player)));
        }

    }
	
}
