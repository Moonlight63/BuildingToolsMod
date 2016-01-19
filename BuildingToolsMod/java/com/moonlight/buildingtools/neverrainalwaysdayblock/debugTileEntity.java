package com.moonlight.buildingtools.neverrainalwaysdayblock;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.tileentity.TileEntity;

public class debugTileEntity extends TileEntity implements ITickable{
	
	int tick = 0;
	 
    public static final String publicName = "tileEntitySmasher";
    private String name = "tileEntitySmasher";
    
    public debugTileEntity(){
	}
 
    public String getName() {
 
        return name;
    }


	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if(!worldObj.isRemote) {
            tick++;
            if(tick == 100) {
                this.worldObj.setWorldTime(1600);
                this.worldObj.setRainStrength(0);
                this.worldObj.setThunderStrength(0);
                tick = 0;
            }
        }
	}

}
