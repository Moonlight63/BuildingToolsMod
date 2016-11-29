package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
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

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.buildingtool.BuildingShapeVisualizer;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IItemBlockAffector;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.Key.KeyCode;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;

public class ToolFilter extends Item
    implements IKeyHandler, IOutlineDrawer, /*IItemBlockAffector, IShapeable,*/ IGetGuiButtonPressed
{
	
	public EnumFacing targetFace;
	private static Set<Key.KeyCode> handledKeys;
    public BlockPos targetBlock;
    public World world;
    
    public ProceduralTreeData treeData = new ProceduralTreeData();
    
    public boolean updateVisualizer = true;
	private FilterShapeVisualizer visualizer;
	
	private RenderHelper renderer;

    static 
    {
        handledKeys = new HashSet<KeyCode>();
        handledKeys.add(com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_DECREASE);
    }
    

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
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){		
		if(this.world == null){
			this.world = world;
		}
		
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

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
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
	            PlayerWrapper player = (PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
	            System.out.println("FilterToolUsed");
	            if(getNBT(itemStackIn).getInteger("filter") == 1)
	            	//player.addPending(new ThreadBonemeal(worldIn, targetBlock, getNBT(itemStackIn).getInteger("radiusX"), getNBT(itemStackIn).getInteger("radiusY"), getNBT(itemStackIn).getInteger("radiusZ"), targetFace, playerIn));
	                player.addPending(new ThreadTopsoil(worldIn, targetBlock, getNBT(itemStackIn).getInteger("radiusX"), getNBT(itemStackIn).getInteger("radiusY"), getNBT(itemStackIn).getInteger("radiusZ"), getNBT(itemStackIn).getInteger("topsoildepth"), targetFace, playerIn));
	            else if(getNBT(itemStackIn).getInteger("filter") == 2)
	                player.addPending(new ThreadClearWater(worldIn, targetBlock, getNBT(itemStackIn).getInteger("radiusX"), getNBT(itemStackIn).getInteger("radiusY"), getNBT(itemStackIn).getInteger("radiusZ"), getNBT(itemStackIn).getInteger("fillorclear") != 1, targetFace, playerIn));
	            else if(getNBT(itemStackIn).getInteger("filter") == 3)
	                player.addPending(new ThreadClearFoliage(worldIn, targetBlock, getNBT(itemStackIn).getInteger("radiusX"), getNBT(itemStackIn).getInteger("radiusY"), getNBT(itemStackIn).getInteger("radiusZ"), getNBT(itemStackIn).getInteger("fillorclear") != 1, targetFace, playerIn));
	            else if(getNBT(itemStackIn).getInteger("filter") == 4)
	            	player.addPending(new ThreadBonemeal(worldIn, targetBlock, getNBT(itemStackIn).getInteger("radiusX"), getNBT(itemStackIn).getInteger("radiusY"), getNBT(itemStackIn).getInteger("radiusZ"), targetFace, playerIn));
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
	            		
	            	//new CustomTreeTest(true).generate(world, new Random(), targetBlock);
	            
	        }
    	}
    	
    	return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, 
            float hitZ)
    {
    	onItemRightClick(worldIn, playerIn, EnumHand.MAIN_HAND);
        return true;
    }

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
            getNBT(itemStack).setInteger("radiusY", (int)yMult != 0 ? (int)((float)radius / yMult) : 1);
        else
            getNBT(itemStack).setInteger("radiusY", (int)yMult != 0 ? (int)((float)radius * yMult) : 0);
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
            getNBT(itemStack).setInteger("radiusZ", (int)zMult != 0 ? (int)((float)radius / zMult) : 1);
        else
            getNBT(itemStack).setInteger("radiusZ", (int)zMult != 0 ? (int)((float)radius * zMult) : 0);
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
    }

    public Set getHandledKeys()
    {
        return handledKeys;
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

    public void GetGuiButtonPressed(byte buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown, ItemStack stack)
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
            int filter = getNBT(stack).getInteger("filter");
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
            getNBT(stack).setInteger("filter", filter);
        } else
        if(buttonID == 2)
        {
            int radiusx = getNBT(stack).getInteger("radiusX");
            radiusx += amount;
            if(radiusx < 1)
                radiusx = 1;
            getNBT(stack).setInteger("radiusX", radiusx);
        } else
        if(buttonID == 3)
        {
            int radiusy = getNBT(stack).getInteger("radiusY");
            radiusy += amount;
            if(radiusy < 1)
                radiusy = 1;
            getNBT(stack).setInteger("radiusY", radiusy);
        } else
        if(buttonID == 4)
        {
            int radiusz = getNBT(stack).getInteger("radiusZ");
            radiusz += amount;
            if(radiusz < 1)
                radiusz = 1;
            getNBT(stack).setInteger("radiusZ", radiusz);
        } else
        if(buttonID == 5)
        {
            int depth = getNBT(stack).getInteger("topsoildepth");
            depth += amount;
            if(depth < 1)
                depth = 1;
            getNBT(stack).setInteger("topsoildepth", depth);
        } else
        if(buttonID == 6)
        {
            int fillorclear = getNBT(stack).getInteger("fillorclear");
            if(mouseButton == 0)
                fillorclear++;
            else
            if(mouseButton == 1)
                fillorclear--;
            if(fillorclear < 1)
                fillorclear = 2;
            if(fillorclear > 2)
                fillorclear = 1;
            getNBT(stack).setInteger("fillorclear", fillorclear);
        }
        
        if(buttonID == 7)
        {
            int treetype = getNBT(stack).getInteger("treetype");
            if(mouseButton == 0)
            	treetype++;
            else
            if(mouseButton == 1)
            	treetype--;
            if(treetype < 0)
            	treetype = ETreeTypes.values().length-1;
            if(treetype > ETreeTypes.values().length-1)
            	treetype = 0;
            getNBT(stack).setInteger("treetype", treetype);
        }
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
        updateVisualizer = true;
    }
    
    @Override
	public int getMetadata(int damage)
    {
		updateVisualizer = true;
        return super.getMetadata(damage);
    }
    
    public void SetTreeData(ProceduralTreeData data){
    	this.treeData = data;
    	for(List list : this.treeData.foliage_shape){
    		System.out.println(list.toString());
    	}
    }
    
    public void SetTreeMaterials(int id1, int meat1, int id2, int meta2){
    	System.out.println(meat1);
    	this.treeData.SetMatValues(id1, meat1, id2, meta2);
    }

//	@Override
//	public void shapeFinished() {
//		// TODO Auto-generated method stub
//		
//	}

}
