package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class AxisAlignedBox extends Shape {
	private Point minPoint;
	private Point maxPoint;
	private String name = "";
	static private int CURR_IDX;

	/**
	 * Creates an axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox(Point minPoint, Point maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		name = new String("Box " + CURR_IDX);
		CURR_IDX += 1;
		fixBoundryPoints();
	}

	/**
	 * Creates a default axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox() {
		minPoint = new Point(-1.0, -1.0, -1.0);
		maxPoint = new Point(1.0, 1.0, 1.0);
	}
	
	/**
	 * This methods fixes the boundary points minPoint and maxPoint so that the values are consistent.
	 */
	private void fixBoundryPoints() {
		double min_x = Math.min(minPoint.x, maxPoint.x), max_x = Math.max(minPoint.x, maxPoint.x),
				min_y = Math.min(minPoint.y, maxPoint.y), max_y = Math.max(minPoint.y, maxPoint.y),
				min_z = Math.min(minPoint.z, maxPoint.z), max_z = Math.max(minPoint.z, maxPoint.z);
		minPoint = new Point(min_x, min_y, min_z);
		maxPoint = new Point(max_x, max_y, max_z);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return name + endl + "Min Point: " + minPoint + endl + "Max Point: " + maxPoint + endl;
	}
	
	//Initializers
	public AxisAlignedBox initMinPoint(Point minPoint) {
		this.minPoint = minPoint;
		fixBoundryPoints();
		return this;
	}

	public AxisAlignedBox initMaxPoint(Point maxPoint) {
		this.maxPoint = maxPoint;
		fixBoundryPoints();
		return this;
	}

	@Override
	public Hit intersect(Ray ray) 
	{
		double closeP = -0.000000001;
		double farP = 10000000000.0;
	    		
		//take the points as arrays 
		double[] ray_P = ray.source().asArray();
	    double[] ray_D = ray.direction().asArray();
	    double[] min = minPoint.asArray();
	    double[] max = maxPoint.asArray();
	    
	    for (int i = 0; i < 3; i++) {
	      if (Math.abs(ray_D[i]) < 0.000000001) {
	        if ((ray_P[i] > max[i]) || (ray_P[i] < min[i])) return null;
	      }else{
	        
	    	double p1 = findIntersectionParameter(ray_D[i], ray_P[i], min[i]);
	        double p2 = findIntersectionParameter(ray_D[i], ray_P[i], max[i]);
	        
	         if (p1 > p2) {
	          double tmp = p1;
	          p1 = p2;
	          p2 = tmp;
	        }
	        
	         //edge case of 1 t 
	        if ((Double.isNaN(p1)) || (Double.isNaN(p2))) return null;
	        
	        if(p1 > closeP) closeP = p1;
	        if(p2 < farP)  farP = p2;
	        if((closeP > farP) || (farP < 0.000001))  return null;
	      }
	    }
	    
	    double minP = closeP;
	    boolean isWithin = false;
	    
	    if (minP < 0.000001) { 
	      isWithin = true;
	      minP = farP;
	    }
	    
	    Vec norm = normal(ray.add(minP));
	    
	    if (isWithin) {
	      norm = norm.neg();
	    }
	    
	    return new Hit(minP, ray.add(minP), norm).setIsWithin(isWithin);
	}
	
	private Vec normal(Point point) {
	    if (Math.abs(point.z - minPoint.z) <= 0.00001) {
	      return new Vec(0.0, 0.0, -1.0);
	    }
	    if (Math.abs(point.z - maxPoint.z) <= 0.00001) {
	      return new Vec(0.0, 0.0, 1.0);
	    }
	    if (Math.abs(point.y - minPoint.y) <= 0.00001) {
	      return new Vec(0.0D, -1.0D, 0.0D);
	    }
	    if (Math.abs(point.y - maxPoint.y) <= 0.00001) {
	      return new Vec(0.0, 1.0, 0.0);
	    }
	    if (Math.abs(point.x - minPoint.x) <= 0.00001) {
	      return new Vec(-1.0, 0.0, 0.0);
	    }
	    if (Math.abs(point.x - maxPoint.x) <= 0.00001) {
	      return new Vec(1.0, 0.0, 0.0);
	    }
	    
	    return null;
	}
	
	  private static double findIntersectionParameter(double x, double y, double z) 
	  {
		double result = (z - y) / x;
		  
	    if ((Math.abs(x) < 0.000001) && (Math.abs(y - z) > 0.00001)) {
	    	result = 10000000;
	    }
	    if ((Math.abs(x) < 0.00001) && (Math.abs(y - z) < 0.00001))
	    	result = 0.0;
	    
	    return result;
	  }
}
