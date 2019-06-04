package lift.stm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modelliert einen Zustand mit entry, exit und do Verhalten.
 * 
 * Außerdem sind die ausgehenden Transitionen vermerkt, da deren Trigger beim
 * Eintritt aktiviert und beim Verlassen deaktiviert werden müssen.
 */
public abstract class State {
	public final String name;
	private final List<Transition> outgoing = new ArrayList<>();

	public State(String name) {
		this.name = name;
	}

	/**
	 * Fügt neue Transition hinzu, wird nur von Transition aufgerufen!
	 * 
	 * @param transition
	 */
	void addOutgoingTransition(Transition transition) {
		if (this == transition.source) {
			outgoing.add(transition);
		}
	}
	

	@Override
	public String toString() {
		return name
		// + "(" + outgoing.stream().map(t ->
		// t.toString()).collect(Collectors.joining(",")) + ")"
		;
	}

	public Transition trigger() {
		List<Transition> transitions = outgoing.stream().filter(t -> t.trigger.applies())
				.collect(Collectors.toList());
		switch (transitions.size()) {
		case 0:
			return null; // no trigger was activated
		case 1:
			return transitions.get(0);
		default:
			throw new IllegalStateException("More than one transition was triggered in state " + this);
		}

	}

	public void deactivate() {
		outgoing.stream().forEach(t -> t.trigger.deactivate());
	}

	public void activate() {
		outgoing.stream().forEach(t -> t.trigger.activate());
	}
	
	
	/**
	 * Kann von konkretem Zustand überschrieben werden.
	 */
	public void entry() {
	}

	/**
	 * Kann von konkretem Zustand überschrieben werden.
	 */
	public void exit() {
	}

	/**
	 * Kann von konkretem Zustand überschrieben werden.
	 */
	public void do_() {
	}


}
