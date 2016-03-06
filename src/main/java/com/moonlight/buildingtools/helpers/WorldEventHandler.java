package com.moonlight.buildingtools.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.UpdateHandler;
import com.moonlight.buildingtools.items.tools.undoTool.ThreadLoadUndo;
import com.moonlight.buildingtools.items.tools.undoTool.ThreadSaveUndoList;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class WorldEventHandler{
	
//	private Minecraft mc = Minecraft.getMinecraft();
	
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
		event.player.addChatMessage(new ChatComponentText(UpdateHandler.updateStatus));
	}
	
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
			s.get().addPending(new ThreadSaveUndoList(event.player, event.player.getName() + "." + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
			//System.out.println("\n\n\n\n"+s.get().getName()+"\n\n\n\n");
			BuildingTools.getPlayerRegistry().invalidate(s.get().getName());
			//BuildingTools.getPlayerRegistry().invalidate(event.player);
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
		
		//event.world.setRainStrength(0);
		//event.world.setThunderStrength(0);
		//event.world.setWorldTime(1600);
		
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;			
		if (world != null && player != null){
			RayTracing.instance().fire(4, true);
			MovingObjectPosition target = RayTracing.instance().getTarget();
			
			if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
				
			}
			
		}
		
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

	}
		
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

}
