package com.moonlight.buildingtools.utils;

import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IItemBlockAffector
{
    public Set<BlockPos> blocksAffected(ItemStack item, World world, BlockPos origin, EnumFacing side, int radius, boolean fill);
}
