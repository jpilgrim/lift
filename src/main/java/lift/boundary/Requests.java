package lift.boundary;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Liste der Anfragen für Zielstockwerke.
 */
public class Requests {
	private final LinkedHashSet<Integer> floors = new LinkedHashSet<>();
	
	public int min() {
		Optional<Integer> optMin = floors.stream().min((i1, i2) -> i1 - i2);
		if (optMin.isPresent()) {
			return optMin.get();
		}
		throw new IllegalStateException("No request found");
	}

	public int max() {
		Optional<Integer> optMax = floors.stream().max((i1, i2) -> i1 - i2);
		if (optMax.isPresent()) {
			return optMax.get();
		}
		throw new IllegalStateException("No request found");
	}

	public void remove(int floor) {
		floors.remove(floor);
		
	}

	public void add(int floor) {
		floors.add(floor);
	}

	public boolean isEmpty() {
		return floors.isEmpty();
	}

	public boolean contains(int floor) {
		return floors.contains(floor);
	}

	public void clear() {
		floors.clear();
	}
	
	@Override
	public String toString() {
		return "("+floors.stream().map(f -> f.toString()).collect(Collectors.joining(","))+")";
	}

}
