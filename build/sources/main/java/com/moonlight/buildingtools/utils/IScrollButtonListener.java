package com.moonlight.buildingtools.utils;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiSlider;

public interface IScrollButtonListener {

	public void ScrollButtonPressed(GuiButton button);

	public void GetGuiSliderValue(GuiSlider slider);
}
