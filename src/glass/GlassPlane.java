package glass;

import data.Image;
import data.Video;
import helpers.MathHelpers;
import helpers.VoronoiBuilder;
import processing.core.PApplet;

public class GlassPlane {

	private int width;
	private int height;
	private PApplet p;
	private Crack crack;

	public GlassPlane(PApplet p, int width, int height) {

		this.p = p;
		this.width = width;
		this.height = height;
	}

	public void deleteCrack() {

		crack = null;
	}

	public boolean isOccupied(float x, float y) {

		if (MathHelpers.isIn(x, y, crack.getBoundingBox()))
			return true;

		return false;
	}

	/**
	 * Create a Crack at the specified position
	 * 
	 * @param centroidX
	 * @param centroidY
	 * @param crackwidth
	 * @param crackheight
	 */
	public void crack(int centroidX, int centroidY, Image image) {
		
		if(image == null)
			return;

		Crack temporaryCrack = VoronoiBuilder.createVoronoiCrack(p, centroidX, centroidY,
				image.getResizedWidth(), image.getResizedHeight());

		temporaryCrack.setImage(image);

		// reduceCrackLines(temporaryCrack);

		crack = temporaryCrack;
	}

	public void crack(int centroidX, int centroidY, Video video) {

		if (video == null)
			return;

		Crack temporaryCrack = VoronoiBuilder.createVoronoiCrack(p, centroidX, centroidY,
				video.getResizedWidth(), video.getResizedHeight());

		temporaryCrack.setMovie(video);

		// reduceCrackLines(temporaryCrack);

		crack = temporaryCrack;
	}

	public Crack getCrack() {

		return crack;
	}
}
