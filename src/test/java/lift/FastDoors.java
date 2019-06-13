package lift;

import lift.logic.IDoors;

public class FastDoors implements IDoors {

	private boolean open = false;
	
	@Override
	public void open() {
		open = true;
	}

	@Override
	public void close() {
		open = false;
		
	}

	@Override
	public boolean isOpened() {
		return open;
	}

	

}
