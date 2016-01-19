package com.moonlight.buildingtools.network.playercontroller;

public interface IExtendedPlayerController {

	/**
	 * Sets the extra reach the player should have.
	 */
	public void setReachDistanceExtension(float f);

	/**
	 * Gets the current reach extension.
	 */
	public float getReachDistanceExtension();
}
