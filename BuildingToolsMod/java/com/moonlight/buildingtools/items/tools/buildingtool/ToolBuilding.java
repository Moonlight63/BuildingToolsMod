package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.HashSet;
import java.util.List;
//import java.util.Optional;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.brushtool.BrushShapeVisualizer;
import com.moonlight.buildingtools.items.tools.filtertool.FilterShapeVisualizer;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IItemBlockAffector;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

public class ToolBuilding extends Item implements IKeyHandler, IOutlineDrawer, IGetGuiButtonPressed{
	
	private static Set<Key.KeyCode> handledKeys;
	
	public Set<BlockPos> blocksForOutline;
	
	public BlockPos targetBlock;
	public EnumFacing targetFace;
	public World world;
	
	public boolean updateVisualizer = true;
	private BuildingShapeVisualizer visualizer;
	private RenderHelper renderer;
	
	static{
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	public ToolBuilding(){
		super();
		setUnlocalizedName("buildingTool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){		
		if(this.world == null){
			this.world = world;
		}
		
		if(world.isRemote){
			RayTracing.instance().fire(1000, true);
			MovingObjectPosition target = RayTracing.instance().getTarget();
		
			if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){				
				PacketDispatcher.sendToServer(new SendRaytraceResult(target.getBlockPos(), target.sideHit));
				this.targetBlock = target.getBlockPos();
				this.targetFace = target.sideHit;
			}
			else{
				PacketDispatcher.sendToServer(new SendRaytraceResult(null, null));
				this.targetBlock = null;
				this.targetFace = null;
			}
		}
	}
	
	public void setTargetBlock(BlockPos pos, EnumFacing side){
		this.targetBlock = pos;
		this.targetFace = side;
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("radiusX", 1);
	        stack.getTagCompound().setInteger("radiusZ", 1);
	        stack.getTagCompound().setBoolean("placeAll", true);
	    }
	    return stack.getTagCompound();	    
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, @SuppressWarnings("rawtypes") List list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        if (KeyHelper.isShiftDown())
        {
            if (stack.getTagCompound() == null)
            {
                //setDefaultTag(stack, 0);
            }

        } else
        {
            //list.add("Hold SHIFT for details");
            
            //list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if (targetBlock != null) {
			if (playerIn.isSneaking()) {
				playerIn.openGui(BuildingTools.instance,
						GuiHandler.GUIBuildingTool, worldIn, 0, 0, 0);
			} else {
				if (!worldIn.isRemote) {
					this.world = worldIn;

					PlayerWrapper player = BuildingTools.getPlayerRegistry()
							.getPlayer(playerIn).get();

					
					System.out.println(getNBT(itemStackIn).getInteger("radiusX"));
					player.addPending(new ThreadBuildersTool(worldIn,
							targetBlock, getNBT(itemStackIn).getInteger(
									"radiusX"), getNBT(itemStackIn).getBoolean(
									"placeAll"), getNBT(itemStackIn)
									.getInteger("radiusZ"), targetFace,
							playerIn));

					return itemStackIn;
				}
			}
		}
		return itemStackIn;
    }
		
	public boolean onItemUse(ItemStack stack,
            EntityPlayer playerIn,
            World worldIn,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ){
		
		onItemRightClick(stack, worldIn, playerIn);
		return true;
		
	}
    
	@Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key){

    	//System.out.print("Key Recived");
    	
        int radius = getNBT(itemStack).getInteger("radiusX");
        
        float zMult = 0;
        
	        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ")){
	        	if(getNBT(itemStack).getInteger("radiusZ") > 0)
	        		zMult = (getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusZ"));
	        }
	        else{
	        	if(getNBT(itemStack).getInteger("radiusX") > 0)
	        		zMult = (getNBT(itemStack).getInteger("radiusZ") / getNBT(itemStack).getInteger("radiusX"));
	        }
	        
	        
        

        if (key == Key.KeyCode.TOOL_INCREASE){
            if (player.isSneaking()){
                radius += 10;
            } else{
                radius++;
            }

        } else if (key == Key.KeyCode.TOOL_DECREASE){
            if (player.isSneaking())
            {
                radius -= 10;
            } else
            {
                radius--;
            }
        }
        

        if (radius < 0){radius = 0;}
        
        getNBT(itemStack).setInteger("radiusX", radius);
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 1 : (int) (radius / zMult));
        else
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 0 : (int) (radius * zMult));
        
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
        
    }
	
    
    @Override
    public Set<Key.KeyCode> getHandledKeys()
    {
        return ToolBuilding.handledKeys;
    }
    
    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {

    	if(visualizer==null){
    		visualizer = new BuildingShapeVisualizer();
    	}
    	if(renderer == null){
    		renderer = new RenderHelper();
    	}
    	
    	
    	
        if(targetBlock != null){
        	
        	renderer.startDraw();
        	
	        if (event.player.isSneaking())
	        {
	        	renderer.addOutlineToBuffer(event.player, targetBlock, RGBA.Green.setAlpha(150), event.partialTicks);
	        	renderer.finalizeDraw();
	            //RenderHelper.renderBlockOutline(event.context, event.player, targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.partialTicks);
	            return true;
	        }
	        	
        	if(checkVisualizer(visualizer, event.currentItem)){
                visualizer.RegenShape(
            			Shapes.Cuboid.generator, 
            			getNBT(event.currentItem).getInteger("radiusX"),
            			getNBT(event.currentItem).getInteger("radiusZ"),
            			true
        		);
                updateVisualizer = false;
        	}
        	
        	if(visualizer.finishedGenerating){
	        	Set<BlockPos> blockData = visualizer.GetBlocks();
	        	
	        	if(blockData != null){
		        	for(BlockPos pos : blockData){
		        		BlockPos newPos = visualizer.CalcOffset(pos, targetBlock, targetFace, world);
		        		
		        		if(newPos != null){
		        			renderer.addOutlineToBuffer(event.player, newPos, RGBA.White.setAlpha(150), event.partialTicks);
		        			//RenderHelper.renderBlockOutline(event.context, event.player, newPos, RGBA.White.setAlpha(150), 1.0f, event.partialTicks);
		        		}
		        	}
	        	}
        	}
	        renderer.finalizeDraw();
        }
        return true;
    }
    
    public boolean checkVisualizer(BuildingShapeVisualizer vis, ItemStack stack){
    	
    	return (
    			(visualizer.replaceblock != getNBT(stack).getBoolean("placeAll")) ||
    			(visualizer.x != getNBT(stack).getInteger("radiusX")) ||
    			(visualizer.z != getNBT(stack).getInteger("radiusZ")) ||
    			updateVisualizer
    			);
    	
    }

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown,
			ItemStack stack) {
		
		int multiplier = 0;
		if(isShiftDown)
			multiplier = 10;
		else
			multiplier = 1;
		
		int amount = 0;
		if(mouseButton == 0)
			amount = multiplier;
		else if(mouseButton == 1)
			amount = -multiplier;
		else 
			return;
		
		
		if (buttonID == 1) {
			int radiusx = getNBT(stack).getInteger("radiusX");
			radiusx+=amount;
			if (radiusx < 1){radiusx = 1;}
			getNBT(stack).setInteger("radiusX", radiusx);
		} else if (buttonID == 2) {
			int radiusz = getNBT(stack).getInteger("radiusZ");
			radiusz+=amount;
			if (radiusz < 1){radiusz = 1;}
			getNBT(stack).setInteger("radiusZ", radiusz);
		} else if (buttonID == 3) {
			getNBT(stack).setBoolean("placeAll", !getNBT(stack).getBoolean("placeAll"));
		} else {
		}
		
		//System.out.println(getNBT(stack).getInteger("generator"));
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
		
	}
	
	@Override
	public int getMetadata(int damage)
    {
		updateVisualizer = true;
        return super.getMetadata(damage);
    }

}
