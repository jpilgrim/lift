package lift.boundary;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Die Türen benötigen 500  Millisekunden zum schließen.
 */
public class Doors {

	private boolean open = false;
	
	public void open() {
		doMove(true);
	}
	public void close() {
		doMove(false);
	}
	public boolean isOpened() {
		return open;
	}
	
	public boolean isClosed() {
		return !open;
	}
	
	private void doMove(final boolean val) {
		if (open==val) {
			return;
		}
		// Wir simulieren hier einen Sensor, der nach 500 ms den Status der Türen bestätigt.
		new Timer().schedule(
			new TimerTask() {
				
				@Override
				public void run() {
					setOpen(val);
				}
			}, 500);
	}
	
	
	
	private synchronized void setOpen(boolean val) {
		open = val;
	}
}
