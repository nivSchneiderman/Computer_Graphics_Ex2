package edu.cg.scene.lightSources;

import java.util.List;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class DirectionalLight extends Light {
	private Vec direction = new Vec(0, -1, -1);

	public DirectionalLight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Directional Light:" + endl + super.toString() +
				"Direction: " + direction + endl;
	}

	@Override
	public DirectionalLight initIntensity(Vec intensity) {
		return (DirectionalLight)super.initIntensity(intensity);
	}
	
	/**
	 * Constructs a ray originated from the given point to the light.
	 * @param fromPoint - The initial point of the ray
	 * @return a ray origniated from 'fromPoint' to the light source.
	 */
	public Ray rayToLight(Point fromPoint) {
		return new Ray(fromPoint, this.direction.neg());
	}
	
	/**
	 * Checks if the given surface occludes the light-source. The surface occludes the light source
	 * if the given ray first intersects the surface before reaching the light source.
	 * @param surface -The given surface
	 * @param rayToLight - the ray to the light source
	 * @return true if the ray is occluded by the surface..
	 */
	public boolean isOccludedBy(Surface surface, Ray rayToLight, List<Surface> surfaces) {
		
		Hit hit = surface.intersect(rayToLight);
		return hit != null;
		
	}
	
	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		Vec directionIntensity = this.direction.normalize().neg();
		Vec directionToLight = rayToLight(hittingPoint).direction();
		
		return Ops.mult(directionIntensity.dot(directionToLight), this.intensity);
	}
}
