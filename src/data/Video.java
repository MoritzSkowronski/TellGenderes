package data;

import display.MuseumFinal;
import processing.core.PApplet;
import processing.video.Movie;

public class Video {

	private Movie video;
	private int originalWidth;
	private int originalHeight;
	private int resizedWidth;
	private int resizedHeight;

	public Video(PApplet p, Movie video) {
		this.video = video;
		this.video.play();
		this.video.read();
		
		resizedWidth = this.video.width;
		resizedHeight = this.video.height;

		// resize Video
		if (resizedWidth >= 1280
				|| resizedHeight >= 720) {
			
			int tempResizeWidth = this.video.width / MuseumFinal.scaleFactor;
			int tempResizeHeight = this.video.height / MuseumFinal.scaleFactor;
			resizedWidth = tempResizeWidth;
			resizedHeight = tempResizeHeight;

			int i = 1;
			while (resizedWidth <= 1280
					&& resizedHeight <= 720) {

				i++;
				resizedWidth = tempResizeWidth * i;
				resizedHeight = tempResizeHeight * i;
			}

			resizedWidth -= tempResizeWidth;
			resizedHeight -= tempResizeHeight;
		}
		
		this.video.pause();
	}

	public Movie getMovie() {
		return video;
	}

	public int getResizedWidth() {
		return resizedWidth;
	}

	public int getResizedHeight() {
		return resizedHeight;
	}
}
