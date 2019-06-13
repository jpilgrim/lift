package lift.logic;

import javax.inject.Inject;

import lift.stm.State;
import lift.stm.Transition;

/**
 * MainControl für eine Liftsteuerung, State-Machine siehe Aufgabenstellung.
 *
 */
public class MainControl extends StateMachine {

	/**
	 * Die Anfragen nach oben. Diese werden über {@link #requestUp(int)}
	 * eingetragen. Praktisch sind das die Schalter im Stockwerk, wobei sich dann
	 * der "Floor" aus dem Stockwerk ergibt, oder die Schalter im Lift, wobei das
	 * "up/down" dann berechnet werden -- das interessiert hier aber aktuell nicht.
	 */
	@Inject
	/* final */ IRequests upRequests;

	/**
	 * Die Anfragen nach oben. Diese werden über {@link #requestDown(int)}
	 * eingetragen. Praktisch sind das die Schalter im Stockwerk, wobei sich dann
	 * der "Floor" aus dem Stockwerk ergibt, oder die Schalter im Lift, wobei das
	 * "up/down" dann berechnet werden -- das interessiert hier aber aktuell nicht.
	 */
	@Inject
	/* final */ IRequests downRequests;


	/**
	 * Die Türen.
	 */
	@Inject
	IDoors doors;
	
	/**
	 * Das aktuelle Stockwerk.
	 */
	int currentFloor = 0;

	/**
	 * Flag das anzeigt, ob der Lift sich bewegt.
	 */
	boolean moving;

	/**
	 * Erzeugt die States mit Transitionen.
	 */
	public MainControl() {}
	
	@Override
	protected lift.stm.State initStateMachine() {
	
		// zuerst alle States mit Verhalten (Behavior)
		State initializing = new State("initializing") {
			@Override
			public void entry() {
				clearRequests();
			}

			@Override
			public void do_() {
				moveDown();
			}
		};

		State openingDoors = new State("openingDoors") {
			@Override
			public void entry() {
				stop();
			}

			@Override
			public void do_() {
				openDoors();
			}
		};
		State idle = new State("idle") {
		};

		// TODO ergänzen Sie hier die fehlenden States

		// und nun alle Transitionen:

		new Transition(initializing, openingDoors).when(() -> currentFloor == 0);
		new Transition(openingDoors, idle).when(() -> doors.isOpened());

		// TODO ergänzen Sie hier die fehlenden Transitionen

	
		return initializing;
	
	}

	/**
	 * Behaviour: Öffnen der Türen
	 */
	protected void openDoors() {
		doors.open();
	}

	/**
	 * Behaviour: Stoppen des Aufzugs, aufgrund der aktuellen Implementierung hier
	 * einfach als Flag umgesetzt.
	 */
	protected void stop() {
		moving = false;
	}

	/**
	 * Behaviour: Löschen aller Anfragen.
	 */
	protected void clearRequests() {
		upRequests.clear();
		downRequests.clear();
	}

	/**
	 * Behaviour: Nach unten Fahren, aufgrund der aktuellen Implementierung wird
	 * hier immer nur ein Stockwerk nach unten gefahren.
	 */
	protected void moveDown() {
		moving = true;
		if (currentFloor > 0) {
			currentFloor--;
		}
	}

	// TODO ergänzen Sie bei Bedarf hier weitere "Behaviours"

	public synchronized void requestUp(int floor) {
		upRequests.add(floor);
	}

	public synchronized void requestDown(int floor) {
		downRequests.add(floor);
	}

	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		if (currentState == null) {
			return "no state set";
		}
		strb.append(currentState).append(": currentFloor=").append(currentFloor)
				.append(doors.isOpened() ? ", open" : ", closed").append(moving ? ", moving" : "");
		if (!downRequests.isEmpty()) {
			strb.append(", downRequests=").append(downRequests);
		}
		if (!upRequests.isEmpty()) {
			strb.append(", upRequests=").append(upRequests);
		}
		return strb.toString();
	}
}
