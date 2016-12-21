package com.moonlight.buildingtools.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.UpdateHandler;
import com.moonlight.buildingtools.items.tools.undoTool.ThreadSaveUndoList;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldEventHandler{	
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
		event.player.addChatMessage(new TextComponentString(UpdateHandler.updateStatus));
	}
	
	@SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedInEvent event)
    {
		Optional<PlayerWrapper> s = BuildingTools.getPlayerRegistry().getPlayer(event.player.getName());
		if(s.isPresent()){
			
		}
    }
	
	@SubscribeEvent
    public void onSpawn(PlayerEvent.PlayerLoggedOutEvent event)
    {
		Optional<PlayerWrapper> s = BuildingTools.getPlayerRegistry().getPlayer(event.player.getName());
		if(s.isPresent()){
			s.get().addPending(new ThreadSaveUndoList(event.player, event.player.getName() + "." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
			BuildingTools.getPlayerRegistry().invalidate(s.get().getName());
		}
    }
	
	
	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
		BuildingTools.scheduler.onTick();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;			
		if (world != null && player != null){
			RayTracing.instance().fire(4, true);
			RayTraceResult target = RayTracing.instance().getTarget();
			
			if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK){
				
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event){
		
	}
	
	@SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
		
    }
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event){

	}
		
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

}
