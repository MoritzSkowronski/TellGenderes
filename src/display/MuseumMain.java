package display;

import processing.core.PApplet;

public class MuseumMain {

	public static void main(String[] args) {
		
		MuseumFinal museum = new MuseumFinal();
		
		PApplet.runSketch(new String[] { "Start" }, museum);
	}
}
