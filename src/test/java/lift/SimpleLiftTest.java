package lift;

import org.junit.jupiter.api.Test;

import lift.logic.MainControl;

class SimpleLiftTest {

	MainControl main = new MainControl();

	@Test
	void test() {
		System.out.println("Test 5");
		main.start();
		System.out.println(main);
		for (int i = 0; i < 60; i++) {
			try {
				Thread.sleep(400); // Türen schließen in 500 ms, Wait 1000-2000 ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
