package movement;

import java.util.ArrayList;
import java.util.HashMap;

import oscP5.OscEventListener;
import oscP5.OscMessage;
import oscP5.OscP5;
import oscP5.OscProperties;
import oscP5.OscStatus;
import persons.Person;
import processing.core.PVector;

public class OSCReceiver implements OscEventListener {

	private OscP5 oscReceiver;
	private MovementHandler handler;
	private OscProperties properties;

	public OSCReceiver(MovementHandler handler) {
		
		properties = new OscProperties();
		properties.setDatagramSize(65536);
		properties.setListeningPort(9999);
		oscReceiver = new OscP5(this, properties);
		this.handler = handler;
	}

	public void interrupt() {
		oscReceiver.stop();
	}
	
	@Override
	public void oscEvent(OscMessage message) {

		String typetag = message.typetag();

		String messageType = message.addrPattern();

		// Switch message handling via Adress Pattern
		if (messageType.equals("/argusClient/personEntered")
				|| messageType.equals("/argusClient/personUpdated")) {
			Person tempPerson = null;
			for (int i = 0; i < typetag.length(); i++) {
				if (typetag.charAt(i) == 's') {
					switch (message.get(i).stringValue()) {
					case "set":
						tempPerson = new Person(message.get(++i).stringValue(),
								message.get(++i).intValue());
						break;
					case "centroid":
						tempPerson.setCentroid(new PVector(message.get(++i).floatValue(),
								message.get(++i).floatValue(), message.get(++i).floatValue()));
						break;
					case "velocity":
						tempPerson.setVelocity(new PVector(message.get(++i).floatValue(),
								message.get(++i).floatValue(), message.get(++i).floatValue()));
						break;
					case "acceleration":
						tempPerson.setAcceleration(new PVector(message.get(++i).floatValue(),
								message.get(++i).floatValue(), message.get(++i).floatValue()));
						break;
					case "center":
						tempPerson.setCenter(new PVector(message.get(++i).floatValue(),
								message.get(++i).floatValue(), message.get(++i).floatValue()));
						break;
					case "contour":
						ArrayList<PVector> contour = new ArrayList<PVector>();
						while (typetag.charAt(++i) != 's') {
							contour.add(new PVector(message.get(i).floatValue(),
									message.get(++i).floatValue(), message.get(++i).floatValue()));
						}
						i--;
						tempPerson.setContour(contour);
						break;
					case "fseq":
						tempPerson.setLastUdate((message.get(++i).intValue()));
						if (messageType.equals("/argusClient/personEntered"))
							handler.personEntered(tempPerson);
						else
							handler.personMoved(tempPerson);
						break;
					default:
						System.out.println("Couldn't parse new Person");
						break;
					}
				}
			}
		} else {
			if (messageType.equals("/argusClient/personLeft")) {

				handler.personLeft(message.get(1).stringValue());
			} else if (messageType.equals("/argusClient/triggerzone")) {
				String id = null;
				int pointsInsideBox = 0;
				HashMap<Integer, Integer> pointsperPerson = new HashMap<Integer, Integer>();

				for (int i = 0; i < typetag.length(); i++) {

					if (typetag.charAt(i) == 's') {
						if (message.get(i).stringValue().equals("set")) {

							id = message.get(++i).stringValue();
							i+=2;
							pointsInsideBox = message.get(i).intValue();
							i+=2;
							while (typetag.charAt(i) == 'i') {

								pointsperPerson.put(message.get(i++).intValue(),
										message.get(i++).intValue());
							}
							i--;
						} else {
							if (message.get(i).stringValue().equals("fseq")) {

								handler.updateTriggerZone(id, pointsInsideBox, pointsperPerson,
										(long)(message.get(++i).intValue()));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void oscStatus(OscStatus theStatus) {
		System.out.println("### Printing Status");
		System.out.println(theStatus);
	}
}
