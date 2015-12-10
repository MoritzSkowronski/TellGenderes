package persons;

import java.util.ArrayList;

import processing.core.PShape;
import processing.core.PVector;

public class Person {


	// The id of a person
	private String id;
	// Time of Person entry
	private long starttime;
	// How long the person has been in the scene
	private long timestamp;
	// The last time a Person has been updated by Client
	private long lastUpdate;
	// how many frames the person has been in the view of the camera
	private int age;
	// average of all pixels of a person
	private PVector centroid;
	// absolute distance between to frames
	private float distance;
	// Velocity of a Person
	private PVector velocity;
	// Acceleration of a Person
	private PVector acceleration;
	// center of the bounding box
	private PVector center;
	// Contour of a person, consists of many points
	private ArrayList<PVector> contour;
	// Smallest possible rectangle around tracked contour
	private PShape boundingBox;

	/*------------------Constructor------------------*/

	public Person(String id, int age, long lastUpdate, PVector centroid, PVector center, PVector velocity,
			PVector acceleration, ArrayList<PVector> contour) {
		this.id = id;
		starttime = System.currentTimeMillis();
		this.lastUpdate = lastUpdate;
		this.age = age;
		this.centroid = centroid;
		this.center = center;
		this.contour = contour;
		this.velocity = velocity;
		this.acceleration = acceleration;

		// TODO calculate boundingBox
	}

	/**
	 * For Creating a temporary Person while receiving data
	 * 
	 * @param id
	 * @param age
	 */
	public Person(String id, int age) {
		this.id = id;
		this.age = age;
	}

	/*------------------Methods------------------*/

	/**
	 * Update all Values
	 * 
	 * @param Person
	 *            person
	 */
	public synchronized void update(Person person) {
		age++;
		this.timestamp = System.currentTimeMillis() - starttime;
		this.lastUpdate = person.getLastUpdate();
		this.acceleration = person.getAcceleration();
		this.centroid = person.getCentroid();
		this.center = person.getCenter();
		this.contour = person.getContour();
		this.velocity = person.getVelocity();

		// TODO calculate BoundingBox
	}

	/*------------------Getter------------------*/

	public PVector getCentroid() {
		return centroid;
	}

	public String getId() {
		return id;
	}

	public int getAge() {
		return age;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public PVector getVelocity() {
		return velocity;
	}

	public PVector getCenter() {
		return center;
	}

	public ArrayList<PVector> getContour() {
		return contour;
	}

	public PShape getBoundingBox() {
		return boundingBox;
	}

	public PVector getAcceleration() {
		return acceleration;
	}

	public float getDistance() {
		return distance;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setVelocity(PVector velocity) {
		this.velocity = velocity;
	}

	public void setAcceleration(PVector acceleration) {
		this.acceleration = acceleration;
	}

	public void setCentroid(PVector centroid) {
		this.centroid = centroid;
	}

	public void setCenter(PVector center) {
		this.center = center;
	}

	public void setContour(ArrayList<PVector> contour) {
		this.contour = contour;
	}

	public void setLastUdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
