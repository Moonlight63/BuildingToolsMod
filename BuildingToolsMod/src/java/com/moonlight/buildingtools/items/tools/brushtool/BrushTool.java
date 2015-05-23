package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.HashSet;
import java.util.List;
//import java.util.Optional;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.IToolOverrideHitDistance;
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

public class BrushTool extends Item implements IKeyHandler, IOutlineDrawer, IItemBlockAffector, IShapeable, IGetGuiButtonPressed, IToolOverrideHitDistance{
	
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
	
	
	public BrushTool(){
		super();
		setUnlocalizedName("brushTool");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("radiusX", 1);
	        stack.getTagCompound().setInteger("radiusY", 1);
	        stack.getTagCompound().setInteger("radiusZ", 1);
	        stack.getTagCompound().setInteger("generator", 0);
	        stack.getTagCompound().setBoolean("forcefall", false);
	        stack.getTagCompound().setBoolean("fillmode", true);
	        stack.getTagCompound().setInteger("replacemode", 1);
	        stack.getTagCompound().setTag("sourceblock", new ItemStack(Blocks.stone).writeToNBT(new NBTTagCompound()));
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


            ItemStack pb = ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock"));
            list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.source " + pb.getDisplayName()) + EnumChatFormatting.RESET);

            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.radius " + this.getTargetRadius(stack)));

            list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + /*LocalisationHelper.localiseString*/("info.exchanger.shift_to_select_source") + EnumChatFormatting.RESET);
        } else
        {
            list.add("Hold SHIFT for details");
            
            list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		targetBlock = null;
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, 2, worldIn, 0, 0, 0);
		//System.out.println(targetBlock);
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
			//getNBT(stack).setTag("sourceblock", new ItemStack(worldIn.getBlockState(pos).getBlock(), 1, worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos))).writeToNBT(new NBTTagCompound()));
			playerIn.openGui(BuildingTools.instance, 2, worldIn, 0, 0, 0);
			return false;
		}
		
		
		if(!worldIn.isRemote){
		
			//System.out.print("Item used on block " + pos);
			
			
			this.world = worldIn;			
			
			outlineing = false;
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			
			if(getNBT(stack).getInteger("replacemode") == 1){
				player.addPending(new BlockShapeThread(worldIn, pos,					
						getNBT(stack).getInteger("radiusX"),
						getNBT(stack).getInteger("radiusY"), 
						getNBT(stack).getInteger("radiusZ"), 
						side,
						
						playerIn, Shapes.VALUES[getNBT(stack).getInteger("generator")].generator, 
						getNBT(stack).getBoolean("fillmode"), 
						getNBT(stack).getBoolean("forcefall"),
						Block.getBlockFromItem(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getMetadata())
						));
			}
			else if(getNBT(stack).getInteger("replacemode") == 2){
				player.addPending(new BlockShapeThread(worldIn, pos,					
						getNBT(stack).getInteger("radiusX"),
						getNBT(stack).getInteger("radiusY"), 
						getNBT(stack).getInteger("radiusZ"), 
						side,
						
						playerIn, Shapes.VALUES[getNBT(stack).getInteger("generator")].generator, 
						getNBT(stack).getBoolean("fillmode"), 
						getNBT(stack).getBoolean("forcefall"),
						Block.getBlockFromItem(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getMetadata()),
						worldIn.getBlockState(pos)
						));
			}
			else if(getNBT(stack).getInteger("replacemode") == 3){
				player.addPending(new BlockShapeThread(worldIn, pos,					
						getNBT(stack).getInteger("radiusX"),
						getNBT(stack).getInteger("radiusY"), 
						getNBT(stack).getInteger("radiusZ"), 
						side,
						
						playerIn, Shapes.VALUES[getNBT(stack).getInteger("generator")].generator, 
						getNBT(stack).getBoolean("fillmode"), 
						getNBT(stack).getBoolean("forcefall"),
						Block.getBlockFromItem(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock")).getMetadata()),
						true
						));
			}
			
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
        return BrushTool.handledKeys;
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
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }
        
        if(outlineing){
        
	        Set<BlockPos> blocks = this.blocksAffected(event.currentItem, world, target, event.target.sideHit, getNBT(event.currentItem).getInteger("radiusX") < 25 ? getNBT(event.currentItem).getInteger("radiusX") : 25, false);
	        if (blocks == null || blocks.size() == 0) return false;
	        for (BlockPos blockPos : blocks){
	        	//if(world.isAirBlock(blockPos.add(target)))
	        		RenderHelper.renderBlockOutline(event.context, event.player, blockPos, RGBA.White.setAlpha(0.6f), 2.0f, event.partialTicks);
	        }
        
        }
        return true;
    }
	
	@Override
    public Set<BlockPos> blocksAffected(ItemStack item, World world, BlockPos origin, EnumFacing side, int radius, boolean fill)
    {
        if (!(item.getItem() instanceof BrushTool)) return null;
        
        targetBlock = origin;        
        
    	blocksForOutline = Sets.newHashSet();
    	Shapes.VALUES[getNBT(item).getInteger("generator")].generator.generateShape(
    			getNBT(item).getInteger("radiusX"),
    			getNBT(item).getInteger("radiusY"),
    			getNBT(item).getInteger("radiusZ"),
    			this, getNBT(item).getInteger("replacemode") == 2);
    	
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
				
				if(getNBT(thisStack).getInteger("replacemode") == 1){
					if(!world.isAirBlock(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock))){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 2){
					if(world.getBlockState(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock)){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 3){
				}
				
				blocksForOutline.add(new BlockPos(bpos.getX(), curside == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock));
			}
			else if (curside == EnumFacing.NORTH || curside == EnumFacing.SOUTH){
				
				if(getNBT(thisStack).getInteger("replacemode") == 1){
					if(!world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock))){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 2){
					if(world.getBlockState(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock)) != world.getBlockState(targetBlock)){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 3){
				}
				
				blocksForOutline.add(new BlockPos(bpos.getX(), bpos.getZ(), curside == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock));
			}
			else if (curside == EnumFacing.EAST || curside == EnumFacing.WEST){
				
				if(getNBT(thisStack).getInteger("replacemode") == 1){
					if(!world.isAirBlock(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock))){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 2){
					if(world.getBlockState(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock)){
						return;
					}
				}else if(getNBT(thisStack).getInteger("replacemode") == 3){
				}
				
				blocksForOutline.add(new BlockPos(curside == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock));
			}
			
				//blocksForOutline.add(bpos.add(targetBlock));
		}
	}

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown,
			ItemStack stack) {
		
		
		//System.out.println("Got To GetGuiButtonPressed");
		switch (buttonID) {
		case 1:
			if(mouseButton == 0){
				if(getNBT(stack).getInteger("generator") < Shapes.VALUES.length - 1)
					getNBT(stack).setInteger("generator", getNBT(stack).getInteger("generator") + 1);
				else
					getNBT(stack).setInteger("generator", 0);
			}
			else if(mouseButton == 1){
				if(getNBT(stack).getInteger("generator") > 0)
					getNBT(stack).setInteger("generator", getNBT(stack).getInteger("generator") - 1);
				else
					getNBT(stack).setInteger("generator", Shapes.VALUES.length - 1);
			}
			
			//System.out.println(getNBT(stack).getInteger("generator"));
			break;
			
		case 2:
			int radiusx = getNBT(stack).getInteger("radiusX");
	        if (mouseButton == 0){
	                radiusx++;
	        } else if (mouseButton == 1){
	                radiusx--;
	        }

	        if (radiusx < 1){radiusx = 1;}

	        getNBT(stack).setInteger("radiusX", radiusx);
			break;
			
		case 3:
			int radiusy = getNBT(stack).getInteger("radiusY");
	        if (mouseButton == 0){
	                radiusy++;
	        } else if (mouseButton == 1){
	                radiusy--;
	        }

	        if (radiusy < 1){radiusy = 1;}

	        getNBT(stack).setInteger("radiusY", radiusy);
			break;
			
		case 4:
			int radiusz = getNBT(stack).getInteger("radiusZ");
	        if (mouseButton == 0){
	                radiusz++;
	        } else if (mouseButton == 1){
	                radiusz--;
	        }

	        if (radiusz < 1){radiusz = 1;}

	        getNBT(stack).setInteger("radiusZ", radiusz);
			break;
			
		case 5:
			getNBT(stack).setBoolean("fillmode", !getNBT(stack).getBoolean("fillmode"));
			break;
			
		case 6:
			getNBT(stack).setBoolean("forcefall", !getNBT(stack).getBoolean("forcefall"));
			break;
			
		case 7:
			if(getNBT(stack).getInteger("replacemode") == 1){
				getNBT(stack).setInteger("replacemode", 2);
			}
			else if(getNBT(stack).getInteger("replacemode") == 2){
				getNBT(stack).setInteger("replacemode", 3);
			}
			else if(getNBT(stack).getInteger("replacemode") == 3){
				getNBT(stack).setInteger("replacemode", 1);
			}
			
			break;
			
		case 8:
			getNBT(stack).setTag("sourceblock", new ItemStack(world.getBlockState(targetBlock).getBlock(), 1, world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock))).writeToNBT(new NBTTagCompound()));
			
			break;
			
		case 9:
			getNBT(stack).setTag("sourceblock", new ItemStack(BlockLoader.tempBlock, 1, BlockLoader.tempBlock.getMetaFromState(BlockLoader.tempBlock.getDefaultState())).writeToNBT(new NBTTagCompound()));
			
			break;

		default:
			break;
		}
		
		//System.out.println(getNBT(stack).getInteger("generator"));
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
}
