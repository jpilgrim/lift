package lift.stm;

import java.util.function.Supplier;

/**
 * Transition werden über ein einfaches sogenanntes "Builder"-Pattern erzeugt und verwenden die in Java 8 eingeführten Features aus der funktionalen Programmierung, also
 * 
 * <pre><code>
 * new Transition(source, target).when(()->...).guard(() -> ...);
 * </code></pre>
 * wobei die "..." mit Expressions zu füllen sind. 
 * 
 * {@link #when(Supplier)} und der optionalle {@link Trigger#guard(Supplier)} geben den Trigger zurück, um das Builder-Pattern zu ermöglichen. D.h. wir können durch
 * eine Aufrufkette die Transition mit Trigger konfigurieren.
 * 
 * Beispiel:
 * 
 * <pre><code>
 * new Transition(source, target).when(()-> x>5 );
 * new Transition(source, target).after(100).guard(()-> i<10 && doSometing()==0 );
 * </code></pre>
 */
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
