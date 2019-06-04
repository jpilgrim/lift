package lift.logic;

import java.util.function.Supplier;

import lift.Doors;
import lift.Requests;
import lift.stm.State;
import lift.stm.Transition;

/**
 * MainControl für eine Liftsteuerung, State-Machine siehe Aufgabenstellung.
 *
 */
public class MainControl {

	/**
	 * Kommt irgendwie bekannt vor...
	 */
	private static final int MAX_FLOOR = 13;

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
	 * gesetzt.
	 * Public for testing!
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

		State closeForUp = new State("closeForUp") {
			@Override
			public void do_() {
				closeDoors();
			}
		};
		State fastMovingDown = new State("fastMovingDown") {
			@Override
			public void do_() {
				moveDown();
			}
		};
		State movingUp = new State("movingUp") {
			@Override
			public void do_() {
				moveUp();
			}
		};
		State openingDoorsDuringUp = new State("openingDoorsDuringUp") {
			@Override
			public void entry() {
				stop();
				upRequests.remove(currentFloor);
			}

			@Override
			public void do_() {
				openDoors();
			}
		};
		State waitAfterUp = new State("waitAfterUp") {
		};
		State closeDuringUp = new State("closeDuringUp") {
			@Override
			public void do_() {
				closeDoors();
			}
		};

		State closeForDown = new State("closeForDown") {
			@Override
			public void do_() {
				closeDoors();
			}
		};
		State fastMovingUp= new State("fastMovingUp") {
			@Override
			public void do_() {
				moveUp();
			}
		};
		State movingDown = new State("movingDown") {
			@Override
			public void do_() {
				moveDown();
			}
		};
		State openingDoorsDuringDown = new State("openingDoorsDuringDown") {
			@Override
			public void entry() {
				stop();
				downRequests.remove(currentFloor);
			}
			
			@Override
			public void do_() {
				openDoors();
			}
		};
		State waitAfterDown = new State("waitAfterDown") {
		};
		State closeDuringDown = new State("closeDuringDown") {
			@Override
			public void do_() {
				closeDoors();
			}
		};

		// und nun alle Transitionen:
		new Transition(initializing, openingDoors).when(() -> currentFloor == 0);
		new Transition(openingDoors, idle).when(() -> doors.isOpened());
		new Transition(idle, closeForUp).when(() -> !upRequests.isEmpty() && downRequests.isEmpty());
		new Transition(idle, closeForDown).when(() -> !downRequests.isEmpty());
		// hoch fahren (oder runter fahren um hoch zu fahren)
		Supplier<Boolean> fastMovingDownCond = () -> currentFloor > upRequests.min();
		new Transition(closeForUp, fastMovingDown).when(() -> doors.isClosed() && fastMovingDownCond.get());
		new Transition(closeForUp, movingUp).when(() -> doors.isClosed() && !fastMovingDownCond.get()); // else
		
		new Transition(fastMovingDown, openingDoorsDuringUp)
				.when(() -> doors.isClosed() && currentFloor == upRequests.min());
		new Transition(movingUp, openingDoorsDuringUp).when(() -> upRequests.contains(currentFloor));

		new Transition(openingDoorsDuringUp, waitAfterUp).when(() -> doors.isOpened());

		new Transition(waitAfterUp, closeDuringUp).after(1000).guard(() -> !upRequests.isEmpty() && currentFloor < upRequests.max());
		new Transition(waitAfterUp, idle).after(2000);
		new Transition(closeDuringUp, movingUp).when(() -> doors.isClosed());

		// runter fahren (oder hoch fahren um runter zu fahren)
		Supplier<Boolean> fastMovingUpCond = () -> currentFloor < downRequests.max();
		new Transition(closeForDown, fastMovingUp).when(() -> doors.isClosed() && fastMovingUpCond.get());
		new Transition(closeForDown, movingDown).when(() -> doors.isClosed() && !fastMovingUpCond.get()); // else
		
		new Transition(fastMovingUp, openingDoorsDuringDown)
				.when(() -> doors.isClosed() && currentFloor == downRequests.max());
		new Transition(movingDown, openingDoorsDuringDown).when(() -> downRequests.contains(currentFloor));
		
		new Transition(openingDoorsDuringDown, waitAfterDown).when(() -> doors.isOpened());
		
		new Transition(waitAfterDown, closeDuringDown).after(1000).guard(() -> !downRequests.isEmpty() && currentFloor > downRequests.max());
		new Transition(waitAfterDown, idle).after(2000);
    	new Transition(closeDuringDown, movingDown).when(() -> doors.isClosed());
	}

	protected void openDoors() {
		doors.open();
	}

	protected void closeDoors() {
		doors.close();
	}

	protected void stop() {
		moving = false;
	}

	protected void clearRequests() {
		upRequests.clear();
		downRequests.clear();
	}

	protected void moveDown() {
		moving = true;
		if (currentFloor > 0) {
			currentFloor--;
		}
	}

	protected void moveUp() {
		moving = true;
		if (currentFloor < MAX_FLOOR) {
			currentFloor++;
		}
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
			strb.append(", upRequests=")
				.append(upRequests);}
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
		if (transition!=null) {
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
