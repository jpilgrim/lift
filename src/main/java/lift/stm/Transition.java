package lift.stm;

import java.util.function.Supplier;

public class Transition {

	public final State source;
	public final State target;
	public Trigger trigger;

	public Transition(State source, State target) {
		this.source = source;
		this.target = target;
		source.addOutgoingTransition(this);
	}

	public Trigger when(Supplier<Boolean> change) {
		this.trigger = new Trigger();
		return trigger.when(change);
	}

	public Trigger after(long timeout) {
		this.trigger = new Trigger();
		return trigger.after(timeout);
	}
	
	@Override
	public String toString() {
		return "->"+target.name+trigger.toString();
		
	}

}
