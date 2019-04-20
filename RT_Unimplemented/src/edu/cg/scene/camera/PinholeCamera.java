package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public class PinholeCamera {
	//TODO Add your fields
	
	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
	 * @param upVec - The up vector of the camera.
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 * 
	 */
	
	//class fieleds 
	public Point p0;  //camera position
	public Point imageCenter;
	public double plainWidth;
	public double distanceToPlane;
	public Vec VTowards; //Normalized 
	public Vec VRight;
	public Vec Vup;
	public double pixcelSize;
	public int imageRes;
	public int Rx;
	public int Ry;
	
	
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
		
		//set p0
		p0 = cameraPosition;
		
		//set towards and normalize
		VTowards = towardsVec.normalize();
		
		//find center point 
		imageCenter = p0.add(distanceToPlain, VTowards);
		
		//find V right 
		VRight = (VTowards.cross(upVec)).normalize();
		
		//correct Vup
		Vup = (VRight.cross(VTowards)).normalize();
		
				
		
	}
	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth) {
	         
		Rx = width;
		Ry= height;
		pixcelSize = viewPlainWidth /(double)Rx;
		
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y) {

		Vec right = VRight.mult(x - Math.floor((double)Rx/2)*pixcelSize);
		Vec up = Vup.mult(y - Math.floor((double)Ry/2)*pixcelSize);
		return p0.add(right.add(up.neg()));
		
	}
	
	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Point getCameraPosition() throws InstantiationException, IllegalAccessException {
		
		Point copyOfCameraPosition = Point.class.newInstance();
		
		copyOfCameraPosition.x = p0.x;
		copyOfCameraPosition.y = p0.y;
		copyOfCameraPosition.z = p0.z;
		
		return copyOfCameraPosition; 
	}
}
