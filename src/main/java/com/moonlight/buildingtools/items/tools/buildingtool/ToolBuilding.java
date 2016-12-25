package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.List;
//import java.util.Optional;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolBuilding extends ToolBase{
	
	private BuildingShapeVisualizer visualizer;
	
	public ToolBuilding(){
		super();
		setUnlocalizedName("ToolBuilding");
		setRegistryName("buildingtool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
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
	
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check)
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		
		if (targetBlock != null  && !worldIn.isAirBlock(targetBlock)) {
			if (playerIn.isSneaking()) {
				playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBuildingTool, worldIn, 0, 0, 0);
			} else {
				if (!worldIn.isRemote) {
					this.world = worldIn;

					PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();

					System.out.println(getNBT(itemStackIn).getInteger("radiusX"));
					player.addPending(new ThreadBuildersTool(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
    
	@Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key){
    	
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
    public boolean drawOutline(DrawBlockHighlightEvent event){

    	if(visualizer==null){
    		visualizer = new BuildingShapeVisualizer();
    	}
    	if(renderer == null){
    		renderer = new RenderHelper();
    	}
    	
        if(targetBlock != null){
        	
        	renderer.startDraw();
        	
	        if (event.getPlayer().isSneaking()){
	        	renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
	        	renderer.finalizeDraw();
	            return true;
	        }
	        	
        	if(checkVisualizer(visualizer, event.getPlayer().getHeldItemMainhand())){
                visualizer.RegenShape(
            			Shapes.Cuboid.generator, 
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusX"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusZ"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getBoolean("placeAll")
        		);
                updateVisualizer = false;
        	}
        	
        	if(visualizer.finishedGenerating){
	        	Set<BlockPos> blockData = visualizer.GetBlocks();
	        	
	        	if(blockData != null){
		        	for(BlockPos pos : blockData){
		        		BlockPos newPos = visualizer.CalcOffset(pos, targetBlock, targetFace, world);
		        		
		        		if(newPos != null){
		        			renderer.addOutlineToBuffer(event.getPlayer(), newPos, RGBA.White.setAlpha(150), event.getPartialTicks());
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

	public void GuiButtonPressed(int buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
		
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
			int radiusx = getNBT(thisStack).getInteger("radiusX");
			radiusx+=amount;
			if (radiusx < 1){radiusx = 1;}
			getNBT(thisStack).setInteger("radiusX", radiusx);
		} else if (buttonID == 2) {
			int radiusz = getNBT(thisStack).getInteger("radiusZ");
			radiusz+=amount;
			if (radiusz < 1){radiusz = 1;}
			getNBT(thisStack).setInteger("radiusZ", radiusz);
		} else if (buttonID == 3) {
			getNBT(thisStack).setBoolean("placeAll", !getNBT(thisStack).getBoolean("placeAll"));
		} else {
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(thisStack)));
		
	}
	
	@Override
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
		
		for (String key : commandset) {
			String command = nbtcommand.getCompoundTag("Commands").getString(key);
			
			if (command.equals("GetButton")) {
				GuiButtonPressed(nbtcommand.getInteger("ButtonID"), nbtcommand.getInteger("Mouse"), nbtcommand.getBoolean("CTRL"), nbtcommand.getBoolean("ALT"), nbtcommand.getBoolean("SHIFT"));
			} else {
			}
		}
	}

}
