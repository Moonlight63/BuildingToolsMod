package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Key.KeyCode;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolFilter extends ToolBase{
    
    public ProceduralTreeData treeData = new ProceduralTreeData();
	private FilterShapeVisualizer visualizer;
    

    public ToolFilter()
    {
        setUnlocalizedName("ToolFilter");
        setRegistryName("filtertool");
        setCreativeTab(BuildingTools.tabBT);
        setMaxStackSize(1);
    }

    public static NBTTagCompound getNBT(ItemStack stack)
    {
        if(stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("filter", 1);
            stack.getTagCompound().setInteger("radiusX", 1);
            stack.getTagCompound().setInteger("radiusY", 1);
            stack.getTagCompound().setInteger("radiusZ", 1);
            stack.getTagCompound().setInteger("topsoildepth", 1);
            stack.getTagCompound().setInteger("fillorclear", 1);
            stack.getTagCompound().setInteger("treetype", 0);
        }
        return stack.getTagCompound();
    }

    @Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check)
    {
        super.addInformation(stack, player, list, check);
        if(KeyHelper.isShiftDown())
            stack.getTagCompound();
    }

    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		
    	if(targetBlock != null && !worldIn.isAirBlock(targetBlock)){
    		if(playerIn.isSneaking())
	            playerIn.openGui(BuildingTools.instance, 3, worldIn, 0, 0, 0);
	        else
	        if(!worldIn.isRemote)
	        {
	            world = worldIn;
	            PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
	            System.out.println("FilterToolUsed");
	            if(getNBT(itemStackIn).getInteger("filter") == 1)
	                player.addPending(new ThreadTopsoil(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
	            else if(getNBT(itemStackIn).getInteger("filter") == 2)
	                player.addPending(new ThreadClearWater(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
	            else if(getNBT(itemStackIn).getInteger("filter") == 3)
	                player.addPending(new ThreadClearFoliage(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
	            else if(getNBT(itemStackIn).getInteger("filter") == 4)
	            	player.addPending(new ThreadBonemeal(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
	            else if(getNBT(itemStackIn).getInteger("filter") == 5)
	            	switch (ETreeTypes.values()[getNBT(itemStackIn).getInteger("treetype")]) {
					case Tree:
						new WorldGenTrees(true).generate(world, new Random(), targetBlock.up());
						break;
					case Taiga1:
						new WorldGenTaiga1().generate(world, new Random(), targetBlock.up());
						break;
					case Taiga2:
						new WorldGenTaiga2(true).generate(world, new Random(), targetBlock.up());
						break;
					case Swamp:
						new WorldGenSwamp().generate(world, new Random(), targetBlock.up());
						break;	
					case Shrub:
						new WorldGenShrub(Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false))).generate(world, new Random(), targetBlock.up());
						break;	
					case Savanna:
						new WorldGenSavannaTree(true).generate(world, new Random(), targetBlock.up());
						break;	
					case MegaPine:
						new WorldGenMegaPineTree(true, true).generate(world, new Random(), targetBlock.up());
						break;	
					case MegaJungle:
						IBlockState iblockstate = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
		                IBlockState iblockstate1 = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
						new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1).generate(world, new Random(), targetBlock.up());
						break;	
					case IceSpike:
						new WorldGenIceSpike().generate(world, new Random(), targetBlock.up());
						break;
					case Forest:
						new WorldGenTrees(true).generate(world, new Random(), targetBlock.up());
						break;	
					case Canopy:
						new WorldGenCanopyTree(true).generate(world, new Random(), targetBlock.up());
						break;	
					case BigTree:
						new WorldGenBigTree(true).generate(world, new Random(), targetBlock.up());
						break;	
					case BigMushroomBrown:
						new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK).generate(world, new Random(), targetBlock.up());
						break;	
					case BigMushroomRed:
						new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK).generate(world, new Random(), targetBlock.up());
						break;
					case Custom:
						player.addPending(new ThreadMakeTree(worldIn, targetBlock, playerIn, treeData));
						break;
					default:
						break;
					}
	            
	        }
    	}
    	
    	return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }

    @Override
	public void handleKey(EntityPlayer player, ItemStack itemStack, KeyCode key)
    {
        int radius = getNBT(itemStack).getInteger("radiusX");
        float yMult = 0.0F;
        float zMult = 0.0F;
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY"))
        {
            if(getNBT(itemStack).getInteger("radiusY") > 0)
                yMult = getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusY");
        } else
        if(getNBT(itemStack).getInteger("radiusX") > 0)
            yMult = getNBT(itemStack).getInteger("radiusY") / getNBT(itemStack).getInteger("radiusX");
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
        {
            if(getNBT(itemStack).getInteger("radiusZ") > 0)
                zMult = getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusZ");
        } else
        if(getNBT(itemStack).getInteger("radiusX") > 0)
            zMult = getNBT(itemStack).getInteger("radiusZ") / getNBT(itemStack).getInteger("radiusX");
        if(key == KeyCode.TOOL_INCREASE)
        {
            if(player.isSneaking())
                radius += 10;
            else
                radius++;
        } else
        if(key == KeyCode.TOOL_DECREASE)
            if(player.isSneaking())
                radius -= 10;
            else
                radius--;
        if(radius < 0)
            radius = 0;
        getNBT(itemStack).setInteger("radiusX", radius);
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY"))
            getNBT(itemStack).setInteger("radiusY", (int)yMult != 0 ? (int)(radius / yMult) : 1);
        else
            getNBT(itemStack).setInteger("radiusY", (int)yMult != 0 ? (int)(radius * yMult) : 0);
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
            getNBT(itemStack).setInteger("radiusZ", (int)zMult != 0 ? (int)(radius / zMult) : 1);
        else
            getNBT(itemStack).setInteger("radiusZ", (int)zMult != 0 ? (int)(radius * zMult) : 0);
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
    }
    
    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {        
    	
    	if(visualizer==null){
    		visualizer = new FilterShapeVisualizer();
    	}
    	
    	if(renderer == null){
    		renderer = new RenderHelper();
    	}
    	
        if(targetBlock != null){
        	
        	renderer.startDraw();
        	
	        if (event.getPlayer().isSneaking())
	        {
	        	renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
	        	renderer.finalizeDraw();
	        	//RenderHelper.renderBlockOutline(event.context, event.player, targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.partialTicks);
	            return true;
	        }
	        
	        if(checkVisualizer(visualizer, event.getPlayer().getHeldItemMainhand())){
                visualizer.RegenShape(
            			Shapes.Cuboid.generator, 
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusX"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusY"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusZ"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("filter")
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
		        			//RenderHelper.renderBlockOutline(event.context, event.player, newPos, RGBA.White.setAlpha(150), 1.0f, event.partialTicks);
		        		}
		        	}
	        	}
        	}
	        renderer.finalizeDraw();
        }
        return true;
    }
    
    public boolean checkVisualizer(FilterShapeVisualizer vis, ItemStack stack){
    	
    	return (
    			(visualizer.filterType != getNBT(stack).getInteger("filter")) ||
    			(visualizer.x != getNBT(stack).getInteger("radiusX")) ||
    			(visualizer.y != getNBT(stack).getInteger("radiusY")) ||
    			(visualizer.z != getNBT(stack).getInteger("radiusZ")) ||
    			updateVisualizer
    			);
    	
    }

    public void GuiButtonPressed(int buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown)
    {
        int multiplier = 0;
        if(isShiftDown)
            multiplier = 10;
        else
            multiplier = 1;
        int amount = 0;
        if(mouseButton == 0)
            amount = multiplier;
        else
        if(mouseButton == 1)
            amount = -multiplier;
        else
            return;
        if(buttonID == 1)
        {
            int filter = getNBT(thisStack).getInteger("filter");
            if(mouseButton == 0)
                filter++;
            else
            if(mouseButton == 1)
                filter--;
            if(filter < 1)
                filter = 5;
            if(filter > 5)
                filter = 1;
            //getNBT(stack).setInteger("fillorclear", 2);
            getNBT(thisStack).setInteger("filter", filter);
        } else
        if(buttonID == 2)
        {
            int radiusx = getNBT(thisStack).getInteger("radiusX");
            radiusx += amount;
            if(radiusx < 1)
                radiusx = 1;
            getNBT(thisStack).setInteger("radiusX", radiusx);
        } else
        if(buttonID == 3)
        {
            int radiusy = getNBT(thisStack).getInteger("radiusY");
            radiusy += amount;
            if(radiusy < 1)
                radiusy = 1;
            getNBT(thisStack).setInteger("radiusY", radiusy);
        } else
        if(buttonID == 4)
        {
            int radiusz = getNBT(thisStack).getInteger("radiusZ");
            radiusz += amount;
            if(radiusz < 1)
                radiusz = 1;
            getNBT(thisStack).setInteger("radiusZ", radiusz);
        } else
        if(buttonID == 5)
        {
            int depth = getNBT(thisStack).getInteger("topsoildepth");
            depth += amount;
            if(depth < 1)
                depth = 1;
            getNBT(thisStack).setInteger("topsoildepth", depth);
        } else
        if(buttonID == 6)
        {
            int fillorclear = getNBT(thisStack).getInteger("fillorclear");
            if(mouseButton == 0)
                fillorclear++;
            else
            if(mouseButton == 1)
                fillorclear--;
            if(fillorclear < 1)
                fillorclear = 2;
            if(fillorclear > 2)
                fillorclear = 1;
            getNBT(thisStack).setInteger("fillorclear", fillorclear);
        }
        
        if(buttonID == 7)
        {
            int treetype = getNBT(thisStack).getInteger("treetype");
            if(mouseButton == 0)
            	treetype++;
            else
            if(mouseButton == 1)
            	treetype--;
            if(treetype < 0)
            	treetype = ETreeTypes.values().length-1;
            if(treetype > ETreeTypes.values().length-1)
            	treetype = 0;
            getNBT(thisStack).setInteger("treetype", treetype);
        }
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(thisStack)));
        updateVisualizer = true;
    }
    
    public void SetTreeData(ProceduralTreeData data){
    	this.treeData.SetTreeHeight(data.GetTreeHeight());
    	this.treeData.SetTrunkBottom(data.GetTrunkBottom());
    	this.treeData.SetTrunkMiddle(data.GetTrunkMiddle());
    	this.treeData.SetTrunkTop(data.GetTrunkTop());
    	this.treeData.SetTrunkHeight(data.GetTrunkHeight());
    	this.treeData.SetTrunkMidPoint(data.GetTrunkMidPoint());
    	this.treeData.SetBranchStart(data.GetBranchStart());
    	this.treeData.SetFoliageStart(data.GetFoliageStart());
    	this.treeData.SetBranchSlope(data.GetBranchSlope());
    	this.treeData.SetLeafDensity(data.GetLeafDensity());
    	this.treeData.SetBranchDensity(data.GetBranchDensity());
    	this.treeData.SetFoliageShapes(data.GetFoliageShapes());
    	this.treeData.SetScaleWidth(data.GetScaleWidth());
    	this.treeData.SetTrunkWallThickness(data.GetTrunkWallThickness());
    	this.treeData.SetHollowTrunk(data.GetHollowTrunk());
    }
    
    @SuppressWarnings("deprecation")
	@Override
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
//		World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
//		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		
		for (String key : commandset) {
			String command = nbtcommand.getCompoundTag("Commands").getString(key);
			
			if (command.equals("GetButton")) {
				GuiButtonPressed(nbtcommand.getInteger("ButtonID"), nbtcommand.getInteger("Mouse"), nbtcommand.getBoolean("CTRL"), nbtcommand.getBoolean("ALT"), nbtcommand.getBoolean("SHIFT"));
			} else if (command.equals("SetTreeMaterial")) {
				ItemStack logItem = new ItemStack(nbtcommand.getCompoundTag("log"));
				ItemStack leafItem = new ItemStack(nbtcommand.getCompoundTag("leaf"));
				this.treeData.SetMatValues(Block.getBlockFromItem(logItem.getItem()).getStateFromMeta(logItem.getMetadata()), Block.getBlockFromItem(leafItem.getItem()).getStateFromMeta(leafItem.getMetadata()));
			} else {
			}
		}
	}

}
