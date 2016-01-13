package persons;

import java.util.HashMap;

public class TriggerBox {


	private String id;

	// amount of PersonPoints in the Box
	private int depthPointsInBox;

	// <PersonID, Points of that Person>
	private HashMap<String, Integer> pointsPerPerson;

	/**
	 * Constructs a new TriggerBox
	 * 
	 * @param id
	 *            ID
	 * @param x
	 *            x Position
	 * @param y
	 *            y Position
	 * @param z
	 *            z Position
	 * @param width
	 *            width of Box
	 * @param height
	 *            height of Box
	 * @param depth
	 *            depth of Box
	 * @param color
	 *            color of Box
	 */
	public TriggerBox(String id, int depthPoints, HashMap<String, Integer> pointsPerPerson) {

		this.id = id;
		this.depthPointsInBox = depthPoints;
		this.pointsPerPerson = pointsPerPerson;
	}

	/*-------------Setters--------------*/

	public void setID(String id) {
		this.id = id;
	}

	public void setPointsPerPerson(HashMap<String, Integer> depthPointsPerPerson) {

		this.pointsPerPerson = depthPointsPerPerson;
	}

	public void setPointsInsideBox(int points) {
		this.depthPointsInBox = points;
	}

	/*-------------Getters--------------*/

	public String getID() {
		return id;
	}

	public int getPointsInsideBox() {
		return depthPointsInBox;
	}

	public HashMap<String, Integer> getPointsPerPerson() {
		return pointsPerPerson;
	}

}
