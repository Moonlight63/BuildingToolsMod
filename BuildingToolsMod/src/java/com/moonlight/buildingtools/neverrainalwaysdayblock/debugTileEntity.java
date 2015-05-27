package com.moonlight.buildingtools.neverrainalwaysdayblock;

import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

public class debugTileEntity extends TileEntity implements IUpdatePlayerListBox{
	
	int tick = 0;
	 
    public static final String publicName = "tileEntitySmasher";
    private String name = "tileEntitySmasher";
    
    public debugTileEntity(){
	}
 
    public String getName() {
 
        return name;
    }

	@Override
	public void update() {
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
