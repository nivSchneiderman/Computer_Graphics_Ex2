package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public class PinholeCamera {
		
	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized).
	 * @param upVec - The up vector of the camera.
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 * 
	 */
	
	private Point p0;  //camera position
	private Vec VTowards; //Normalized 
	private Vec VRight;
	private Vec Vup;
	private Point imageCenter;
	
	private double plainWidth;
	private double Rx;
	private double Ry;
	
	
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlane) {
	
		//set camera in 3D position
		p0 = cameraPosition;
		VTowards = towardsVec.normalize();
		imageCenter = p0.add(distanceToPlane, VTowards);
		VRight = VTowards.cross(upVec).normalize();
		Vup = (VRight.cross(VTowards)).normalize();
				
		//set default rez
		Rx = 200.0;
		Ry = 200.0;
		plainWidth = 2.0;		
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
		this.plainWidth = viewPlainWidth;
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y) {

		double pixcelSize = plainWidth / Rx;
		Vec right = VRight.mult((x - Math.floor(Rx/2)) * pixcelSize);
		Vec up = Vup.mult(-1.0 * (y - Math.floor(Ry/2)) * pixcelSize);
		return imageCenter.add(right.add(up));
	}
	
	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Point getCameraPosition() throws InstantiationException, IllegalAccessException {
		
		Point copyOfCameraPosition = new Point();
		copyOfCameraPosition.x = p0.x;
		copyOfCameraPosition.y = p0.y;
		copyOfCameraPosition.z = p0.z;
		
		return copyOfCameraPosition; 
	}
}
