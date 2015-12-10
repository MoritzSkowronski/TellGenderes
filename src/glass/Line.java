package glass;

import processing.core.PApplet;
import processing.core.PVector;

public class Line {

	private PVector start;
	private PVector end;
	private float distance;
	private LineType type;
	private Fragment belongsTo;
	private boolean visible;

	// TODO
	private static int lineCount;

	public Line(Fragment fragment, PVector start, PVector end, LineType type, boolean calc) {

		this.belongsTo = fragment;
		this.setStart(start);
		this.setEnd(end);
		if (calc) {

			// TODO cut them down
			calculateRandomizedEnd();
		}
		this.type = type;
		visible = false;
	}

	private void calculateRandomizedEnd() {

		float interp = (float) Math.random();

		if (Math.random() < 0.2) {

			interp = PApplet.map(interp, 0, 1, 0, 0.5f);
		} else {

			interp = PApplet.map(interp, 0, 1, 0.5f, 2);
		}

		float x = PApplet.lerp(start.x, end.x, interp);
		float y = PApplet.lerp(start.y, end.y, interp);
		end = new PVector(x, y);
	}

	public Fragment belongsTo() {

		return belongsTo;
	}

	public void setVisiblity(boolean visibility) {

		visible = visibility;
	}

	public boolean isVisible() {

		return visible;
	}

	public PVector getStart() {
		return start;
	}

	public LineType getType() {

		return type;
	}

	public void setStart(PVector start) {
		this.start = start;
		calculateLineDistance();
	}

	public PVector getEnd() {
		return end;
	}

	public void setEnd(PVector end) {
		this.end = end;
		calculateLineDistance();
	}

	public float getDistance() {

		return distance;
	}

	private void calculateLineDistance() {

		if (start != null && end != null) {

			distance = PApplet.dist(start.x, start.y, end.x, end.y);
		}
	}

}
