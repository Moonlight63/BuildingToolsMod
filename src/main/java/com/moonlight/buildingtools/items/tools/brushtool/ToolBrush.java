package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.HashSet;
import java.util.List;
//import java.util.Optional;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.buildingtool.ToolBuilding;
import com.moonlight.buildingtools.items.tools.filtertool.FilterShapeVisualizer;
import com.moonlight.buildingtools.items.tools.selectiontool.ThreadAdvancedFill;
import com.moonlight.buildingtools.items.tools.selectiontool.ThreadSimpleFill;
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

public class ToolBrush extends Item implements IKeyHandler, IOutlineDrawer, IGetGuiButtonPressed{
	
	private static Set<Key.KeyCode> handledKeys;
	public Set<BlockPos> blocksForOutline;
	public World world;
	public BlockPos targetBlock;
	public EnumFacing targetFace;
	
	public ItemStack thisStack;
	public EntityPlayer currPlayer;
	
	private List<IBlockState> blockStates = Lists.<IBlockState>newArrayList();
	private List<IBlockState> replaceBlocks = Lists.<IBlockState>newArrayList();
	private List<Integer> fillBlockChance = Lists.<Integer>newArrayList();
	
	public boolean updateVisualizer = true;
	private BrushShapeVisualizer visualizer;
	
	private RenderHelper renderer;
	
	static{
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	
	public ToolBrush(){
		super();
		setUnlocalizedName("brushTool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){		
		if(this.world == null){
			this.world = world;
		}
		if(this.currPlayer != entity)
			this.currPlayer = (EntityPlayer) entity;
		
		if(this.thisStack != itemstack)
			this.thisStack = itemstack;
		
		if(world.isRemote){
			RayTracing.instance().fire(1000, true);
			RayTraceResult target = RayTracing.instance().getTarget();
		
			if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK){				
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
	        stack.getTagCompound().setInteger("radiusY", 1);
	        stack.getTagCompound().setInteger("radiusZ", 1);
	        stack.getTagCompound().setInteger("generator", 0);
	        stack.getTagCompound().setBoolean("forcefall", false);
	        stack.getTagCompound().setBoolean("fillmode", true);
	        stack.getTagCompound().setInteger("replacemode", 1);
	        stack.getTagCompound().setTag("sourceblock", new ItemStack(Blocks.STONE).writeToNBT(new NBTTagCompound()));
	        stack.getTagCompound().setIntArray("sourceBlockValues", new int[2]);
	        stack.getTagCompound().setBoolean("useNBTBlock", true);
	        stack.getTagCompound().setIntArray("targetBlockPos", new int[]{0,0,0});
	    }
	    //thisStack = stack;
	    return stack.getTagCompound();	    
	}
	
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
            //list.add("Hold SHIFT for details");
            
            //list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		
		//System.out.println(targetBlock);
		if(targetBlock != null && !worldIn.isAirBlock(targetBlock)){
		
			
	//		targetBlock = null;
	//		if(playerIn.isSneaking())
	//			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBrushTool, worldIn, 0, 0, 0);
	//		
	//		
	//        return itemStackIn;
			
			
			//getNBT(itemStackIn).setIntArray("targetBlockPos", new int[]{targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()});
			PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStackIn)));
			//targetBlock = pos;
			
			if(playerIn.isSneaking()){
				//getNBT(stack).setTag("sourceblock", new ItemStack(worldIn.getBlockState(pos).getBlock(), 1, worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos))).writeToNBT(new NBTTagCompound()));
				playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBrushTool, worldIn, 0, 0, 0);
				return new ActionResult(EnumActionResult.PASS, itemStackIn);
				//return false;
			}
			
			
			if(!worldIn.isRemote){
			
				//System.out.print("Item used on block " + pos);
				
				//this.world = worldIn;			
				
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				
				if(getNBT(itemStackIn).getInteger("replacemode") == 1){
					player.addPending(new ThreadPaintShape(worldIn, targetBlock,					
							getNBT(itemStackIn).getInteger("radiusX"),
							getNBT(itemStackIn).getInteger("radiusY"), 
							getNBT(itemStackIn).getInteger("radiusZ"), 
							targetFace,
							
							playerIn, Shapes.VALUES[getNBT(itemStackIn).getInteger("generator")].generator, 
							getNBT(itemStackIn).getBoolean("fillmode"), 
							getNBT(itemStackIn).getBoolean("forcefall"),
							getNBT(itemStackIn).getBoolean("useNBTBlock") ?
								Block.getBlockFromItem(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getMetadata())
							:
								Block.getBlockById(getNBT(itemStackIn).getIntArray("sourceBlockValues")[0]).getStateFromMeta(getNBT(itemStackIn).getIntArray("sourceBlockValues")[1]),
							this.blockStates,
							this.fillBlockChance
							));
				}
				else if(getNBT(itemStackIn).getInteger("replacemode") == 2){
					player.addPending(new ThreadPaintShape(worldIn, targetBlock,					
							getNBT(itemStackIn).getInteger("radiusX"),
							getNBT(itemStackIn).getInteger("radiusY"), 
							getNBT(itemStackIn).getInteger("radiusZ"), 
							targetFace,
							
							playerIn, Shapes.VALUES[getNBT(itemStackIn).getInteger("generator")].generator, 
							getNBT(itemStackIn).getBoolean("fillmode"), 
							getNBT(itemStackIn).getBoolean("forcefall"),
							getNBT(itemStackIn).getBoolean("useNBTBlock") ?
								Block.getBlockFromItem(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getMetadata())
							:
								Block.getBlockById(getNBT(itemStackIn).getIntArray("sourceBlockValues")[0]).getStateFromMeta(getNBT(itemStackIn).getIntArray("sourceBlockValues")[1])
							,
							worldIn.getBlockState(targetBlock),
							this.blockStates,
							this.fillBlockChance
							));
				}
				else if(getNBT(itemStackIn).getInteger("replacemode") == 3){
					player.addPending(new ThreadPaintShape(worldIn, targetBlock,					
							getNBT(itemStackIn).getInteger("radiusX"),
							getNBT(itemStackIn).getInteger("radiusY"), 
							getNBT(itemStackIn).getInteger("radiusZ"), 
							targetFace,
							
							playerIn, Shapes.VALUES[getNBT(itemStackIn).getInteger("generator")].generator, 
							getNBT(itemStackIn).getBoolean("fillmode"), 
							getNBT(itemStackIn).getBoolean("forcefall"),
							getNBT(itemStackIn).getBoolean("useNBTBlock") ?
								Block.getBlockFromItem(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getMetadata())
							:
								Block.getBlockById(getNBT(itemStackIn).getIntArray("sourceBlockValues")[0]).getStateFromMeta(getNBT(itemStackIn).getIntArray("sourceBlockValues")[1])
							,
							true,
							this.blockStates,
							this.fillBlockChance
							));
				}
				else if(getNBT(itemStackIn).getInteger("replacemode") == 4){
					player.addPending(new ThreadPaintShape(worldIn, targetBlock,					
							getNBT(itemStackIn).getInteger("radiusX"),
							getNBT(itemStackIn).getInteger("radiusY"), 
							getNBT(itemStackIn).getInteger("radiusZ"), 
							targetFace,
							
							playerIn, Shapes.VALUES[getNBT(itemStackIn).getInteger("generator")].generator, 
							getNBT(itemStackIn).getBoolean("fillmode"), 
							getNBT(itemStackIn).getBoolean("forcefall"),
							getNBT(itemStackIn).getBoolean("useNBTBlock") ?
								Block.getBlockFromItem(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getItem()).getStateFromMeta(new ItemStack(getNBT(itemStackIn).getCompoundTag("sourceblock")).getMetadata())
							:
								Block.getBlockById(getNBT(itemStackIn).getIntArray("sourceBlockValues")[0]).getStateFromMeta(getNBT(itemStackIn).getIntArray("sourceBlockValues")[1])
							,
							this.replaceBlocks,
							this.blockStates,
							this.fillBlockChance
							));
				}
				
				
			}
			
		}
		
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
		
    }
		
	public boolean onItemUse(ItemStack stack,
            EntityPlayer playerIn,
            World worldIn,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ){
		
		onItemRightClick(worldIn, playerIn, EnumHand.MAIN_HAND);
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
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY"))
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 1 : (int) (radius / yMult));
        else
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 0 : (int) (radius * yMult));
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 1 : (int) (radius / zMult));
        else
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 0 : (int) (radius * zMult));
        
        
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
        
    }
	
    @Override
    public Set<Key.KeyCode> getHandledKeys()
    {
        return ToolBrush.handledKeys;
    }
	
    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {

    	if(visualizer==null){
    		visualizer = new BrushShapeVisualizer();
    	}
    	if(renderer == null){
    		renderer = new RenderHelper();
    	}
    	
        if(targetBlock != null){
        	
        	//RenderHelper renderer = new RenderHelper();
        	
        	renderer.startDraw();
        	
	        if (event.getPlayer().isSneaking())
	        {
	        	renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
	            //RenderHelper.renderBlockOutline(event.context, event.player, targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.partialTicks);
	        	renderer.finalizeDraw();
	            return true;
	        }
	        	
        	if(checkVisualizer(visualizer, event.getPlayer().getHeldItemMainhand())){
                visualizer.RegenShape(
            			Shapes.VALUES[getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("generator")].generator, 
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusX"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusY"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusZ"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("replacemode"),
            			this.replaceBlocks
        		);
                updateVisualizer = false;
        	}
        	
        	if(visualizer.finishedGenerating){
	        	Set<BlockPos> blockData = visualizer.GetBlocks();
	        	
	        	if(blockData != null){
		        	for(BlockPos pos : blockData){
		        		BlockPos newPos = visualizer.CalcOffset(pos, targetBlock, targetFace, world);
		        		
		        		if(newPos != null){
		        			if(!(getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("replacemode") == 2 && world.isAirBlock(newPos)))
		        				renderer.addOutlineToBuffer(event.getPlayer(), newPos, RGBA.White.setAlpha(150), event.getPartialTicks());
		        			//RenderHelper.renderBlockOutline(event.context, event.player, newPos, RGBA.White.setAlpha(150), 1.0f, event.partialTicks);
		        		}
		        	}
	        	}
        	}
	        
        	renderer.finalizeDraw();
        }
        return true;
    }
    
    public boolean checkVisualizer(BrushShapeVisualizer vis, ItemStack stack){
    	
    	return (
    			(visualizer.currentGen != Shapes.VALUES[getNBT(stack).getInteger("generator")].generator) ||
    			(visualizer.x != getNBT(stack).getInteger("radiusX")) ||
    			(visualizer.y != getNBT(stack).getInteger("radiusY")) ||
    			(visualizer.z != getNBT(stack).getInteger("radiusZ")) ||
    			(visualizer.replaceblock != getNBT(stack).getInteger("replacemode")) ||
    			(visualizer.replaceBlocks != this.replaceBlocks) ||
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
		} else if (buttonID == 2) {
			int radiusx = getNBT(stack).getInteger("radiusX");
	        radiusx+=amount;
			if (radiusx < 0){radiusx = 0;}
			getNBT(stack).setInteger("radiusX", radiusx);
		} else if (buttonID == 3) {
			int radiusy = getNBT(stack).getInteger("radiusY");
	        radiusy+=amount;
			if (radiusy < 0){radiusy = 0;}
			getNBT(stack).setInteger("radiusY", radiusy);
		} else if (buttonID == 4) {
			int radiusz = getNBT(stack).getInteger("radiusZ");
			radiusz+=amount;
			if (radiusz < 0){radiusz = 0;}
			getNBT(stack).setInteger("radiusZ", radiusz);
		} else if (buttonID == 5) {
			System.out.println(world);
			System.out.println(targetBlock);
			System.out.println(stack);
			System.out.println(world.getBlockState(targetBlock));
			System.out.println(Block.getIdFromBlock(world.getBlockState(targetBlock).getBlock()));
			System.out.println(world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock)));
			if(Item.getItemFromBlock(world.getBlockState(targetBlock).getBlock()) != null){
				getNBT(stack).setTag("sourceblock", new ItemStack(world.getBlockState(targetBlock).getBlock(), 1, world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock))).writeToNBT(new NBTTagCompound()));
				getNBT(stack).setBoolean("useNBTBlock", true);
				System.out.println("Using NBT");
			}
			else{
				int[] values = {
						Block.getIdFromBlock(world.getBlockState(targetBlock).getBlock()), 
						world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock))
						};
				getNBT(stack).setIntArray("sourceBlockValues", values);
				getNBT(stack).setBoolean("useNBTBlock", false);
				System.out.println("Using Basic Block Data");
			}
			this.blockStates.clear();
		} else if (buttonID == 6) {
			getNBT(stack).setTag("sourceblock", new ItemStack(BlockLoader.tempBlock, 1, BlockLoader.tempBlock.getMetaFromState(BlockLoader.tempBlock.getDefaultState())).writeToNBT(new NBTTagCompound()));
		} else if (buttonID == 7) {
			getNBT(stack).setBoolean("fillmode", !getNBT(stack).getBoolean("fillmode"));
		} else if (buttonID == 8) {
			getNBT(stack).setBoolean("forcefall", !getNBT(stack).getBoolean("forcefall"));
		} else if (buttonID == 9) {
			if(getNBT(stack).getInteger("replacemode") == 1){
				getNBT(stack).setInteger("replacemode", 2);
			}
			else if(getNBT(stack).getInteger("replacemode") == 2){
				getNBT(stack).setInteger("replacemode", 3);
			}
			else if(getNBT(stack).getInteger("replacemode") == 3){
				getNBT(stack).setInteger("replacemode", 4);
			}
			else if(getNBT(stack).getInteger("replacemode") == 4){
				getNBT(stack).setInteger("replacemode", 1);
			}
		}  else {
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
		
	}
	
	@Override
	public int getMetadata(int damage)
    {
		System.out.println("Update Visualization");
		updateVisualizer = true;
        return super.getMetadata(damage);
    }
	
	public void SimpleFill(int ID, int DATA){
		System.out.println("Recieved Message!");
		
		IBlockState fillBlock = Block.getBlockById(ID).getStateFromMeta(DATA);
		
		if(Item.getItemFromBlock(fillBlock.getBlock()) != null){
			getNBT(thisStack).setTag("sourceblock", new ItemStack(fillBlock.getBlock(), 1, DATA).writeToNBT(new NBTTagCompound()));
			getNBT(thisStack).setBoolean("useNBTBlock", true);
			System.out.println("Using NBT");
		}
		else{
			int[] values = {
					Block.getIdFromBlock(fillBlock.getBlock()), 
					DATA
					};
			getNBT(thisStack).setIntArray("sourceBlockValues", values);
			getNBT(thisStack).setBoolean("useNBTBlock", false);
			System.out.println("Using Basic Block Data");
		}
		this.blockStates.clear();
		//player.addPending(new ThreadSimpleFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, fillBlock));
	}
	
	public void AdvancedFill(List<Integer> ID, List<Integer> DATA, List<Integer> COUNT){
		System.out.println("Recieved Message!");
		System.out.println(ID + "   " + DATA + "   " + COUNT);
		
		List<IBlockState> blockStates = Lists.<IBlockState>newArrayList();
		
		for (int i = 0; i < ID.size(); i++) {
			blockStates.add(Block.getBlockById(ID.get(i)).getStateFromMeta(DATA.get(i)));
		}
		
		this.blockStates = blockStates;
		this.fillBlockChance = COUNT;
		
		//player.addPending(new ThreadAdvancedFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, blockStates, COUNT));
		//player.addPending(new ThreadSimpleFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, fillBlock));
	}
	
	public void AdvancedReplace(List<Integer> ID2, List<Integer> DATA2){
		System.out.println("Recieved Message!");
		
		List<IBlockState> blockStatesReplace = Lists.<IBlockState>newArrayList();
		
		for (int i = 0; i < ID2.size(); i++) {
			blockStatesReplace.add(Block.getBlockById(ID2.get(i)).getStateFromMeta(DATA2.get(i)));
		}
		
		System.out.println("CUSTOM REPLACE " + blockStatesReplace);
		this.replaceBlocks = blockStatesReplace;
	}
	
}
