package edu.cg.scene.lightSources;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

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
	
	/**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source
	 * @return A vector representing the light intensity (the r,g and b channels). 
	 */
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		Vec v = rayToLight.direction().neg();
		double d = v.norm();
		v = v.normalize();
		double vDotD = v.dot(this.direction);
		double delimiter = this.kc + this.kl*d + this.kq*Math.pow(d, 2);
		
		return this.intensity.mult(vDotD/delimiter);
	}
}
