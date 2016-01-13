package display;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import box2DWorld.Polygon;
import data.ContentGenerator;
import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
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
import persons.TriggerBox;
import processing.core.PApplet;
import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;

public class MuseumFinal extends PApplet {

	public final int firstColor = color(139, 137, 137);
	public static final int secondColor = 255;
	public static final int strokeStrength = 1;

	public static final int formFactorX = 16;
	public static final int formFactorY = 10;

	public static int maxImageWidth;
	public static int maxImageHeight;
	public static final int scaleFactor = 20;

	public static final int fadeoutTime = 1000;
	public static final int showTime = 6000;

	public static final int cutAmount = 250;
	public static int destroyableCuts = 0;
	public static final int interactionSteps = 5;

	private MovementHandler movementHandler;
	private OSCReceiver receiver;

	public static ArrayList<PVector> boundingWindowBox;

	public boolean running;
	private GlassPlane glass;
	private Box2DProcessing box2dWorld;

	private ContentGenerator generator;

	public static Minim minim;
	public static ArrayList<AudioSample> glassBreakSamples;
	public static int breakCounter;

	@Override
	public void settings() {

		// size(1280,800, P2D);
		fullScreen(P2D);
		smooth(8);
	}

	@Override
	public void setup() {

		noCursor();
		background(firstColor);
		maxImageWidth = width;
		maxImageHeight = height;

		boundingWindowBox = MathHelpers.createBoundingVectors(width / 2, height / 2, width, height);

		glass = new GlassPlane(this, maxImageWidth, maxImageHeight);

		generator = new ContentGenerator(this, "/Users/Moritz/Desktop/TellImage",
				"/Users/Moritz/Desktop/TellVideo");

		initiateAudio();

		box2dWorld = new Box2DProcessing(this);
		box2dWorld.createWorld();
		box2dWorld.setGravity(0, -150);

		movementHandler = new MovementHandler();

		receiver = new OSCReceiver(movementHandler);

		running = true;

		frameRate(30);
	}

	@Override
	public void draw() {
		// frameRate(30);

		background(firstColor);
		box2dWorld.step();
		box2dWorld.step();
		strokeWeight(strokeStrength);

		updateCrack();

		displayAll();

		fill(0);
		// text(frameRate, 20, 20);
	}

	public void updateCrack() {

		if (glass != null) {

			if (glass.getCrack() == null) {

				if (Math.random() < 0.4f) {

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
						// mouseY, 3,
						// PVector.random2D());
						if (movementHandler.updateReady()) {

							Iterator<Person> iterator = movementHandler.getPersons().values()
									.iterator();

							while (iterator.hasNext()) {

								Person person = iterator.next();

								// Iterator<TriggerBox> boxIterator =
								// movementHandler.getTriggerZones()
								// .values().iterator();
								//
								// while (boxIterator.hasNext()) {
								//
								// TriggerBox box = boxIterator.next();
								//
								// if (box.getID().equals("0-Triggerbox 1")) {
								//
								// if
								// (box.getPointsPerPerson().containsKey(person.getId()))
								// {
								//
								// }
								// }
								// break;
								// }

								PVector centroid = person.getCentroid();
								if (centroid.x > 1 || centroid.y > 1)
									continue;
								PVector velocity = person.getVelocity();

								if (velocity == null) {
									velocity = PVector.random2D();
								}

								if (centroid.x < 0.1) {

									continue;
								}

								if (centroid.x > 0.7) {

									continue;
								}

								centroid.x *= width;

								if (centroid.y < 0.3) {

									glass.getCrack().breakParts(this, box2dWorld, centroid.x,
											height / 2, 1, velocity);
								} else if (centroid.y < 0.5) {

									glass.getCrack().breakParts(this, box2dWorld, centroid.x,
											height / 2, 3, velocity);
								} else {
									if (centroid.y < 0.7) {

										glass.getCrack().breakParts(this, box2dWorld, centroid.x,
												height / 2, 4, velocity);
									} else {
										if (centroid.y < 1) {

											if (person.getAge() > 10) {

												glass.getCrack().breakCrack(this, box2dWorld,
														velocity);
											} else {

												glass.getCrack().breakParts(this, box2dWorld,
														centroid.x, height / 2, 4, velocity);
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

	public void initiateAudio() {

		minim = new Minim(this);
		AudioPlayer loopPlayer = minim.loadFile("/Users/Moritz/Desktop/FinalSound.mp3");
		loopPlayer.loop();

		glassBreakSamples = new ArrayList<AudioSample>();
		glassBreakSamples.add(minim.loadSample("/Users/Moritz/Desktop/SoundFinal/1.wav"));
		glassBreakSamples.add(minim.loadSample("/Users/Moritz/Desktop/SoundFinal/2.wav"));
		glassBreakSamples.add(minim.loadSample("/Users/Moritz/Desktop/SoundFinal/3.wav"));
		glassBreakSamples.add(minim.loadSample("/Users/Moritz/Desktop/SoundFinal/4.wav"));
		glassBreakSamples.add(minim.loadSample("/Users/Moritz/Desktop/SoundFinal/5.wav"));
		breakCounter = 0;
	}
}
