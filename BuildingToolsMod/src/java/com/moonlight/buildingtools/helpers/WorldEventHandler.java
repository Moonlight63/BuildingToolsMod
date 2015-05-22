package com.moonlight.buildingtools.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class WorldEventHandler{
	
	@SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedInEvent event)
    {
		Optional<PlayerWrapper> s = BuildingTools.getPlayerRegistry().getPlayer(event.player.getName());
		if(s.isPresent()){
			//System.out.println("\n\n\n\n"+s.get().getName()+"\n\n\n\n");
		}
    }
	
	@SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedOutEvent event)
    {
		Optional<PlayerWrapper> s = BuildingTools.getPlayerRegistry().getPlayer(event.player.getName());
		if(s.isPresent()){
			//System.out.println("\n\n\n\n"+s.get().getName()+"\n\n\n\n");
			BuildingTools.getPlayerRegistry().invalidate(s.get().getName());
		}
    }
	
	
	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
		BuildingTools.scheduler.onTick();
		//BlockFalling.fallInstantly = true;
		
		//BlockFalling.canFallInto(event.world, pos)
		
		//for(Object fallingBlock : event.world.loadedEntityList){
		//	if(fallingBlock instanceof EntityFallingBlock){
		//		((EntityFallingBlock) fallingBlock).setDead();
		//	}
		//}
		
		event.world.setRainStrength(0);
		event.world.setThunderStrength(0);
		event.world.setWorldTime(1600);
		
	}
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event){
		
	}
	
	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
		//System.out.println("WorldEventHandler.onTick()\n");
        
    }
		
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

}
