package edu.cg.scene.lightSources;

import java.util.List;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public abstract class Light {
	protected Vec intensity = new Vec(1, 1, 1); //white color
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl;
	}
	
	public Light initIntensity(Vec intensity) {
		this.intensity = intensity;
		return this;
	}
	
	/**
	 * Checks if the given surface occludes the light-source. The surface occludes the light source
	 * if the given ray first intersects the surface before reaching the light source.
	 * @param surface -The given surface
	 * @param rayToLight - the ray to the light source
	 * @return true if the ray is occluded by the surface..
	 */
	public abstract boolean isOccludedBy(Surface surface, Ray rayToLight);
	
	/**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source (this is relevant for point-light and spotlight)
	 * @return A vector representing the light intensity (the r,g and b channels). 
	 */
	public abstract Vec intensity(Point hittingPoint, Ray rayToLight);

	public abstract Ray rayToLight(Point source);
}
