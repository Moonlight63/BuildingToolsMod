package com.moonlight.buildingtools.items.tools;

public interface BlockChangeBase {
	public static boolean isFinished = false;
	public void perform();
	public boolean isFinished();
}
