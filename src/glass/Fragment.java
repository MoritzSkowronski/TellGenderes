package glass;

import java.util.ArrayList;
import java.util.Arrays;

import display.MuseumFinal;
import processing.core.PApplet;
import processing.core.PVector;

public class Fragment {

	private PVector centroid;
	private ArrayList<PVector> corners;
	private ArrayList<Line> lines;
	private ArrayList<Fragment> neighbors;
	private int lineVisibiltyCounter;
	private boolean hasBroken;
	private boolean isVisible;
	private float area;
	private boolean isBorderFragment;

	public Fragment(ArrayList<PVector> corners) {

		area = calculateArea(corners);
		centroid = calculateCentroid(corners, area);
		this.corners = orderPoints(corners, centroid, true);
		hasBroken = false;
		lines = new ArrayList<Line>();
		lineVisibiltyCounter = 0;
		neighbors = new ArrayList<Fragment>();
		isBorderFragment = false;
	}

	public float getMinX() {

		float minX = Float.MAX_VALUE;
		for (PVector pVector : corners) {

			if (pVector.x < minX)
				minX = pVector.x;
		}

		return minX;
	}

	public float getMaxX() {

		float maxX = Float.MIN_VALUE;
		for (PVector pVector : corners) {

			if (pVector.x > maxX)
				maxX = pVector.x;
		}

		return maxX;
	}

	public float getMinY() {

		float minY = Float.MAX_VALUE;
		for (PVector pVector : corners) {

			if (pVector.y < minY)
				minY = pVector.y;
		}

		return minY;
	}

	public float getMaxY() {

		float maxY = Float.MIN_VALUE;
		for (PVector pVector : corners) {

			if (pVector.y > maxY)
				maxY = pVector.y;
		}

		return maxY;
	}

	public PVector getCentroid() {

		return centroid;
	}

	public ArrayList<PVector> getPoints() {

		return corners;
	}

	public float getArea() {

		return area;
	}

	public void setLines(ArrayList<Line> lines) {

		this.lines = lines;
	}

	public boolean hasLine(Line line) {

		for (Line line1 : lines) {
			if (line1.equals(line))
				return true;
		}

		return false;
	}

	public boolean isBroken() {

		return hasBroken;
	}

	public void setBroken(boolean broken) {

		this.hasBroken = broken;
	}

	public void setVisible(Line line) {

		for (Line line1 : lines) {

			if (line1.equals(line)) {
				if (!line1.isVisible()) {

					line1.setVisiblity(true);
					lineVisibiltyCounter++;
					if (lineVisibiltyCounter == lines.size())
						isVisible = true;
				}
			}
		}
	}

	public boolean isVisible() {

		return isVisible;
	}

	public ArrayList<Line> getLines() {

		return lines;
	}

	/**
	 * Calculate Centroid of Polygon
	 * 
	 * @param area
	 * @param points
	 * @return
	 */
	private static PVector calculateCentroid(ArrayList<PVector> points, float area) {

		PVector centroid = new PVector();
		PVector p1;
		PVector p2;
		for (int i = 0; i < points.size(); i++) {
			p1 = points.get(i);
			p2 = points.get((i + 1) % points.size());
			centroid.x += (p1.x + p2.x) * (p1.x * p2.y - p2.x * p1.y);
			centroid.y += (p1.y + p2.y) * (p1.x * p2.y - p2.x * p1.y);
		}
		return centroid.div(6 * area);
	}

	/**
	 * Calculate Area of Polygon(Fragment)
	 * 
	 * @param points
	 * @return
	 */
	private static float calculateArea(ArrayList<PVector> points) {

		float a = 0;
		PVector p1;
		PVector p2;
		for (int i = 0; i < points.size(); i++) {
			p1 = points.get(i);
			p2 = points.get((i + 1) % points.size());
			a += p1.x * p2.y - p2.x * p1.y;
		}
		return a / 2;
	}

	/**
	 * order all points of a Fragment in counter/clockwise order, so it can be
	 * used in Box2D
	 * 
	 * @param points
	 * @return
	 */
	private ArrayList<PVector> orderPoints(ArrayList<PVector> points, PVector centroid,
			boolean clockwise) {

		PVector[] orderedList = new PVector[points.size()];
		orderedList = points.toArray(orderedList);

		float[] polarAngleValues = new float[points.size()];
		PVector tempPoint;
		for (int i = 0; i < polarAngleValues.length; i++) {
			tempPoint = points.get(i);
			polarAngleValues[i] = PApplet.atan2(tempPoint.y - centroid.y, tempPoint.x - centroid.x);
		}
		float tempValue;
		for (int i = 1; i < polarAngleValues.length; i++) {
			tempValue = polarAngleValues[i];
			tempPoint = orderedList[i];
			int j = i;
			if (clockwise) {
				while (j > 0 && polarAngleValues[j - 1] > tempValue) {
					polarAngleValues[j] = polarAngleValues[j - 1];
					orderedList[j] = orderedList[j - 1];
					j -= 1;
				}
			}
			if (!clockwise) {
				while (j > 0 && polarAngleValues[j - 1] < tempValue) {
					polarAngleValues[j] = polarAngleValues[j - 1];
					orderedList[j] = orderedList[j - 1];
					j -= 1;
				}
			}
			polarAngleValues[j] = tempValue;
			orderedList[j] = tempPoint;
		}

		return new ArrayList<PVector>(Arrays.asList(orderedList));
	}

	public void addLine(Line line) {

		lines.add(line);
	}

	public void addNeighbor(Fragment fragment) {

		neighbors.add(fragment);
	}

	public ArrayList<Fragment> getNeighbors() {

		return neighbors;
	}
	
	public void setBorderFragment(boolean isBorderFragment){
		
		this.isBorderFragment = isBorderFragment;
	}
	
	public boolean isBorderFragment(){
		
		return isBorderFragment;
	}
}
