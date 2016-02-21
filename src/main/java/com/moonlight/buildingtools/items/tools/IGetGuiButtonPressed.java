package com.moonlight.buildingtools.items.tools;

import net.minecraft.item.ItemStack;

public interface IGetGuiButtonPressed {
	public void GetGuiButtonPressed(byte buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown, ItemStack stack);
}
