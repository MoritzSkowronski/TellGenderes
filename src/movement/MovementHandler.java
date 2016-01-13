package movement;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import persons.Person;
import persons.TriggerBox;

public class MovementHandler {

	private ConcurrentHashMap<String, TriggerBox> triggerzones;
	private ConcurrentHashMap<String, Person> persons;

	private boolean personUpdate;
	private boolean triggerUpdate;

	public MovementHandler() {

		triggerzones = new ConcurrentHashMap<String, TriggerBox>();
		persons = new ConcurrentHashMap<String, Person>();
	}

	public void personMoved(Person tempPerson) {

		Person temp = persons.get(tempPerson.getId());
		if (temp != null) {
			
			temp.update(tempPerson);
		}
		else{
			
			personEntered(tempPerson);
		}
		personUpdate = true;
	}

	public void updateTriggerZone(String id, int pointsInsideBox,
			HashMap<String, Integer> pointsperPerson, Long valueOf) {

		if (triggerzones.containsKey(id)) {

			TriggerBox box = triggerzones.get(id);
			box.setPointsInsideBox(pointsInsideBox);
			box.setPointsPerPerson(pointsperPerson);
		} else {

			triggerzones.put(id, new TriggerBox(id, pointsInsideBox, pointsperPerson));
		}

		triggerUpdate = true;
	}

	public void personEntered(Person tempPerson) {

		persons.put(tempPerson.getId(), tempPerson);

		personUpdate = true;
	}

	public void personLeft(String value) {

		persons.remove(value);

		personUpdate = true;
	}

	public boolean updateReady() {

		if (triggerUpdate) {

			triggerUpdate = false;
			return true;
		}

		if (personUpdate) {

			personUpdate = false;
			return true;
		}

		return false;
	}
	
	public ConcurrentHashMap<String, Person> getPersons(){
		
		return persons;
	}
	
	public ConcurrentHashMap<String, TriggerBox> getTriggerZones(){
		
		return triggerzones;
	}

}
