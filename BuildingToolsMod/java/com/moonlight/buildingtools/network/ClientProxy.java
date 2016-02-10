package com.moonlight.buildingtools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.helpers.loaders.BlockRenderRegister;
import com.moonlight.buildingtools.helpers.loaders.ItemRenderRegister;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistryProvider;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Pair;
//import com.moonlight.buildingtools.buildingGuide.TileEntityGuideRenderer;

public class ClientProxy extends CommonProxy {

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
	 // Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
	 // your packets will not work because you will be getting a client
	 // player even when you are on the server! Sounds absurd, but it's true.

	 // Solution is to double-check side before returning the player:
	 return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}
	
	public PlayerRegistry getPlayerRegistry(){
		return new PlayerRegistry(new PlayerRegistryProviderClient());
	}
	
	@Override
	public void registerRenderInformation(){
		BlockRenderRegister.registerBlockRenderer();
		ItemRenderRegister.registerItemRenderer();
	}
		
	
	private class PlayerRegistryProviderClient implements PlayerRegistryProvider{

        /**
         * {@inheritDoc}
         */
        public Optional<Pair<String, PlayerWrapper>> get(String name)
        {
            EntityPlayer player = null;
            for (Object e : net.minecraft.client.Minecraft.getMinecraft().getIntegratedServer().getConfigurationManager().playerEntityList)
            {
            	//System.out.println("\n\n\n\n" + e + "\n\n\n\n");
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
