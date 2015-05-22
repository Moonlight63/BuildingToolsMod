package com.moonlight.buildingtools.helpers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import com.moonlight.buildingtools.utils.RGBA;

public class RenderHelper
{
    // Similar to vanilla's "drawSelectionBox" with some customisability and without block checks
    public static void renderBlockOutline(RenderGlobal context, EntityPlayer entityPlayer, BlockPos blockpos, RGBA colour, float lineWidth, float partialTicks){
        Block block = entityPlayer.worldObj.getBlockState(blockpos).getBlock();
        block.setBlockBoundsBasedOnState(entityPlayer.worldObj, blockpos);
        renderAABBOutline(context, entityPlayer, block.getSelectedBoundingBox(entityPlayer.worldObj, blockpos), colour, lineWidth, partialTicks);
    }

    public static void renderSelectionBox(RenderGlobal context, EntityPlayer entityPlayer, BlockPos blockpos, BlockPos blockpos2, RGBA colour, float lineWidth, float partialTicks){
       
        int p1x = (blockpos.getX() <= blockpos2.getX()) ? blockpos.getX() : blockpos.getX() + 1;
        int p1y = (blockpos.getY() <= blockpos2.getY()) ? blockpos.getY() : blockpos.getY() + 1;
        int p1z = (blockpos.getZ() <= blockpos2.getZ()) ? blockpos.getZ() : blockpos.getZ() + 1;
        int p2x = (blockpos2.getX() < blockpos.getX()) ? blockpos2.getX() : blockpos2.getX() + 1;
        int p2y = (blockpos2.getY() < blockpos.getY()) ? blockpos2.getY() : blockpos2.getY() + 1;
        int p2z = (blockpos2.getZ() < blockpos.getZ()) ? blockpos2.getZ() : blockpos2.getZ() + 1;
        
        renderAABBOutline(context, entityPlayer, new AxisAlignedBB(new BlockPos(p1x, p1y, p1z), new BlockPos(p2x, p2y, p2z)), colour, lineWidth, partialTicks);
        
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
        float f1 = 0.02F;

        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) partialTicks;
        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) partialTicks;
        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) partialTicks;
        RenderGlobal.drawOutlinedBoundingBox(aabb.expand((double) f1, (double) f1, (double) f1).offset(-d0, -d1, -d2), -1);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public static Vec3 translateToWorldCoords(Entity entity, float frame) {
        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) frame;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) frame;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) frame;

        //System.out.println(new Vec3(interpPosX, interpPosY, interpPosZ));
        return new Vec3(-interpPosX, -interpPosY, -interpPosZ);
        
        //GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
    }
}