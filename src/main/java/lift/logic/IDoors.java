package lift.logic;

public interface IDoors {

	void open();

	void close();

	boolean isOpened();

	default boolean isClosed() {
		return !isOpened();
	}

}