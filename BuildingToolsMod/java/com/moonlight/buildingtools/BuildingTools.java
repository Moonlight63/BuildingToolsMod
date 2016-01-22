package com.moonlight.buildingtools;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.moonlight.buildingtools.creativetab.CreativeTabMain;
import com.moonlight.buildingtools.helpers.DrawBlockHighlightEventHandler;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.loaders.ItemLoader;
import com.moonlight.buildingtools.items.tools.BlockChangerGuiHelper;
import com.moonlight.buildingtools.items.tools.tasks.BlockChangeTask;
import com.moonlight.buildingtools.items.tools.tasks.ChangeQueueTask;
import com.moonlight.buildingtools.items.tools.tasks.ForgeSchedulerService;
import com.moonlight.buildingtools.network.CommonProxy;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.utils.KeyBindsHandler;

@Mod(modid = Reference.MODID, version = Reference.VERSION)
public class BuildingTools
{
	public static CreativeTabs tabBT = new CreativeTabMain(CreativeTabs.getNextID(), "buildingToolsMainTab");
	
	@SidedProxy(clientSide = "com.moonlight.buildingtools.network.ClientProxy", serverSide = "com.moonlight.buildingtools.network.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(Reference.MODID)
    public static BuildingTools instance;
	
	public static ForgeSchedulerService scheduler;
    public static PlayerRegistry playerregistry;
    
    public static File clipboardSaveDir;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	
    	proxy.preinit(event);
    	
    	UpdateHandler.init();
    	
    	ItemLoader.loadItems();
    	BlockLoader.loadBlocks();
    	
    	if (FMLCommonHandler.instance().getSide().isClient()){
    		MinecraftForge.EVENT_BUS.register(new KeyBindsHandler());
    		MinecraftForge.EVENT_BUS.register(new DrawBlockHighlightEventHandler());
            KeyBindsHandler.init();
            
        }
    	
    	System.out.println("Making directory");
    	
    	try{
	    	clipboardSaveDir = new File(event.getModConfigurationDirectory().getParent().concat("/BuildingToolsSaves"));
	    	System.out.println(clipboardSaveDir.exists());
	    	if(!clipboardSaveDir.exists()){
	    		System.out.println(clipboardSaveDir.mkdir());
	    	}
	    	System.out.println(clipboardSaveDir);
    	}
    	catch(Exception e){
    		System.out.println(e);
    	}
    	
    	PacketDispatcher.registerPackets();
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){    	
    	proxy.init(event);
    	proxy.registerRenderInformation();
    	
    	
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	
    	if(FMLCommonHandler.instance().getSide().isClient())
    		MinecraftForge.EVENT_BUS.register(new BlockChangerGuiHelper(Minecraft.getMinecraft()));
    	
    	scheduler = new ForgeSchedulerService();
    	scheduler.init();
    	scheduler.startAsynchronousTask(new ChangeQueueTask(), 1000);
    	scheduler.startSynchronousTask(new BlockChangeTask(), 0);
    	
    	playerregistry = proxy.getPlayerRegistry();
    	playerregistry.init();
    	
    	
    	
    	
    }
    
    
    public static PlayerRegistry getPlayerRegistry(){
    	if(playerregistry != null){
    		return playerregistry;
    	}else{
    		playerregistry = proxy.getPlayerRegistry();
        	playerregistry.init();
        	return playerregistry;
    	}
    }
}