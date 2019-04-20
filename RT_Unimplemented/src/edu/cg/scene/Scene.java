package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.lightSources.PointLight;
import edu.cg.scene.objects.Material;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
	private boolean renderRefarctions = false;
	private boolean renderReflections = false;
	
	private PinholeCamera camera;
	private Vec ambient = new Vec(1, 1, 1); //white
	private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();
	
	
	//MARK: initializers
	public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec,  double distanceToPlain) {
		this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec,  distanceToPlain);
		return this;
	}
	
	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}
	
	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}
	
	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}
	
	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}
	
	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}
	
	public Scene initName(String name) {
		this.name = name;
		return this;
	}
	
	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefarctions = renderRefarctions;
		return this;
	}
	
	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}
	
	//MARK: getters
	public String getName() {
		return name;
	}
	
	public int getFactor() {
		return antiAliasingFactor;
	}
	
	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}
	
	public boolean getRenderRefarctions() {
		return renderRefarctions;
	}
	
	public boolean getRenderReflections() {
		return renderReflections;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator(); 
		return "Camera: " + camera + endl +
				"Ambient: " + ambient + endl +
				"Background Color: " + backgroundColor + endl +
				"Max recursion level: " + maxRecursionLevel + endl +
				"Anti aliasing factor: " + antiAliasingFactor + endl +
				"Light sources:" + endl + lightSources + endl +
				"Surfaces:" + endl + surfaces;
	}
	
	private transient ExecutorService executor = null;
	private transient Logger logger = null;
	
	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
		//TODO: initialize your additional field here.
		//      You can also change the method signature if needed.
	}
	
	public BufferedImage render(int imgWidth, int imgHeight, double viewPlainWidth,Logger logger)
			throws InterruptedException, ExecutionException {
		// TODO: Please notice the following comment.
		// This method is invoked each time Render Scene button is invoked.
		// Use it to initialize additional fields you need.
		initSomeFields(imgWidth, imgHeight, logger);
		
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		camera.initResolution(imgHeight, imgWidth, viewPlainWidth);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);
		
		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][])(new Future[imgHeight][imgWidth]);
		
		this.logger.log("Starting to shoot " +
			(imgHeight*imgWidth*antiAliasingFactor*antiAliasingFactor) +
			" rays over " + name);
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);
		
		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}
		
		executor.shutdown();
		
		this.logger.log("Ray tracing of " + name + " has been completed.");
		
		executor = null;
		this.logger = null;
		
		return img;
	}
	
	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			// TODO: You need to re-implement this method if you want to handle
			//       super-sampling. You're also free to change the given implementation as you like.
			Point centerPoint = camera.transform(x, y);
			Ray ray = new Ray(camera.getCameraPosition(), centerPoint);
			Vec color = calcColor(ray, 0);
			return color.toColor();
		});
	}
	
	//TODO: add shadows affect
	private Vec calcColor(Ray ray, int recursionLevel) {
		Hit hit = findIntersection(ray);

		if (hit == null)
			return this.backgroundColor;
		
		Point hitPoint = ray.source().add(ray.direction().mult(hit.t()));
		Surface surface = hit.getSurface();
		
		// Ambient calculation 
		// we assume that there is no emission color
		Vec color = surface.Ka().mult(this.ambient);
		
		for (Light light : lightSources) {
			color.add(surface.Kd().mult(calcDiffuseColor(hitPoint, hit, ray, light)));
			color.add(surface.Ks().mult(calcSpecularColor(hitPoint, hit, ray, light, surface)));
		}
		
		 recursionLevel++;
		 if (recursionLevel > maxRecursionLevel)
			 return color;
		 
		 // reflective calculations
		 Ray rRay = constractReflectiveRayR(ray, hitPoint, hit);
		 color.add(calcColor(rRay, recursionLevel).mult(surface.reflectionIntensity()));
		 // refractive calculations - 
		 Ray tRay = constractRefractiveRayT(ray, hitPoint, hit, surface);
		 color.add(calcColor(tRay, recursionLevel).mult(surface.refractionIntensity()));
		 
		 return color;
	}
	
	// TODO: implement intersect in Surface class
	private Hit findIntersection(Ray ray) {
		
		Hit minHit = new Hit(Double.MAX_VALUE, new Vec(1,1,1));
		Hit hit = null;
		
		for (Surface surface : this.surfaces) {
			hit = surface.intersect(ray);
			if (hit != null && hit.compareTo(minHit) == -1) {
				minHit = hit;
			}
		}
		
		return minHit.t() != Double.MAX_VALUE  ? minHit : null;
	}
	
	// TODO: implement that
	private Vec calcDiffuseColor(Point hitPoint,Hit hit, Ray ray, Light light) {
		Ray rayToLight = light.rayToLight(hitPoint);
		Vec lightIntensity = light.intensity(hitPoint, rayToLight);
		Vec NormalToSurface = hit.getNormalToSurface();
		double nDotL = NormalToSurface.dot(rayToLight.direction());
		Vec diffuseColor = lightIntensity.mult(nDotL);
		
		return diffuseColor;
	}
	
	// TODO: implement that
	private Vec calcSpecularColor(Point hitPoint, Hit hit, Ray ray, Light light, Surface surface) {
		Ray rayToLight = light.rayToLight(hitPoint);
		Vec lightIntensity = light.intensity(hitPoint, rayToLight);
		Vec mirrorOfRayToLight = Ops.reflect(rayToLight.direction(), hit.getNormalToSurface()); 
		Vec vecToViewer = ray.direction().neg();
		double vDotLBar = vecToViewer.dot(mirrorOfRayToLight);
		
		return lightIntensity.mult(Math.pow(vDotLBar, surface.shininess()));
	}
	
	private Ray constractReflectiveRayR(Ray ray, Point hitPoint, Hit hit) {
		return new Ray(hitPoint, Ops.reflect(ray.direction(), hit.getNormalToSurface()));
	}
	
	// TODO: I assumed that the ray came from the air to the surface - It should be changed
	private Ray constractRefractiveRayT(Ray ray, Point hitPoint, Hit hit, Surface surface) {
		return new Ray(hitPoint, Ops.refract(ray.direction(), hit.getNormalToSurface(), 1.0, surface.n1(hit)));
	}
}
