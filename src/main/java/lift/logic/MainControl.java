package lift.logic;

import lift.boundary.Doors;
import lift.boundary.Requests;
import lift.stm.State;
import lift.stm.Transition;

/**
 * MainControl für eine Liftsteuerung, State-Machine siehe Aufgabenstellung.
 *
 */
public class MainControl {

	/**
	 * Die Anfragen nach oben. Diese werden über {@link #requestUp(int)}
	 * eingetragen. Praktisch sind das die Schalter im Stockwerk, wobei sich dann
	 * der "Floor" aus dem Stockwerk ergibt, oder die Schalter im Lift, wobei das
	 * "up/down" dann berechnet werden -- das interessiert hier aber aktuell nicht.
	 */
	final Requests upRequests = new Requests();

	/**
	 * Die Anfragen nach oben. Diese werden über {@link #requestDown(int)}
	 * eingetragen. Praktisch sind das die Schalter im Stockwerk, wobei sich dann
	 * der "Floor" aus dem Stockwerk ergibt, oder die Schalter im Lift, wobei das
	 * "up/down" dann berechnet werden -- das interessiert hier aber aktuell nicht.
	 */
	final Requests downRequests = new Requests();

	/**
	 * Das aktuelle Stockwerk.
	 */
	int currentFloor = 0;

	/**
	 * Die Türen.
	 */
	Doors doors = new Doors();

	/**
	 * Der aktuelle Zustand, wird über {@link #start()} in den ersten Zustand
	 * gesetzt. Public for testing!
	 */
	public State currentState = null;

	/**
	 * Der initiale Zustand, wird im Konstruktor gesetzt
	 */
	final State initialState;

	/**
	 * Flag das anzeigt, ob der Lift sich bewegt.
	 */
	private boolean moving;

	/**
	 * Erzeugt die States mit Transitionen.
	 */
	public MainControl() {

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

		initialState = initializing;

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

	public synchronized void requestUp(int floor) {
		upRequests.add(floor);
	}

	public synchronized void requestDown(int floor) {
		downRequests.add(floor);
	}

	public void start() {
		currentState = initialState;
		currentState.activate();
	}

	/**
	 * @return true wenn der State geändert wurde
	 */
	public boolean step() {
		boolean changed = false;
		Transition transition = currentState.trigger();
		if (transition != null) {
			currentState.deactivate();
			currentState.exit();
			currentState = transition.target;
			currentState.entry();
			currentState.activate();
			changed = true;

		}
		currentState.do_();
		return changed;
	}

}
