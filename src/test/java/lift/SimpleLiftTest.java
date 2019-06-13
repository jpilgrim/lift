package lift;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import lift.boundary.Requests;
import lift.logic.IDoors;
import lift.logic.IRequests;
import lift.logic.MainControl;

class SimpleLiftTest {

	MainControl main;
	
	
	public SimpleLiftTest() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
//				bind(IDoors.class).to(Doors.class);
				bind(IDoors.class).to(FastDoors.class);
				bind(IRequests.class).to(Requests.class);
			}
		});
		
		main = injector.getInstance(MainControl.class);
	}
	

	@Test
	void test() {
		main.start();
		System.out.println(main);
		for (int i = 0; i < 60; i++) {
//			try {
//				Thread.sleep(400); // Türen schließen in 500 ms, Wait 1000-2000 ms
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			if (main.step()) {
				System.out.println(main);
			}
			if (main.currentState.name.contentEquals("idle")) {
				if (i < 7) {
					main.requestUp(3);
					main.requestUp(5);
				}
			}
			if (i == 10) {
				main.requestDown(8);
				main.requestDown(1);

			}
		}

	}

}
