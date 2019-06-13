package lift.logic;

import lift.stm.State;
import lift.stm.Transition;

public abstract class StateMachine {

	/**
	 * Der aktuelle Zustand, wird über {@link #start()} in den ersten Zustand
	 * gesetzt. Public for testing!
	 */
	public State currentState = null;
	/**
	 * Der initiale Zustand, wird im Konstruktor gesetzt
	 */
	protected final State initialState;

	protected StateMachine() {
		initialState = initStateMachine();
	}
	
	/**
	 * Override in subtype, create all states and transitions, return initial state. Called from constructor.
	 */
	protected abstract State initStateMachine();

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