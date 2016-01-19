package com.moonlight.buildingtools.helpers.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtils {
	public static final double lengthSq(double x, double y, double z) {
		return x * x + y * y + z * z;
	}

	public static final double lengthSq(double x, double z) {
		return x * x + z * z;
	}

	public static Matrix4f createEntityRotateMatrix(Entity entity) {
		double yaw = Math.toRadians(entity.rotationYaw - 180);
		double pitch = Math.toRadians(entity.rotationPitch);

		Matrix4f initial = new Matrix4f();
		initial.rotate((float)pitch, new Vector3f(1, 0, 0));
		initial.rotate((float)yaw, new Vector3f(0, 1, 0));
		return initial;
	}

	public static int getSphericalDistance(BlockPos startPos, BlockPos endPos) {
		final int dx = endPos.getX() - startPos.getX();
		final int dy = endPos.getZ() - startPos.getZ();
		final int dz = endPos.getY() - startPos.getY();
		return (int)Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
	}

	public static int getCubicDistance(BlockPos startPos, BlockPos endPos) {
		return Math.abs(endPos.getX() - startPos.getX()) + Math.abs(endPos.getY() - startPos.getY()) + Math.abs(endPos.getZ() - startPos.getZ());
	}

	public static int getHorSquaredDistance(BlockPos startPos, BlockPos endPos) {
		return Math.abs(endPos.getX() - startPos.getX()) + Math.abs(endPos.getZ() - startPos.getZ());
	}

	public static int getVerDistance(BlockPos startPos, BlockPos endPos) {
		return Math.abs(endPos.getY() - startPos.getY());
	}

	public static double getDistanceRatioToCenter(int point1, int point2, int pos) {
		double radius = Math.abs(point2 - point1) / 2D;
		double dar = Math.abs(Math.abs(pos - point1) - radius);
		return radius != 0.0D? dar / radius : 0.0D;
	}

	public static int parseInt(String string) {
		return parseInt(string, 0);
	}

	public static int parseInt(String string, int defaultValue) {
		try {
			return Integer.parseInt(string.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

}