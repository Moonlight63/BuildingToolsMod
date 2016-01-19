package com.moonlight.buildingtools.utils;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface IOutlineDrawer {
	public boolean drawOutline(DrawBlockHighlightEvent event);
}
