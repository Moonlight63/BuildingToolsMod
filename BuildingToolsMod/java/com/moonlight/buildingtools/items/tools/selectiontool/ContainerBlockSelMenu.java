package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;

@SideOnly(Side.CLIENT)
public final class ContainerBlockSelMenu extends Container{
	
	private static InventoryBasic blocksINV = new InventoryBasic("tmp", true, 45);
	
    public static List<ItemStack> itemList = Lists.<ItemStack>newArrayList();

    public ContainerBlockSelMenu(){

        for (int i = 0; i < 5; ++i){
            for (int j = 0; j < 9; ++j){
                this.addSlotToContainer(new Slot(blocksINV, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }

        ContainerBlockSelMenu.scrollTo(0.0F);
    }

    public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

    /**
     * Updates the gui slots ItemStack's based on scroll position.
     */
    public static void scrollTo(float p_148329_1_){
        int i = (ContainerBlockSelMenu.itemList.size() + 9 - 1) / 9 - 5;
        int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);

        if (j < 0){
            j = 0;
        }

        for (int k = 0; k < 5; ++k){
            for (int l = 0; l < 9; ++l){
                int i1 = l + (k + j) * 9;

                if (i1 >= 0 && i1 < ContainerBlockSelMenu.itemList.size()){
                    blocksINV.setInventorySlotContents(l + k * 9, (ItemStack)ContainerBlockSelMenu.itemList.get(i1));
                }
                else{
                    blocksINV.setInventorySlotContents(l + k * 9, (ItemStack)null);
                }
            }
        }
    }
    
}