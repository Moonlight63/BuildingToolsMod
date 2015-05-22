package com.moonlight.buildingtools.items.tools.filtertool;

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
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IItemBlockAffector;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

public class FilterTool extends Item implements IKeyHandler, IOutlineDrawer, IItemBlockAffector, IShapeable, IGetGuiButtonPressed{
	
	private static Set<Key.KeyCode> handledKeys;
	
	public Set<BlockPos> blocksForOutline;
	
	private boolean outlineing = true;
	
	public BlockPos targetBlock;
	public World world;
	
	public ItemStack thisStack;
	
	static{
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	
	public FilterTool(){
		super();
		setUnlocalizedName("filterTool");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("radiusX", 1);
	        stack.getTagCompound().setInteger("radiusY", 1);
	        stack.getTagCompound().setInteger("radiusZ", 1);
	        stack.getTagCompound().setInteger("topsoildepth", 1);
	    }
	    return stack.getTagCompound();	    
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){
		EntityPlayer player = (EntityPlayer) entity;
		
		if(player.getCurrentEquippedItem() == itemstack){
			BuildingTools.proxy.setExtraReach(player, 200);
		}
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


            //ItemStack pb = ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock"));
            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.source " + pb.getDisplayName()) + EnumChatFormatting.RESET);

            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.radius " + this.getTargetRadius(stack)));

            //list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + /*LocalisationHelper.localiseString*/("info.exchanger.shift_to_select_source") + EnumChatFormatting.RESET);
        } else
        {
            list.add("Hold SHIFT for details");
            
            list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, 4, worldIn, 0, 0, 0);
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
		
		if(!worldIn.isRemote){
			this.world = worldIn;
			
			outlineing = false;
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			
			System.out.println("FilterToolUsed");
			//player.addPending(new PlaceTreeThread(worldIn, pos, playerIn));
			
			player.addPending(new TopSoilThread(worldIn, pos, getNBT(stack).getInteger("radiusX"),	getNBT(stack).getInteger("radiusY"), getNBT(stack).getInteger("radiusZ"), getNBT(stack).getInteger("topsoildepth"), side, playerIn));
			
			outlineing = true;
			return true;
		}
		
		return true;
	}
    
	@Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key){

    	//System.out.print("Key Recived");
    	
        int radius = getNBT(itemStack).getInteger("radiusX");
        
        float yMult = 0;
        float zMult = 0;
        
        
        
        
	        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY")){
	        	if(getNBT(itemStack).getInteger("radiusY") > 0)
	        		yMult = (getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusY"));
	        }
	        else{
	        	if(getNBT(itemStack).getInteger("radiusX") > 0)
	        		yMult = (getNBT(itemStack).getInteger("radiusY") / getNBT(itemStack).getInteger("radiusX"));
	        }
        
        
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
        
        //System.out.println(radius + "\n" + yMult + "\n" + (radius * yMult) + "\n" + (Math.round(radius * yMult)));
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY"))
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 1 : (int) (radius / yMult));
        else
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 0 : (int) (radius * yMult));
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 1 : (int) (radius / zMult));
        else
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 0 : (int) (radius * zMult));
        
        //if (radius > 25){radius = 25;}

        //getNBT(itemStack).setInteger("radius", radius);
        //this.setTargetRadius(itemStack, radius);
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
        
    }
	
    
    @Override
    public Set<Key.KeyCode> getHandledKeys()
    {
        return FilterTool.handledKeys;
    }
	
	@Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
		BlockPos target = event.target.getBlockPos();
        world = event.player.worldObj;
        thisStack = event.currentItem;

        if (event.player.isSneaking())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }
        
        //if(outlineing){
        
	        Set<BlockPos> blocks = this.blocksAffected(event.currentItem, world, target, event.target.sideHit, getNBT(event.currentItem).getInteger("radiusX") < 25 ? getNBT(event.currentItem).getInteger("radiusX") : 25, false);
	        if (blocks == null || blocks.size() == 0) return false;
	        for (BlockPos blockPos : blocks){
	        	//if(world.isAirBlock(blockPos.add(target)))
	        		RenderHelper.renderBlockOutline(event.context, event.player, blockPos, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
	        }
        
        //}
        return true;
    }
	
	@Override
    public Set<BlockPos> blocksAffected(ItemStack item, World world, BlockPos origin, EnumFacing side, int radius, boolean fill)
    {
        if (!(item.getItem() instanceof FilterTool)) return null;
        
        targetBlock = origin;        
        
    	blocksForOutline = Sets.newHashSet();
    	Shapes.Cuboid.generator.generateShape(
    			getNBT(item).getInteger("radiusX"),
    			getNBT(item).getInteger("radiusY"),
    			getNBT(item).getInteger("radiusZ"),
    			this, true);
    	
        if(outlineing){
        	return blocksForOutline;
        }
        else{
        	return null;
        }        
    }

	@Override
	public void setBlock(BlockPos bpos) {
		if(outlineing){
			if(blocksForOutline == null){
				blocksForOutline = Sets.newHashSet();
			}
			if(!world.isAirBlock(bpos.add(targetBlock)) && world.isAirBlock(bpos.add(targetBlock).up()))
				blocksForOutline.add(new BlockPos(bpos.add(targetBlock)));
			
		}
	}

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown,
			ItemStack stack) {
		
		
		//System.out.println("Got To GetGuiButtonPressed");
		switch (buttonID) {			
		case 1:
			int radiusx = getNBT(stack).getInteger("radiusX");
	        if (mouseButton == 0){
	                radiusx++;
	        } else if (mouseButton == 1){
	                radiusx--;
	        }

	        if (radiusx < 1){radiusx = 1;}

	        getNBT(stack).setInteger("radiusX", radiusx);
			break;
			
		case 2:
			int radiusy = getNBT(stack).getInteger("radiusY");
	        if (mouseButton == 0){
	                radiusy++;
	        } else if (mouseButton == 1){
	                radiusy--;
	        }

	        if (radiusy < 1){radiusy = 1;}

	        getNBT(stack).setInteger("radiusY", radiusy);
			break;
			
		case 3:
			int radiusz = getNBT(stack).getInteger("radiusZ");
	        if (mouseButton == 0){
	                radiusz++;
	        } else if (mouseButton == 1){
	                radiusz--;
	        }

	        if (radiusz < 1){radiusz = 1;}

	        getNBT(stack).setInteger("radiusZ", radiusz);
			break;
			
		case 4:
			int depth = getNBT(stack).getInteger("topsoildepth");
	        if (mouseButton == 0){
	        	depth++;
	        } else if (mouseButton == 1){
	        	depth--;
	        }

	        if (depth < 1){depth = 1;}

	        getNBT(stack).setInteger("topsoildepth", depth);
			break;
			
	
		default:
			break;
		}
		
		//System.out.println(getNBT(stack).getInteger("generator"));
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
}
