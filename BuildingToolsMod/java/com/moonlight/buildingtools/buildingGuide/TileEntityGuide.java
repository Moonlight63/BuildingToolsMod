package com.moonlight.buildingtools.buildingGuide;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

public class TileEntityGuide extends TileEntity implements IShapeable, IActivateAwareTile {

	private Set<BlockPos> shape;
	private Set<BlockPos> previousShape;
	public Set<BlockPos> markers;
	public Set<BlockPos> previousmarkers;
	private float timeSinceChange = 0;
	
	public Integer width;
	public Integer height;
	public Integer depth;
	public Enum<Shapes> mode = Shapes.Sphere;
	public Integer color;
	
	public TileEntityGuide(){
	}
	
	public int getWidth() {
		if(width != null)
			return width;
		else
			return 1;
	}
	
	public int getHeight() {
		if(height != null)
			return height;
		else
			return 1;
	}
	
	public int getDepth() {
		if(depth != null)
			return depth;
		else
			return 1;
	}
	
	public int getColor() {
		if(color != null)
			return color & 0x00FFFFFF;
		else
			return 1 & 0x00FFFFFF;
	}
	
	public int getCount() {
		if (shape == null) recreateShape();
		return shape.size();
	}
	
	public Shapes getCurrentMode() {
		mode.getDeclaringClass().getEnumConstants();
		return (Shapes) mode;
	}
	
	public void setWidth(int w) {
		Preconditions.checkArgument(w > 0, "Width must be > 0");
		width = w;

		if (getCurrentMode().fixedRatio) {
			height = w;
			depth = w;
		}

		//recreateShape();
	}
	
	public void setDepth(int d) {
		Preconditions.checkArgument(d > 0, "Depth must be > 0");
		depth = d;

		if (getCurrentMode().fixedRatio) {
			width = d;
			depth = d;
		}

		//recreateShape();
	}
	
	public void setHeight(int h) {
		Preconditions.checkArgument(h > 0, "Height must be > 0");
		height = h;

		if (getCurrentMode().fixedRatio) {
			width = h;
			depth = h;
		}

		//recreateShape();
	}
	
	public void setShape(Shapes shape) {
		mode = shape;

		if (getCurrentMode().fixedRatio) {
			final int width = getWidth();
			height = width;
			depth = width;
		}

		//recreateShape();
	}
	
	public void setColor(int color) {
		this.color = (color & 0x00FFFFFF);
	}

	
	public boolean shouldRender() {
		return true;//Config.guideRedstone == 0 || ((Config.guideRedstone < 0) ^ active.get());
	}

	/*@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (timeSinceChange < 1.0) {
				timeSinceChange = (float)Math.min(1.0f, timeSinceChange + 0.1);
			}
		}
	}*/

	public float getTimeSinceChange() {
		return timeSinceChange;
	}
	
	public boolean removeMarkers(Set<BlockPos> mrks){
		if(mrks != null){
			for (BlockPos markerPos : mrks){
//				if(worldObj.getBlockState(markerPos.add(pos)) == BlockLoader.markerBlock.getDefaultState()){  REMOVE COMMENT TO REACTIVATE BLOCK
					worldObj.setBlockToAir(markerPos.add(pos));
					//return false;
//				} REMOVE COMMENT TO REACTIVATE BLOCK
			}
		}
		return true;
	}

	public void recreateShape() {
		
		previousShape = shape;
		shape = Sets.newHashSet();
		//removeMarkers();
		
		previousmarkers = markers;
		markers = Sets.newHashSet();
		
		getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this, false);
		
		//if(previousmarkers != null){
		//	for (BlockPos markerPos : previousmarkers){
		//		if(worldObj.getBlockState(markerPos) == blockLoader.markerBlock.getDefaultState()){
		//			worldObj.setBlockToAir(markerPos);
		//		}
		//	}
		//}
		
		removeMarkers(previousShape);
		
		if(shape != null){
			for (BlockPos bpos : shape){
				if(pos != null){
					if(worldObj.isAirBlock(bpos.add(pos))){
					//if(worldObj.getBlockState(new BlockPos(coord.x, coord.y, coord.z)) == Blocks.air.getDefaultState()){
//						worldObj.setBlockState(bpos.add(pos), BlockLoader.markerBlock.getDefaultState()); REMOVE COMMENT TO REACTIVATE BLOCK
						markers.add(bpos.add(pos));
					}
				}
			}
		}
		
		this.markDirty();
	}

	@Override
	public void setBlock(BlockPos bpos) {
		shape.add(bpos);
	}

	public Set<BlockPos> getShape() {
		return shape;
	}

	public Set<BlockPos> getPreviousShape() {
		return previousShape;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(getWidth(), getHeight(), getDepth());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 64;
	}

	private void switchMode(EntityPlayer player) {
		switchMode();
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_mode", getCurrentMode().getLocalizedName()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	
	public Shapes switchMode() {
		final int next = mode.ordinal() + 1;
		if (next >= mode.getDeclaringClass().getEnumConstants().length) mode = mode.getDeclaringClass().getEnumConstants()[0];
		else mode = mode.getDeclaringClass().getEnumConstants()[next];
		final Shapes shape = (Shapes) mode;

		if (shape.fixedRatio) {
			final int width = getWidth();
			height = width;
			depth = width;
		}

		recreateShape();
		return shape;
	}

	private void changeDimensions(EntityPlayer player, EnumFacing orientation) {
		changeDimensions(orientation);
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_size", width, height, depth));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private void changeDimensions(EnumFacing orientation) {
		switch (orientation) {
			case EAST:
				//dec(width);
				if (getWidth() > 0) setWidth(getWidth() - 1);
				break;
			case WEST:
				//inc(width);
				setWidth(getWidth() + 1);
				break;

			case SOUTH:
				//dec(depth);
				if (getDepth() > 0) setDepth(getDepth() - 1);
				break;
			case NORTH:
				//inc(depth);
				setDepth(getDepth() + 1);
				break;

			case DOWN:
				//dec(height);
				if (getHeight() > 0) setHeight(getHeight() - 1);
				break;
			case UP:
				//inc(height);
				setHeight(getHeight() + 1);
				break;

			default:
				return;
		}

		if (getCurrentMode().fixedRatio) {
			int h = getHeight();
			int w = getWidth();
			int d = getDepth();
			if (w != h && w != d) {
				height = (w);
				depth = (w);
			} else if (h != w && h != d) {
				depth = (h);
				width = (h);
			} else if (d != w && d != h) {
				width = (d);
				height = (d);
			}
		}
		recreateShape();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;
		playerIn.addChatMessage(new ChatComponentText("Got Right Click"));
		if (playerIn.isSneaking()) switchMode(playerIn);
		else {
			ItemStack heldStack = playerIn.getHeldItem();
			if (heldStack == null || !tryUseItem(playerIn, heldStack)) changeDimensions(playerIn, side);
		}
		
		return true;
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {	
		previousShape = shape;
		return removeMarkers(shape);
	}
	
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		previousShape = shape;
		removeMarkers(shape);
	}

	protected boolean tryUseItem(EntityPlayer player, ItemStack heldStack) {
		if (player.capabilities.isCreativeMode && isInFillMode()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				if(isInSolidMode()){
					getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this, true);
				}
				replaceBlocks(heldStack, heldItem);
				return true;
			}
		}

		return false;
	}

	protected void replaceBlocks(ItemStack heldStack, final Item heldItem) {
		if (shape == null) recreateShape();

		final ItemBlock itemBlock = (ItemBlock)heldItem;
		final Block block = itemBlock.getBlock();
		final int blockMeta = itemBlock.getMetadata(heldStack.getItemDamage());
		//if(isInSolidMode()){
		//	getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this, true);
		//}
		for (BlockPos bpos : shape)
			worldObj.setBlockState(bpos.add(pos), block.getStateFromMeta(blockMeta));
			//worldObj.setBlock(pos.getX() + coord.x, pos.getY() + coord.y, pos.getZ() + coord.z, block, blockMeta, BlockNotifyFlags.ALL);
	}

	private boolean isInFillMode() {
		return worldObj.getBlockState(new BlockPos(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ())) == Blocks.obsidian.getDefaultState();//worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.obsidian;
	}
	private boolean isInSolidMode() {
		return worldObj.getBlockState(new BlockPos(this.pos.getX(), this.pos.getY() - 1, this.pos.getZ())) == Blocks.obsidian.getDefaultState();//worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.obsidian;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
	 NBTTagCompound var1 = new NBTTagCompound();
	 this.writeToNBT(var1);
	 return new S35PacketUpdateTileEntity(pos, 3, var1);
	}
		
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}
	
	//@Override
	public void writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);
		compound.setInteger("width", this.getWidth());
		compound.setInteger("height", this.getHeight());
		compound.setInteger("depth", this.getDepth());		
		compound.setInteger("curShape", mode.ordinal());
	}
	
	//@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);
		if(compound.getInteger("width") > 0)
			this.setWidth(compound.getInteger("width"));
		else
			this.setWidth(1);
		
		if(compound.getInteger("height") > 0)
			this.setHeight(compound.getInteger("height"));
		else
			this.setHeight(1);
		
		if(compound.getInteger("depth") > 0)
			this.setDepth(compound.getInteger("depth"));
		else
			this.setDepth(1);
		
		this.mode = mode.getDeclaringClass().getEnumConstants()[compound.getInteger("curShape")];
		
		shape = Sets.newHashSet();
		getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this, false);
		previousShape = shape;
		
		//recreateShape();
	}

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void onNeighbourChanged(Block block) {
		if (Config.guideRedstone != 0) {
			boolean redstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			active.set(redstoneState);
			sync();
		}
	}*/
}
