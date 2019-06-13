package lift.boundary;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import lift.logic.IDoors;
import lift.logic.TimePiece;

/**
 * Die Türen benötigen 500  Millisekunden zum schließen.
 */
public class Doors implements IDoors {

	private boolean open = false;
	
	@Inject TimePiece timePiece;
	
	@Override
	public void open() {
		doMove(true);
	}
	@Override
	public void close() {
		doMove(false);
	}
	@Override
	public boolean isOpened() {
		return open;
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
			}, timePiece.doorsClosingTime());
	}
	
	
	
	private synchronized void setOpen(boolean val) {
		open = val;
	}
}
