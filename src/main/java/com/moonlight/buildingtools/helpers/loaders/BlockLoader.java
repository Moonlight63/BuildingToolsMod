package com.moonlight.buildingtools.helpers.loaders;

import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.items.tools.placetempblock.BlockTemporary;

import net.minecraft.block.Block;

public final class BlockLoader {

		//public static BlockGuide guideBlock;
		//public static BlockMarker markerBlock;
		public static BlockTemporary tempBlock;
		//public static Block alwaysdaynorain;
		public static Set<Block> blocksToRegister;
		
		
		public static void loadBlocks(){
			//guideBlock = new BlockGuide();
			//markerBlock = new BlockMarker();
			tempBlock = new BlockTemporary();
			//alwaysdaynorain = new debugblock();
			
			blocksToRegister = Sets.newHashSet();
			
			//blocksToRegister.add(guideBlock);
			//blocksToRegister.add(markerBlock);
			blocksToRegister.add(tempBlock);
			//blocksToRegister.add(alwaysdaynorain);
			
			
			//RegisterHelper.registerBlock(guideBlock);
			//RegisterHelper.registerTileEntity(TileEntityGuide.class, guideBlock);
			
			//RegisterHelper.registerBlock(markerBlock);
			RegisterHelper.registerBlock(tempBlock);
			//RegisterHelper.registerBlock(alwaysdaynorain);
			
		}	
}
