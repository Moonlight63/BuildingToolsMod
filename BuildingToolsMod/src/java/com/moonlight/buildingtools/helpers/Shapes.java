package com.moonlight.buildingtools.helpers;

import net.minecraft.util.StatCollector;

import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.ShapeCuboidGenerator;
import com.moonlight.buildingtools.helpers.shapes.ShapeCylinderGenerator;
import com.moonlight.buildingtools.helpers.shapes.ShapeDomeGenerator;
import com.moonlight.buildingtools.helpers.shapes.ShapeEquilateral2dGenerator;
import com.moonlight.buildingtools.helpers.shapes.ShapeSphereGenerator;

public enum Shapes {
	Sphere(false, new ShapeSphereGenerator(), "sphere"),
	Cylinder(false, new ShapeCylinderGenerator(), "cylinder"),
	Cuboid(false, new ShapeCuboidGenerator(), "cuboid"),
	Dome(false, new ShapeDomeGenerator(), "dome"),
	Triangle(true, new ShapeEquilateral2dGenerator(3), "triangle"),
	Pentagon(true, new ShapeEquilateral2dGenerator(5), "pentagon"),
	Hexagon(true, new ShapeEquilateral2dGenerator(6), "hexagon"),
	Octagon(true, new ShapeEquilateral2dGenerator(8), "octagon");

	public final String unlocalizedName;
	public final boolean fixedRatio;
	public final IShapeGenerator generator;

	private Shapes(boolean fixedRatio, IShapeGenerator generator, String name) {
		this.unlocalizedName = "openblocks.misc.shape." + name;
		this.fixedRatio = fixedRatio;
		this.generator = generator;
	}

	public String getLocalizedName() {
		return StatCollector.translateToLocal(unlocalizedName);
	}

	public static final Shapes[] VALUES = values();
}