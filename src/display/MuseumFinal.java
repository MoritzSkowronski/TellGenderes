package display;

import java.util.ArrayList;
import java.util.Iterator;

import box2DWorld.Polygon;
import data.ContentGenerator;
import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.effects.BandPass;
import ddf.minim.ugens.FilePlayer;
import glass.Crack;
import glass.Fragment;
import glass.GlassPlane;
import glass.Line;
import helpers.CrackType;
import helpers.MathHelpers;
import helpers.Stage;
import movement.MovementHandler;
import movement.OSCReceiver;
import persons.Person;
import processing.core.PApplet;
import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;

public class MuseumFinal extends PApplet {

	public static final int firstColor = 255;
	public static final int secondColor = 0;
	public static final int strokeStrength = 1;

	public static final int formFactorX = 16;
	public static final int formFactorY = 10;

	public static int maxImageWidth;
	public static int maxImageHeight;
	public static final int scaleFactor = 20;

	public static final int fadeoutTime = 1000;
	public static final int showTime = 6000;

	public static final int cutAmount = 250;
	public static final int interactionSteps = 5;

	private MovementHandler movementHandler;
	private OSCReceiver receiver;

	public static ArrayList<PVector> boundingWindowBox;

	public boolean running;
	private GlassPlane glass;
	private Box2DProcessing box2dWorld;

	private ContentGenerator generator;

	@Override
	public void settings() {

		size(1280, 800, P2D);
		// fullScreen(P2D);
		smooth(8);
	}

	@Override
	public void setup() {

		background(firstColor);
		maxImageWidth = width;
		maxImageHeight = height;

		boundingWindowBox = MathHelpers.createBoundingVectors(width / 2, height / 2, width, height);

		glass = new GlassPlane(this, maxImageWidth, maxImageHeight);

		generator = new ContentGenerator(this, "/Users/Moritz/Dropbox/TellImage",
				"/Users/Moritz/Dropbox/TellVideo");

		//
		// Minim minim = new Minim(this);
		// AudioOutput output = minim.getLineOut();
		// FilePlayer sound1 = new
		// FilePlayer(minim.loadFileStream("/Users/Moritz/Dropbox/Sound/Crack/SuddenCrack.wav"));
		// BandPass bpf = new BandPass(440, 20, output.sampleRate());
		// bpf.setFreq(300);
		// bpf.setBandWidth(500);
		// sound1.patch(bpf).patch(output);
		// sound1.loop();

		box2dWorld = new Box2DProcessing(this);
		box2dWorld.createWorld();
		box2dWorld.setGravity(0, -100);

		movementHandler = new MovementHandler();

		receiver = new OSCReceiver(movementHandler);

		running = true;
	}

	@Override
	public void draw() {
		frameRate(30);

		background(firstColor);
		box2dWorld.step();
		strokeWeight(strokeStrength);

		updateCrack();

		displayAll();

		fill(0);
		text(frameRate, 20, 20);
	}

	public void updateCrack() {

		if (glass != null) {

			if (glass.getCrack() == null) {

				if (Math.random() < 0) {

					glass.crack(width / 2, height / 2,
							generator.getRandomVideo(maxImageWidth, maxImageHeight));
				} else {

					glass.crack(width / 2, height / 2,
							generator.getRandomImage(maxImageWidth, maxImageHeight));
				}
			} else {

				if (glass.getCrack().getStage() == Stage.DEAD) {

					glass.deleteCrack();
				} else {

					glass.getCrack().updateCrack();

					if (glass.getCrack().getStage() == Stage.WHOLE) {

						// glass.getCrack().breakParts(this, box2dWorld, mouseX,
						// mouseY, 1, PVector.random2D());
						if (movementHandler.updateReady()) {

							Iterator<Person> iterator = movementHandler.getPersons().values()
									.iterator();
							while (iterator.hasNext()) {

								Person person = iterator.next();
								PVector centroid = person.getCentroid();
								PVector velocity = person.getVelocity();

								if (velocity == null) {
									velocity = PVector.random2D();
								}
								centroid.x *= width;

								// how much will break
								if (centroid.y < 0.2) {

									glass.getCrack().breakParts(this, box2dWorld, centroid.x,
											height / 2, 1, velocity);
								} else {
									if (centroid.y < 0.4) {

										glass.getCrack().breakParts(this, box2dWorld, centroid.x,
												height / 2, 2, velocity);
									} else {
										if (centroid.y < 0.6) {

											glass.getCrack().breakParts(this, box2dWorld,
													centroid.x, height / 2, 3, velocity);
										} else {
											if (centroid.y < 0.8) {

												glass.getCrack().breakParts(this, box2dWorld,
														centroid.x, height / 2, 4, velocity);
											} else {
												if (centroid.y < 1) {

													glass.getCrack().breakCrack(this, box2dWorld,
															velocity);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Displays cracks, images, video & splitter
	 */
	public void displayAll() {

		if (glass != null) {

			// Get current Crack
			Crack crack = glass.getCrack();

			// TODO update Crack

			// Paint Image or Video

			if (crack != null) {

				if (crack.getType() == CrackType.IMAGE) {

					tint(255, crack.getVisiblity());
					image(crack.getImage().getResizedImage(), crack.getBoundingBox().get(0).x,
							crack.getBoundingBox().get(0).y);
				} else {

					tint(255, crack.getVisiblity());
					image(crack.getVideo().getMovie(), crack.getBoundingBox().get(0).x,
							crack.getBoundingBox().get(0).y);
				}

				// Overpaint image with fragments WITHOUT Lines

				noStroke();
				fill(firstColor);
				for (Fragment fragment : crack.getFragments()) {

					if (!fragment.isVisible()) {

						beginShape();
						for (PVector vector : fragment.getPoints()) {

							vertex(vector.x, vector.y);
						}

						endShape();
					}
				}

				for (Fragment fragment : crack.getHiddenFragment()) {

					beginShape();
					for (PVector vector : fragment.getPoints()) {

						vertex(vector.x, vector.y);
					}

					endShape();
				}

				// Now draw visible fragments and lines

				stroke(secondColor, crack.getVisiblity());
				for (Fragment fragment : crack.getFragments()) {

					if (fragment.isVisible() && !fragment.isBroken()) {

						beginShape();
						for (PVector vector : fragment.getPoints()) {

							vertex(vector.x, vector.y);
						}

						endShape(CLOSE);

					} else {

						if (!fragment.isBroken()) {

							for (Line line : fragment.getLines()) {

								if (line.isVisible()) {

									line(line.getStart().x, line.getStart().y, line.getEnd().x,
											line.getEnd().y);
								}
							}
						}
					}
				}

				for (Fragment fragment : crack.getHiddenFragment()) {

					for (Line line : fragment.getLines()) {

						if (line.isVisible()) {
							line(line.getStart().x, line.getStart().y, line.getEnd().x,
									line.getEnd().y);
						}
					}
				}

				// Display the box2d Polygons
				Iterator<Polygon> polygonIterator = crack.getBoxPolygons().iterator();

				while (polygonIterator.hasNext()) {

					Polygon polygon = polygonIterator.next();
					if (polygon.isDead()) {

						polygonIterator.remove();
					} else {

						polygon.display();
					}
				}
			}
		}
	}

	public void setGlass(GlassPlane glass) {

		this.glass = glass;
	}

	public void mousePressed() {

		if (glass != null) {
			if (glass.getCrack() != null) {

				glass.getCrack().breakCrack(this, box2dWorld, PVector.random2D());
			}
		}

	}
}
