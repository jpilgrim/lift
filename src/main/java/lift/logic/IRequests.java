package lift.logic;

public interface IRequests {

	int min();

	int max();

	void remove(int floor);

	void add(int floor);

	boolean isEmpty();

	boolean contains(int floor);

	void clear();

}