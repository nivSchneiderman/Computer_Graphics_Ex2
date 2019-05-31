package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Sphere extends Shape {
	private Point center;
	private double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + "Center: " + center + endl + "Radius: " + radius + endl;
	}

	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}

	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}

	@Override
	public Hit intersect(Ray ray) {
		double b = ray.direction().mult(2.0).dot(ray.source().sub(center));
	    double c = substituteFromCenter(ray.source());
	    double disc = Math.sqrt(b * b - 4.0 * c);//using discriminanta calc 
	    
	    //edge case of no intersection
	    if (Double.isNaN(disc)) return null;
	    	    
	    double p1 = (-b - disc) / 2.0;
	    double p2 = (-b + disc) / 2.0;
	    
	    if (p2 < 0.000001) return null;
	    	    
	    double min = p1;
	    Vec normal = ray.add(p1).sub(center).normalize();
	    boolean sourceIsInside = false;
	    
	    if (p1 < 0.000001) 
	    {
	      sourceIsInside = true;
	      min = p2;
	      normal = ray.add(p2).sub(center).normalize().neg();
	    }
	    
	    if (min > 10000000) return null;
	    	    
	    return new Hit(min, ray.add(min), normal).setIsWithin(sourceIsInside);
	}
	
	private double substituteFromCenter(Point p) {
	    return p.distSqr(center) - radius * radius;
  	}
}
