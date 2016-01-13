package helpers;

import java.util.ArrayList;
import java.util.HashSet;

import display.MuseumFinal;
import glass.Crack;
import glass.Fragment;
import glass.Line;
import glass.LineType;
import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;
import processing.core.PApplet;
import processing.core.PVector;

public class VoronoiBuilder {

	private static Voronoi voronoiCreator;

	/**
	 * Create Voronoi Cracks
	 * 
	 * @param corners
	 * @return
	 */
	public static Crack createVoronoiCrack(PApplet p, int centerX, int centerY, int width,
			int height) {
		
		MuseumFinal.destroyableCuts = 0;

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		ArrayList<Fragment> hiddenFragments = new ArrayList<Fragment>();

		ArrayList<PVector> boundingBox = MathHelpers.createBoundingVectors(centerX, centerY,
				width + 1, height + 1);

		// Create cutAmount random points
		float[][] points = new float[MuseumFinal.cutAmount][2];

		int widthFromCenter = width / 2;
		int heightFromCenter = height / 2;

		// Transform PVectors to float[][]
		for (int i = 0; i < points.length; i++) {

			points[i][0] = p.random(centerX - widthFromCenter, centerX + widthFromCenter);
			points[i][1] = p.random(centerY - heightFromCenter, centerY + heightFromCenter);
		}

		// create Voronoi Cracks
		voronoiCreator = new Voronoi(points);

		ArrayList<PVector> vectors = new ArrayList<PVector>();
		ArrayList<PVector> clippedVectors = new ArrayList<PVector>();
		// Transform Regions to my polygons
		for (MPolygon temporaryPolygon : voronoiCreator.getRegions()) {

			float[][] edges = temporaryPolygon.getCoords();
			vectors.clear();

			for (int i = 0; i < edges.length; i++) {

				vectors.add(new PVector(edges[i][0], edges[i][1]));
			}

			// Create Fragments
			Fragment temporaryFragment = new Fragment(vectors);
			// Clip those Fragments with bounding box
			clippedVectors = SutherlandHodgmanClipping.clipPolygon(vectors, boundingBox);

			// if result is different, we have a clipping
			if (!vectors.equals(clippedVectors)) {

				addLines(temporaryFragment, LineType.OUTER, centerX, centerY);

				hiddenFragments.add(temporaryFragment);

			} else {

				MuseumFinal.destroyableCuts++;
				
				addLines(temporaryFragment, LineType.FRAGMENT, centerX, centerY);

				fragments.add(temporaryFragment);
			}

		}
		
		for (Fragment fragment : fragments) {
			
			checkForNeighbors(fragments, hiddenFragments, fragment);
		}

		return new Crack(fragments, hiddenFragments, centerX, centerY, width, height);
	}

	private static void checkForNeighbors(ArrayList<Fragment> fragments,
			ArrayList<Fragment> hiddenFragments, Fragment fragment) {

		// for every fragment that has already been added
		for (Fragment fragment2 : fragments) {

			// check all lines
			for (Line line2 : fragment2.getLines()) {

				// agains the lines of the new fragment
				for (Line line : fragment.getLines()) {

					// if we have matching lines, they are neighbors
					if (line2.getStart().equals(line.getStart())
							&& line2.getEnd().equals(line.getEnd())) {

						fragment.addNeighbor(fragment2);
						fragment2.addNeighbor(fragment);
					}
				}
			}
		}

		// Same for hidden fragments
		for (Fragment fragment2 : hiddenFragments) {

			for (Line line2 : fragment2.getLines()) {

				for (Line line : fragment.getLines()) {

					// if we have matching lines, they are neighbors
					if (line2.getStart().equals(line.getStart())
							&& line2.getEnd().equals(line.getEnd())) {

						fragment.setBorderFragment(true);
					}
				}
			}
		}
	}

	private static void addLines(Fragment fragment, LineType type, float centerX, float centerY) {

		// add all non clipped fragments to fragments
		// add all lines belonging to a clip fragment to the cracklines
		ArrayList<PVector> lineVectors = fragment.getPoints();
		for (int i = 1; i < lineVectors.size(); i++) {

			boolean calc = false;
			PVector line1 = lineVectors.get(i - 1);
			PVector line2 = lineVectors.get(i);
			if (type == LineType.OUTER) {
				if ((line1.x < 0 || line1.x > MuseumFinal.maxImageWidth || line1.y < 0
						|| line1.y > MuseumFinal.maxImageHeight) || line2.x < 0
						|| line2.x > MuseumFinal.maxImageWidth || line2.y < 0
						|| line2.y > MuseumFinal.maxImageHeight) {

					calc = true;
					ArrayList<PVector> temporaryLines = new ArrayList<PVector>();
					temporaryLines.add(line1);
					temporaryLines.add(line2);

					temporaryLines = SutherlandHodgmanClipping.clipPolygon(temporaryLines,
							MuseumFinal.boundingWindowBox);
					if (temporaryLines.size() == 3) {
						line1 = temporaryLines.get(0);
						line2 = temporaryLines.get(2);
					}
				}
			}
			if (PApplet.dist(line1.x, line1.y, centerX, centerY) < PApplet.dist(line2.x, line2.y,
					centerX, centerY)) {

				fragment.addLine(new Line(fragment, line1, line2, type, calc));
			} else {

				fragment.addLine(new Line(fragment, line2, line1, type, calc));
			}
		}

		PVector line1 = lineVectors.get(lineVectors.size() - 1);
		PVector line2 = lineVectors.get(0);

		boolean calc = false;
		if (type == LineType.OUTER) {
			if ((line1.x < 0 || line1.x > MuseumFinal.maxImageWidth || line1.y < 0
					|| line1.y > MuseumFinal.maxImageHeight) || line2.x < 0
					|| line2.x > MuseumFinal.maxImageWidth || line2.y < 0
					|| line2.y > MuseumFinal.maxImageHeight) {

				calc = true;
				ArrayList<PVector> temporaryLines = new ArrayList<PVector>();
				temporaryLines.add(line1);
				temporaryLines.add(line2);
				temporaryLines = SutherlandHodgmanClipping.clipPolygon(temporaryLines,
						MuseumFinal.boundingWindowBox);

				if (temporaryLines.size() == 3) {
					line1 = temporaryLines.get(0);
					line2 = temporaryLines.get(2);
				}
			}
		}

		if (PApplet.dist(line1.x, line1.y, centerX, centerY) < PApplet.dist(line2.x, line2.y,
				centerX, centerY)) {
			fragment.addLine(new Line(fragment, line1, line2, type, calc));
		} else {
			fragment.addLine(new Line(fragment, line2, line1, type, calc));
		}
	}

}
