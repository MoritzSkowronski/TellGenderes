package helpers;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class RandomPolygonGenerator {

	/**
	 * Creates a random vertex (as ArrayList PVector), which resembles a
	 * "hole punched in glass". This is done by taking an ellipse and use random
	 * points along its contour to build the Polygon.
	 * 
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 * @return
	 */
	public static ArrayList<PVector> generateRandomPolygon(float posX, float posY, float width, float height) {

		float a, x, y;

		// divide by 2 since we use the radius
		width /= 2;
		height /= 2;

		ArrayList<PVector> shape = new ArrayList<PVector>();

		for (a = 0; a < 2.0 * PApplet.PI;) {

			// TODO idea for a more glass like look: take a point, make two
			// adjacent points and move the original point along the x and y
			// axes, so it gets sharper
			x = posX + (width * PApplet.cos(a));// +random(-30,30);
			y = posY + (height * PApplet.sin(a)); // + random(-30,30);
			// random range says how many vertexes are going to be produced
			a += (20.0 + 40 * Math.random()) * PApplet.PI / 180.0;

			shape.add(new PVector(x, y));
		}

		shape.add(shape.get(0));
		
		return shape;
	}
}
