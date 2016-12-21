package com.moonlight.buildingtools.helpers;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderHelper
{
	public static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public Tessellator tessellator;
	public VertexBuffer worldrenderer;
	
	public RenderHelper(){
		tessellator = new Tessellator(2097152*4);
        worldrenderer = tessellator.getBuffer();
	}
	
	public void addOutlineToBuffer(EntityPlayer entityPlayer, BlockPos blockpos, RGBA colour, float partialTicks){
		addOutlineToBuffer(entityPlayer, blockpos, colour, partialTicks, FULL_BLOCK_AABB);
	}
	
	public void addOutlineToBuffer(EntityPlayer entityPlayer, BlockPos blockpos, RGBA colour, float partialTicks, AxisAlignedBB aabb){
		World world = entityPlayer.worldObj;
		if(world == null)
        	return;
		
        float f1 = 0.02F;

        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks;
        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * partialTicks;
        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks;
        
        AxisAlignedBB box = aabb.expand(f1, f1, f1).offset(-d0, -d1, -d2).offset(blockpos);
        
        int h = worldrenderer.getVertexCount() * worldrenderer.getVertexFormat().getNextOffset();
        
        if(h > worldrenderer.getByteBuffer().capacity() - 1000){
        	return;
        }
        
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(colour.red, colour.green, colour.blue, colour.alpha).endVertex();
	}
	
	public void startDraw(){
		GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        worldrenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
	}
	
	public void finalizeDraw(){
		tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
	}

    public void renderSelectionOutline(EntityPlayer entityPlayer, BlockPos blockpos, BlockPos blockpos2, RGBA colour, float partialTicks){
    	int p1x = (blockpos.getX() <= blockpos2.getX()) ? blockpos.getX() : blockpos.getX() + 1;
        int p1y = (blockpos.getY() <= blockpos2.getY()) ? blockpos.getY() : blockpos.getY() + 1;
        int p1z = (blockpos.getZ() <= blockpos2.getZ()) ? blockpos.getZ() : blockpos.getZ() + 1;
        int p2x = (blockpos2.getX() < blockpos.getX()) ? blockpos2.getX() : blockpos2.getX() + 1;
        int p2y = (blockpos2.getY() < blockpos.getY()) ? blockpos2.getY() : blockpos2.getY() + 1;
        int p2z = (blockpos2.getZ() < blockpos.getZ()) ? blockpos2.getZ() : blockpos2.getZ() + 1;
        
		addOutlineToBuffer(entityPlayer, new BlockPos(0,0,0), colour, partialTicks, new AxisAlignedBB(new BlockPos(p1x, p1y, p1z), new BlockPos(p2x, p2y, p2z)));
	}
    public static void renderSelectionBox(RenderGlobal context, EntityPlayer entityPlayer, BlockPos blockpos, BlockPos blockpos2, RGBA colour, float lineWidth, float partialTicks){
       
        int p1x = (blockpos.getX() <= blockpos2.getX()) ? blockpos.getX() : blockpos.getX() + 1;
        int p1y = (blockpos.getY() <= blockpos2.getY()) ? blockpos.getY() : blockpos.getY() + 1;
        int p1z = (blockpos.getZ() <= blockpos2.getZ()) ? blockpos.getZ() : blockpos.getZ() + 1;
        int p2x = (blockpos2.getX() < blockpos.getX()) ? blockpos2.getX() : blockpos2.getX() + 1;
        int p2y = (blockpos2.getY() < blockpos.getY()) ? blockpos2.getY() : blockpos2.getY() + 1;
        int p2z = (blockpos2.getZ() < blockpos.getZ()) ? blockpos2.getZ() : blockpos2.getZ() + 1;
                
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GL11.glLineWidth(0.5F);
        GL11.glBegin(GL11.GL_LINES);
        
        GlStateManager.color(0, 0.9F, 0, 0.2F);
        
        for (int selx = Math.min(p1x, p2x) + 1; selx < Math.max(p1x, p2x); selx++){
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(selx + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        }
        
        for (int sely = Math.min(p1y, p2y) + 1; sely < Math.max(p1y, p2y); sely++){
        	
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p1z + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, sely + translateToWorldCoords(entity, partialTicks).yCoord, p2z + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        }
        
        for (int selz = Math.min(p1z, p2z) + 1; selz < Math.max(p1z, p2z); selz++){
        	
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, p1y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p1x + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	GL11.glVertex3d(p2x + translateToWorldCoords(entity, partialTicks).xCoord, p2y + translateToWorldCoords(entity, partialTicks).yCoord, selz + translateToWorldCoords(entity, partialTicks).zCoord);
        	
        }
        
        GL11.glEnd();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static Vec3d translateToWorldCoords(Entity entity, float frame) {
        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;

        //System.out.println(new Vec3(interpPosX, interpPosY, interpPosZ));
        return new Vec3d(-interpPosX, -interpPosY, -interpPosZ);
        //GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
    }
    
}