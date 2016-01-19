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
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.IToolOverrideHitDistance;
import com.moonlight.buildingtools.network.GuiHandler;
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

public class ToolBuilding extends Item implements IKeyHandler, IOutlineDrawer, IItemBlockAffector, IShapeable, IGetGuiButtonPressed, IToolOverrideHitDistance{
	
	private static Set<Key.KeyCode> handledKeys;
	
	public Set<BlockPos> blocksForOutline;
	
	private boolean outlineing = true;
	
	private EnumFacing curside;
	
	public BlockPos targetBlock;
	public World world;
	
	public ItemStack thisStack;
	
	static{
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	public ToolBuilding(){
		super();
		setUnlocalizedName("buildingTool");
		setCreativeTab(BuildingTools.tabBT);
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
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBuildingTool, worldIn, 0, 0, 0);
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
		
		if(playerIn.isSneaking()){
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBuildingTool, worldIn, 0, 0, 0);
		}
		else{
			if(!worldIn.isRemote){
				this.world = worldIn;			
				
				outlineing = false;
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				
				player.addPending(new ThreadBuildersTool(worldIn, pos, getNBT(stack).getInteger("radiusX"),	getNBT(stack).getBoolean("placeAll"), getNBT(stack).getInteger("radiusZ"), side, playerIn));
				
				outlineing = true;
				return true;
			}
		}
		
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
        
        //System.out.println(radius + "\n" + yMult + "\n" + (radius * yMult) + "\n" + (Math.round(radius * yMult)));
        
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
        return ToolBuilding.handledKeys;
    }
	
	@Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
		BlockPos target = event.target.getBlockPos();
        world = event.player.worldObj;
        curside = event.target.sideHit;
        thisStack = event.currentItem;

        if (event.player.isSneaking())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(150), 2.0f, event.partialTicks);
            return true;
        }
        
        if(outlineing){
        
	        Set<BlockPos> blocks = this.blocksAffected(event.currentItem, world, target, event.target.sideHit, getNBT(event.currentItem).getInteger("radiusX") < 25 ? getNBT(event.currentItem).getInteger("radiusX") : 25, false);
	        if (blocks == null || blocks.size() == 0) return false;
	        for (BlockPos blockPos : blocks){
	        	//if(world.isAirBlock(blockPos.add(target)))
	        		RenderHelper.renderBlockOutline(event.context, event.player, blockPos, RGBA.White.setAlpha(150), 2.0f, event.partialTicks);
	        }
        
        }
        return true;
    }
	
	@Override
    public Set<BlockPos> blocksAffected(ItemStack item, World world, BlockPos origin, EnumFacing side, int radius, boolean fill)
    {
        if (!(item.getItem() instanceof ToolBuilding)) return null;
        
        targetBlock = origin;        
        
    	blocksForOutline = Sets.newHashSet();
    	Shapes.Cuboid.generator.generateShape(
    			getNBT(item).getInteger("radiusX"),
    			0,
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
			
			//if(!blocksForOutline.contains(bpos.add(targetBlock)))
						
			if (curside == EnumFacing.UP || curside == EnumFacing.DOWN){
				if(!world.isAirBlock(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock).offset(curside))){
					return;
				}
				if(world.isAirBlock(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock))){
					return;
				}
				if(!getNBT(thisStack).getBoolean("placeAll") && world.getBlockState(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock))
            		return;
				blocksForOutline.add(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock).offset(curside));
			}
			else if (curside == EnumFacing.NORTH || curside == EnumFacing.SOUTH){
				
				if(!world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock).offset(curside))){
					return;
				}
				if(world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock))){
					return;
				}
				if(!getNBT(thisStack).getBoolean("placeAll") && world.getBlockState(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock)) != world.getBlockState(targetBlock))
            		return;
				blocksForOutline.add(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock).offset(curside));
			}
			else if (curside == EnumFacing.EAST || curside == EnumFacing.WEST){
				
				if(!world.isAirBlock(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock).offset(curside))){
					return;
				}
				if(world.isAirBlock(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock))){
					return;
				}
				if(!getNBT(thisStack).getBoolean("placeAll") && world.getBlockState(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock))
            		return;
				blocksForOutline.add(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock).offset(curside));
			}
			
				//blocksForOutline.add(bpos.add(targetBlock));
		}
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
}
