package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class Spotlight extends PointLight {
	
	private Vec direction;
	
	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() + 
				"Direction: " + direction + endl;
	}
	
	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}
	
	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}
	
	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}
	
	
	@Override
	/**
	 * Constructs a ray originated from the given point to the light.
	 * @param fromPoint - The initial point of the ray
	 * @return a ray origniated from 'fromPoint' to the light source.
	 */
	public Ray rayToLight(Point fromPoint) {
		return new Ray(fromPoint, this.position.sub(fromPoint));
	}
	
	@Override
	/**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source (this is relevant for point-light and spotlight)
	 * @return A vector representing the light intensity (the r,g and b channels). 
	 */
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		Vec v = rayToLight.direction().neg();
		double d = v.norm();
		double delimiter = this.kc + this.kl*d + this.kq*Math.pow(d, 2);
		double vBarDotDBar = v.normalize().dot(this.direction.normalize());
		Vec intensity = this.intensity.mult(vBarDotDBar/delimiter);
		
		return intensity;
	}
	
	/**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source
	 * @return A vector representing the light intensity (the r,g and b channels). 
	 */
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		double epsilon = 1.0E-5;
		
		if (rayToLight.direction().neg().dot(direction.normalize()) < epsilon) {
	        return true;
	      }
	     
		return super.isOccludedBy(surface, rayToLight);
	}
}
