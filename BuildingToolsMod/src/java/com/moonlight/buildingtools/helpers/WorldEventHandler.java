package com.moonlight.buildingtools.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.IToolOverrideHitDistance;
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
	
	private boolean checkflag = true;
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.phase == TickEvent.Phase.START){
			if(checkflag){
				if(event.player.getCurrentEquippedItem()!=null){
					if(event.player.getCurrentEquippedItem().getItem() instanceof IToolOverrideHitDistance){
						System.out.println("CurrentItemIsTool");
						System.out.println(event.side);
						if(event.side == Side.SERVER)
							checkflag = false;
						BuildingTools.proxy.setExtraReach(event.player, 200);
					}
				}
			}
			else{
				if(event.player.getCurrentEquippedItem()!=null){
					if(!(event.player.getCurrentEquippedItem().getItem() instanceof IToolOverrideHitDistance)){
						System.out.println("NoToolFound");
						System.out.println(event.side);
						if(event.side == Side.SERVER)
							checkflag = true;
						BuildingTools.proxy.setExtraReach(event.player, 0);
					}
				}
				else{
					System.out.println("NoItem");
						System.out.println(event.side);
						if(event.side == Side.SERVER)
							checkflag = true;
						BuildingTools.proxy.setExtraReach(event.player, 0);
				}
			}
		}
	}
		
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

}
