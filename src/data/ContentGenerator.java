package data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Movie;

public class ContentGenerator {

	private List images;
	private List videos;
	private boolean isReady;
	private PApplet p;
	private Random randomizer;

	public ContentGenerator(PApplet p, String imagePath, String moviePath) {

		this.p = p;
		images = new ArrayList<Image>();
		videos = new ArrayList<Video>();
		randomizer = new Random();

		initialize(images, imagePath);
		initialize(videos, moviePath);
		isReady = true;
		System.out.println("Loaded " + images.size() + " Images");
		System.out.println("Loaded " + videos.size() + " Videos");
	}

	private void initialize(List list, String path) {

		File file = new File(path);
		if (file.isDirectory()) {

			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {

				String fileName = files[i].getAbsolutePath();
				if (fileName.toLowerCase().endsWith(".png")
						|| fileName.toLowerCase().endsWith(".jpg")) {

					list.add(new Image(p, p.loadImage(fileName)));
				}
				if (fileName.endsWith(".avi") || fileName.endsWith(".mp4")
						|| fileName.endsWith(".mov")) {

					list.add(new Video(p, new Movie(p, fileName)));
				}
			}
		}
	}

	public Image getRandomImage(int maxWidth, int maxHeight) {

		// preselection
		ArrayList<Image> possibleImages = new ArrayList<Image>();
		for (Image image : (ArrayList<Image>) images) {

			if (image.getResizedWidth() <= maxWidth && image.getResizedHeight() <= maxHeight) {
				possibleImages.add(image);
			}
		}

		if (possibleImages.size() < 1)
			return null;

		int index = randomizer.nextInt(possibleImages.size());

		return possibleImages.get(index);
	}

	public Video getRandomVideo(int maxWidth, int maxHeight) {

		// preselection
		ArrayList<Video> possibleMovies = new ArrayList<Video>();
		for (Video video : (ArrayList<Video>) videos) {

			if (video.getResizedWidth() <= maxWidth && video.getResizedHeight() <= maxHeight) {

				possibleMovies.add(video);
			}
		}

		if (possibleMovies.size() < 1)
			return null;

		int index = randomizer.nextInt(possibleMovies.size());

		Video video = possibleMovies.get(index);

		video.getMovie().jump(0);

		return video;
	}

	public boolean isReady() {
		return isReady;
	}
}
