package com.moonlight.buildingtools.utils;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IKeyHandler
{
    public abstract void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key);

    public abstract Set<Key.KeyCode> getHandledKeys();
}
