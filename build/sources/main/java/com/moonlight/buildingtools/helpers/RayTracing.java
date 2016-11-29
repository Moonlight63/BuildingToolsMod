package com.moonlight.buildingtools.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RayTracing {

	private static RayTracing _instance;
	private RayTracing(){}	
	public static RayTracing instance(){
		if(_instance == null)
			_instance = new RayTracing();
		return _instance;
	}
	
	private RayTraceResult target = null;
	@SideOnly(Side.CLIENT)
	private Minecraft mc = Minecraft.getMinecraft();
	@SideOnly(Side.CLIENT)
	public void fire(double distance, boolean hitLiquids){
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY){
			this.target = mc.objectMouseOver;
			return;
		}
		
		Entity viewpoint = mc.getRenderViewEntity();
		if (viewpoint == null) return;
			
		this.target = this.rayTrace(viewpoint, distance, 0, hitLiquids);
		
		if (this.target == null) return;
	}
	
	public RayTraceResult getTarget(){ 
		return this.target;
	}
	
    public RayTraceResult rayTrace(Entity entity, double dist, float par3, boolean hitLiquid)
    {
        Vec3d vec3  = entity.getPositionEyes(par3);
        Vec3d vec31 = entity.getLook(par3);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist);
        
        return entity.worldObj.rayTraceBlocks(vec3, vec32, hitLiquid);
    }	
    
}
