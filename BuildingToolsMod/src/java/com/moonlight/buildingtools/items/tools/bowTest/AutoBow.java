package com.moonlight.buildingtools.items.tools.bowTest;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
//import java.util.Optional;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

public class AutoBow extends ItemBow{
    
    public World world;
    public ItemStack thisStack;

    public AutoBow()
    {
    	this.setUnlocalizedName("autoBow");
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     *  
     * @param timeLeft The amount of ticks left before the using would have been complete
     */
    public int timeUsed = 5;
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer playerIn, int timeLeft){
    	timeUsed++;
    	if(timeUsed > 5){
    		System.out.println(timeUsed);
    		timeUsed = 0;
	    	EntityArrow entityarrow = new EntityArrow(world, playerIn, 3);
	    	world.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F));
	    	if (!world.isRemote){
	        	world.spawnEntityInWorld(entityarrow);
	        }
    	}
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 2000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
    	world = worldIn;
    	thisStack = itemStackIn;
        net.minecraftforge.event.entity.player.ArrowNockEvent event = new net.minecraftforge.event.entity.player.ArrowNockEvent(playerIn, itemStackIn);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return event.result;

        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow))
        {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 1;
    }
	
}
