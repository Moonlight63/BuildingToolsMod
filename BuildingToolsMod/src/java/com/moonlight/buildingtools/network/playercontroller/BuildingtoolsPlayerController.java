package com.moonlight.buildingtools.network.playercontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BuildingtoolsPlayerController extends PlayerControllerMP implements IExtendedPlayerController {

	private float distance = 0F;

	public BuildingtoolsPlayerController(Minecraft p_i45062_1_, NetHandlerPlayClient p_i45062_2_) {
		super(p_i45062_1_, p_i45062_2_);
	}
	
	@Override
	public boolean shouldDrawHUD()
    {
        return true;
    }

	@Override
	public float getBlockReachDistance() {
		return (this.isInCreativeMode() ? 5.0F : 4.5F) + distance;
	}

	@Override
	public void setReachDistanceExtension(float f) {
		distance = f;
	}

	@Override
	public float getReachDistanceExtension() {
		return distance;
	}
	
}
