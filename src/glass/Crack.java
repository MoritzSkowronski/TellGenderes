package glass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import box2DWorld.Polygon;
import data.Image;
import data.Video;
import display.MuseumFinal;
import helpers.CrackType;
import helpers.MathHelpers;
import helpers.RandomPolygonGenerator;
import helpers.Stage;
import helpers.SutherlandHodgmanClipping;
import processing.core.PApplet;
import processing.core.PVector;
import processing.video.Movie;
import shiffman.box2d.Box2DProcessing;

public class Crack {

	private ArrayList<Fragment> fragments;
	private ArrayList<PVector> boundingBox;
	private ArrayList<Polygon> box2dWorldPolygons;
	private ArrayList<Fragment> hiddenFragments;

	private int visibility;
	private long breakTime;
	private long fadeTime;

	private long lastUpdate;

	private int crackAmount;

	private Stage stage;
	private Image image;
	private Video movie;
	private CrackType cracktype;

	private PVector minInteractionWidthHeight;

	public Crack(ArrayList<Fragment> fragments, ArrayList<Fragment> hiddenFragments, float centerx,
			float centery, float width, float height) {

		visibility = 255;
		this.lastUpdate = System.currentTimeMillis();
		stage = Stage.WHOLE;

		this.hiddenFragments = hiddenFragments;
		this.fragments = fragments;
		box2dWorldPolygons = new ArrayList<Polygon>();
		boundingBox = MathHelpers.createBoundingVectors(centerx, centery, width, height);

		// create Minimum Bounding Box
		minInteractionWidthHeight = new PVector(width / MuseumFinal.interactionSteps,
				height / MuseumFinal.interactionSteps);
	}

	public void setImage(Image image) {

		this.image = image;
		cracktype = CrackType.IMAGE;
	}

	public void setMovie(Video video) {

		this.movie = video;
		cracktype = CrackType.MOVIE;
	}

	public ArrayList<Fragment> getFragments() {

		return fragments;
	}

	public ArrayList<Fragment> getHiddenFragment() {
		return hiddenFragments;
	}

	public ArrayList<PVector> getBoundingBox() {

		return boundingBox;
	}

	public Image getImage() {

		return image;
	}

	public Video getVideo() {

		return movie;
	}

	public CrackType getType() {

		return cracktype;
	}

	public Stage getStage() {

		return stage;
	}

	public int getVisiblity() {

		return visibility;
	}

	/**
	 * Break all
	 * 
	 * @param p
	 * @param box2d
	 * @param velocity
	 */
	public void breakCrack(PApplet p, Box2DProcessing box2d, PVector velocity) {

		lastUpdate = System.currentTimeMillis();

		for (Fragment fragment : fragments) {

			if (!fragment.isBroken())
				box2dWorldPolygons.add(new Polygon(p, box2d, fragment, velocity, 50));
		}

		// make all hidden lines visible
		for (Fragment fragment : hiddenFragments) {

			for (Line line : fragment.getLines()) {

				line.setVisiblity(true);
			}
		}
		fragments.clear();

		stage = Stage.BROKEN;
		breakTime = System.currentTimeMillis();
	}

	public void breakParts(PApplet p, Box2DProcessing box2d, float xPoint, float yPoint,
			int interactionStep, PVector velocity) {

		lastUpdate = System.currentTimeMillis();

		// If there are less than half of the fragments break all
		// if (fragments.size() / 2 < crackAmount) {
		//
		// breakCrack(p, box2d, velocity);
		// }

		// calculate rectangle size
		float rectangleWidth = minInteractionWidthHeight.x * interactionStep;
		float rectangleHeight = minInteractionWidthHeight.y * interactionStep;
		if (rectangleWidth == 0 || rectangleHeight == 0)
			return;

		ArrayList<PVector> temporaryBoundingBox = MathHelpers.createBoundingVectors(xPoint, yPoint,
				rectangleWidth, rectangleHeight);

		/* Test all fragments that CAN break out */

		// Test whether line is inside polygon, if so make it visible
		Iterator<Fragment> fragmentIterator = fragments.iterator();
		while (fragmentIterator.hasNext()) {

			Fragment fragment = fragmentIterator.next();

			boolean fragmentIsInRange = false;

			for (Line line : fragment.getLines()) {

				// first check if already visible

				if (MathHelpers.isIn(line.getStart(), temporaryBoundingBox)) {

					if (!line.isVisible()) {
						fragment.setVisible(line);
					}

					fragmentIsInRange = true;
				}
			}

			// if the fragment is visible, there is a probability that it
			// breaks out
			if (fragment.isVisible() && !fragment.isBroken() && fragmentIsInRange) {

				// TODO maybe also use velocity?!
				if (Math.random() < 0.01f) {

					breakFragment(fragment, p, box2d, velocity, 0);

					crackAmount++;
				}
			}

		}

		/* Test all Fragments that CANNOUT break out */

		for (Fragment fragment : hiddenFragments) {

			for (Line line : fragment.getLines()) {

				// first check if already visible
				if (line.isVisible())
					continue;

				// if not check if linestart is inside the polygon
				if (MathHelpers.isIn(line.getStart(), temporaryBoundingBox))
					fragment.setVisible(line);

			}
		}

	}

	public ArrayList<Polygon> getBoxPolygons() {

		return box2dWorldPolygons;
	}

	public void updateCrack() {

		if (stage == Stage.WHOLE) {

			if (System.currentTimeMillis() - lastUpdate >= 6000) {
				stage = Stage.INACTIVE;
			}
		}
		if (stage == Stage.BROKEN) {

			if (cracktype == CrackType.IMAGE) {
				if (System.currentTimeMillis() - breakTime >= MuseumFinal.showTime) {

					stage = Stage.FADING;
				}
			}

			if (cracktype == CrackType.MOVIE) {

				if (movie.getMovie().available()) {
					movie.getMovie().read();
				}

				if (movie.getMovie().time() == movie.getMovie().duration()) {
					movie.getMovie().jump(0);
					movie.getMovie().read();
					stage = Stage.FADING;
				}
			}
		}

		if (stage == Stage.FADING) {

			if (visibility <= 0) {

				for (Polygon polygon : box2dWorldPolygons) {

					if (!polygon.isDead())
						polygon.kill();
				}

				stage = Stage.DEAD;

			}

			visibility -= 15;

		}

		if (stage == Stage.INACTIVE) {

			stage = Stage.FADING;
		}
	}

	// public void breakLooseFragments(Fragment fragment, PApplet p,
	// Box2DProcessing box2d,
	// PVector velocity, int i) {
	//
	// boolean breaks = true;
	//
	// // Check all neighbors of the given fragment
	// for (Fragment temporaryFragment : fragment.getNeighbors()) {
	//
	// // if a fragment is not broken, we have an adjacent neigbhor
	// // so it doesn't have to break
	// if (!temporaryFragment.isBroken()) {
	// breaks = false;
	// }
	// }
	//
	// if (breaks) {
	//
	// breakFragment(fragment, p, box2d, velocity, i);
	// }
	// }

	public void breakFragment(Fragment fragment, PApplet p, Box2DProcessing box2d, PVector velocity,
			int i) {

		// TODO check velocity and angular values
		box2dWorldPolygons.add(new Polygon(p, box2d, fragment, velocity, 5));

		// Remove fragment from List, since it's broken out
		fragment.setBroken(true);

		for (Fragment fragment2 : fragment.getNeighbors()) {

			if (fragment2.isBorderFragment() || fragment2.isBroken())
				continue;

			if (createPathToBorder(0, fragment2, new ArrayList<>(Arrays.asList(fragment))) == 0) {
				destroyAllNeighBors(fragment2, p, box2d, velocity);
			}

		}

	}

	private void destroyAllNeighBors(Fragment fragment2, PApplet p, Box2DProcessing box2d,
			PVector velocity) {

		box2dWorldPolygons.add(new Polygon(p, box2d, fragment2, velocity, 5));

		// Remove fragment from List, since it's broken out
		fragment2.setBroken(true);

		for (Fragment fragment : fragment2.getNeighbors()) {

			if (!fragment.isBroken())
				destroyAllNeighBors(fragment, p, box2d, velocity);
		}
	}

	public int createPathToBorder(int i, Fragment startFragment, ArrayList<Fragment> noReturn) {

		noReturn.add(startFragment);

		if (i > 0)
			return i;

		if (startFragment.isBorderFragment()) {

			i++;
			return i;
		}

		for (Fragment fragment : startFragment.getNeighbors()) {

			if (!fragment.isBroken()) {

				boolean isEqual = false;

				for (Fragment fragment2 : noReturn) {

					if (fragment.equals(fragment2))
						isEqual = true;
				}
				if (!isEqual) {

					i += createPathToBorder(i, fragment, noReturn);
				}
			}
		}

		return i;
	}

}
