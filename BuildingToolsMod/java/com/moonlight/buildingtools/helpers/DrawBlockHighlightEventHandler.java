package com.moonlight.buildingtools.helpers;

import com.moonlight.buildingtools.utils.IOutlineDrawer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DrawBlockHighlightEventHandler
{
    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event)
    {
//        if (event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
//        Block block = event.player.worldObj.getBlockState(event.target.getBlockPos()).getBlock();

        boolean cancelEvent = false;
        boolean drewItem = false;
        

        if (event.currentItem != null)
        {
            Item item = event.currentItem.getItem();
            if (item instanceof IOutlineDrawer)
            {
                cancelEvent = ((IOutlineDrawer) item).drawOutline(event);
                drewItem = !cancelEvent;
            }
        }

//        if (!drewItem && block instanceof IOutlineDrawer)
//        {
//            cancelEvent = ((IOutlineDrawer) block).drawOutline(event);
//        }

        event.setCanceled(cancelEvent);
    }
}
