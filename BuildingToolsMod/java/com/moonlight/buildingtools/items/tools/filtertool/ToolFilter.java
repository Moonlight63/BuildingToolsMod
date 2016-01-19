// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ToolFilter.java

package com.moonlight.buildingtools.items.tools.filtertool;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.IToolOverrideHitDistance;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.*;
import java.io.PrintStream;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

// Referenced classes of package com.moonlight.buildingtools.items.tools.filtertool:
//            ThreadTopsoil, ThreadClearWater, ThreadClearFoliage

public class ToolFilter extends Item
    implements IKeyHandler, IOutlineDrawer, IItemBlockAffector, IShapeable, IGetGuiButtonPressed, IToolOverrideHitDistance
{

    public ToolFilter()
    {
        outlineing = true;
        setUnlocalizedName("filterTool");
        setCreativeTab(BuildingTools.tabBT);
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
        }
        return stack.getTagCompound();
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
    {
        super.addInformation(stack, player, list, check);
        if(KeyHelper.isShiftDown())
            stack.getTagCompound();
    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if(playerIn.isSneaking())
            playerIn.openGui(BuildingTools.instance, 3, worldIn, 0, 0, 0);
        return itemStackIn;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, 
            float hitZ)
    {
        if(playerIn.isSneaking())
            playerIn.openGui(BuildingTools.instance, 3, worldIn, 0, 0, 0);
        else
        if(!worldIn.isRemote)
        {
            world = worldIn;
            outlineing = false;
            PlayerWrapper player = (PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
            System.out.println("FilterToolUsed");
            if(stack.getTagCompound().getInteger("filter") == 1)
                player.addPending(new ThreadTopsoil(worldIn, pos, getNBT(stack).getInteger("radiusX"), getNBT(stack).getInteger("radiusY"), getNBT(stack).getInteger("radiusZ"), getNBT(stack).getInteger("topsoildepth"), side, playerIn));
            else
            if(stack.getTagCompound().getInteger("filter") == 2)
                player.addPending(new ThreadClearWater(worldIn, pos, getNBT(stack).getInteger("radiusX"), getNBT(stack).getInteger("radiusY"), getNBT(stack).getInteger("radiusZ"), getNBT(stack).getInteger("fillorclear") != 1, side, playerIn));
            else
            if(stack.getTagCompound().getInteger("filter") == 3)
                player.addPending(new ThreadClearFoliage(worldIn, pos, getNBT(stack).getInteger("radiusX"), getNBT(stack).getInteger("radiusY"), getNBT(stack).getInteger("radiusZ"), getNBT(stack).getInteger("fillorclear") != 1, side, playerIn));
            System.out.println(world.getBlockState(targetBlock).getBlock().getClass());
            outlineing = true;
            return true;
        }
        return true;
    }

    public void handleKey(EntityPlayer player, ItemStack itemStack, com.moonlight.buildingtools.utils.Key.KeyCode key)
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
        if(key == com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_INCREASE)
        {
            if(player.isSneaking())
                radius += 10;
            else
                radius++;
        } else
        if(key == com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_DECREASE)
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

    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
        BlockPos target = event.target.getBlockPos();
        world = event.player.worldObj;
        thisStack = event.currentItem;
        if(event.player.isSneaking())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(150), 2.0F, event.partialTicks);
            return true;
        }
        Set blocks = blocksAffected(event.currentItem, world, target, event.target.sideHit, getNBT(event.currentItem).getInteger("radiusX") >= 25 ? 25 : getNBT(event.currentItem).getInteger("radiusX"), false);
        if(blocks == null || blocks.size() == 0)
            return false;
        BlockPos blockPos;
        for(Iterator iterator = blocks.iterator(); iterator.hasNext(); RenderHelper.renderBlockOutline(event.context, event.player, blockPos, RGBA.Green.setAlpha(150), 2.0F, event.partialTicks))
            blockPos = (BlockPos)iterator.next();

        return true;
    }

    public Set blocksAffected(ItemStack item, World world, BlockPos origin, EnumFacing side, int radius, boolean fill)
    {
        if(!(item.getItem() instanceof ToolFilter))
            return null;
        targetBlock = origin;
        blocksForOutline = Sets.newHashSet();
        Shapes.Cuboid.generator.generateShape(getNBT(item).getInteger("radiusX"), getNBT(item).getInteger("radiusY"), getNBT(item).getInteger("radiusZ"), this, true);
        if(outlineing)
            return blocksForOutline;
        else
            return null;
    }

    public void setBlock(BlockPos bpos)
    {
        if(outlineing)
        {
            if(blocksForOutline == null)
                blocksForOutline = Sets.newHashSet();
            if(getNBT(thisStack).getInteger("filter") == 1)
            {
                if(!world.isAirBlock(bpos.add(targetBlock)) && world.isAirBlock(bpos.add(targetBlock).up()))
                    blocksForOutline.add(new BlockPos(bpos.add(targetBlock)));
            } else
            if(getNBT(thisStack).getInteger("filter") == 2)
            {
                if(!world.isAirBlock(bpos.add(targetBlock)) && (world.getBlockState(bpos.add(targetBlock)) == Blocks.water.getDefaultState() || world.getBlockState(bpos.add(targetBlock)) == Blocks.flowing_water.getDefaultState()) && world.getBlockState(bpos.add(targetBlock).up()) != Blocks.water.getDefaultState() && world.getBlockState(bpos.add(targetBlock).up()) != Blocks.flowing_water.getDefaultState())
                    blocksForOutline.add(new BlockPos(bpos.add(targetBlock)));
            } else
            if(getNBT(thisStack).getInteger("filter") == 3 && ((world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockDoublePlant) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockLeaves) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockBush) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockTallGrass) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockCrops) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockFlower) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockVine) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockReed) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockSapling) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockWeb) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockCactus) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockLilyPad) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockSign) || (world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockStem)))
                blocksForOutline.add(new BlockPos(bpos.add(targetBlock)));
        }
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
                filter = 3;
            if(filter > 3)
                filter = 1;
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
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
    }

    private static Set handledKeys;
    public Set blocksForOutline;
    private boolean outlineing;
    public BlockPos targetBlock;
    public World world;
    public ItemStack thisStack;

    static 
    {
        handledKeys = new HashSet();
        handledKeys.add(com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(com.moonlight.buildingtools.utils.Key.KeyCode.TOOL_DECREASE);
    }
}
