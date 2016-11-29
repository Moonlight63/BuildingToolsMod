package com.moonlight.buildingtools.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.utils.RGBA;

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
		//IBlockState state = world.getBlockState(blockpos);
    	//state.getCollisionBoundingBox(world, blockpos);
        //block.setBlockBoundsBasedOnState(entityPlayer.worldObj, blockpos);
		
        float f1 = 0.02F;

        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) partialTicks;
        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) partialTicks;
        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) partialTicks;
        
        //AxisAlignedBB box = state.getCollisionBoundingBox(entityPlayer.worldObj, blockpos).expand(f1, f1, f1).offset(-d0, -d1, -d2);
        AxisAlignedBB box = aabb.expand(f1, f1, f1).offset(-d0, -d1, -d2).offset(blockpos);
        
        
        int h = worldrenderer.getVertexCount() * worldrenderer.getVertexFormat().getNextOffset();
        
        if(h > worldrenderer.getByteBuffer().capacity() - 1000){
        	return;
        }
//        worldrenderer.checkAndGrow();
//        if(worldrenderer.getByteBuffer().remaining()!=8388596)
//        	System.out.println(i);
        
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
	
	
    // Similar to vanilla's "drawSelectionBox" with some customizability and without block checks
    public static void renderBlockOutline(RenderGlobal context, EntityPlayer entityPlayer, BlockPos blockpos, RGBA colour, float lineWidth, float partialTicks){
    	World world = entityPlayer.worldObj;
		if(world == null)
        	return;
		//IBlockState state = world.getBlockState(blockpos);
    	//state.getCollisionBoundingBox(world, blockpos);
        
        renderAABBOutline(context, entityPlayer, FULL_BLOCK_AABB.offset(blockpos), colour, lineWidth, partialTicks);
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
        
        //renderAABBOutline(context, entityPlayer, new AxisAlignedBB(new BlockPos(p1x, p1y, p1z), new BlockPos(p2x, p2y, p2z)), colour, lineWidth, partialTicks);
        
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
    
    public static void renderAABBOutline(RenderGlobal context, EntityPlayer entityPlayer, AxisAlignedBB aabb, RGBA colour, float lineWidth, float partialTicks)
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(colour.red, colour.green, colour.blue, colour.alpha);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        float f1 = 0.02F;

        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) partialTicks;
        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) partialTicks;
        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) partialTicks;
        RenderGlobal.drawSelectionBoundingBox(aabb.expand((double) f1, (double) f1, (double) f1).offset(-d0, -d1, -d2), colour.red, colour.green, colour.blue, colour.alpha);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public static Vec3d translateToWorldCoords(Entity entity, float frame) {
        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) frame;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) frame;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) frame;

        //System.out.println(new Vec3(interpPosX, interpPosY, interpPosZ));
        return new Vec3d(-interpPosX, -interpPosY, -interpPosZ);
        
        //GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
    }
    
}