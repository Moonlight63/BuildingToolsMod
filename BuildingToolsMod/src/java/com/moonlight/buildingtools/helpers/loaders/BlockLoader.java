package com.moonlight.buildingtools.helpers.loaders;

import java.util.Set;

import net.minecraft.block.Block;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.buildingGuide.BlockGuide;
import com.moonlight.buildingtools.buildingGuide.BlockMarker;
import com.moonlight.buildingtools.buildingGuide.TileEntityGuide;
import com.moonlight.buildingtools.items.tools.placetempblock.BlockTemporary;

public final class BlockLoader {

		public static BlockGuide guideBlock;
		public static BlockMarker markerBlock;
		public static BlockTemporary tempBlock;
		public static Set<Block> blocksToRegister;
		
		public static void loadBlocks(){
			guideBlock = new BlockGuide();
			markerBlock = new BlockMarker();
			tempBlock = new BlockTemporary();
			
			blocksToRegister = Sets.newHashSet();
			
			blocksToRegister.add(guideBlock);
			blocksToRegister.add(markerBlock);
			blocksToRegister.add(tempBlock);
			
			
			RegisterHelper.registerBlock(guideBlock);
			RegisterHelper.registerTileEntity(TileEntityGuide.class, guideBlock);
			
			RegisterHelper.registerBlock(markerBlock);
			RegisterHelper.registerBlock(tempBlock);
			
		}	
}
