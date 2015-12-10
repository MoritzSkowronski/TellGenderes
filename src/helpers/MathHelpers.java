package helpers;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import glass.Crack;
import processing.core.PApplet;
import processing.core.PVector;

public class MathHelpers {

	public static char occuppied;
	public static char vacant;
	public static char mark;

	/**
	 * Return true if the given point is contained inside the boundary. See:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	 * 
	 * @param test
	 *            The point to check
	 * @return true if the point is inside the boundary, false otherwise
	 *
	 */
	public static boolean contains(PVector test, PVector[] points) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = points.length - 1; i < points.length; j = i++) {
			if ((points[i].y > test.y) != (points[j].y > test.y)
					&& (test.x < (points[j].x - points[i].x) * (test.y - points[i].y)
							/ (points[j].y - points[i].y) + points[i].x)) {
				result = !result;
			}
		}
		return result;
	}

	public static ArrayList<ArrayList<PVector>> packingRectangle(CopyOnWriteArrayList<Crack> cracks,
			ArrayList<PVector> rectangle, PVector centroid) {

		ArrayList<ArrayList<PVector>> temporaryList = new ArrayList<ArrayList<PVector>>();

		mark = 'A';
		occuppied = '#';
		vacant = '-';

		int rows = (int) (rectangle.get(1).x - rectangle.get(0).x);
		int cols = (int) (rectangle.get(2).y - rectangle.get(1).y);
		System.out.println(rows);
		System.out.println(cols);

		char[][] matrix = new char[rows][cols];
		setRect(matrix, vacant, 0, 0, rows, cols);
		for (Crack crack : cracks) {

			ArrayList<PVector> boundingBox = crack.getBoundingBox();

			if (boundingBox.get(0).y < 0) {
				boundingBox.get(0).y = 0;
			}
			if (boundingBox.get(0).x < 0) {
				boundingBox.get(0).x = 0;
			}
			setRect(matrix, occuppied, (int) boundingBox.get(0).x, (int) boundingBox.get(0).y,
					(int) (boundingBox.get(1).x - boundingBox.get(0).x),
					(int) (boundingBox.get(2).y - boundingBox.get(1).y));
		}

		for (int i = 0; i < rows; i++) {
			int colstart = 0;
			while ((colstart = nextEmptyCol(matrix[i], colstart)) != -1) {
				int width = 0;
				for (int j = colstart; j < cols; j++) {
					if (matrix[i][j] == vacant)
						width++;
					else
						break;
				}
				if (width == 0)
					continue;
				int height = 1;
				outer: for (; height + i < rows; height++)
					for (int n = 0; n < width; n++) {
						if (matrix[i + height][colstart + n] == occuppied)
							break outer;
					}

				setRect(matrix, mark, i, colstart, height, width);
				ArrayList<PVector> temporaryRectangle = createBoundingVectors(colstart + height / 2,
						i + width / 2, height, width);

				for (PVector pVector : temporaryRectangle) {
					System.out.println("temporary x: " + pVector.x + " y: " + pVector.y);
				}

				if (isIn(centroid, temporaryRectangle)) {
					temporaryList.add(temporaryRectangle);
				}

				mark++;
			}
		}

		return temporaryList;
	}

	public static int nextEmptyCol(char[] row, int start) {
		for (int i = start; i < row.length; i++)
			if (row[i] == vacant)
				return i;
		return -1;
	}

	public static void setRect(char[][] matrix, char c, int startrow, int startcol, int numrows,
			int numcols) {
		for (int i = 0; i < numrows; i++)
			for (int j = 0; j < numcols; j++) {
				matrix[startrow + i][startcol + j] = c;
			}
	}

	/**
	 * Creates a Bounding Box from Center and width & height of the bounding box
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static ArrayList<PVector> createBoundingVectors(float x, float y, float width,
			float height) {

		float halfwidth = width / 2;
		float halfheight = height / 2;

		ArrayList<PVector> result = new ArrayList<PVector>();
		result.add(new PVector(x - halfwidth, y - halfheight));
		result.add(new PVector(x + halfwidth, y - halfheight));
		result.add(new PVector(x + halfwidth, y + halfheight));
		result.add(new PVector(x - halfwidth, y + halfheight));

		return result;
	}

	/**
	 * Checks if Vector is inside bounding box
	 * 
	 * @param subject
	 * @param boundingBox
	 * @return
	 */
	public static boolean isIn(PVector subject, ArrayList<PVector> boundingBox) {

		return subject.x <= boundingBox.get(1).x && subject.x >= boundingBox.get(0).x
				&& subject.y <= boundingBox.get(2).y && subject.y >= boundingBox.get(0).y;
	}

	/**
	 * Checks if Vector is inside bounding box
	 * 
	 * @param subject
	 * @param boundingBox
	 * @return
	 */
	public static boolean isIn(float x, float y, ArrayList<PVector> boundingBox) {

		return x <= boundingBox.get(1).x && x >= boundingBox.get(0).x && y <= boundingBox.get(2).y
				&& y >= boundingBox.get(0).y;
	}

	/**
	 * Returns intersection between two lines, if there is none null
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param x4
	 * @param y4
	 * @return
	 */
	public static PVector lineLineIntersect(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {

		float a1 = y2 - y1;
		float b1 = x1 - x2;
		float c1 = a1 * x1 + b1 * y1;

		float a2 = y4 - y3;
		float b2 = x3 - x4;
		float c2 = a2 * x3 + b2 * y3;

		float det = a1 * b2 - a2 * b1;
		if (det == 0) {
			return null;
		} else {
			float x = (b2 * c1 - b1 * c2) / det;
			float y = (a1 * c2 - a2 * c1) / det;
			if (x > PApplet.min(x1, x2) && x < PApplet.max(x1, x2) && x > PApplet.min(x3, x4)
					&& x < PApplet.max(x3, x4) && y > PApplet.min(y1, y2) && y < PApplet.max(y1, y2)
					&& y > PApplet.min(y3, y4) && y < PApplet.max(y3, y4)) {
				return new PVector(x, y);
			}
		}
		return null;
	}

	public static boolean pickRandom() {

		return Math.random() < 0.5;
	}

}
