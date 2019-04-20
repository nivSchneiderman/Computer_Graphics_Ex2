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
	 * Checks if the given surface occludes the light-source - like the point light but also regarding 
	 * the angle of the spot light  
	 * @param rayToLight - the ray to the light source
	 * @return true if the ray is occluded by the surface
	 */
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		
		boolean isOccluded = false;
		
		double angleToSpotLight =
		
		if(hit == null){
			isOccluded = true;
		}else{
		   
		   //get the 3 points 
		   Point light = rayToLight.source(); 
		   Point hittingPoint = hit.hittingPoint;
		   Point objectPoint = findIntersection(rayToLight); 
		   
		   //get 2 distances 
		   double toLight = light.distSqr(hittingPoint);
		   double toObject = light.distSqr(objectPoint);
		   
		   isOccluded = toLight >= toObject;
		   
		}
		
		return isOccluded; 
	}
}
