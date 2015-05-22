package com.moonlight.buildingtools;

import com.moonlight.buildingtools.buildingGuide.BlockGuide;
import com.moonlight.buildingtools.creativetab.CreativeTabMain;
import com.moonlight.buildingtools.helpers.DrawBlockHighlightEventHandler;
import com.moonlight.buildingtools.helpers.loaders.BlockRenderRegister;
import com.moonlight.buildingtools.helpers.loaders.ItemLoader;
import com.moonlight.buildingtools.helpers.loaders.ItemRenderRegister;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.loaders.RegisterHelper;
import com.moonlight.buildingtools.items.tools.BlockChangerGuiHelper;
import com.moonlight.buildingtools.items.tools.tasks.BlockChangeTask;
import com.moonlight.buildingtools.items.tools.tasks.ChangeQueueTask;
import com.moonlight.buildingtools.items.tools.tasks.ForgeSchedulerService;
import com.moonlight.buildingtools.network.CommonProxy;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.utils.KeyBindsHandler;
import com.google.common.base.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	
    	proxy.preinit(event);
    	
    	ItemLoader.loadItems();
    	BlockLoader.loadBlocks();
    	
    	MinecraftForge.EVENT_BUS.register(new DrawBlockHighlightEventHandler());
    	FMLCommonHandler.instance().bus().register(new KeyBindsHandler());
    	//PacketHandler.instance.init();
    	if (FMLCommonHandler.instance().getSide().isClient()){
            KeyBindsHandler.init();
        }
    	
    	PacketDispatcher.registerPackets();
    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	//NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
    	
    	//PacketKeyPressed.init();
    	
    	proxy.init(event);
    	
    	proxy.registerRenderInformation();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	
    	//PacketHandler.instance.postInit();
    	
    	if(FMLCommonHandler.instance().getSide().isClient())
    		MinecraftForge.EVENT_BUS.register(new BlockChangerGuiHelper(Minecraft.getMinecraft()));
    	
    	scheduler = new ForgeSchedulerService();
    	scheduler.init();
    	scheduler.startAsynchronousTask(new ChangeQueueTask(), 1000);
    	scheduler.startSynchronousTask(new BlockChangeTask(), 0);
    	
    	playerregistry = proxy.getPlayerRegistry();
    	playerregistry.init();
    	
    	//System.out.println("\n\n\n\n" + playerregistry.getPlayers() + "\n\n\n\n\n\n");
    	
    	//Thread newThread = new Thread(new NewAsyncThread(), "NewThread");
    	//newThread.start();
    	
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