package com.moonlight.buildingtools.items.tools.undoTool;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.items.tools.selectiontool.ThreadPasteClipboard;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.KeyHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ToolUndo extends ToolBase{
	
	public ToolUndo(){
		super();
		setUnlocalizedName("ToolUndo");
		setRegistryName("undotool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        list.add("Right click to undo your last change.");
        list.add("");
        list.add("Shift + Right click to view/load/save old undo sessions. (USE ONLY IN CASE OF EMERGENCY)");
        
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		if(playerIn.isSneaking()){
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIUndoSave, worldIn, 0, 0, 0);
			//PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			//player.addPending(new ThreadSaveUndoList(playerIn, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
		}
		else{
			if(!worldIn.isRemote){
				System.out.println("Used Undo Tool");
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				//System.out.println(player.undolist);
				if(!player.undolist.isEmpty() && player.UndoIsSaved){
					player.addPending(new ThreadPasteClipboard(worldIn, playerIn, /*player.lastUndo, */new LinkedHashSet<Entity>()));		
					playerIn.addChatMessage(new TextComponentString("Undoing"));
				}
				if(!player.UndoIsSaved){
					playerIn.addChatMessage(new TextComponentString("The last operation is not finished saving. Please Wait!"));
				}
				else if (player.undolist.isEmpty()){
					playerIn.addChatMessage(new TextComponentString("No Undo operations are recorded"));
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
		
	@Override
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
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
		//World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		
		for (String key : commandset) {
			String command = nbtcommand.getCompoundTag("Commands").getString(key);
			
			switch (command) {
			case "LoadFile":
				player.addPending(new ThreadLoadUndo(currPlayer, nbtcommand.getString("File")));
				break;
			default:
				break;
			}
		}
	}
	
	
}
