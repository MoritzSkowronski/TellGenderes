package data;

import display.MuseumFinal;
import processing.core.PApplet;
import processing.core.PImage;

public class Image {

	private PImage image;
	private PImage resizedImage;
	private int originalWidth;
	private int originalHeight;
	private int resizedWidth;
	private int resizedHeight;

	public Image(PApplet p, PImage image) {

		this.image = image;
		resizedImage = image;
		originalWidth = image.width;
		originalHeight = image.height;
		resizedWidth = originalWidth;
		resizedHeight = originalHeight;

		// resize Image
		if (image.width >= MuseumFinal.maxImageWidth || image.height >= MuseumFinal.maxImageHeight) {

			int tempResizeWidth = image.width / MuseumFinal.scaleFactor;
			int tempResizeHeight = image.height / MuseumFinal.scaleFactor;
			resizedWidth = tempResizeWidth;
			resizedHeight = tempResizeHeight;

			int i = 1;
			while (resizedWidth <= MuseumFinal.maxImageWidth && resizedHeight <= MuseumFinal.maxImageHeight) {

				i++;
				resizedWidth = tempResizeWidth * i;
				resizedHeight = tempResizeHeight * i;
			}

			if (i != 1) {
				resizedWidth -= tempResizeWidth;
				resizedHeight -= tempResizeHeight;
			}

			resizedImage.resize(resizedWidth, resizedHeight);
		}
	}

	public PImage getResizedImage() {
		return resizedImage;
	}

	public int getResizedWidth() {
		return resizedWidth;
	}

	public int getResizedHeight() {
		return resizedHeight;
	}

}
